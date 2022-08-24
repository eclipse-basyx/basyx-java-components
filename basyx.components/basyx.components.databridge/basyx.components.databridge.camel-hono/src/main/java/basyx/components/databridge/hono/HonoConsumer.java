/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.databridge.hono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;
import org.eclipse.hono.application.client.ApplicationClient;
import org.eclipse.hono.application.client.DownstreamMessage;
import org.eclipse.hono.application.client.MessageConsumer;
import org.eclipse.hono.application.client.MessageContext;
import org.eclipse.hono.application.client.amqp.AmqpApplicationClient;
import org.eclipse.hono.application.client.amqp.ProtonBasedApplicationClient;
import org.eclipse.hono.client.HonoConnection;
import org.eclipse.hono.client.ServiceInvocationException;
import org.eclipse.hono.config.ClientConfigProperties;
import org.eclipse.hono.util.TimeUntilDisconnectNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

public class HonoConsumer extends DefaultConsumer {
	private static final Logger logger = LoggerFactory.getLogger(HonoConsumer.class);
	private final HonoEndpoint endpoint;
    private final Vertx vertx = Vertx.vertx();
    private final ApplicationClient<? extends MessageContext> client;
    private static final long COMMAND_INTERVAL_FOR_DEVICES_CONNECTED_WITH_UNLIMITED_EXPIRY = 5;
    public static final Boolean SEND_ONE_WAY_COMMANDS = false;
    private MessageConsumer eventConsumer;
    private MessageConsumer telemetryConsumer;    
    
    /**
     * A map holding the last reported notification for a device being connected. Will be emptied as soon as the notification
     * is handled.
     * Only affects devices that use a connection oriented protocol like MQTT.
     */
    private final Map<String, TimeUntilDisconnectNotification> pendingTtdNotification = new HashMap<>();
    

    /**
     * A map holding a handler to cancel a timer that was started to send commands periodically to a device.
     * Only affects devices that use a connection oriented protocol like MQTT.
     */
    private final Map<String, Handler<Void>> periodicCommandSenderTimerCancelerMap = new HashMap<>();

	public HonoConsumer(HonoEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
		logger.info(endpoint.getEndpointUri());
		logger.info(endpoint.getEndpointBaseUri());
		this.endpoint = endpoint;
		this.client = createAmqpApplicationClient();
	}

	@Override
	protected void doStart() throws Exception {
		final CountDownLatch latch = new CountDownLatch(1);

        final Future<CompositeFuture> startFuture = client.start()
                .onSuccess(v -> {
                    final AmqpApplicationClient ac = (AmqpApplicationClient) client;
                    ac.addDisconnectListener(c -> logger.info("lost connection to Hono, trying to reconnect ..."));
                    ac.addReconnectListener(c -> logger.info("reconnected to Hono"));
                })
                .compose(v -> CompositeFuture.all(createEventConsumer(), createTelemetryConsumer()))
                .onSuccess(v -> logger.info("Consumer ready for telemetry and event messages."))
                .onFailure(cause -> logger.error("{} consumer failed to start [{}:{}]",
                        "AMQP", endpoint.getName(), endpoint.getPort(), cause))
                .onComplete(ar -> latch.countDown());

        latch.await();

        @SuppressWarnings("rawtypes")
        final List<Future> closeFutures = new ArrayList<>();
        if (startFuture.succeeded()) {
            System.in.read();
            closeFutures.add(eventConsumer.close());
            closeFutures.add(telemetryConsumer.close());
            closeFutures.add(client.stop());
        }
        CompositeFuture.join(closeFutures).onComplete(ar -> vertx.close()); // wait for clients to be closed
	}

	@Override
	protected void doStop() throws Exception {
		logger.debug("Message processor is shutdown");
		if (client != null) {
			client.stop();	
		}
	}

	/**
     * The consumer needs one connection to the AMQP 1.0 messaging network from which it can consume data.
     * <p>
     * The client for receiving data is instantiated here.
     * </p>
     */
    private ApplicationClient<? extends MessageContext> createAmqpApplicationClient() {
        final ClientConfigProperties props = new ClientConfigProperties();
        props.setHost(this.endpoint.getHonoHost());
        props.setPort(Integer.parseInt(this.endpoint.getPort()));
        
        props.setUsername(this.endpoint.getUserName());
        props.setPassword(this.endpoint.getPassword());
        props.setHostnameVerificationRequired(false);

        return new ProtonBasedApplicationClient(HonoConnection.newConnection(vertx, props));
    }

	public ApplicationClient<? extends MessageContext> getClient() {
		return client;
	}
	
    /**
     * Handler method for a Message from Hono that was received as telemetry data.
     * <p>
     * The tenant, the device, the payload, the content-type, the creation-time and the application properties will be printed to stdout.
     *
     * @param msg The message that was received.
     */
    private void handleTelemetryMessage(final DownstreamMessage<? extends MessageContext> msg) {
    	logger.debug("received telemetry data [tenant: {}, device: {}, content-type: {}]: [{}].",
                msg.getTenantId(), msg.getDeviceId(), msg.getContentType(), msg.getPayload());
    }

    /**
     * Handler method for a Message from Hono that was received as event data.
     * <p>
     * The tenant, the device, the payload, the content-type, the creation-time and the application properties will be printed to stdout.
     *
     * @param msg The message that was received.
     */
    private void handleEventMessage(final DownstreamMessage<? extends MessageContext> msg) {
    	logger.debug("received event [tenant: {}, device: {}, content-type: {}]: [{}].",
                msg.getTenantId(), msg.getDeviceId(), msg.getContentType(), msg.getPayload());
    }
    
    /**
     * Create the message consumer that handles event messages and invokes the notification callback
     * {@link #handleCommandReadinessNotification(TimeUntilDisconnectNotification)} if the message indicates that it
     * stays connected for a specified time.
     *
     * @return A succeeded future if the creation was successful, a failed Future otherwise.
     */
    private Future<MessageConsumer> createEventConsumer() {
        return client.createEventConsumer(
                this.endpoint.getTenantId(),
                msg -> {
                    // handle command readiness notification
                    msg.getTimeUntilDisconnectNotification().ifPresent(this::handleCommandReadinessNotification);
                    handleEventMessage(msg);
                },
                cause -> logger.error("event consumer closed by remote", cause))
                .onSuccess(eventConsumer -> this.eventConsumer = eventConsumer);
    }

    /**
     * Create the message consumer that handles telemetry messages and invokes the notification callback
     * {@link #handleCommandReadinessNotification(TimeUntilDisconnectNotification)} if the message indicates that it
     * stays connected for a specified time.
     *
     * @return A succeeded future if the creation was successful, a failed Future otherwise.
     */
    private Future<MessageConsumer> createTelemetryConsumer() {
        return client.createTelemetryConsumer(
                this.endpoint.getTenantId(),
                msg -> {
                    // handle command readiness notification
                    msg.getTimeUntilDisconnectNotification().ifPresent(this::handleCommandReadinessNotification);
                    handleTelemetryMessage(msg);
                },
                cause -> logger.error("telemetry consumer closed by remote", cause))
                .onSuccess(telemetryConsumer -> this.telemetryConsumer = telemetryConsumer);
    }
    
    /**
     * Handler method for a <em>device ready for command</em> notification (by an explicit event or contained implicitly in
     * another message).
     * <p>
     * For notifications with a positive ttd value (as usual for request-response protocols), the
     * code creates a simple command in JSON.
     * <p>
     * For notifications signalling a connection oriented protocol, the handling is delegated to
     * {@link #handlePermanentlyConnectedCommandReadinessNotification(TimeUntilDisconnectNotification)}.
     *
     * @param notification The notification containing the tenantId, deviceId and the Instant (that
     *                     defines until when this notification is valid). See {@link TimeUntilDisconnectNotification}.
     */
    private void handleCommandReadinessNotification(final TimeUntilDisconnectNotification notification) {
        if (notification.getTtd() <= 0) {
            handlePermanentlyConnectedCommandReadinessNotification(notification);
        } else {
            logger.info("Device is ready to receive a command : [{}].", notification);
            sendCommand(notification);
        }
    }
    
    /**
     * Handle a ttd notification for permanently connected devices.
     * <p>
     * Instead of immediately handling the notification, it is first put to a map and a timer is started to handle it
     * later. Notifications for the same device that are received before the timer expired, will overwrite the original
     * notification. By this an <em>event flickering</em> (like it could occur when starting the app while several
     * notifications were persisted in the messaging network) is handled correctly.
     * <p>
     * If the contained <em>ttd</em> is set to -1, a command will be sent periodically every
     * {@link HonoExampleConstants#COMMAND_INTERVAL_FOR_DEVICES_CONNECTED_WITH_UNLIMITED_EXPIRY} seconds to the device
     * until a new notification was received with a <em>ttd</em> set to 0.
     *
     * @param notification The notification of a permanently connected device to handle.
     */
    private void handlePermanentlyConnectedCommandReadinessNotification(final TimeUntilDisconnectNotification notification) {
        final String keyForDevice = notification.getTenantAndDeviceId();

        final TimeUntilDisconnectNotification previousNotification = pendingTtdNotification.get(keyForDevice);
        if (previousNotification != null) {
            if (notification.getCreationTime().isAfter(previousNotification.getCreationTime())) {
                logger.info("Set new ttd value [{}] of notification for [{}]", notification.getTtd(), notification.getTenantAndDeviceId());
                pendingTtdNotification.put(keyForDevice, notification);
            } else {
            	logger.trace("Received notification for [{}] that was already superseded by newer [{}]", notification, previousNotification);
            }
        } else {
            pendingTtdNotification.put(keyForDevice, notification);
            // there was no notification available already, so start a handler now
            vertx.setTimer(1000, timerId -> {
            	logger.debug("Handle device notification for [{}].", notification.getTenantAndDeviceId());
                // now take the notification from the pending map and handle it
                final TimeUntilDisconnectNotification notificationToHandle = pendingTtdNotification.remove(keyForDevice);
                if (notificationToHandle != null) {
                    if (notificationToHandle.getTtd() == -1) {
                    	logger.info("Device notified as being ready to receive a command until further notice : [{}].", notificationToHandle);

                        // cancel a still existing timer for this device (if found)
                        cancelPeriodicCommandSender(notification);
                        // immediately send the first command
                        sendCommand(notificationToHandle);

                        // for devices that stay connected, start a periodic timer now that repeatedly sends a command
                        // to the device
                        vertx.setPeriodic(
                                COMMAND_INTERVAL_FOR_DEVICES_CONNECTED_WITH_UNLIMITED_EXPIRY
                                        * 1000,
                                id -> {
                                    sendCommand(notificationToHandle);
                                    // register a canceler for this timer directly after it was created
                                    setPeriodicCommandSenderTimerCanceler(id, notification);
                                });
                    } else {
                        logger.info("Device notified as not being ready to receive a command (anymore) : [{}].", notification);
                        cancelPeriodicCommandSender(notificationToHandle);
                        logger.debug("Device will not receive further commands : [{}].", notification.getTenantAndDeviceId());
                    }
                }
            });
        }
    }
    
    private void cancelPeriodicCommandSender(final TimeUntilDisconnectNotification notification) {
        if (isPeriodicCommandSenderActiveForDevice(notification)) {
            logger.debug("Cancelling periodic sender for {}", notification.getTenantAndDeviceId());
            periodicCommandSenderTimerCancelerMap.get(notification.getTenantAndDeviceId()).handle(null);
        } else {
            logger.debug("Wanted to cancel periodic sender for {}, but could not find one", notification.getTenantAndDeviceId());
        }
    }
    
    private boolean isPeriodicCommandSenderActiveForDevice(final TimeUntilDisconnectNotification notification) {
        return (periodicCommandSenderTimerCancelerMap.containsKey(notification.getTenantAndDeviceId()));
    }
    
    /**
     * Sends a command to the device for which a {@link TimeUntilDisconnectNotification} was received.
     *
     * @param notification The notification that was received for the device.
     */
    private void sendCommand(final TimeUntilDisconnectNotification notification) {

        final long commandTimeout = calculateCommandTimeout(notification);
        // TODO set request timeout

        if (SEND_ONE_WAY_COMMANDS) {
            sendOneWayCommandToAdapter(notification.getTenantId(), notification.getDeviceId(), notification);
        } else {
            sendCommandToAdapter(notification.getTenantId(), notification.getDeviceId(), notification);
        }
    }
    
    /**
     * Calculate the timeout for a command that is tried to be sent to a device for which a {@link TimeUntilDisconnectNotification}
     * was received.
     *
     * @param notification The notification that was received for the device.
     * @return The timeout to be set for the command.
     */
    private long calculateCommandTimeout(final TimeUntilDisconnectNotification notification) {
        if (notification.getTtd() == -1) {
            // let the command expire directly before the next periodic timer is started
            return COMMAND_INTERVAL_FOR_DEVICES_CONNECTED_WITH_UNLIMITED_EXPIRY * 1000;
        } else {
            // let the command expire when the notification expires
            return notification.getMillisecondsUntilExpiry();
        }
    }
    
    /**
     * Send a command to the device for which a {@link TimeUntilDisconnectNotification} was received.
     * <p>
     * If the contained <em>ttd</em> is set to a value @gt; 0, the commandClient will be closed after a response was received.
     * If the contained <em>ttd</em> is set to -1, the commandClient will remain open for further commands to be sent.
     * @param ttdNotification The ttd notification that was received for the device.
     */
    private void sendCommandToAdapter(final String tenantId, final String deviceId,
            final TimeUntilDisconnectNotification ttdNotification) {
        final Buffer commandBuffer = buildCommandPayload();
        final String command = "setBrightness";
        if (logger.isDebugEnabled()) {
            logger.debug("Sending command [{}] to [{}].", command, ttdNotification.getTenantAndDeviceId());
        }

        client.sendCommand(tenantId, deviceId, command, "application/json", commandBuffer, buildCommandProperties()).map(result -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully sent command payload: [{}].", commandBuffer.toString());
                logger.debug("And received response: [{}].", Optional.ofNullable(result.getPayload()).orElseGet(Buffer::buffer).toString());
            }
            return result;
        }).otherwise(t -> {
            if (t instanceof ServiceInvocationException) {
                final int errorCode = ((ServiceInvocationException) t).getErrorCode();
                logger.debug("Command was replied with error code [{}].", errorCode);
            } else {
                logger.debug("Could not send command : {}.", t.getMessage());
            }
            return null;
        });
    }

    /**
     * Send a one way command to the device for which a {@link TimeUntilDisconnectNotification} was received.
     * <p>
     * If the contained <em>ttd</em> is set to a value @gt; 0, the commandClient will be closed after a response was received.
     * If the contained <em>ttd</em> is set to -1, the commandClient will remain open for further commands to be sent.
     * @param ttdNotification The ttd notification that was received for the device.
     */
    private void sendOneWayCommandToAdapter(final String tenantId, final String deviceId,
            final TimeUntilDisconnectNotification ttdNotification) {
        final Buffer commandBuffer = buildOneWayCommandPayload();
        final String command = "sendLifecycleInfo";

        if (logger.isDebugEnabled()) {
            logger.debug("Sending one-way command [{}] to [{}].", command, ttdNotification.getTenantAndDeviceId());
        }

        client.sendOneWayCommand(tenantId, deviceId, command, commandBuffer).map(statusResult -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Successfully sent one-way command payload: [{}] and received status [{}].", commandBuffer.toString(), statusResult);
            }
            return statusResult;
        }).otherwise(t -> {
            if (t instanceof ServiceInvocationException) {
                final int errorCode = ((ServiceInvocationException) t).getErrorCode();
                logger.debug("One-way command was replied with error code [{}].", errorCode);
            } else {
                logger.debug("Could not send one-way command : {}.", t.getMessage());
            }
            return null;
        });
    }

    /**
     * Provides an application property that is suitable to be sent with the command upstream.
     *
     * @return Map The application property map.
     */
    private Map<String, Object> buildCommandProperties() {
        final Map<String, Object> applicationProperties = new HashMap<>(1);
        applicationProperties.put("appId", "example#1");
        return applicationProperties;
    }

    private Buffer buildCommandPayload() {
        final JsonObject jsonCmd = new JsonObject().put("brightness", (int) (Math.random() * 100));
        return Buffer.buffer(jsonCmd.encodePrettily());
    }

    private Buffer buildOneWayCommandPayload() {
        final JsonObject jsonCmd = new JsonObject().put("info", "app restarted.");
        return Buffer.buffer(jsonCmd.encodePrettily());
    }
    
    private void setPeriodicCommandSenderTimerCanceler(final Long timerId,
            final TimeUntilDisconnectNotification ttdNotification) {
        this.periodicCommandSenderTimerCancelerMap.put(ttdNotification.getTenantAndDeviceId(), v -> {
            vertx.cancelTimer(timerId);
            periodicCommandSenderTimerCancelerMap.remove(ttdNotification.getTenantAndDeviceId());
        });
    }
}

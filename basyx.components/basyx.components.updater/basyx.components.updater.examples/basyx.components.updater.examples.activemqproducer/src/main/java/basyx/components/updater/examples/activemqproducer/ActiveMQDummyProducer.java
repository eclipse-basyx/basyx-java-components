/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.examples.activemqproducer;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQDummyProducer {
	public static final String SERVER = "tcp://127.0.0.1:61616";
	public static final String QUEUE = "first-topic";
    
	public static void main(String[] args) throws InterruptedException {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(SERVER);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(QUEUE);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            for (int i = 0; i < 100; i++) {
            	// Create a messages
            	String dataToSend = "{\"id\":" + i + ", \"temperature\": " + 50*i + "}";
                TextMessage message = session.createTextMessage(dataToSend);

                // Tell the producer to send the message
                System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);

                Thread.sleep(1000);
            }
            
            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}

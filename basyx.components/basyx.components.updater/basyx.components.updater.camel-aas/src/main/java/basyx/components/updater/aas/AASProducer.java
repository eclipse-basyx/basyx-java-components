/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.dataelement.ConnectedProperty;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AASProducer extends DefaultProducer {
	private static final Logger LOG = LoggerFactory.getLogger(AASProducer.class);
    private AASEndpoint endpoint;
	private ConnectedProperty connectedProperty;

    public AASProducer(AASEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
		LOG.info("Creating ASS Producer for endpoint " + endpoint.getEndpointUri());
		connectToElement();
    }

	@Override
	public void process(Exchange exchange) throws Exception {
		LOG.info("Got msg for property: " + exchange.getMessage().getBody(String.class));
    	connectedProperty.setValue(exchange.getMessage().getBody(String.class));
    }
	
	/**
	 * Connect the the Submodel Element for data dumping
	 */
    private void connectToElement() {
    	HTTPConnectorFactory factory = new HTTPConnectorFactory();
    	String proxyUrl = this.endpoint.getFullProxyUrl();
    	IModelProvider provider = factory.getConnector(proxyUrl);
    	VABElementProxy proxy = new VABElementProxy("", provider);
    	this.connectedProperty = new ConnectedProperty(proxy);
	}
}

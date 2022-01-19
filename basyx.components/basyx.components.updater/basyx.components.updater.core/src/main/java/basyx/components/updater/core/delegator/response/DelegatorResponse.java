/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.delegator.response;

import com.google.gson.annotations.Expose;

/**
 * Contains a response to the delegator
 * @author haque
 *
 */
public class DelegatorResponse {
	@Expose (serialize = true, deserialize = true) 
	private String value;
	
	@Expose (serialize = false, deserialize = false) 
	private boolean messageReceived = false;
	
	public DelegatorResponse() {}
	
	public DelegatorResponse(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isMessageReceived() {
		return messageReceived;
	}

	public void setMessageReceived(boolean messageReceived) {
		this.messageReceived = messageReceived;
	}
}

/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.messaging.subscribers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.messages.IRequestAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.messages.IResponseAgreementMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRequestAgreementSubscriber;

public class RequestAgreementSubscriberImpl implements IRequestAgreementSubscriber {

	private static Logger logger = LoggerFactory.getLogger(RequestAgreementSubscriberImpl.class);

	private IMessageFactory messageFactory;
	private IConfigurationService configurationService;

	@Override
	public IResponseAgreementMessage messageReceived(IRequestAgreementMessage message) throws Exception {
		IResponseAgreementMessage response = messageFactory.createResponseAgreementMessage(message.getFrom(),
				configurationService.getFileServerAgreementParams(), configurationService.getFileServerProtocolEnum());
		logger.info("Agreement found. Sending agreement info: {}", response);
		return response;
	}

	/**
	 * 
	 * @param messageFactory
	 */
	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}

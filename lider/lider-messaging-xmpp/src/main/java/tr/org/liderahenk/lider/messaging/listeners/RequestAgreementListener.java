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
package tr.org.liderahenk.lider.messaging.listeners;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.subscribers.IRequestAgreementSubscriber;
import tr.org.liderahenk.lider.messaging.XMPPClientImpl;
import tr.org.liderahenk.lider.messaging.messages.RequestAgreementMessageImpl;

/**
 * Listens to agreement-related messages and notifies related subscribers.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RequestAgreementListener implements StanzaListener, StanzaFilter {

	private static Logger logger = LoggerFactory.getLogger(RequestAgreementListener.class);

	/**
	 * Pattern used to filter messages
	 */
	private static final Pattern messagePattern = Pattern.compile(".*\\\"type\\\"\\s*:\\s*\\\"REQUEST_AGREEMENT\\\".*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * Message subscriber
	 */
	private IRequestAgreementSubscriber subscriber;

	// TODO IMPROVEMENT: separate xmpp client into two classes. one for
	// configuration/setup, other for functional methods
	private XMPPClientImpl client;

	public RequestAgreementListener(XMPPClientImpl client) {
		this.client = client;
	}

	@Override
	public boolean accept(Stanza stanza) {
		if (stanza instanceof Message) {
			Message msg = (Message) stanza;
			// All messages from agents are type normal
			// Message body must contain => "type": "REQUEST_AGREEMENT"
			if (Message.Type.normal.equals(msg.getType()) && messagePattern.matcher(msg.getBody()).matches()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void processPacket(Stanza packet) throws NotConnectedException {
		try {
			if (packet instanceof Message) {

				Message msg = (Message) packet;
				logger.info("Request agreement message received from => {}, body => {}", msg.getFrom(), msg.getBody());

				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));

				// Construct message
				RequestAgreementMessageImpl message = mapper.readValue(msg.getBody(),
						RequestAgreementMessageImpl.class);
				message.setFrom(msg.getFrom());

				if (subscriber != null) {
					ILiderMessage response = subscriber.messageReceived(message);
					logger.debug("Notified subscriber => {}", subscriber);
					client.sendMessage(new ObjectMapper().writeValueAsString(response), msg.getFrom());
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setSubscriber(IRequestAgreementSubscriber subscriber) {
		this.subscriber = subscriber;
	}

}

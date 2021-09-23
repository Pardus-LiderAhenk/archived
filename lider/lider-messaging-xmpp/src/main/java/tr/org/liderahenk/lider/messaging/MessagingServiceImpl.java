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
package tr.org.liderahenk.lider.messaging;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.INotification;

/**
 * Default implementation for {@link IMessagingService}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 * 
 */
public class MessagingServiceImpl implements IMessagingService {

	private static Logger logger = LoggerFactory.getLogger(MessagingServiceImpl.class);

	private XMPPClientImpl xmppClient;

	@Override
	public boolean isRecipientOnline(String jid) {
		return xmppClient.isRecipientOnline(jid);
	}

	@Override
	public void sendMessage(String message, String jid) throws Exception {
		xmppClient.sendMessage(message, jid);
	}

	@Override
	public void sendMessage(ILiderMessage message) throws Exception {
		xmppClient.sendMessage(message);
	}

	@Override
	public void sendNotification(INotification notification) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
		xmppClient.sendMessage(mapper.writeValueAsString(notification), notification.getRecipient());
	}

	@Override
	public void sendFile(byte[] file, String jid) throws Exception {
		xmppClient.sendFile(file, jid);
	}

	@Override
	public void sendFile(File file, String jid) throws Exception {
		xmppClient.sendFile(Files.readAllBytes(file.toPath()), jid);
	}

	@Override
	public void createAccount(String username, String password) throws Exception {
		try {
			xmppClient.createAccount(username, password);
		} catch (Exception e) {
			if (e.getMessage().contains("conflict")) { // Ignore
				logger.warn("Already registered: {}", username);
			} else {
				throw e; // Let the caller class handle it
			}
		}
	}

	@Override
	public List<String> getOnlineUsers() {
		return xmppClient.getOnlineUsers();
	}

	public void setXmppClient(XMPPClientImpl xmppClient) {
		this.xmppClient = xmppClient;
	}

}

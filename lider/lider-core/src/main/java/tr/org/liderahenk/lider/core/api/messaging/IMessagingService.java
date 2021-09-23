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
package tr.org.liderahenk.lider.core.api.messaging;

import java.io.File;
import java.util.List;

import tr.org.liderahenk.lider.core.api.messaging.messages.ILiderMessage;
import tr.org.liderahenk.lider.core.api.messaging.notifications.INotification;

/**
 * Provides messaging services throughout system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:bm.volkansahin@gmail.com">Volkan Şahin</a>
 *
 */
public interface IMessagingService {

	/**
	 * 
	 * @param jid
	 * @return true if jid is online, false otherwise
	 */
	boolean isRecipientOnline(String jid);

	/**
	 * Send message to agent
	 * 
	 * @param to
	 *            recipient of message
	 * @param message
	 *            to be sent
	 * @throws Exception
	 */
	void sendMessage(String message, String jid) throws Exception;

	/**
	 * Send pre-defined Lider message to agent
	 * 
	 * @param message
	 *            {@link ILiderMessage} to be sent
	 * @throws Exception
	 */
	void sendMessage(ILiderMessage message) throws Exception;

	/**
	 * Send pre-defined notification to Lider Console
	 * 
	 * @param notification
	 * @throws Exception
	 */
	void sendNotification(INotification notification) throws Exception;

	/**
	 * Send file to agent via XEP-0065
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(byte[] file, String jid) throws Exception;

	/**
	 * Send file to agent via XEP-0065
	 * 
	 * @param file
	 * @param jid
	 * @throws Exception
	 */
	void sendFile(File file, String jid) throws Exception;

	/**
	 * Createn XMPP account on server
	 * 
	 * @param username
	 * @param password
	 * @throws Exception
	 */
	void createAccount(String username, String password) throws Exception;

	/**
	 * 
	 * @return currently online users
	 */
	List<String> getOnlineUsers();

}
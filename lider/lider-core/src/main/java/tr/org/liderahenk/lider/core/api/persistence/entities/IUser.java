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
package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;
import java.util.Set;

/**
 * IAgent entity class is responsible for storing agent records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IUser extends IEntity {

	/**
	 * 
	 * @return
	 */
	Boolean getDeleted();

	/**
	 * 
	 * @return
	 */
	String getJid();

	/**
	 * 
	 * @return
	 */
	String getPassword();

	/**
	 * 
	 * @return
	 */
	String getHostname();

	/**
	 * 
	 * @return
	 */
	String getIpAddresses();

	/**
	 * 
	 * @return
	 */
	String getMacAddresses();

	/**
	 * 
	 * @return
	 */
	String getDn();

	/**
	 * 
	 * @return
	 */
	Set<? extends IAgentProperty> getProperties();

	/**
	 * 
	 * @param property
	 */
	void addProperty(IAgentProperty property);

	/**
	 * 
	 * @return
	 */
	Set<? extends IUserSession> getSessions();

	/**
	 * 
	 * @param userSession
	 */
	void addUserSession(IUserSession userSession);

	/**
	 * 
	 * @return
	 */
	Date getModifyDate();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

}

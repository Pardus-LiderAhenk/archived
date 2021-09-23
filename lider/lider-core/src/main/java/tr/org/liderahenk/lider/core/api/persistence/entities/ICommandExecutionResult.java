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

import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.enums.ContentType;

/**
 * ICommandExecutionResult entity class is responsible for storing command
 * execution result records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface ICommandExecutionResult extends IEntity {

	/**
	 * 
	 * @return related agent ID
	 */
	Long getAgentId();

	/**
	 * 
	 * @return related ICommandExecution instance
	 */
	ICommandExecution getCommandExecution();

	/**
	 * 
	 * @return response code indicating status of response
	 */
	StatusCode getResponseCode();

	/**
	 * 
	 * @return response message
	 */
	String getResponseMessage();

	/**
	 * 
	 * @return response data sent from a agent or a task
	 */
	byte[] getResponseData();

	/**
	 * 
	 * @return content type of response data
	 */
	ContentType getContentType();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

	String getMailSubject();

	String getMailContent();

}

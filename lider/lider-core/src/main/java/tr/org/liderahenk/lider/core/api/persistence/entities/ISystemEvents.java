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

import java.io.Serializable;
import java.util.Date;

public interface ISystemEvents extends Serializable {

	/**
	 * 
	 * @return
	 */
	Integer getSystemEventsId();

	/**
	 * 
	 * @return
	 */
	Long getCustomerId();

	/**
	 * 
	 * @return
	 */
	Date getReceivedAt();

	/**
	 * 
	 * @return
	 */
	Date getDeviceReportedTime();

	/**
	 * 
	 * @return
	 */
	Integer getFacility();

	/**
	 * 
	 * @return
	 */
	Integer getPriority();

	/**
	 * 
	 * @return
	 */
	String getFromHost();

	/**
	 * 
	 * @return
	 */
	String getMessage();

	/**
	 * 
	 * @return
	 */
	Integer getNtSeverity();

	/**
	 * 
	 * @return
	 */
	Integer getImportance();

	/**
	 * 
	 * @return
	 */
	String getEventSource();

	/**
	 * 
	 * @return
	 */
	String getEventUser();

	/**
	 * 
	 * @return
	 */
	Integer getEventCategory();

	/**
	 * 
	 * @return
	 */
	Integer getEventId();

	/**
	 * 
	 * @return
	 */
	String getEventBinaryData();

	/**
	 * 
	 * @return
	 */
	Integer getMaxAvailable();

	/**
	 * 
	 * @return
	 */
	Integer getCurrUsage();

	/**
	 * 
	 * @return
	 */
	Integer getMinUsage();

	/**
	 * 
	 * @return
	 */
	Integer getMaxUsage();

	/**
	 * 
	 * @return
	 */
	Integer getInfoUnitId();

	/**
	 * 
	 * @return
	 */
	String getSysLogTag();

	/**
	 * 
	 * @return
	 */
	String getEventLogType();

	/**
	 * 
	 * @return
	 */
	String getGenericFileName();

	/**
	 * 
	 * @return
	 */
	Integer getSystemId();

	/**
	 * 
	 * @return JSON string representation of this instance
	 */
	String toJson();

}

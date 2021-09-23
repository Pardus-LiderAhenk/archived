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
import java.util.Map;

/**
 * IAgent entity class is responsible for storing plugin related profile
 * records.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
public interface IProfile extends IEntity {

	/**
	 * 
	 * @return related IPlugin instance
	 */
	IPlugin getPlugin();

	/**
	 * 
	 * @return profile label
	 */
	String getLabel();

	/**
	 * 
	 * @return profile description
	 */
	String getDescription();

	/**
	 * Indicates this profile record is overridable by a higher priority profile
	 * during agent execution.
	 * 
	 * @return overridable flag
	 */
	boolean isOverridable();

	/**
	 * 
	 * @return active flag
	 */
	boolean isActive();

	/**
	 * 
	 * @return deleted flag
	 */
	boolean isDeleted();

	/**
	 * 
	 * @return profile data sent from Lider Console
	 */
	Map<String, Object> getProfileData();

	/**
	 * 
	 * @return profile data sent from Lider Console as json byte array
	 */
	byte[] getProfileDataBlob();

	/**
	 * 
	 * @return record modification date
	 */
	Date getModifyDate();

}

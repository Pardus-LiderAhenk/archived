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
package tr.org.liderahenk.lider.core.api.constants;

/**
 * Provides common constants used throughout the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LiderConstants {

	/**
	 * Event topics used by {@link EventAdmin}
	 */
	public static final class EVENTS {
		/**
		 * Thrown when file received
		 */
		public static final String FILE_RECEIVED = "tr/org/liderahenk/file/received";
		/**
		 * Thrown when plugin registered
		 */
		public static final String PLUGIN_REGISTERED = "tr/org/liderahenk/plugin/registered";
		/**
		 * Thrown when task status message received
		 */
		public static final String TASK_STATUS_RECEIVED = "tr/org/liderahenk/task/status/received";
		/**
		 * Thrown when policy status message received
		 */
		public static final String POLICY_STATUS_RECEIVED = "tr/org/liderahenk/policy/status/received";
		/**
		 * Thrown when new report view created
		 */
		public static final String REPORT_VIEW_CREATED = "tr/org/liderahenk/report/view/created";
		/**
		 * Thrown when existing report view updated
		 */
		public static final String REPORT_VIEW_UPDATED = "tr/org/liderahenk/report/view/updated";
		/**
		 * Thrown when repot view deleted
		 */
		public static final String REPORT_VIEW_DELETED = "tr/org/liderahenk/report/view/deleted";
	}

}

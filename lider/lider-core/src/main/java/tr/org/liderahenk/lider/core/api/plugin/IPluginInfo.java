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
package tr.org.liderahenk.lider.core.api.plugin;

/**
 * Plugin info interface is used to register new plugins to the system.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPluginInfo {

	/**
	 * 
	 * @return plugin name
	 */
	String getPluginName();

	/**
	 * 
	 * @return plugin version
	 */
	String getPluginVersion();

	/**
	 * 
	 * @return description
	 */
	String getDescription();

	/**
	 * 
	 * @return true if profiles of this plugin can be executed for machines.
	 */
	Boolean getMachineOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be executed for users.
	 */
	Boolean getUserOriented();

	/**
	 * 
	 * @return true if profiles of this plugin can be used in a policy.
	 */
	Boolean getPolicyPlugin();

	/**
	 * 
	 * @return true if profiles of this plugin can be used in a task.
	 */
	Boolean getTaskPlugin();

	/**
	 * 
	 * @return true if the plugin needs/uses X
	 */
	Boolean getXbased();

	/**
	 * 
	 * @return true if the plugin uses file transfer, false otherwise.
	 */
	Boolean getUsesFileTransfer();

}

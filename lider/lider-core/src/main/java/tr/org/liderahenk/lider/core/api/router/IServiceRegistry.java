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
package tr.org.liderahenk.lider.core.api.router;

import java.util.HashMap;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;

/**
 * Service registry keeping list of {@link ICommand} implementations
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IServiceRegistry {

	/**
	 * 
	 * @param pluginName
	 * @param pluginVersion
	 * @param resource
	 * @param action
	 * @return
	 */
	public String buildKey(String pluginName, String pluginVersion, String commandId);

	/**
	 * 
	 * @param key
	 * @return
	 */
	public ICommand lookupCommand(String key);

	/**
	 * 
	 * @return
	 */
	Set<String> getTaskCodes();

	/**
	 * 
	 * @return
	 */
	Set<String> getReportCodes();
	
	/**
	 * 
	 * @return
	 */
	HashMap<String, ICommand> getCommands();
	
	

}

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
package tr.org.liderahenk.lider.core.api.service;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

/**
 * 
 * Factory to create {@link ICommandResult}
 *
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 * 
 */
public interface ICommandResultFactory {

	/**
	 * 
	 * @param status
	 *            of command result
	 * @param messages
	 *            in command result
	 * @param command
	 *            creating command result
	 * @return new command result
	 */
	ICommandResult create(CommandResultStatus status, List<String> messages, ICommand command);

	/**
	 * @param status
	 *            of command result
	 * @param messages
	 *            in command result
	 * @param command
	 *            creating this command result
	 * @param resultMap
	 *            containing command execution results
	 * @return new command result
	 */
	ICommandResult create(CommandResultStatus status, List<String> messages, ICommand command,
			Map<String, Object> resultMap);

}

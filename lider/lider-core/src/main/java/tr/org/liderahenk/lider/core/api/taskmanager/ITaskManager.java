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
package tr.org.liderahenk.lider.core.api.taskmanager;

import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.taskmanager.exceptions.TaskExecutionFailedException;

/**
 * Provides {@link ITask} lifecycle management services
 * 
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 *
 */
public interface ITaskManager {

	/**
	 * creates a task for request
	 * 
	 * @param entries
	 * 
	 * @param {@link
	 * 			IRequest} to be submitted as a task
	 * @return String[] of tasks created as a result of {@link IRequest}, will
	 *         be a single task for single node task, or all subtask id's in
	 *         case of a subtree request creating subtasks
	 * @throws TaskExecutionFailedException
	 *             on any failure during task creation
	 * 
	 */
	void executeTask(ITaskRequest request, List<LdapEntry> entries) throws TaskExecutionFailedException;

}

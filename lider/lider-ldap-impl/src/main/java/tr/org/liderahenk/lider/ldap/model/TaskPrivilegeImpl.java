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
package tr.org.liderahenk.lider.ldap.model;

import tr.org.liderahenk.lider.core.api.ldap.model.ITaskPrivilege;

/**
 * Default implementation of {@link ITaskPrivilege}
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TaskPrivilegeImpl implements ITaskPrivilege {

	/**
	 * DN of the target entry
	 */
	private String target;

	/**
	 * operation either contains 'plugin name'/'command class ID' or 'ALL'
	 */
	private String operation;

	/**
	 * 
	 * @param target
	 * @param operation
	 * @param allowed
	 */
	public TaskPrivilegeImpl(String target, String operation) {
		super();
		this.target = target;
		this.operation = operation;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public String getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return "TaskPrivilegeImpl [target=" + target + ", operation=" + operation + "]";
	}

}

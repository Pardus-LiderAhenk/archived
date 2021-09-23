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
package tr.org.liderahenk.lider.karaf.commands;

import java.util.List;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.Col;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;

import tr.org.liderahenk.lider.core.api.persistence.dao.IProfileDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Custom Karaf console command to list profiles. Type <code>profile:list</code>
 * to execute command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Service
@Command(scope = "profile", name = "list", description = "Lists profile records")
public class ProfileListCommand implements Action {

	@Reference
	private IProfileDao profileDao;

	public ProfileListCommand() {
	}

	@Override
	public Object execute() throws Exception {
		ShellTable table = new ShellTable();
		// ID
		Col column = table.column("ID");
		column.alignCenter().bold();
		// Label
		column = table.column("Label");
		column.alignCenter().cyan();
		// Plugin
		column = table.column("Related Plugin");
		column.alignCenter();
		// Create Date
		column = table.column("Create Date");
		column.alignCenter();
		// Deleted
		column = table.column("Deleted");
		column.alignCenter();

		List<? extends IProfile> list = profileDao.findAll(IProfile.class, null);
		if (list != null) {
			for (IProfile profile : list) {
				Row row = table.addRow();
				row.addContent(profile.getId(), profile.getLabel(),
						profile.getPlugin().getName() + "-" + profile.getPlugin().getVersion(), profile.getCreateDate(),
						profile.isDeleted() ? "x" : "");
			}
		}
		table.print(System.out);

		return null;
	}

	public void setProfileDao(IProfileDao profileDao) {
		this.profileDao = profileDao;
	}

}

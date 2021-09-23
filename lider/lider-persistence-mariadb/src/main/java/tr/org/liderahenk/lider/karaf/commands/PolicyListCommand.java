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

import tr.org.liderahenk.lider.core.api.persistence.dao.IPolicyDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;

/**
 * Custom Karaf console command to list policies. Type <code>policy:list</code>
 * to execute command.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Service
@Command(scope = "policy", name = "list", description = "Lists plugin records")
public class PolicyListCommand implements Action {

	@Reference
	private IPolicyDao policyDao;

	public PolicyListCommand() {
	}

	@Override
	public Object execute() throws Exception {
		ShellTable table = new ShellTable();
		// ID
		Col column = table.column("ID");
		column.alignCenter().bold();
		// Label
		column = table.column("Name");
		column.alignCenter().cyan();
		// Version
		column = table.column("Version");
		column.alignCenter().cyan();
		// Create Date
		column = table.column("Create Date");
		column.alignCenter();
		// Modify Date
		column = table.column("Modify Date");
		column.alignCenter();
		// Deleted
		column = table.column("Deleted");
		column.alignCenter();

		List<? extends IPolicy> list = policyDao.findAll(IPolicy.class, null);
		if (list != null) {
			for (IPolicy policy : list) {
				Row row = table.addRow();
				row.addContent(policy.getId(), policy.getLabel(), policy.getPolicyVersion(), policy.getCreateDate(),
						policy.getModifyDate() != null ? policy.getModifyDate() : "", policy.isDeleted() ? "x" : "");
			}
		}
		table.print(System.out);

		return null;
	}

	public void setPolicyDao(IPolicyDao policyDao) {
		this.policyDao = policyDao;
	}

}

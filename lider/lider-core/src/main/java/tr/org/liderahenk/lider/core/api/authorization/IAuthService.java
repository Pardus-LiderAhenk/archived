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
package tr.org.liderahenk.lider.core.api.authorization;

import java.util.List;

import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;

/**
 * Provides authorization services
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IAuthService {

	/**
	 * Calculate 'permitted' LDAP entries of the specified user for the
	 * specified target operation.
	 * 
	 * @param userDn
	 * @param entries
	 * @param targetOperation
	 * @return
	 */
	List<LdapEntry> getPermittedEntries(String userDn, List<LdapEntry> entries, String targetOperation);

	/**
	 * Calculate 'permitted' report templates of the specified user
	 * 
	 * @param userDn
	 * @param templates
	 * @return
	 */
	List<? extends IReportTemplate> getPermittedTemplates(String userDn, List<? extends IReportTemplate> templates);

	/**
	 * Calculate 'permitted' report views of the specified user
	 * 
	 * @param userDn
	 * @param views
	 * @return
	 */
	List<? extends IReportView> getPermittedViews(String userDn, List<? extends IReportView> views);

	/**
	 * Check if the specified user can view/generate the specified report.
	 * 
	 * @param userDn
	 * @param reportCode
	 * @return
	 */
	boolean canGenerateReport(String userDn, String reportCode);
}

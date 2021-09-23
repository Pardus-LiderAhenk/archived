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
package tr.org.liderahenk.lider.report;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

/**
 * This class listens to new installed bundles on Lider server and manages their
 * report templates if an implementation of {@link BaseReportTemplate} is
 * provided.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class TemplateManager {

	private Logger logger = LoggerFactory.getLogger(TemplateManager.class);

	private IReportDao reportDao;
	private IEntityFactory entityFactory;

	public void init() {
		logger.info("Initializing template manager.");
	}

	public void destroy() {
		logger.info("Destroying template manager...");
	}

	public void bindTemplate(IReportTemplate template) {
		if (template == null || template.getName() == null || template.getCode() == null
				|| template.getQuery() == null) {
			logger.warn("Template name, code and query can't be empty. Passing registration of template: ",
					template != null ? template.toString() : "NULL");
			return;
		}
		try {
			// Check if the template already exists
			List<? extends IReportTemplate> templates = reportDao.findTemplates("name", template.getName(), 1);
			IReportTemplate temp = templates != null && !templates.isEmpty() ? templates.get(0) : null;

			if (temp != null) {
				// Template already exists! Update its properties
				temp = entityFactory.createReportTemplate(temp, template);
				temp = reportDao.updateTemplate(temp);
			} else {
				// Create new template!
				temp = entityFactory.createReportTemplate(template);
				temp = reportDao.saveTemplate(temp);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}

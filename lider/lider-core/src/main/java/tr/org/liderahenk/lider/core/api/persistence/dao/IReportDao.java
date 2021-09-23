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
package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;

public interface IReportDao {

	IReportTemplate saveTemplate(IReportTemplate template);

	IReportView saveView(IReportView view);

	IReportTemplate updateTemplate(IReportTemplate template);

	IReportView updateView(IReportView view);

	void deleteTemplate(Long id);

	void deleteView(Long id);

	IReportTemplate findTemplate(Long id);

	IReportView findView(Long id);

	IReportTemplateColumn findTemplateColumn(Long id);

	IReportTemplateParameter findTemplateParameter(Long id);

	List<? extends IReportTemplate> findTemplates(Integer maxResults);

	List<? extends IReportView> findViews(Integer maxResults);

	List<? extends IReportTemplate> findTemplates(String propertyName, Object propertyValue, Integer maxResults);

	List<? extends IReportView> findViews(String propertyName, Object propertyValue, Integer maxResults);

	List<? extends IReportTemplate> findTemplates(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults);

	List<? extends IReportView> findViews(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults);

	void validateTemplate(String query, Set<? extends IReportTemplateParameter> params) throws Exception;

	List<Object[]> generateView(IReportView view, Map<String, Object> values) throws Exception;

	List<? extends IReportView> findViewsWithAlarm();

	void resetAlarmFields(IReportView view);

}

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

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

public interface IPluginDao extends IBaseDao<IPlugin> {

	/**
	 * 
	 * @param plugin
	 * @return
	 */
	IPlugin save(IPlugin plugin);

	/**
	 * 
	 * @param plugin
	 * @return
	 */
	IPlugin update(IPlugin plugin);

	/**
	 * 
	 * @param pluginId
	 */
	void delete(Long pluginId);

	/**
	 * 
	 * @param pluginId
	 * @return
	 */
	IPlugin find(Long pluginId);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findAll(Class<? extends IPlugin> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findByProperty(Class<? extends IPlugin> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IPlugin> findByProperties(Class<? extends IPlugin> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

	int updateByProperties(Map<String, Object> propertiesMap, List<IQueryCriteria> criterias);

}

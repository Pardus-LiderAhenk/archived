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
package tr.org.liderahenk.lider.core.api.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides database (ORM) access for server side plugins
 * 
 * @author <a href="mailto:birkan.duman@gmail.com">Birkan Duman</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IPluginDbService {

	/**
	 * Save entity.
	 * 
	 * @param entity
	 */
	void save(Object entity);

	/**
	 * Update entity.
	 * 
	 * @param entity
	 * @return 
	 */
	Object update(Object entity);

	/**
	 * Save or update given entity.
	 * 
	 * @param entity
	 * @return entity object saved/updated
	 */
	Object saveOrUpdate(Object entity);

	/**
	 * Delete entity by given ID.
	 * 
	 * @param entityClass
	 * @param id
	 */
	void delete(Class entityClass, Object id);

	/**
	 * Delete matching entities by given property name-value pair.
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param propertyValue
	 */
	void deleteByProperty(Class entityClass, String propertyName, Object propertyValue);

	/**
	 * Find entity by given ID.
	 * 
	 * @param entityClass
	 * @param id
	 * @return list of entity objects
	 */
	<T> T find(Class<T> entityClass, Object id);

	/**
	 * Find all entities.
	 * 
	 * @param entityClass
	 * @return list of entity objects
	 */
	<T> List<T> findAll(Class<T> entityClass);

	/**
	 * Find entities by given property name-value pair.
	 * 
	 * @param entityClass
	 * @param propertyName
	 * @param propertyValue
	 * @param maxResults
	 * @return list of entity objects
	 */
	<T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object propertyValue, Integer maxResults);

	/**
	 * Find entities by given properties.
	 * 
	 * @param entityClass
	 * @param propertiesMap
	 * @param orders
	 * @param maxResults
	 * @return
	 */
	<T> List<T> findByProperties(Class<T> entityClass, Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults);

	/**
	 * Find entities by given properties.
	 * 
	 * @param obj
	 * @param propertiesMap
	 * @param orders
	 * @param offset
	 * @param maxResults
	 * @return
	 */
	<T> List<T> findByPropertiesAndOperators(Class<T> obj, Map<String, ArrayList> propertiesMap,
			List<PropertyOrder> orders, Integer offset, Integer maxResults);

	/**
	 * Find table name for a given entity class.
	 * 
	 * @param entityClass
	 * @return
	 */
	<T> String getTableName(Class<T> entityClass);

}

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
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;

/**
 * Provides log related database operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IOperationLogDao extends IBaseDao<IOperationLog> {

	IOperationLog save(IOperationLog o) throws Exception;

	IOperationLog update(IOperationLog t) throws Exception;

	void delete(Long id);

	IOperationLog find(Long id);

	List<? extends IOperationLog> findAll(Class<? extends IOperationLog> obj, Integer maxResults);

	List<? extends IOperationLog> findByProperty(Class<? extends IOperationLog> obj, String propertyName, Object value,
			Integer maxResults);

	List<? extends IOperationLog> findByProperties(Class<? extends IOperationLog> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

}

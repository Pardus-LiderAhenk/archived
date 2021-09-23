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

import java.util.List;
import java.util.Map;

/**
 * Base DAO interface for all database operations. Other DAO classes should
 * implement this interface.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 * @param <T>
 *            any class with {@link javax.persistence.Entity} annotation.
 */
public interface IBaseDao<T> {

	T save(T o) throws Exception;

	T update(T t) throws Exception;

	void delete(Long id);

	T find(Long id);

	List<? extends T> findAll(Class<? extends T> obj, Integer maxResults);

	List<? extends T> findByProperty(Class<? extends T> obj, String propertyName, Object value, Integer maxResults);

	List<? extends T> findByProperties(Class<? extends T> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}

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
package tr.org.liderahenk.lider.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import tr.org.liderahenk.lider.core.api.caching.ICacheService;

public class CacheServiceImpl implements ICacheService {

	private CacheManager manager;

	public void init() {

		
		Configuration configuration = new Configuration().defaultCache(new CacheConfiguration("defaultCache", 1000))
				.cache(new CacheConfiguration("lider-cache", 1000).timeToIdleSeconds(5).timeToLiveSeconds(120));
		manager = CacheManager.create(configuration);
	}

	public void destroy() {
		manager.shutdown();
	}

	@Override
	public Object get(Object key) {
		if (null == manager.getCache("lider-cache").get(key)) {
			return null;
		}
		Element elt = manager.getCache("lider-cache").get(key);
		return elt.getObjectValue();
	}

	@Override
	public void put(Object key, Object value) {
		manager.getCache("lider-cache").put(new Element(key, value));

	}

}

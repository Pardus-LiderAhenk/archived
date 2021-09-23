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
package tr.org.liderahenk.lider.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.ISearchGroupDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroup;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.ISearchGroupRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.ISearchGroupRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing search group data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class SearchGroupRequestProcessorImpl implements ISearchGroupRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(SearchGroupRequestProcessorImpl.class);

	private ISearchGroupDao searchGroupDao;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IEntityFactory entityFactory;

	@Override
	public IRestResponse add(String json) {
		try {
			ISearchGroupRequest request = requestFactory.createSearchGroupRequest(json);
			ISearchGroup searchGroup = entityFactory.createSearchGroup(request);
			searchGroup = searchGroupDao.save(searchGroup);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("searchGroup", searchGroup);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse list(String name, Integer maxResults) {

		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("deleted", false);
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired search groups
		List<? extends ISearchGroup> searchGroups = searchGroupDao.findByProperties(propertiesMap, maxResults);
		logger.debug("Found search groups: {}", searchGroups);

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("searchGroups", searchGroups);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		ISearchGroup searchGroup = searchGroupDao.find(id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("searchGroup", searchGroup);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse delete(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		searchGroupDao.delete(new Long(id));
		logger.info("Search group record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	public void setSearchGroupDao(ISearchGroupDao searchGroupDao) {
		this.searchGroupDao = searchGroupDao;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}

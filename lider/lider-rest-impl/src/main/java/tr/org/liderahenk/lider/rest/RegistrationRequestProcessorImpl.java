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

import tr.org.liderahenk.lider.core.api.persistence.dao.IRegistrationDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IRegistrationRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing register .
 */
public class RegistrationRequestProcessorImpl implements IRegistrationRequestProcessor{

	private static Logger logger = LoggerFactory.getLogger(RegistrationRequestProcessorImpl.class);

	private IRegistrationDao registrationDao;
	private IResponseFactory responseFactory;
	private IRequestFactory requestFactory;

	@Override
	public IRestResponse list() {

		List<? extends IRegistrationTemplate> resultList = registrationDao.findAll();
		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("registrationTemplateList", resultList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);

	}
	
	@Override
	public IRestResponse add(String json) {

		try {
			IRegistrationTemplate template = requestFactory.createRegistrationTemplateRequest(json);
			
			template= registrationDao.save(template);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}

	}
	
	@Override
	public IRestResponse delete(Long id) {
		
		try {
			registrationDao.delete(id);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			
			return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
		
	}
	
	@Override
	public IRestResponse get(Long id) {
		
		IRegistrationTemplate template= registrationDao.find(id);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("request", template);

		return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
	}

	
	public IRegistrationDao getRegistrationDao() {
		return registrationDao;
	}

	public void setRegistrationDao(IRegistrationDao registrationDao) {
		this.registrationDao = registrationDao;
	}

	public IResponseFactory getResponseFactory() {
		return responseFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public IRequestFactory getRequestFactory() {
		return requestFactory;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

}

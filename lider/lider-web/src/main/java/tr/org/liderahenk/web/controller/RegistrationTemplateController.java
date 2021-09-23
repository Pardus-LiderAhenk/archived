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
package tr.org.liderahenk.web.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.processors.IRegistrationRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for Registration operations.
 * 
 */
@Controller
@RequestMapping("/lider/registrationTemplate")
public class RegistrationTemplateController {

	private static Logger logger = LoggerFactory.getLogger(RegistrationTemplateController.class);

	@Autowired
	private IResponseFactory responseFactory;
	
	
	@Autowired
	private IRegistrationRequestProcessor registrationRequestProcessor;

	/**
	 * List RegistrationTemplates according to given parameters.
	 * 
	 * @param hostname
	 * @param dn
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public IRestResponse listRegistrationTemplates(HttpServletRequest request) {
		logger.info("Request received. URL: " + request.getRequestURI());
		IRestResponse restResponse = registrationRequestProcessor.list();
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}


	@RequestMapping(value = "/add", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public IRestResponse addRegistrationTemplate(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/add/addRegistrationTemplate' Body: {}", requestBodyDecoded.length() > ControllerUtils.MAX_LOG_SIZE ? requestBodyDecoded.substring(0, ControllerUtils.MAX_LOG_SIZE) : requestBodyDecoded);
		IRestResponse restResponse = registrationRequestProcessor.add(requestBodyDecoded);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}
	
	@RequestMapping(value = "/{id}/delete", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public IRestResponse deleteRegistrationTemplate(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		
		logger.info("Request received. URL: '/lider/delete/deleteRegistrationTemplate' id: {}", id);
		IRestResponse restResponse = registrationRequestProcessor.delete(id);
		
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	
	/**
	 * Handle predefined exceptions that we did not write and did not throw.
	 * 
	 * @param e
	 * @return IRestResponse instance which holds exception message with ERROR
	 *         status
	 */
	@ExceptionHandler(Exception.class)
	public IRestResponse handleAllException(Exception e) {
		return ControllerUtils.handleAllException(e, responseFactory);
	}

}

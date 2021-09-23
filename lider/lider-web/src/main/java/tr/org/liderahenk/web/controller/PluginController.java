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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.processors.IMailManagementRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.processors.IPluginRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.web.controller.utils.ControllerUtils;

/**
 * Controller for plugin related operations.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Controller
@RequestMapping("/lider/plugin")
public class PluginController {

	private static Logger logger = LoggerFactory.getLogger(PluginController.class);

	@Autowired
	private IResponseFactory responseFactory;
	
	@Autowired
	private IPluginRequestProcessor pluginProcessor;

	@Autowired
	private IMailManagementRequestProcessor mailManagementProcessor;

	/**
	 * List plugins according to given parameters.
	 * 
	 * @param label
	 * @param active
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/list", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse listPlugins(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "version", required = false) String version, HttpServletRequest request)
					throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/plugin/list?name={}&version={}'", new Object[] { name, version });
		IRestResponse restResponse = pluginProcessor.list(name, version);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	/**
	 * Retrieve plugin specified by id
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/{id:[\\d]+}/get", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getPlugin(@PathVariable final long id, HttpServletRequest request)
			throws UnsupportedEncodingException {
		logger.info("Request received. URL: '/lider/plugin/{}/get'", id);
		IRestResponse restResponse = pluginProcessor.get(id);
		logger.debug("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}

	
	@RequestMapping(value = "/listMail", method = { RequestMethod.GET })
	@ResponseBody
	public IRestResponse getMailList(
			@RequestParam(value = "pluginId", required = false) String pluginName
		,	 @RequestParam(value = "version", required = false) String version 
			,HttpServletRequest request)
			throws UnsupportedEncodingException {

		logger.info("Request received. URL: '/lider/plugin/listMail' plugin id : "+pluginName +" version : "+version);
		
		IRestResponse restResponse = mailManagementProcessor.list(pluginName, version);
		logger.info("Completed processing request, returning result: {}", restResponse.toJson());
		return restResponse;
	}
	
	
	@RequestMapping(value = "/addMailConfiguration", method = { RequestMethod.POST })
	@ResponseBody
	public IRestResponse addMailConfiguration(@RequestBody String requestBody, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String requestBodyDecoded = ControllerUtils.decodeRequestBody(requestBody);
		logger.info("Request received. URL: '/lider/plugn/addMailConfiguration' Body: {}", requestBodyDecoded.length() > ControllerUtils.MAX_LOG_SIZE ? requestBodyDecoded.substring(0, ControllerUtils.MAX_LOG_SIZE) : requestBodyDecoded);
		IRestResponse restResponse = mailManagementProcessor.add(requestBodyDecoded);
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

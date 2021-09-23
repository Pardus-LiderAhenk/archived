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
package tr.org.liderahenk.lider.core.api.rest;

import tr.org.liderahenk.lider.core.api.rest.requests.IMailManagementRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyExecutionRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ISearchGroupRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;

/**
 * Interface for request factory. Request factories are used to create request
 * objects from given JSON string.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IRequestFactory {

	IProfileRequest createProfileRequest(String json) throws Exception;

	IPolicyRequest createPolicyRequest(String json) throws Exception;

	ITaskRequest createTaskCommandRequest(String json) throws Exception;

	IPolicyExecutionRequest createPolicyExecutionRequest(String json) throws Exception;

	IReportTemplateRequest createReportTemplateRequest(String json) throws Exception;

	IReportGenerationRequest createReportGenerationRequest(String json) throws Exception;

	IReportViewRequest createReportViewRequest(String json) throws Exception;

	ISearchGroupRequest createSearchGroupRequest(String json) throws Exception;
	
	IMailManagementRequest createMailManagementRequest(String json) throws Exception;

}

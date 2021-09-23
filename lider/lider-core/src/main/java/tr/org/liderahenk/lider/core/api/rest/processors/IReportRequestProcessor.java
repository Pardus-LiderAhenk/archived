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
package tr.org.liderahenk.lider.core.api.rest.processors;

import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public interface IReportRequestProcessor {

	IRestResponse addTemplate(String json);

	IRestResponse updateTemplate(String json);

	IRestResponse listTemplates(String name);

	IRestResponse getTemplate(Long id);

	IRestResponse deleteTemplate(Long id);

	IRestResponse validateTemplate(String json);

	IRestResponse generateView(String json);

	IRestResponse addView(String json);

	IRestResponse updateView(String json);

	IRestResponse listViews(String name);

	IRestResponse getView(Long id);

	IRestResponse deleteView(Long id);

	IRestResponse exportPdf(String json);

}

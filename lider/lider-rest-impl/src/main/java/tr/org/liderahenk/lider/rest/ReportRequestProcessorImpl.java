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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import tr.org.liderahenk.lider.core.api.authorization.IAuthService;
import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.PdfReportParamType;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IReportRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportTemplateRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewColumnRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewParameterRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

/**
 * Processor class for handling/processing report data.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ReportRequestProcessorImpl implements IReportRequestProcessor {

	private static Logger logger = LoggerFactory.getLogger(ReportRequestProcessorImpl.class);

	private IReportDao reportDao;
	private IEntityFactory entityFactory;
	private IRequestFactory requestFactory;
	private IResponseFactory responseFactory;
	private IAuthService authService;
	private IConfigurationService configService;
	private EventAdmin eventAdmin;

	private static final String DEFAULT_ENCODING = "cp1254";
	private static final String DEFAULT_FONT = "times-roman";

	@Override
	public IRestResponse validateTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = entityFactory.createReportTemplate(request);
			reportDao.validateTemplate(template.getQuery(), template.getTemplateParams());

			return responseFactory.createResponse(RestResponseStatus.OK, "Query validated.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			List<String> messages = new ArrayList<String>();
			messages.add(e.getMessage());
			if (e.getCause() != null) {
				messages.add(e.getCause().getMessage());
			}
			return responseFactory.createResponse(RestResponseStatus.ERROR, messages);
		}
	}

	@Override
	public IRestResponse addTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = entityFactory.createReportTemplate(request);
			template = reportDao.saveTemplate(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse updateTemplate(String json) {
		try {
			IReportTemplateRequest request = requestFactory.createReportTemplateRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getId());
			template = entityFactory.createReportTemplate(template, request);
			template = reportDao.updateTemplate(template);

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("template", template);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse listTemplates(String name) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired templates
		List<? extends IReportTemplate> templates = reportDao.findTemplates(propertiesMap, null, null);
		// Authorize templates
		if (configService.getUserAuthorizationEnabled()) {
			Subject currentUser = null;
			try {
				currentUser = SecurityUtils.getSubject();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (currentUser != null && currentUser.getPrincipal() != null) {
				templates = authService.getPermittedTemplates(currentUser.getPrincipal().toString(), templates);
			} else {
				logger.warn("Unauthenticated user access.");
				return responseFactory.createResponse(RestResponseStatus.ERROR,
						Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
			}
		}

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("templates", templates);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse getTemplate(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IReportTemplate template = reportDao.findTemplate(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("template", template);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse deleteTemplate(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		reportDao.deleteTemplate(new Long(id));
		logger.info("Report template record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	@Override
	public IRestResponse exportPdf(String json) {
		try {
			IReportGenerationRequest request = requestFactory.createReportGenerationRequest(json);
			IReportView view = reportDao.findView(request.getViewId());

			// Authorize report action
			IRestResponse response = authAction(view, request);
			if (response != null) {
				return response;
			}

			List<Object[]> resultList = reportDao.generateView(view, request.getParamValues());

			// Determine temporary report path
			String fileName = view.getName() + new Date().getTime() + ".pdf";
			String filePath = Files.createTempDirectory("lidertmp-").toAbsolutePath() + "/" + fileName;
			
			// Fonts
			FontFactory.defaultEncoding = DEFAULT_ENCODING;
			Font titleFont = FontFactory.getFont(DEFAULT_FONT, DEFAULT_ENCODING, 12, Font.BOLD);
			Font headerFont = FontFactory.getFont(DEFAULT_FONT, DEFAULT_ENCODING, 10, Font.BOLD);
			Font cellFont = FontFactory.getFont(DEFAULT_FONT, DEFAULT_ENCODING, 7, Font.NORMAL);

			// Create report document
			Document doc = new Document(PageSize.A4, 20, 20, 50, 25);
			PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File(filePath)));
			writer.setPageEvent(new HeaderFooterPageEvent(request, cellFont));
			doc.open();

			// Title & header
			doc.addTitle(view.getName());
			Paragraph reportTitle = new Paragraph(view.getDescription(), titleFont);
			reportTitle.setAlignment(Element.ALIGN_CENTER);
			doc.add(reportTitle);
			doc.add(new Paragraph(" "));

			// Table headers
			PdfPTable table = new PdfPTable(view.getViewColumns().size());
			int[] colWidths = new int[view.getViewColumns().size()];
			int[] colIndices = new int[view.getViewColumns().size()];
			ArrayList<IReportViewColumn> columns = new ArrayList<IReportViewColumn>(view.getViewColumns());
			for (int i = 0; i < columns.size(); i++) {
				IReportViewColumn column = columns.get(i);
				PdfPCell cell = new PdfPCell(new Phrase(column.getReferencedCol().getName(), headerFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				colWidths[i] = column.getWidth();
				colIndices[i] = column.getReferencedCol().getColumnOrder() - 1;
			}

			// Table rows
			for (Object[] row : resultList) {
				for (int index : colIndices) {
					Phrase phrase = new Phrase(
							(index >= row.length || row[index] == null) ? " " : row[index].toString(), cellFont);
					PdfPCell cell = new PdfPCell(phrase);
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
				}
			}

			// Finalise table
			table.setWidths(colWidths);
			doc.add(table);
			doc.add(new Paragraph(" "));
			doc.close();

			byte[] pdf = Files.readAllBytes(Paths.get(filePath));

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("report", pdf);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	public class HeaderFooterPageEvent extends PdfPageEventHelper {

		private IReportGenerationRequest request;
		private Font font;

		public HeaderFooterPageEvent(IReportGenerationRequest request, Font font) {
			this.request = request;
			this.font = font;
		}

		public void onStartPage(PdfWriter writer, Document document) {
			if (request.getTopLeft() != null) {
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
						new Phrase(getPhrase(request.getTopLeft(), request.getTopLeftText(), document.getPageNumber()), font),
						50, 800, 0);
			}
			if (request.getTopRight() != null) {
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
						new Phrase(
								getPhrase(request.getTopRight(), request.getTopRightText(), document.getPageNumber()), font),
						550, 800, 0);
			}
		}

		public void onEndPage(PdfWriter writer, Document document) {
			if (request.getBottomLeft() != null) {
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(
						getPhrase(request.getBottomLeft(), request.getBottomLeftText(), document.getPageNumber()), font), 60,
						30, 0);
			}
			if (request.getBottomRight() != null) {
				ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(
						getPhrase(request.getBottomRight(), request.getBottomRightText(), document.getPageNumber()), font),
						550, 30, 0);
			}
		}

		private String getPhrase(PdfReportParamType type, String text, int pageNumber) {
			String phrase = "";
			if (type != null) {
				switch (type) {
				case DATE:
					phrase = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
					break;
				case PAGE_NO:
					phrase = pageNumber + "";
					break;
				case TEXT:
					phrase = text;
					break;
				default:
					break;
				}
			}
			return phrase;
		}

	}

	@Override
	public IRestResponse generateView(String json) {
		try {
			IReportGenerationRequest request = requestFactory.createReportGenerationRequest(json);
			IReportView view = reportDao.findView(request.getViewId());

			// Authorize report action
			IRestResponse response = authAction(view, request);
			if (response != null) {
				return response;
			}

			// Generic type can be an entity class or an object array!
			List<Object[]> resultList = reportDao.generateView(view, request.getParamValues());

			Map<String, Object> resultMap = new HashMap<String, Object>();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new SimpleDateFormat("dd-MM-yyyy HH:mm"));
			resultMap.put("data", mapper.writeValueAsString(resultList));
			resultMap.put("type", view.getType());
			resultMap.put("columns", view.getViewColumns());

			return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	private IRestResponse authAction(IReportView view, IRequest request) {
		if (configService.getUserAuthorizationEnabled()) {
			Subject currentUser = null;
			try {
				currentUser = SecurityUtils.getSubject();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (currentUser != null && currentUser.getPrincipal() != null) {
				String reportCode = view.getTemplate().getCode();
				if (!authService.canGenerateReport(currentUser.getPrincipal().toString(), reportCode)) {
					logger.error("User not authorized: {}", currentUser.getPrincipal().toString());
					return responseFactory.createResponse(request, RestResponseStatus.ERROR,
							Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
				}
			} else {
				logger.warn("Unauthenticated user access.");
				return responseFactory.createResponse(request, RestResponseStatus.ERROR,
						Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
			}
		}
		return null;
	}

	@Override
	public IRestResponse addView(String json) {
		try {
			IReportViewRequest request = requestFactory.createReportViewRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getTemplateId());
			IReportView view = entityFactory.createReportView(request, template);
			if (request.getViewColumns() != null) {
				for (IReportViewColumnRequest c : request.getViewColumns()) {
					IReportTemplateColumn tCol = reportDao.findTemplateColumn(c.getReferencedColumnId());
					IReportViewColumn vCol = entityFactory.createReportViewColumn(c, tCol);
					view.addViewColumn(vCol);
				}
			}
			if (request.getViewParams() != null) {
				for (IReportViewParameterRequest p : request.getViewParams()) {
					IReportTemplateParameter tParam = reportDao.findTemplateParameter(p.getReferencedParameterId());
					IReportViewParameter vParam = entityFactory.createReportViewParameter(p, tParam);
					view.addViewParameter(vParam);
				}
			}
			view = reportDao.saveView(view);

			Dictionary<String, Object> payload = new Hashtable<String, Object>();
			payload.put("view", view);
			eventAdmin.postEvent(new Event(LiderConstants.EVENTS.REPORT_VIEW_CREATED, payload));

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("view", view);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse updateView(String json) {
		try {
			IReportViewRequest request = requestFactory.createReportViewRequest(json);
			IReportTemplate template = reportDao.findTemplate(request.getTemplateId());
			IReportView view = reportDao.findView(request.getId());
			view = entityFactory.createReportView(view, request, template);
			if (request.getViewColumns() != null) {
				for (IReportViewColumnRequest c : request.getViewColumns()) {
					IReportTemplateColumn tCol = reportDao.findTemplateColumn(c.getReferencedColumnId());
					IReportViewColumn vCol = entityFactory.createReportViewColumn(c, tCol);
					view.addViewColumn(vCol);
				}
			}
			if (request.getViewParams() != null) {
				for (IReportViewParameterRequest p : request.getViewParams()) {
					IReportTemplateParameter tParam = reportDao.findTemplateParameter(p.getReferencedParameterId());
					IReportViewParameter vParam = entityFactory.createReportViewParameter(p, tParam);
					view.addViewParameter(vParam);
				}
			}
			view = reportDao.updateView(view);

			// FIXME OpenJPA does not update NULL values (even if we use an
			// attached object!)
			// Note that entityFactory.createReportView() method now creates a
			// detached object
			// but we also tried the scenario with an attached object with no
			// success!
			//
			// See
			// http://stackoverflow.com/questions/3869543/issue-with-updating-record-in-database-using-jpa
			// http://stackoverflow.com/questions/3870248/setting-values-of-some-fields-to-null-using-jpa
			//
			// So we forcefully update alarm-related values to null if they are
			// null in the request:
			if (request.getAlarmCheckPeriod() == null || request.getAlarmMail() == null
					|| request.getAlarmRecordNumThreshold() == null) {
				reportDao.resetAlarmFields(view);
			}

			Dictionary<String, Object> payload = new Hashtable<String, Object>();
			payload.put("view", view);
			eventAdmin.postEvent(new Event(LiderConstants.EVENTS.REPORT_VIEW_UPDATED, payload));

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("view", view);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record updated.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
	}

	@Override
	public IRestResponse listViews(String name) {
		// Build search criteria
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		if (name != null && !name.isEmpty()) {
			propertiesMap.put("name", name);
		}

		// Find desired views
		List<? extends IReportView> views = reportDao.findViews(propertiesMap, null, null);
		// Authorize templates
		if (configService.getUserAuthorizationEnabled()) {
			Subject currentUser = null;
			try {
				currentUser = SecurityUtils.getSubject();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (currentUser != null && currentUser.getPrincipal() != null) {
				views = authService.getPermittedViews(currentUser.getPrincipal().toString(), views);
			} else {
				logger.warn("Unauthenticated user access.");
				return responseFactory.createResponse(RestResponseStatus.ERROR,
						Arrays.asList(new String[] { "NOT_AUTHORIZED" }));
			}
		}

		// Construct result map
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap.put("views", views);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse getView(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		IReportView view = reportDao.findView(new Long(id));
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("view", view);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record retrieved.", resultMap);
	}

	@Override
	public IRestResponse deleteView(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("ID was null.");
		}
		Dictionary<String, Object> payload = new Hashtable<String, Object>();
		payload.put("viewId", id);
		eventAdmin.postEvent(new Event(LiderConstants.EVENTS.REPORT_VIEW_DELETED, payload));
		reportDao.deleteView(new Long(id));
		logger.info("Report view record deleted: {}", id);
		return responseFactory.createResponse(RestResponseStatus.OK, "Record deleted.");
	}

	/**
	 * 
	 * @param reportDao
	 */
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	/**
	 * 
	 * @param requestFactory
	 */
	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	/**
	 * 
	 * @param responseFactory
	 */
	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	/**
	 * 
	 * @param authService
	 */
	public void setAuthService(IAuthService authService) {
		this.authService = authService;
	}

	/**
	 * 
	 * @param configService
	 */
	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

	/**
	 * 
	 * @param eventAdmin
	 */
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

}

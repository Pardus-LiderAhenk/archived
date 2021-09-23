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
package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.enums.PdfReportParamType;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportGenerationRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportGenerationRequestImpl implements IReportGenerationRequest {

	private static final long serialVersionUID = -5620268950586449608L;

	private Long viewId;

	private Map<String, Object> paramValues;

	private PdfReportParamType topLeft;

	private String topLeftText;

	private PdfReportParamType topRight;

	private String topRightText;

	private PdfReportParamType bottomLeft;

	private String bottomLeftText;

	private PdfReportParamType bottomRight;

	private String bottomRightText;

	private Date timestamp;

	@Override
	public Long getViewId() {
		return viewId;
	}

	public void setViewId(Long viewId) {
		this.viewId = viewId;
	}

	@Override
	public Map<String, Object> getParamValues() {
		return paramValues;
	}

	public void setParamValues(Map<String, Object> paramValues) {
		this.paramValues = paramValues;
	}

	@Override
	public PdfReportParamType getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(PdfReportParamType topLeft) {
		this.topLeft = topLeft;
	}

	@Override
	public String getTopLeftText() {
		return topLeftText;
	}

	public void setTopLeftText(String topLeftText) {
		this.topLeftText = topLeftText;
	}

	@Override
	public PdfReportParamType getTopRight() {
		return topRight;
	}

	public void setTopRight(PdfReportParamType topRight) {
		this.topRight = topRight;
	}

	@Override
	public String getTopRightText() {
		return topRightText;
	}

	public void setTopRightText(String topRightText) {
		this.topRightText = topRightText;
	}

	@Override
	public PdfReportParamType getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(PdfReportParamType bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	@Override
	public String getBottomLeftText() {
		return bottomLeftText;
	}

	public void setBottomLeftText(String bottomLeftText) {
		this.bottomLeftText = bottomLeftText;
	}

	@Override
	public PdfReportParamType getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(PdfReportParamType bottomRight) {
		this.bottomRight = bottomRight;
	}

	@Override
	public String getBottomRightText() {
		return bottomRightText;
	}

	public void setBottomRightText(String bottomRightText) {
		this.bottomRightText = bottomRightText;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}

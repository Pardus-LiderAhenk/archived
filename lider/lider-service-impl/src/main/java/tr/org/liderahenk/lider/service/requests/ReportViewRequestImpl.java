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
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportViewRequestImpl implements IReportViewRequest {

	private static final long serialVersionUID = -4108148529037085304L;

	private Long id;

	private Long templateId;

	private String name;

	private String description;

	private ReportType type;

	private List<ReportViewParamReqImpl> viewParams;

	private List<ReportViewColReqImpl> viewColumns;

	private Long alarmCheckPeriod;

	private Long alarmRecordNumThreshold;

	private String alarmMail;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	@Override
	public List<ReportViewParamReqImpl> getViewParams() {
		return viewParams;
	}

	public void setViewParams(List<ReportViewParamReqImpl> viewParams) {
		this.viewParams = viewParams;
	}

	@Override
	public List<ReportViewColReqImpl> getViewColumns() {
		return viewColumns;
	}

	public void setViewColumns(List<ReportViewColReqImpl> viewColumns) {
		this.viewColumns = viewColumns;
	}

	@Override
	public Long getAlarmCheckPeriod() {
		return alarmCheckPeriod;
	}

	public void setAlarmCheckPeriod(Long alarmCheckPeriod) {
		this.alarmCheckPeriod = alarmCheckPeriod;
	}

	@Override
	public Long getAlarmRecordNumThreshold() {
		return alarmRecordNumThreshold;
	}

	public void setAlarmRecordNumThreshold(Long alarmRecordNumThreshold) {
		this.alarmRecordNumThreshold = alarmRecordNumThreshold;
	}

	@Override
	public String getAlarmMail() {
		return alarmMail;
	}

	public void setAlarmMail(String alarmMail) {
		this.alarmMail = alarmMail;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}

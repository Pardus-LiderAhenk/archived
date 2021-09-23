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
package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ReportType;

/**
 * Entity class for {@link IReportView} objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Entity
@Table(name = "R_REPORT_VIEW")
public class ReportViewImpl implements IReportView {

	private static final long serialVersionUID = -1646315239508080076L;

	@Id
	@GeneratedValue
	@Column(name = "REPORT_VIEW_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
	private ReportTemplateImpl template; // unidirectional

	@Column(name = "NAME", unique = true, nullable = false, length = 255)
	private String name;

	@Column(name = "DESCRIPTION", length = 500)
	private String description;

	@Column(name = "REPORT_TYPE", nullable = false, length = 1)
	private Integer type;

	@OneToMany(mappedBy = "view", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReportViewParameterImpl> viewParams = new HashSet<ReportViewParameterImpl>();

	@OneToMany(mappedBy = "view", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReportViewColumnImpl> viewColumns = new HashSet<ReportViewColumnImpl>();

	@Column(name = "ALARM_CHECK_PERIOD")
	private Long alarmCheckPeriod;

	@Column(name = "ALARM_RECORD_NUM_THRESHOLD")
	private Long alarmRecordNumThreshold;

	@Column(name = "ALARM_MAIL")
	private String alarmMail;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public ReportViewImpl() {
	}

	public ReportViewImpl(Long id, ReportTemplateImpl template, String name, String description, ReportType type,
			Set<ReportViewParameterImpl> viewParams, Set<ReportViewColumnImpl> viewColumns, Long alarmCheckPeriod,
			Long alarmRecordNumThreshold, String alarmMail, Date createDate, Date modifyDate) {
		this.id = id;
		this.template = template;
		this.name = name;
		this.description = description;
		setType(type);
		this.viewParams = viewParams;
		this.viewColumns = viewColumns;
		this.alarmCheckPeriod = alarmCheckPeriod;
		this.alarmRecordNumThreshold = alarmRecordNumThreshold;
		this.alarmMail = alarmMail;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public ReportViewImpl(IReportView view) {
		this.id = view.getId();
		this.template = (ReportTemplateImpl) view.getTemplate();
		this.name = view.getName();
		this.description = view.getDescription();
		setType(view.getType());
		this.alarmCheckPeriod = view.getAlarmCheckPeriod();
		this.alarmRecordNumThreshold = view.getAlarmRecordNumThreshold();
		this.alarmMail = view.getAlarmMail();
		this.createDate = view.getCreateDate();
		this.modifyDate = view.getModifyDate();

		// Convert IReportViewParameter to ReportViewParameterImpl
		Set<? extends IReportViewParameter> params = view.getViewParams();
		if (params != null) {
			for (IReportViewParameter param : params) {
				addViewParameter(param);
			}
		}

		// Convert
		Set<? extends IReportViewColumn> columns = view.getViewColumns();
		if (columns != null) {
			for (IReportViewColumn column : columns) {
				addViewColumn(column);
			}
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public ReportTemplateImpl getTemplate() {
		return template;
	}

	public void setTemplate(ReportTemplateImpl template) {
		this.template = template;
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
		return ReportType.getType(type);
	}

	public void setType(ReportType type) {
		if (type == null) {
			this.type = null;
		} else {
			this.type = type.getId();
		}
	}

	@Override
	public Set<ReportViewParameterImpl> getViewParams() {
		return viewParams;
	}

	public void setViewParams(Set<ReportViewParameterImpl> viewParams) {
		this.viewParams = viewParams;
	}

	@Override
	public Set<ReportViewColumnImpl> getViewColumns() {
		return viewColumns;
	}

	public void setViewColumns(Set<ReportViewColumnImpl> viewColumns) {
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
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public void addViewParameter(IReportViewParameter param) {
		if (viewParams == null) {
			viewParams = new HashSet<ReportViewParameterImpl>();
		}
		ReportViewParameterImpl paramImpl = null;
		if (param instanceof ReportViewParameterImpl) {
			paramImpl = (ReportViewParameterImpl) param;
		} else {
			paramImpl = new ReportViewParameterImpl(param);
		}
		if (paramImpl.getView() != this) {
			paramImpl.setView(this);
		}
		viewParams.add(paramImpl);
	}

	@Override
	public void addViewColumn(IReportViewColumn column) {
		if (viewColumns == null) {
			viewColumns = new HashSet<ReportViewColumnImpl>();
		}
		ReportViewColumnImpl columnImpl = null;
		if (column instanceof ReportViewColumnImpl) {
			columnImpl = (ReportViewColumnImpl) column;
		} else {
			columnImpl = new ReportViewColumnImpl(column);
		}
		if (columnImpl.getView() != this) {
			columnImpl.setView(this);
		}
		viewColumns.add(columnImpl);
	}

	@Override
	public String toString() {
		return "ReportViewImpl [id=" + id + ", name=" + name + ", description=" + description + ", type=" + type
				+ ", alarmCheckPeriod=" + alarmCheckPeriod + ", alarmRecordNumThreshold=" + alarmRecordNumThreshold
				+ ", alarmMail=" + alarmMail + ", createDate=" + createDate + ", modifyDate=" + modifyDate + "]";
	}

}

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;

/**
 * Entity class for {@link IReportTemplate} objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "views" })
@Entity
@Table(name = "R_REPORT_TEMPLATE")
public class ReportTemplateImpl implements IReportTemplate {

	private static final long serialVersionUID = 5989888886942906625L;

	@Id
	@GeneratedValue
	@Column(name = "REPORT_TEMPLATE_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "NAME", unique = true, nullable = false, length = 255)
	private String name;

	@Column(name = "DESCRIPTION", length = 500)
	private String description;

	@Column(name = "QUERY", nullable = false, length = 4000)
	private String query;

	@Column(name = "REPORT_CODE", nullable = false, length = 100, unique = true)
	private String code;

	@OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReportTemplateParameterImpl> templateParams = new HashSet<ReportTemplateParameterImpl>(0); // bidirectional

	@OneToMany(mappedBy = "template", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReportTemplateColumnImpl> templateColumns = new HashSet<ReportTemplateColumnImpl>(0); // bidirectional

	/**
	 * This lazy collection of report views is only used to ensure cascade
	 * remove operation.
	 */
	@OneToMany(mappedBy = "template", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<ReportViewImpl> views = new HashSet<ReportViewImpl>(0); // bidirectional

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public ReportTemplateImpl() {
	}

	public ReportTemplateImpl(Long id, String name, String description, String query, String code,
			Set<ReportTemplateParameterImpl> templateParams, Set<ReportTemplateColumnImpl> templateColumns,
			Date createDate, Date modifyDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.query = query;
		this.code = code;
		this.templateParams = templateParams;
		this.templateColumns = templateColumns;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public ReportTemplateImpl(IReportTemplate template) {
		this.id = template.getId();
		this.name = template.getName();
		this.description = template.getDescription();
		this.query = template.getQuery();
		this.code = template.getCode();
		this.createDate = template.getCreateDate();
		this.modifyDate = template.getModifyDate();

		// Convert IReportTemplateParameter to ReportTemplateParameterImpl
		Set<? extends IReportTemplateParameter> params = template.getTemplateParams();
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				addTemplateParameter(param);
			}
		}

		// Convert IReportTemplateColumn to ReportTemplateColumnImpl
		Set<? extends IReportTemplateColumn> columns = template.getTemplateColumns();
		if (columns != null) {
			for (IReportTemplateColumn column : columns) {
				addTemplateColumn(column);
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
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@Override
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Set<ReportTemplateParameterImpl> getTemplateParams() {
		return templateParams;
	}

	public void setTemplateParams(Set<ReportTemplateParameterImpl> templateParams) {
		this.templateParams = templateParams;
	}

	@Override
	public Set<ReportTemplateColumnImpl> getTemplateColumns() {
		return templateColumns;
	}

	public void setTemplateColumns(Set<ReportTemplateColumnImpl> templateColumns) {
		this.templateColumns = templateColumns;
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

	public Set<ReportViewImpl> getViews() {
		return views;
	}

	public void setViews(Set<ReportViewImpl> views) {
		this.views = views;
	}

	@Override
	public void addTemplateParameter(IReportTemplateParameter param) {
		if (templateParams == null) {
			templateParams = new HashSet<ReportTemplateParameterImpl>(0);
		}
		ReportTemplateParameterImpl paramImpl = null;
		if (param instanceof ReportTemplateParameterImpl) {
			paramImpl = (ReportTemplateParameterImpl) param;
		} else {
			paramImpl = new ReportTemplateParameterImpl(param);
		}
		if (paramImpl.getTemplate() != this) {
			paramImpl.setTemplate(this);
		}
		templateParams.add(paramImpl);
	}

	@Override
	public void addTemplateColumn(IReportTemplateColumn column) {
		if (templateColumns == null) {
			templateColumns = new HashSet<ReportTemplateColumnImpl>();
		}
		ReportTemplateColumnImpl columnImpl = null;
		if (column instanceof ReportTemplateColumnImpl) {
			columnImpl = (ReportTemplateColumnImpl) column;
		} else {
			columnImpl = new ReportTemplateColumnImpl(column);
		}
		if (columnImpl.getTemplate() != this) {
			columnImpl.setTemplate(this);
		}
		templateColumns.add(columnImpl);
	}

	@Override
	public String toString() {
		return "ReportTemplateImpl [id=" + id + ", name=" + name + ", description=" + description + ", query=" + query
				+ ", code=" + code + ", templateParams=" + templateParams + ", templateColumns=" + templateColumns
				+ ", views=" + views + ", createDate=" + createDate + ", modifyDate=" + modifyDate + "]";
	}

}

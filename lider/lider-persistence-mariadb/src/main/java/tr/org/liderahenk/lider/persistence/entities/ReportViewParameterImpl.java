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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;

@JsonIgnoreProperties({ "view" })
@Entity
@Table(name = "R_REPORT_VIEW_PARAMETER")
public class ReportViewParameterImpl implements IReportViewParameter {

	private static final long serialVersionUID = -6889314491766033698L;

	@Id
	@GeneratedValue
	@Column(name = "VIEW_PARAMETER_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "REPORT_VIEW_ID", nullable = false)
	private ReportViewImpl view;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TEMPLATE_PARAMETER_ID", nullable = false)
	private ReportTemplateParameterImpl referencedParam;

	@Column(name = "LABEL", nullable = false, length = 250)
	private String label;

	@Column(name = "VALUE", nullable = false, length = 4000)
	private String value;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public ReportViewParameterImpl() {
	}

	public ReportViewParameterImpl(Long id, ReportViewImpl view, ReportTemplateParameterImpl referencedParam,
			String label, String value, Date createDate) {
		this.id = id;
		this.view = view;
		this.referencedParam = referencedParam;
		this.label = label;
		this.value = value;
		this.createDate = createDate;
	}

	public ReportViewParameterImpl(IReportViewParameter param) {
		this.id = param.getId();
		this.label = param.getLabel();
		this.value = param.getValue();
		this.createDate = param.getCreateDate();
		if (param.getView() instanceof ReportViewImpl) {
			this.view = (ReportViewImpl) param.getView();
		}
		if (param.getReferencedParam() instanceof ReportTemplateParameterImpl) {
			this.referencedParam = (ReportTemplateParameterImpl) param.getReferencedParam();
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
	public ReportViewImpl getView() {
		return view;
	}

	public void setView(ReportViewImpl view) {
		this.view = view;
	}

	@Override
	public ReportTemplateParameterImpl getReferencedParam() {
		return referencedParam;
	}

	public void setReferencedParam(ReportTemplateParameterImpl referencedParam) {
		this.referencedParam = referencedParam;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((referencedParam == null) ? 0 : referencedParam.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportViewParameterImpl other = (ReportViewParameterImpl) obj;
		if (referencedParam == null) {
			if (other.referencedParam != null)
				return false;
		} else if (!referencedParam.equals(other.referencedParam))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportViewParameterImpl [id=" + id + ", referencedParam=" + referencedParam + ", label=" + label
				+ ", value=" + value + ", createDate=" + createDate + "]";
	}

}

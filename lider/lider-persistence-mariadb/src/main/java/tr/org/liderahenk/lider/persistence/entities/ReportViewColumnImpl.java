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

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;
import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;

@JsonIgnoreProperties({ "view" })
@Entity
@Table(name = "R_REPORT_VIEW_COLUMN")
public class ReportViewColumnImpl implements IReportViewColumn {

	private static final long serialVersionUID = -8966099076093392712L;

	@Id
	@GeneratedValue
	@Column(name = "VIEW_COLUMN_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REPORT_VIEW_ID", nullable = false)
	private ReportViewImpl view;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TEMPLATE_COLUMN_ID", nullable = false)
	private ReportTemplateColumnImpl referencedCol;

	@Column(name = "COLUMN_TYPE", nullable = false)
	private Integer type;

	@Column(name = "LEGEND", nullable = true)
	private String legend;

	@Column(name = "WIDTH")
	private Integer width;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public ReportViewColumnImpl() {
	}

	public ReportViewColumnImpl(Long id, ReportViewImpl view, ReportTemplateColumnImpl referencedCol,
			ViewColumnType type, String legend, Integer width, Date createDate) {
		this.id = id;
		this.view = view;
		this.referencedCol = referencedCol;
		setType(type);
		this.legend = legend;
		this.width = width;
		this.createDate = createDate;
	}

	public ReportViewColumnImpl(IReportViewColumn column) {
		this.id = column.getId();
		this.legend = column.getLegend();
		setType(column.getType());
		this.legend = column.getLegend();
		this.width = column.getWidth();
		this.createDate = column.getCreateDate();
		if (column.getView() instanceof ReportViewImpl) {
			this.view = (ReportViewImpl) column.getView();
		}
		if (column.getReferencedCol() instanceof ReportTemplateColumnImpl) {
			this.referencedCol = (ReportTemplateColumnImpl) column.getReferencedCol();
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
	public ReportTemplateColumnImpl getReferencedCol() {
		return referencedCol;
	}

	public void setReferencedCol(ReportTemplateColumnImpl referencedCol) {
		this.referencedCol = referencedCol;
	}

	@Override
	public ViewColumnType getType() {
		return ViewColumnType.getType(type);
	}

	public void setType(ViewColumnType type) {
		if (type == null) {
			this.type = null;
		} else {
			this.type = type.getId();
		}
	}

	@Override
	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
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
		result = prime * result + ((referencedCol == null) ? 0 : referencedCol.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ReportViewColumnImpl other = (ReportViewColumnImpl) obj;
		if (referencedCol == null) {
			if (other.referencedCol != null)
				return false;
		} else if (!referencedCol.equals(other.referencedCol))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReportViewColumnImpl [id=" + id + ", referencedCol=" + referencedCol + ", type=" + type + ", legend="
				+ legend + ", width=" + width + ", createDate=" + createDate + "]";
	}

}

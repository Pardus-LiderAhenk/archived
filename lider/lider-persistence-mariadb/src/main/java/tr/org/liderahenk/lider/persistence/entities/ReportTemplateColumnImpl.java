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
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;

/**
 * This class represents a report column defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "template", "viewColumns" })
@Entity
@Table(name = "R_REPORT_TEMPLATE_COLUMN", uniqueConstraints = @UniqueConstraint(columnNames = { "REPORT_TEMPLATE_ID",
		"COLUMN_ORDER" }))
public class ReportTemplateColumnImpl implements IReportTemplateColumn {

	private static final long serialVersionUID = 7196785409916030894L;

	@Id
	@GeneratedValue
	@Column(name = "TEMPLATE_COLUMN_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
	private ReportTemplateImpl template; // bidirectional

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "COLUMN_ORDER", nullable = false)
	private Integer columnOrder;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	/**
	 * This lazy collection of report view columns is only used to ensure
	 * cascade remove operation.
	 */
	@OneToMany(mappedBy = "referencedCol", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<ReportViewColumnImpl> viewColumns = new HashSet<ReportViewColumnImpl>();

	public ReportTemplateColumnImpl() {
	}

	public ReportTemplateColumnImpl(Long id, ReportTemplateImpl template, String name, Integer columnOrder,
			Date createDate) {
		this.id = id;
		this.template = template;
		this.name = name;
		this.columnOrder = columnOrder;
		this.createDate = createDate;
	}

	public ReportTemplateColumnImpl(IReportTemplateColumn column) {
		this.id = column.getId();
		this.name = column.getName();
		this.columnOrder = column.getColumnOrder();
		this.createDate = column.getCreateDate();
		if (column.getTemplate() instanceof ReportTemplateImpl) {
			this.template = (ReportTemplateImpl) column.getTemplate();
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
	public Integer getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(Integer columnOrder) {
		this.columnOrder = columnOrder;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Set<ReportViewColumnImpl> getViewColumns() {
		return viewColumns;
	}

	public void setViewColumns(Set<ReportViewColumnImpl> viewColumns) {
		this.viewColumns = viewColumns;
	}

	@Override
	public String toString() {
		return "ReportTemplateColumnImpl [id=" + id + ", name=" + name + ", columnOrder=" + columnOrder + "]";
	}

	/**
	 * hashCode() & equals() are overridden to prevent duplicate records!
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnOrder == null) ? 0 : columnOrder.hashCode());
		return result;
	}

	/**
	 * hashCode() & equals() are overridden to prevent duplicate records!
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportTemplateColumnImpl other = (ReportTemplateColumnImpl) obj;
		if (columnOrder == null) {
			if (other.columnOrder != null)
				return false;
		} else if (!columnOrder.equals(other.columnOrder))
			return false;
		return true;
	}

}

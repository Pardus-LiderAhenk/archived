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
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISearchGroupEntry;
import tr.org.liderahenk.lider.core.api.rest.enums.DNType;

/**
 * Entity class for search group entries.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Kağan Akkaya</a>
 *
 */
@JsonIgnoreProperties({ "group" })
@Entity
@Table(name = "C_SEARCH_GROUP_ENTRY", uniqueConstraints = @UniqueConstraint(columnNames = { "SEARCH_GROUP_ID", "DN" }) )
public class SearchGroupEntryImpl implements ISearchGroupEntry {

	private static final long serialVersionUID = -2563974322082465664L;

	@Id
	@GeneratedValue
	@Column(name = "SEARCH_GROUP_ENTRY_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SEARCH_GROUP_ID", nullable = false)
	private SearchGroupImpl group; // bidirectional

	@Column(name = "DN", nullable = false)
	private String dn;

	@Column(name = "DN_TYPE", length = 1, nullable = false)
	private Integer dnType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public SearchGroupEntryImpl() {
	}

	public SearchGroupEntryImpl(Long id, SearchGroupImpl group, String dn, DNType dnType, Date createDate) {
		this.id = id;
		this.group = group;
		this.dn = dn;
		setDnType(dnType);
		this.createDate = createDate;
	}

	public SearchGroupEntryImpl(ISearchGroupEntry entry) {
		this.id = entry.getId();
		this.dn = entry.getDn();
		setDnType(entry.getDnType());
		this.createDate = entry.getCreateDate();
		if (entry.getGroup() instanceof SearchGroupEntryImpl) {
			this.group = (SearchGroupImpl) entry.getGroup();
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
	public SearchGroupImpl getGroup() {
		return group;
	}

	public void setGroup(SearchGroupImpl group) {
		this.group = group;
	}

	@Override
	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	@Override
	public DNType getDnType() {
		return DNType.getType(dnType);
	}

	public void setDnType(DNType dnType) {
		if (dnType == null) {
			this.dnType = null;
		} else {
			this.dnType = dnType.getId();
		}
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "SearchGroupEntryImpl [id=" + id + ", dn=" + dn + ", dnType=" + dnType + ", createDate=" + createDate
				+ "]";
	}

}

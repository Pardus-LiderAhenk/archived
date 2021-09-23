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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;

/**
 * Entity class for registration template 
 * 
 * @author <a href="mailto:muhammededip.yildiz">Edip YILDIZ</a>
 *
 */
@Entity
@Table(name = "C_REGISTRATION TEMPLATE")
public class RegistrationTemplateImpl implements IRegistrationTemplate  {

	private static final long serialVersionUID = -241241606291513291L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "UNIT_ID")
	private String unitId;

	@Column(name = "AUHT_GROUP")
	private String authGroup;

	@Column(name = "PARENT_DN")
	private String parentDn;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public RegistrationTemplateImpl() {
	}

	public RegistrationTemplateImpl(Long id, String unitId, String authGroup, String parentDn, Date createDate) {
		this.id = id;
		this.unitId = unitId;
		this.authGroup = authGroup;
		this.parentDn = parentDn;
		this.createDate = createDate;
	}

	public RegistrationTemplateImpl(IRegistrationTemplate template) {
		this.id = template.getId();
		this.unitId = template.getUnitId();
		this.authGroup = template.getAuthGroup();
		this.parentDn = template.getParentDn();
		this.createDate = template.getCreateDate();
	}

	/* (non-Javadoc)
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String getUnitId() {
		return unitId;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String getAuthGroup() {
		return authGroup;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	/* (non-Javadoc)
	 */
	@Override
	public String getParentDn() {
		return parentDn;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setParentDn(String parentDn) {
		this.parentDn = parentDn;
	}

	/* (non-Javadoc)
	 */
	@Override
	public Date getCreateDate() {
		return createDate;
	}

	/* (non-Javadoc)
	 */
	@Override
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}



}

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.ISystemEventsProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "SystemEventsProperties")
public class SystemEventsPropertiesImpl implements ISystemEventsProperties {

	private static final long serialVersionUID = -8006113886525801620L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "SystemEventID", nullable = true)
	private Integer systemEventsId;

	@Column(name = "ParamName", nullable = true)
	private String paramName;

	@Lob
	@Column(name = "ParamValue", nullable = true)
	private String paramValue;

	public SystemEventsPropertiesImpl(Long id, Integer systemEventsId, String paramName, String paramValue) {
		super();
		this.id = id;
		this.systemEventsId = systemEventsId;
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	public SystemEventsPropertiesImpl(ISystemEventsProperties property) {
		this.id = property.getSystemEventPropertyId();
		this.systemEventsId = property.getSystemEventsId();
		this.paramName = property.getParamName();
		this.paramValue = property.getParamValue();
	}

	public SystemEventsPropertiesImpl() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	@Override
	public Integer getSystemEventsId() {
		return systemEventsId;
	}

	public void setSystemEventsId(Integer systemEventsId) {
		this.systemEventsId = systemEventsId;
	}

	@Override
	public Long getSystemEventPropertyId() {
		return id;
	}

}

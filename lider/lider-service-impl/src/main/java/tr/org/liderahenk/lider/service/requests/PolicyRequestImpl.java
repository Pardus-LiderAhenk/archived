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

import tr.org.liderahenk.lider.core.api.rest.requests.IPolicyRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyRequestImpl implements IPolicyRequest {

	private static final long serialVersionUID = 8296439510402013107L;

	private Long id;

	private String label;

	private String description;

	private boolean active;

	private List<Long> profileIdList;

	private Date timestamp;

	public PolicyRequestImpl() {
	}

	public PolicyRequestImpl(Long id, String label, String description, boolean active, List<Long> profileIdList,
			Date timestamp) {
		super();
		this.id = id;
		this.label = label;
		this.description = description;
		this.active = active;
		this.profileIdList = profileIdList;
		this.timestamp = timestamp;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public List<Long> getProfileIdList() {
		return profileIdList;
	}

	public void setProfileIdList(List<Long> profileIdList) {
		this.profileIdList = profileIdList;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "PolicyRequestImpl [id=" + id + ", label=" + label + ", description=" + description + ", active="
				+ active + ", profileIdList=" + profileIdList + ", timestamp=" + timestamp + "]";
	}

}

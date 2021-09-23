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
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileRequestImpl implements IProfileRequest {

	private static final long serialVersionUID = 238992590640947782L;

	private String pluginName;

	private String pluginVersion;

	private Long id;

	private String label;

	private String description;

	private boolean overridable;

	private boolean active;

	private Map<String, Object> profileData;

	private Date timestamp;

	public ProfileRequestImpl() {
	}

	public ProfileRequestImpl(String pluginName, String pluginVersion, Long id, String label, String description,
			boolean overridable, boolean active, Map<String, Object> profileData, Date timestamp) {
		super();
		this.pluginName = pluginName;
		this.pluginVersion = pluginVersion;
		this.id = id;
		this.label = label;
		this.description = description;
		this.overridable = overridable;
		this.active = active;
		this.profileData = profileData;
		this.timestamp = timestamp;
	}

	@Override
	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Override
	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
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
	public boolean isOverridable() {
		return overridable;
	}

	public void setOverridable(boolean overridable) {
		this.overridable = overridable;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public Map<String, Object> getProfileData() {
		return profileData;
	}

	public void setProfileData(Map<String, Object> profileData) {
		this.profileData = profileData;
	}

	@Override
	public String toString() {
		return "ProfileRequestImpl [pluginName=" + pluginName + ", pluginVersion=" + pluginVersion + ", id=" + id
				+ ", label=" + label + ", description=" + description + ", overridable=" + overridable + ", active="
				+ active + ", profileData=" + profileData + ", timestamp=" + timestamp + "]";
	}

}

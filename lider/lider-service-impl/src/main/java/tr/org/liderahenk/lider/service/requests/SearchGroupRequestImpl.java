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
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.ISearchGroupRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchGroupRequestImpl implements ISearchGroupRequest {

	private static final long serialVersionUID = 4438546686735211164L;

	private String name;

	private boolean searchAgents;

	private boolean searchUsers;

	private boolean searchGroups;

	private Map<String, String> criteria;

	private Set<SearchGroupEntryRequestImpl> entries;

	private Date timestamp;

	public SearchGroupRequestImpl() {
	}

	public SearchGroupRequestImpl(String name, boolean searchAgents, boolean searchUsers, boolean searchGroups,
			Map<String, String> criteria, Set<SearchGroupEntryRequestImpl> entries, Date timestamp) {
		this.name = name;
		this.searchAgents = searchAgents;
		this.searchUsers = searchUsers;
		this.searchGroups = searchGroups;
		this.criteria = criteria;
		this.entries = entries;
		this.timestamp = timestamp;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isSearchAgents() {
		return searchAgents;
	}

	public void setSearchAgents(boolean searchAgents) {
		this.searchAgents = searchAgents;
	}

	@Override
	public boolean isSearchUsers() {
		return searchUsers;
	}

	public void setSearchUsers(boolean searchUsers) {
		this.searchUsers = searchUsers;
	}

	@Override
	public boolean isSearchGroups() {
		return searchGroups;
	}

	public void setSearchGroups(boolean searchGroups) {
		this.searchGroups = searchGroups;
	}

	@Override
	public Map<String, String> getCriteria() {
		return criteria;
	}

	public void setCriteria(Map<String, String> criteria) {
		this.criteria = criteria;
	}

	@Override
	public Set<SearchGroupEntryRequestImpl> getEntries() {
		return entries;
	}

	public void setEntries(Set<SearchGroupEntryRequestImpl> entries) {
		this.entries = entries;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}

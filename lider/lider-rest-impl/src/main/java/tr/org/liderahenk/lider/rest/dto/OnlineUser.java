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
package tr.org.liderahenk.lider.rest.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OnlineUser implements Serializable {

	private static final long serialVersionUID = -1532561934289814137L;

	private Long agentId;

	private String hostname;

	private String ipAddresses; // Comma-separated IP addresses

	private String dn;

	private String username;
	
	private List<UserAgent> agentList;

	private Date createDate;
	
	public OnlineUser() {
		// TODO Auto-generated constructor stub
	}

	public OnlineUser(Long agentId, String hostname, String ipAddresses, String dn, String username, Date createDate,  List<UserAgent> agentList) {
		this.agentId = agentId;
		this.hostname = hostname;
		this.ipAddresses = ipAddresses;
		this.dn = dn;
		this.username = username;
		this.createDate = createDate;
		this.agentList=agentList;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddresses() {
		return ipAddresses;
	}

	public void setIpAddresses(String ipAddresses) {
		this.ipAddresses = ipAddresses;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "OnlineUser [hostname=" + hostname + ", ipAddresses=" + ipAddresses + ", dn=" + dn + ", username="
				+ username + ", createDate=" + createDate + "]";
	}

	public List<UserAgent> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<UserAgent> agentList) {
		this.agentList = agentList;
	}

	
}

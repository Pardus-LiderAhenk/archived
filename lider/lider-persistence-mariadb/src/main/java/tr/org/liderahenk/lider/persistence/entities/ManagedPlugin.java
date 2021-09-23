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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.plugin.deployer.IManagedPlugin;
import tr.org.liderahenk.lider.core.api.plugin.deployer.IPluginPart;

@Entity
@Table(name = "MANAGED_PLUGIN")
public class ManagedPlugin implements IManagedPlugin{
	

	private static final long serialVersionUID = 3097032910608775130L;
	
	
	@Id
	@GeneratedValue
	@Column(name = "MANAGED_PLUGIN_ID", unique = true, nullable = false)
	private Long id;
	private String name;
	private String version;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INSTALLATION_DATE")
	private Date installationDate;
	private Boolean active;
	
	@OneToMany(mappedBy = "info", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<PluginPart> parts;
	
	public ManagedPlugin() {
	}
	
	public ManagedPlugin(Long id,String name,String version,Date installationDate,Boolean active,List<IPluginPart> parts) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.installationDate = installationDate;
		this.active = active;
		
		List<PluginPart> implParts = new ArrayList<PluginPart>(parts.size());
		
		if(parts != null){
			for(IPluginPart part:parts){
				implParts.add(new PluginPart(part));
			}
		}
		
		this.parts = implParts;
	}
	
	public ManagedPlugin(IManagedPlugin plugin) {
		this.id = plugin.getId();
		this.name = plugin.getName();
		this.version = plugin.getVersion();
		this.active = plugin.getActive();
		this.installationDate = plugin.getInstallationDate();
		
		List<? extends IPluginPart> tmpParts = plugin.getParts();
		if (tmpParts != null) {
			for (IPluginPart tmpPart : tmpParts) {
				addManagedPlugin(tmpPart);
			}
		}
	}
	
	public void addManagedPlugin(IPluginPart part) {
		if (parts == null) {
			parts = new ArrayList<PluginPart>();
		}
		PluginPart partImpl = null;
		if (part instanceof PluginPart) {
			partImpl = (PluginPart) part;
		} else {
			partImpl = new PluginPart(part);
		}
		if (partImpl.getInfo() != this) {
			partImpl.setInfo(this);
		}
		parts.add(partImpl);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Date getInstallationDate() {
		return installationDate;
	}
	public void setInstallationDate(Date installationDate) {
		this.installationDate = installationDate;
	}
	public List<PluginPart> getParts() {
		return parts;
	}
	public void setParts(List<PluginPart> parts) {
		this.parts = parts;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}

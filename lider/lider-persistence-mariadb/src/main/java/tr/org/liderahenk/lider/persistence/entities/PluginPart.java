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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import tr.org.liderahenk.lider.core.api.plugin.deployer.IManagedPlugin;
import tr.org.liderahenk.lider.core.api.plugin.deployer.IPluginPart;

@Entity
@Table(name = "PLUGIN_PART")
public class PluginPart implements IPluginPart{

	private static final long serialVersionUID = -409362861845723176L;
	
	@Id
	@GeneratedValue
	@Column(name = "PLUGIN_PART_ID", unique = true, nullable = false)
	private Long id;
	
	private String fileName;
	
	private String type;
	
	private String fullPath;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_INFO_ID", nullable = false)
	private ManagedPlugin info;
	
	
	public PluginPart() {
	}

	public PluginPart(Long id,String fileName,String type,String fullPath) {
		this.id = id;
		this.fileName = fileName;
		this.type = type;
		this.fullPath = fullPath;
		this.info = new ManagedPlugin(info);
	}
	
	public PluginPart(IPluginPart part) {
		this.id = part.getId();
		this.fileName = part.getFileName();
		this.type = part.getType();
		this.fullPath = part.getFullPath();
		this.info = new ManagedPlugin(part.getInfo());
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	public ManagedPlugin getInfo() {
		return info;
	}
	public void setInfo(ManagedPlugin info) {
		this.info = info;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}

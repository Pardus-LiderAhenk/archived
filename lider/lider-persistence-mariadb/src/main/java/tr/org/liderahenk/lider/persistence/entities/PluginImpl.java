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
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;

/**
 * Entity class for IPlugin objects.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin
 *
 */
@JsonIgnoreProperties({ "profiles", "distroParamsBlob","mailAddresses" })
@Entity
@Table(name = "C_PLUGIN", uniqueConstraints = @UniqueConstraint(columnNames = { "PLUGIN_NAME", "PLUGIN_VERSION" }))
public class PluginImpl implements IPlugin {

	private static final long serialVersionUID = -6297900066220421421L;

	@Id
	@GeneratedValue
	@Column(name = "PLUGIN_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "PLUGIN_NAME", nullable = false)
	private String name;

	@Column(name = "PLUGIN_VERSION", nullable = false)
	private String version;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "ACTIVE")
	private boolean active = true;

	@Column(name = "DELETED")
	private boolean deleted = false;

	@Column(name = "MACHINE_ORIENTED_PLUGIN")
	private boolean machineOriented;

	@Column(name = "USER_ORIENTED_PLUGIN")
	private boolean userOriented;

	@Column(name = "POLICY_PLUGIN")
	private boolean policyPlugin;

	@Column(name = "TASK_PLUGIN")
	private boolean taskPlugin;

	@Column(name = "USES_FILE_TRANSFER")
	private boolean usesFileTransfer;

	@Column(name = "X_BASED")
	private boolean xBased;

	@OneToMany(mappedBy = "plugin", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	private List<ProfileImpl> profiles = new ArrayList<ProfileImpl>(); // bidirectional
	
	
	@OneToMany(mappedBy = "plugin", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = false)
	private List<MailAddressImpl> mailAddresses = new ArrayList<MailAddressImpl>(); // bidirectional

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	

	public PluginImpl() {
	}

	public PluginImpl(Long id, String name, String version, String description, boolean active, boolean deleted,
			boolean machineOriented, boolean userOriented, boolean policyPlugin, boolean taskPlugin,
			boolean usesFileTransfer, boolean xBased, List<ProfileImpl> profiles, Date createDate, Date modifyDate) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
		this.active = active;
		this.deleted = deleted;
		this.machineOriented = machineOriented;
		this.userOriented = userOriented;
		this.policyPlugin = policyPlugin;
		this.taskPlugin = taskPlugin;
		this.usesFileTransfer = usesFileTransfer;
		this.xBased = xBased;
		this.profiles = profiles;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public PluginImpl(IPlugin plugin) {
		this.id = plugin.getId();
		this.name = plugin.getName();
		this.version = plugin.getVersion();
		this.description = plugin.getDescription();
		this.active = plugin.isActive();
		this.deleted = plugin.isDeleted();
		this.machineOriented = plugin.isMachineOriented();
		this.userOriented = plugin.isUserOriented();
		this.policyPlugin = plugin.isPolicyPlugin();
		this.taskPlugin = plugin.isTaskPlugin();
		this.usesFileTransfer = plugin.isUsesFileTransfer();
		this.xBased = plugin.isxBased();
		this.createDate = plugin.getCreateDate();
		this.modifyDate = plugin.getModifyDate();

		// Convert IProfile to ProfileImpl
		List<? extends IProfile> tmpProfiles = plugin.getProfiles();
		if (tmpProfiles != null) {
			for (IProfile tmpProfile : tmpProfiles) {
				addProfile(tmpProfile);
			}
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
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public List<ProfileImpl> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<ProfileImpl> profiles) {
		this.profiles = profiles;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isMachineOriented() {
		return machineOriented;
	}

	public void setMachineOriented(boolean machineOriented) {
		this.machineOriented = machineOriented;
	}

	@Override
	public boolean isUserOriented() {
		return userOriented;
	}

	public void setUserOriented(boolean userOriented) {
		this.userOriented = userOriented;
	}

	@Override
	public boolean isPolicyPlugin() {
		return policyPlugin;
	}

	public void setPolicyPlugin(boolean policyPlugin) {
		this.policyPlugin = policyPlugin;
	}

	@Override
	public boolean isxBased() {
		return xBased;
	}

	public void setxBased(boolean xBased) {
		this.xBased = xBased;
	}

	@Override
	public void addProfile(IProfile profile) {
		if (profiles == null) {
			profiles = new ArrayList<ProfileImpl>();
		}
		ProfileImpl profImpl = null;
		if (profile instanceof ProfileImpl) {
			profImpl = (ProfileImpl) profile;
		} else {
			profImpl = new ProfileImpl(profile);
		}
		if (profImpl.getPlugin() != this) {
			profImpl.setPlugin(this);
		}
		profiles.add(profImpl);
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public boolean isTaskPlugin() {
		return taskPlugin;
	}

	public void setTaskPlugin(boolean taskPlugin) {
		this.taskPlugin = taskPlugin;
	}

	@Override
	public boolean isUsesFileTransfer() {
		return usesFileTransfer;
	}

	public void setUsesFileTransfer(boolean usesFileTransfer) {
		this.usesFileTransfer = usesFileTransfer;
	}

	@Override
	public String toString() {
		return "PluginImpl [id=" + id + ", name=" + name + ", version=" + version + ", description=" + description
				+ ", active=" + active + ", deleted=" + deleted + ", machineOriented=" + machineOriented
				+ ", userOriented=" + userOriented + ", policyPlugin=" + policyPlugin + ", taskPlugin=" + taskPlugin
				+ ", xBased=" + xBased + ", usesFileTransfer=" + usesFileTransfer + ", profiles=" + profiles
				+ ", createDate=" + createDate + ", modifyDate=" + modifyDate + "]";
	}

	public List<MailAddressImpl> getMailAddresses() {
		return mailAddresses;
	}

	public void setMailAddresses(List<MailAddressImpl> mailAddresses) {
		this.mailAddresses = mailAddresses;
	}

}

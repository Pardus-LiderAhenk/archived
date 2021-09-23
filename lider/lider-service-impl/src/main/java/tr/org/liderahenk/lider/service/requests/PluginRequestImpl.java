package tr.org.liderahenk.lider.service.requests;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PluginRequestImpl implements IPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7377201250413594479L;

	private Long id;

	private String name;

	private String version;

	private String description;

	private boolean active = true;

	private boolean deleted = false;

	private boolean machineOriented;

	private boolean userOriented;

	private boolean policyPlugin;

	private boolean taskPlugin;

	private boolean xBased;

	private Date createDate;

	private Date modifyDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isMachineOriented() {
		return machineOriented;
	}

	public void setMachineOriented(boolean machineOriented) {
		this.machineOriented = machineOriented;
	}

	public boolean isUserOriented() {
		return userOriented;
	}

	public void setUserOriented(boolean userOriented) {
		this.userOriented = userOriented;
	}

	public boolean isPolicyPlugin() {
		return policyPlugin;
	}

	public void setPolicyPlugin(boolean policyPlugin) {
		this.policyPlugin = policyPlugin;
	}

	public boolean isxBased() {
		return xBased;
	}

	public void setxBased(boolean xBased) {
		this.xBased = xBased;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public boolean isTaskPlugin() {
		return taskPlugin;
	}

	public void setTaskPlugin(boolean taskPlugin) {
		this.taskPlugin = taskPlugin;
	}

	@Override
	public boolean isUsesFileTransfer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<? extends IProfile> getProfiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProfile(IProfile profile) {
		// TODO Auto-generated method stub
		
	}



}

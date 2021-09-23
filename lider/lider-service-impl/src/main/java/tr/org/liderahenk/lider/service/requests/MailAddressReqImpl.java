package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MailAddressReqImpl implements IMailAddress{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1985452578229846413L;
	private Long id;
	private String mailAddress;
	
	private PluginRequestImpl plugin;
	private Date createDate;
	private Date modifyDate;
	private boolean deleted = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public PluginRequestImpl getPlugin() {
		return plugin;
	}
	public void setPlugin(PluginRequestImpl plugin) {
		this.plugin = plugin;
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
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}

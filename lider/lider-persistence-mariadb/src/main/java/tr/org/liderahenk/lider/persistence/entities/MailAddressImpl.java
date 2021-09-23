package tr.org.liderahenk.lider.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;

@Entity
@Table(name = "C_MAIL_ADDRESS")
public class MailAddressImpl implements IMailAddress {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5393776049614267262L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MAIL_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "MAIL_ADDRESS", nullable = false)
	private String mailAddress;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_ID", nullable = false)
	private PluginImpl plugin; // bidirectional

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	@Column(name = "DELETED")
	private boolean deleted = false;

	public MailAddressImpl() {
	}

	public MailAddressImpl(Long id, String mailAddress, PluginImpl plugin, Date createDate, Date modifyDate) {
		this.id = id;
		this.mailAddress = mailAddress;
		this.plugin = plugin;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}
	public MailAddressImpl(Long id, String mailAddress, PluginImpl plugin, Date createDate, Date modifyDate, boolean deleted) {
		this.id = id;
		this.mailAddress = mailAddress;
		this.plugin = plugin;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.deleted=deleted;
	}

	public MailAddressImpl(IMailAddress mailAddress) {
		this.id = mailAddress.getId();
		this.mailAddress = mailAddress.getMailAddress();

		this.createDate = mailAddress.getCreateDate();
		this.modifyDate = mailAddress.getModifyDate();

		if (mailAddress.getPlugin() instanceof PluginImpl) {
			this.plugin = (PluginImpl) mailAddress.getPlugin();
		}
	}

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

	public PluginImpl getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}
	

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}


}

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

import tr.org.liderahenk.lider.core.api.persistence.entities.IMailParameter;

@Entity
@Table(name = "C_MAIL_PARAMETER")
public class MailParameterImpl implements IMailParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5393776049614267262L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "MAIL_PARAMETER", nullable = false)
	private String mailParameter;

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

	public MailParameterImpl() {
	}

	public MailParameterImpl(Long id, String mailParameter, PluginImpl plugin, Date createDate, Date modifyDate) {
		this.id = id;
		this.mailParameter = mailParameter;
		this.plugin = plugin;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public MailParameterImpl(IMailParameter par) {
		this.id = par.getId();
		this.mailParameter = par.getMailParameter();

		this.createDate = par.getCreateDate();
		this.modifyDate = par.getModifyDate();

		if (par.getPlugin() instanceof PluginImpl) {
			this.plugin = (PluginImpl) par.getPlugin();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getMailParameter() {
		return mailParameter;
	}

	public void setMailParameter(String mailParameter) {
		this.mailParameter = mailParameter;
	}


}

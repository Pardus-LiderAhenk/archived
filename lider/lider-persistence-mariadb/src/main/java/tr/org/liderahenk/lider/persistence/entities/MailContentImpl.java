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

import tr.org.liderahenk.lider.core.api.persistence.entities.IMailContent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;

@Entity
@Table(name = "C_MAIL_CONTENT")
public class MailContentImpl implements IMailContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5393776049614267262L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "MAIL_CONTENT",  length=4999)
	private String mailContent;
	
	@Column(name = "MAIL_SEND_STRATEGY")
	private int mailSendStartegy;
	
	@Column(name = "MAIL_SCHEDULE_CRON_STR")
	private String mailSchedulerCronString;
	
	@Column(name = "MAIL_SCHD_TIME_PERIOD")
	private String mailSchdTimePeriod;
	
	@Column(name = "MAIL_SCHD_TIME_PERIOD_TYPE")
	private int mailSchdTimePeriodType;
	

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

	public MailContentImpl() {
	}

	public MailContentImpl(Long id, String mailContent, int mailStrategy, 
			String mailSchedulerCronString, 
			String mailSchdTimePeriod,
			Integer mailSchdTimePeriodType,
			PluginImpl plugin, Date createDate, Date modifyDate) {
		this.id = id;
		this.mailContent = mailContent;
		this.plugin = plugin;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.mailSendStartegy=mailStrategy;
		this.mailSchdTimePeriod=mailSchdTimePeriod;
		this.mailSchdTimePeriodType=mailSchdTimePeriodType;
		this.mailSchedulerCronString=mailSchedulerCronString;
	}

	public MailContentImpl(IMailContent mailContent) {
		this.id = mailContent.getId();
		this.mailContent = mailContent.getMailContent();
		this.mailSendStartegy=mailContent.getMailSendStartegy();
		this.mailSchedulerCronString=mailContent.getMailSchedulerCronString();
		
		this.mailSchdTimePeriod=mailContent.getMailSchdTimePeriod();
		this.mailSchdTimePeriodType=mailContent.getMailSchdTimePeriodType();
		
		this.createDate = mailContent.getCreateDate();
		this.modifyDate = mailContent.getModifyDate();

		if (mailContent.getPlugin() instanceof PluginImpl) {
			this.plugin = (PluginImpl) mailContent.getPlugin();
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

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public int getMailSendStartegy() {
		return mailSendStartegy;
	}

	public void setMailSendStartegy(int mailSendStartegy) {
		this.mailSendStartegy = mailSendStartegy;
	}

	public String getMailSchedulerCronString() {
		return mailSchedulerCronString;
	}

	public void setMailSchedulerCronString(String mailSchedulerCronString) {
		this.mailSchedulerCronString = mailSchedulerCronString;
	}

	public String getMailSchdTimePeriod() {
		return mailSchdTimePeriod;
	}

	public void setMailSchdTimePeriod(String mailSchdTimePeriod) {
		this.mailSchdTimePeriod = mailSchdTimePeriod;
	}

	public int getMailSchdTimePeriodType() {
		return mailSchdTimePeriodType;
	}

	public void setMailSchdTimePeriodType(int mailSchdTimePeriodType) {
		this.mailSchdTimePeriodType = mailSchdTimePeriodType;
	}



}

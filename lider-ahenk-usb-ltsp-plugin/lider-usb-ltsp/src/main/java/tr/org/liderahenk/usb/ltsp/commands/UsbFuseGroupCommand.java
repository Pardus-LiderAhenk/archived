package tr.org.liderahenk.usb.ltsp.commands;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.usb.ltsp.entities.UsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.enums.StatusCode;
import tr.org.liderahenk.usb.ltsp.model.AgentUsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.plugininfo.PluginInfoImpl;

public class UsbFuseGroupCommand implements ICommand, ITaskAwareCommand {

	private static final Logger logger = LoggerFactory.getLogger(UsbFuseGroupCommand.class);

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;
	private ILDAPService ldapService;
	private IMailService mailService;

	private SimpleDateFormat sdf= new SimpleDateFormat("dd.MM.yyyy hh:mm");
	
	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> responseData = mapper.readValue(result.getResponseData(), 0,
					result.getResponseData().length, new TypeReference<HashMap<String, Object>>() {
					});
			if (responseData.get("fuse-group-results") != null) {
				logger.info("JSON:" + responseData.get("fuse-group-results").toString());
				;
				List<AgentUsbFuseGroupResult> res = mapper.readValue(responseData.get("fuse-group-results").toString(),
						new TypeReference<List<AgentUsbFuseGroupResult>>() {
						});
				for (AgentUsbFuseGroupResult r : res) {
					Object endDateParam = result.getCommandExecution().getCommand().getTask().getParameterMap()
							.get("endDate");
					Date endDate = null;
					if (endDateParam != null && !endDateParam.toString().isEmpty()) {
						endDate = cronToDate(endDateParam.toString());
					}

					UsbFuseGroupResult obj = new UsbFuseGroupResult(null, r.getUsername(),
							result.getCommandExecution().getUid(),
							StatusCode.getType(Integer.parseInt(r.getStatusCode())), new Date(), endDate);

					obj.setAgentId(result.getAgentId());
					dbService.save(obj);

					String username = r.getUsername();
					List<LdapEntry> ldapUserList = ldapService.search("uid", username,new String[] {"uid","cn","sn","mail"});
					
					if(ldapUserList!=null && !ldapUserList.isEmpty()){
						LdapEntry entry= ldapUserList.get(0);
						
						Map<String, String> attr= entry.getAttributes();
						
						if(attr !=null ){
							String mailAddr = attr.get("mail");
							if(mailAddr !=null){
								
								
								List<String> toList= new ArrayList<String>();
								toList.add(mailAddr);
								
								String subject="Lider Ahenk Merkezi Yönetim Sistemi Usb Hakları";
								String body="Yöneticiniz tarafından " +sdf.format(new Date())+ " tarihinde ";
								
								switch (obj.getStatusCode()) {
								case PRIVILEGED:
									body 	+= obj.getEndDate() != null ?  (sdf.format(obj.getEndDate()) + " tarihine kadar ") : "" ;
									body    +=  "tarafınıza usb yetkisi verilmiştir. ";
									break;

								case UNPRIVILEGED:
									body +=" usb yetkiniz alınmıştır.";
									break;
									
								
								default:
									
									break;
								}
								
								sendMail(toList,subject,body);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void sendMail(List<String> toList, String subject,String body) {
		
		
		mailService.sendMail(toList, subject, body);
		
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	@Override
	public String getCommandId() {
		return "USB_FUSE_GROUP";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setDbService(IPluginDbService dbService) {
		this.dbService = dbService;
	}

	public static Date cronToDate(String cronExpression) throws ParseException {
		String[] splittedCron = cronExpression.split("\\s+");

		String minute = "";
		if (splittedCron[0].equals("*")) {
			minute = "00";
		} else {
			minute = splittedCron[0].length() < 2 ? "0" + splittedCron[0] : splittedCron[0];
		}

		String hour = "";
		if (splittedCron[1].equals("*")) {
			hour = "00";
		} else {
			hour = splittedCron[1].length() < 2 ? "0" + splittedCron[1] : splittedCron[1];
		}

		String day = "";
		if (splittedCron[2].equals("*")) {
			day = "01";
		} else {
			day = splittedCron[2].length() < 2 ? "0" + splittedCron[2] : splittedCron[2];
		}

		String month = "";
		if (splittedCron[3].equals("*")) {
			month = "01";
		} else {
			month = (splittedCron[3].length() < 2 ? "0" + splittedCron[3] : splittedCron[3]);
		}

		String year = splittedCron.length > 5 ? splittedCron[5]
				: Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

		String dateString = (day + "/" + month + "/" + year + " " + hour + ":" + minute + ":00");

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date date = df.parse(dateString);

		return date;
	}

	public ILDAPService getLdapService() {
		return ldapService;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public IMailService getMailService() {
		return mailService;
	}

	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

}

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
package tr.org.liderahenk.lider.taskmanager.notifiers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.i18n.ILocaleService;
import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;
import tr.org.liderahenk.lider.core.api.ldap.model.LdapEntry;
import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.messaging.enums.StatusCode;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailAddressDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.utils.LiderCoreUtils;
import tr.org.liderahenk.lider.core.api.utils.StringJoinCursor;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class MailNotifier {

	private static Logger logger = LoggerFactory.getLogger(MailNotifier.class);

	private IMailService mailService;
	private IMailAddressDao mailAddressDao;
	private ICommandDao commandDao;
	private IConfigurationService configurationService;
	private IEntityFactory entityFactory;
	private ILDAPService ldapService;
	private ILocaleService localeService;

	private ScheduledThreadPoolExecutor threadExecutor;

	private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy H:m");

	public void init() {
		logger.info("Initializing mail notifier.");
		hookListener();
	}

	public void destroy() {
		logger.info("Destroying mail notifier...");
		if (threadExecutor != null) {
			try {
				threadExecutor.shutdown();
			} catch (Exception e) {
			}
		}
	}

	protected class TaskResultListener implements Runnable {
		@Override
		public void run() {
			// No mail were sent for these commands:
			List<? extends ICommand> commands = commandDao.findTaskCommandsWithMailNotification();
			if (commands != null && commands.size() > 0) {
				// We'll send only ONE mail for each task!
				for (ICommand command : commands) {
					try {
						// Build mail to_list
						List<? extends IMailAddress> mailAddressList = mailAddressDao.findByProperty(IMailAddress.class,"plugin.id", command.getTask().getPlugin().getId(), 0);
						List<String> toList = new ArrayList<String>();
						for (IMailAddress iMailAddress : mailAddressList) {
							toList.add(iMailAddress.getMailAddress());
						}

						// Get mail_subject
						String mailSubject = "";
						StringBuilder mailContent = new StringBuilder();

						int totalAgents = command.getUidList().size();
						int onlineAgents = 0, offlineAgents = 0;
						boolean hasContent = false;

						mailContent.append(command.getTask().getPlugin().getDescription()).append(" eklentisi ")
								.append(format.format(command.getCreateDate())).append(" tarihinde ")
								.append(command.getTask().getCommandClsId()).append(" görevi göndermiştir. \n")
								.append("Görev toplam ").append(totalAgents)
								.append(" adet istemci için çalıştırılmıştır. \nGörev toplam ONLINE_AGENTS adet istemciye ulaşmıştır. \nGörev toplam OFFLINE_AGENTS adet istemciye ulaşmamıştır.\nGörev sonuçlarına ilişkin detayları aşağıda inceleyebilirsiniz: \n\n");

						if (toList.size() > 0) {
							for (ICommandExecution execution : command.getCommandExecutions()) {
								if (!execution.isOnline()) {
									offlineAgents++;
									continue;
								}
								onlineAgents++;
								for (ICommandExecutionResult result : execution.getCommandExecutionResults()) {
									if (mailSubject.isEmpty() && result.getMailSubject() != null
											&& !result.getMailSubject().isEmpty()) {
										mailSubject = result.getMailSubject();
									}
									if (StatusCode.getTaskEndingStates().contains(result.getResponseCode())
											&& result.getMailContent() != null
											&& !result.getMailContent().trim().isEmpty()) {
										hasContent = true;
										mailContent.append("\nAhenk: ").append(execution.getUid()).append(", Sonuç: ")
												.append(localeService.getString(result.getResponseCode().toString()))
												.append(", Mesaj:").append(result.getMailContent());
										break;
									}
								}
							}

							// Send mail
							if (hasContent) {
								logger.debug("Sending mail notification.");
								mailSubject = "Lider Ahenk Görev Sonucu " + mailSubject;
								String body = mailContent.toString().replaceFirst("ONLINE_AGENTS", onlineAgents + "")
										.replaceFirst("OFFLINE_AGENTS", offlineAgents + "");
								logger.debug("Task mail content: {}", body);
								mailService.sendMail(toList, mailSubject, body);
							}

							// Mark command as 'sent mail'
							// So that the notifier may ignore it from now on.
							commandDao.update(entityFactory.createCommand(command, true));
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	protected class PolicyResultListener implements Runnable {
		@Override
		public void run() {
			//
			// Send mail if the applied policy is finished OR its expiration date has arrived...
			//
			logger.debug("Querying policy commands with mail notification.");
			List<? extends ICommand> commands = commandDao.findPolicyCommandsWithMailNotification();
			if (commands != null && commands.size() > 0) {
				// We'll send only ONE mail for each policy!
				for (ICommand command : commands) {
					try {
						logger.debug("Preparing mail notification.");
						String mailSubject = "Lider Ahenk Politikası";
						StringBuilder mailContent = new StringBuilder();
						final List<String> toList = new ArrayList<String>();

						boolean mailSend = false;
						boolean hasContent = false;

						
						String policyCreateDate="";
						String policyLabel="";
						
						if(command.getPolicy()!=null && command.getPolicy().getCreateDate()!=null)
						policyCreateDate=format.format(command.getPolicy().getCreateDate());
						
						if(command.getPolicy()!=null  && command.getPolicy().getLabel()!=null)
							policyLabel=command.getPolicy().getLabel();
						
						mailContent.append("Aşağıda isimleri verilen eklentilerden oluşan \"")
								.append(policyLabel).append("\" isimli politika ")
								.append(policyCreateDate)
								.append(" tarihinde aşağıda detaylarıyla belirtilen LDAP ögelerine uygulanmıştır:\n\n");
						mailContent.append("Politikayı oluşturan eklentiler:\n");

						if(command.getPolicy()!=null ){
						
							Set<? extends IProfile> profiles = command.getPolicy().getProfiles();
							List<String> plugins = new ArrayList<String>();
							for (IProfile profile : profiles) {
								Map<String, Object> profileData = profile.getProfileData();
								// Plugin description
								plugins.add(profile.getPlugin().getDescription());
								if (profileData != null) {
									Boolean mailSendParam = (Boolean) profileData.get("mailSend");
									if (mailSendParam != null && mailSendParam.booleanValue()) {
										// At least one profile wants to send mail!
										mailSend = true;
										// Add admin recipients
										List<? extends IMailAddress> mailAddressList = mailAddressDao.findByProperty(
												IMailAddress.class, "plugin.id", profile.getPlugin().getId(), 0);
										if (mailAddressList != null) {
											for (IMailAddress iMailAddress : mailAddressList) {
												toList.add(iMailAddress.getMailAddress());
											}
										}
									}
								}
							}
	
							if (mailSend) {
								logger.debug("At least one profile has mail content.");
								List<LdapEntry> targetEntries = ldapService.findTargetEntries(command.getDnList(),
										command.getDnType());
	
								mailContent.append(StringUtils.join(plugins, ","));
								// LDAP entries and their details (TCK, username
								// etc)
								mailContent.append("\n\nPolitikanın uygulandığı LDAP ögeleri:\n");
								mailContent.append(LiderCoreUtils.join(targetEntries, ",\n", new StringJoinCursor() {
									@Override
									public String getValue(Object object) {
										if (object instanceof LdapEntry) {
											LdapEntry entry = (LdapEntry) object;
											Map<String, String> attributes = entry.getAttributes();
											List<String> attrStr = new ArrayList<String>();
											if (attributes != null) {
												for (Entry<String, String> attr : attributes.entrySet()) {
													// Ignore liderPrivilege
													// attribute...
													if (attr.getKey().equalsIgnoreCase(
															configurationService.getUserLdapPrivilegeAttribute())) {
														continue;
													}
													attrStr.add(attr.getKey() + "=" + attr.getValue());
												}
												String email = attributes.get(configurationService.getLdapEmailAttribute());
												// Add personnel email to recipients
												if (email != null && !email.isEmpty()) {
													toList.add(email);
												}
											}
											return "DN: " + entry.getDistinguishedName() + " Öznitelikler: ["
													+ StringUtils.join(attrStr, ",") + "]";
										}
										return LiderCoreUtils.EMPTY;
									}
								}));
								mailContent
										.append("\n\nPolitika sonuçlarına ilişkin detayları aşağıda inceleyebilirsiniz:");
	
								if (toList.size() > 0) {
									logger.debug("Appending policy results to mail content.");
									for (ICommandExecution execution : command.getCommandExecutions()) {
										for (ICommandExecutionResult result : execution.getCommandExecutionResults()) {
											if (StatusCode.getPolicyEndingStates().contains(result.getResponseCode())
													&& result.getMailContent() != null
													&& !result.getMailContent().trim().isEmpty()) {
												try {
													hasContent = true;
													mailContent.append("\n\nUID: ").append(execution.getUid())
															.append(", Sonuç: ")
															.append(localeService
																	.getString(result.getResponseCode().toString()))
															.append(", Mesaj:\n")
															.append(replaceValues(result.getMailContent(), command));
													break;
												} catch (Exception e) {
												}
											}
										}
									}
	
									if (hasContent) {
										logger.debug("Sending mail notification.");
										String body = mailContent.toString();
										logger.debug("Task mail content: {}", body);
										mailService.sendMail(toList, mailSubject, body);
									}
	
									// Mark command as 'sent mail'
									// So that the notifier may ignore it from now
									// on.
									commandDao.update(entityFactory.createCommand(command, true));
								}
								}
							}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	private Pattern EXPRESSION = Pattern.compile("\\{(.*?)\\}");

	private String replaceValues(String message, ICommand command) {
		Matcher m = EXPRESSION.matcher(message);
		while (m.find()) {
			String expr = m.group(1);
			Object value = null;
			if ((value = LiderCoreUtils.getFieldValueIfExists(command, expr)) != null) {
				message = message.replaceAll("\\{" + expr + "\\}", value.toString());
			} else {
				message = message.replaceAll("\\{" + expr + "\\}", "");
			}
		}
		return message;
	}

	private void hookListener() {
		// Tip: DO NOT use Timer when scheduling multiple threads! Use
		// ScheduledThreadPoolExecutor instead!
		// Thread executor for task mail notification
//		if (configurationService.getMailSendOnTaskCompletion()) {
//			logger.debug("Scheduled thread for task mail notification");
//			if (threadExecutor == null) {
//				threadExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
//			}
//			threadExecutor.scheduleAtFixedRate(new TaskResultListener(), 10000,
//					configurationService.getMailCheckTaskCompletionPeriod(), TimeUnit.MILLISECONDS);
//		}
		// Thread executor for policy mail notification
		if (configurationService.getMailSendOnPolicyCompletion()) {
			logger.debug("Scheduled thread for policy mail notification");
			if (threadExecutor == null) {
				threadExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
			}
			threadExecutor.scheduleAtFixedRate(new PolicyResultListener(), 20000,
					configurationService.getMailCheckPolicyCompletionPeriod(), TimeUnit.MILLISECONDS);
		}
	}

	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	public void setMailAddressDao(IMailAddressDao mailAddressDao) {
		this.mailAddressDao = mailAddressDao;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

	public void setLdapService(ILDAPService ldapService) {
		this.ldapService = ldapService;
	}

	public void setLocaleService(ILocaleService localeService) {
		this.localeService = localeService;
	}

}

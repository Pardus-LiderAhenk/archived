package tr.org.liderahenk.service.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.messaging.IMessageFactory;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.service.entities.ServiceListItem;

public class ServiceCommand implements ICommand {

	private static Logger logger = LoggerFactory.getLogger(ServiceCommand.class);
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;

	private IPluginDbService pluginDbService;
	private IMessagingService messagingService;
	private IMessageFactory messageFactory;
	private ITaskDao taskDao;
	private ICommandDao commandDao;
	

	@Override
	public ICommandResult execute(ICommandContext context) {
		
//		logger.info("ServiceCommand executing");
//		
//		 ITaskRequest req = context.getRequest();
//		 
//		//  List<String> dnList = context.getRequest().getDnList();
//		 
//		// deleteAllCronTasks(req);
//		 
//		 Map<String, Object> parameterMap = req.getParameterMap();
//		 
//		 ObjectMapper mapper = new ObjectMapper();
//		 mapper.configure(
//				    DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//		 try {
//			List<ServiceListItem> serviceList = mapper.readValue(mapper.writeValueAsString(parameterMap.get("serviceRequestParameters")),
//						new TypeReference<List<ServiceListItem>>() {
//				});
//			
//			if(serviceList!=null && serviceList.size()>0){
//				
////				for (ServiceListItem serviceListItem : serviceList) {
////					
////					if(serviceListItem.getId()==null){
////						serviceListItem.setCreateDate(new Date());
////						serviceListItem.setOwner(req.getOwner());
////						pluginDbService.save(serviceListItem);
////					}
////					//parameterMap.put(serviceListItem.getAgentDn(),serviceListItem);
////				}
//				
////				for (int i = 0; i < dnList.size(); i++) {
////					
////					String dn=dnList.get(i);
////					
////					List<ServiceListItem> agentServiceList= new ArrayList<ServiceListItem>();
////					
////					for (ServiceListItem serviceListItem : serviceList) {
////						
////						if(dn.equals(serviceListItem.getAgentDn())){
////							agentServiceList.add(serviceListItem);
////						}
////					}
////					
////					parameterMap.put(dn,agentServiceList);
////				}
////
////				parameterMap.put("dnCheck","true");
//				
////				parameterMap.put("serviceRequestParameters", serviceList);
//			}
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//			return resultFactory.create(CommandResultStatus.ERROR, new ArrayList<String>(), this);
//		} 
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}
	
	
	private void deleteAllCronTasks(ITaskRequest req) {
		String cronExpression = req.getCronExpression();
		 
		 
		 /**
		  * delete all service cron tasks and update agents cron jobs..
		  */
		 if(cronExpression !=null){
			 
			 logger.info("Service command has cron expression Cron: "+ cronExpression);
			 Map<String, Object> propertiesMap= new HashMap<String, Object>();
			 propertiesMap.put("commandClsId", req.getCommandId());
			 propertiesMap.put("deleted", false);
			 
			List<? extends ITask> returns = taskDao.findByProperties(ITask.class, propertiesMap, null, null);
			
			if(returns!=null && returns.size()>0){
			 
				for (ITask iTask : returns) {
					
					if(iTask.getCommandClsId()!=null){
						
						if(iTask.getCronExpression()!=null){
							logger.info("Deleting all service manager tasks : Task CMD ID:  "+req.getCommandId());
							
								List<? extends tr.org.liderahenk.lider.core.api.persistence.entities.ICommand> resultList 
								= commandDao.findByProperty(tr.org.liderahenk.lider.core.api.persistence.entities.ICommand .class, "task.id", iTask.getId(), 1);
										
								if(resultList!=null && resultList.size()>0){
									
									tr.org.liderahenk.lider.core.api.persistence.entities.ICommand command = resultList.get(0);
											
									
									List<String> uidList = command.getUidList();
									
									if (uidList != null) {
										for (String uid : uidList) {
											
											logger.info("Updateting scheduler task uid:  "+uid);
											
											try {
												messagingService.sendMessage(messageFactory.createUpdateScheduledTaskMessage(uid, iTask.getId(), null));
											} catch (Exception e) {
												logger.error(e.getMessage(), e);
											}
										}                                                  
										
										taskDao.delete(iTask.getId());
									}
								}
						}
					}
				}
			}
			 
		 }
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "SERVICE_MANAGEMENT";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}
	
	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}
	
	public void setPluginInfo(IPluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public IPluginDbService getPluginDbService() {
		return pluginDbService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}

//	@Override
//	public void onTaskUpdate(ICommandExecutionResult result) {
//
//		try {
//			
//			long taskId= result.getCommandExecution().getCommand().getTask().getId();
//
//			byte[] data = result.getResponseData();
//			
//			final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
//					new TypeReference<HashMap<String, Object>>() {
//					});
//
//			ObjectMapper mapper = new ObjectMapper();
//			
//			List<ServiceListItem> services = new ObjectMapper().readValue(mapper.writeValueAsString(responseData.get("services")),
//						new TypeReference<List<ServiceListItem>>() {
//				});
//			
//			if(services!=null)
//				for (ServiceListItem serviceListItem : services) {
//					
//					List<PropertyOrder> orders = new ArrayList<PropertyOrder>();
//					orders.add(new PropertyOrder("createDate", OrderType.DESC));
//					
//					HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
//					propertiesMap.put("id", serviceListItem.getId());
//					
//					List<ServiceListItem> serviceList= pluginDbService.findByProperties(ServiceListItem.class,propertiesMap,orders, 1);
//					if(serviceList!=null && serviceList.size()>0){
//						ServiceListItem service= serviceList.get(0);
//						service.setAgentId(result.getAgentId());
//						service.setDeleted(serviceListItem.isDeleted());
//						service.setDesiredServiceStatus(serviceListItem.getDesiredServiceStatus());
//						service.setDesiredStartAuto(serviceListItem.getDesiredStartAuto());
//						service.setStartAuto(serviceListItem.getStartAuto());
//						service.setServiceStatus(serviceListItem.getServiceStatus());
//						
//						service.setTaskId(taskId);
//						
//						service.setModifyDate(new Date());
//						service.setServiceMonitoring(true);
//						pluginDbService.update(service);
//					}
//				}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public ITaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public IMessagingService getMessagingService() {
		return messagingService;
	}

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}

	public IMessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(IMessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public ICommandDao getCommandDao() {
		return commandDao;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}

package tr.org.liderahenk.packagemanager.commands;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ICommandDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.ITaskDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.packagemanager.entities.CommandExecutionStatistics;
import tr.org.liderahenk.packagemanager.entities.CommandPackageVersion;

public class GetExecutionInfoCommand implements ICommand, ITaskAwareCommand {

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IPluginDbService pluginDbService;
	private IAgentDao agentDao;
	private ICommandDao commandDao;
	private ITaskDao taskDao;
	private EntityManager entityManager;

	@Override
	public ICommandResult execute(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {

		ICommandExecutionResult res = getCommandDao().findExecutionResult(result.getId());
		byte[] data = null;
		if (res != null)
			data = res.getResponseData();
		Map<String, Object> responseData;
		try {
			responseData = new ObjectMapper().readValue(data, 0, data.length,
					new TypeReference<HashMap<String, Object>>() {
					});
			if (responseData != null && !responseData.isEmpty() && responseData.containsKey("commandExecutionInfoList")
					&& responseData.containsKey("versionList")) {
				Object object = responseData.get("commandExecutionInfoList");
				Object versionObject = responseData.get("versionList");
				ArrayList<Object> list = (ArrayList<Object>) object;
				ArrayList<Object> versionInfoList = (ArrayList<Object>) versionObject;
				for (Object oldMap : versionInfoList) {
					Map<String, String> map = (Map) oldMap;
					CommandPackageVersion verInfo = new CommandPackageVersion();
					verInfo.setAgentId(result.getAgentId());
					verInfo.setTaskId(result.getCommandExecution().getCommand().getTask().getId());
					verInfo.setCreateDate(new Date());
					verInfo.setCommand(map.get("c").toString());
					verInfo.setPackageName(map.get("p").toString());
					verInfo.setPackageVersion(map.get("v").toString());

					pluginDbService.save(verInfo);
				}

				for (Object oldMap : list) {
					Map<String, String> map = (Map) oldMap;
					CommandExecutionStatistics item = new CommandExecutionStatistics();
					item.setCommand(map.get("c").toString());
					item.setUser(map.get("u").toString());
					Float processTime = Float.parseFloat(map.get("p").toString());
					item.setProcessTime(processTime);
					String currentYearString = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy");
					item.setProcessStartDate(
							(Date) formatter.parse(map.get("s").toString() + ":00 " + currentYearString));
					item.setAgentId(result.getAgentId());
					item.setTaskId(result.getCommandExecution().getCommand().getTask().getId());
					item.setIsActive("1");
					item.setCreateDate(new Date());
					item.setCommandExecutionId(result.getCommandExecution().getId());

					Query query = entityManager.createQuery(
							"UPDATE CommandExecutionStatistics ces SET ces.isActive ='0' WHERE ces.agentId = :agentId AND ces.command = :command AND ces.user = :user AND ces.taskId <> :taskId");
					query.setParameter("agentId", item.getAgentId());
					query.setParameter("taskId", item.getTaskId());
					query.setParameter("command", item.getCommand());
					query.setParameter("user", item.getUser());
					query.executeUpdate();
					pluginDbService.save(item);
				}
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getCommandId() {
		return "GET_EXECUTION_INFO";
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

	public IAgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public ITaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(ITaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public ICommandDao getCommandDao() {
		return commandDao;
	}

	public void setCommandDao(ICommandDao commandDao) {
		this.commandDao = commandDao;
	}

}

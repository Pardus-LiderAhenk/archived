package tr.org.liderahenk.resourceusage.commands;

import java.util.ArrayList;

import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class ResourceUsageAlertCommand implements ICommand {

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IMailService mailService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "RESOURCE_INFO_ALERT";
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

	public IMailService getMailService() {
		return mailService;
	}

	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	// @SuppressWarnings("unchecked")
	// @Override
	// public void onTaskUpdate(ICommandExecutionResult result) {
	// Map<String, Object> responseData;
	// try {
	// responseData = new ObjectMapper().readValue(result.getResponseData(), 0,
	// result.getResponseData().length,
	// new TypeReference<HashMap<String, Object>>() {
	// });
	// List<String> resultList = (List<String>) responseData.get("Result");
	// for(int i = 0 ; i <= resultList.size()/2 ; i=i+2){
	// List<String> to = new ArrayList<String>();
	// to.add(resultList.get(i+1));
	// getMailService().sendMail(to, "Ahenk Makinada Limit Değerler Aşıldı!",
	// resultList.get(i));
	// }
	// } catch (JsonParseException e) {
	// e.printStackTrace();
	// } catch (JsonMappingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// public IMailService getMailService() {
	// return mailService;
	// }
	//
	// public void setMailService(IMailService mailService) {
	// this.mailService = mailService;
	// }

}

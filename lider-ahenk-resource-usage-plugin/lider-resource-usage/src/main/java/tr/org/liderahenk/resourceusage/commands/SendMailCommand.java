package tr.org.liderahenk.resourceusage.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class SendMailCommand implements ICommand {

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IMailService mailService;

	@Override
	public ICommandResult execute(ICommandContext context) {

		ITaskRequest req = context.getRequest();
		Map<String, Object> parameterMap = req.getParameterMap();

		List<String> to = new ArrayList<String>();
		to.add(parameterMap.get("to").toString());
		getMailService().sendMail(to, "Ahenk Makinada Limit Değerler Aşıldı!", parameterMap.get("body").toString());

		Map<String, Object> resultMap = new HashMap<String, Object>();

		ICommandResult commandResult = resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this,
				resultMap);

		return commandResult;
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "SEND_MAIL";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
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

}

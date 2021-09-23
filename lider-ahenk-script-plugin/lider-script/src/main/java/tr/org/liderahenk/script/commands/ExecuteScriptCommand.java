package tr.org.liderahenk.script.commands;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.script.entities.ScriptFile;

/**
 * Task handler for executing scripts.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ExecuteScriptCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(ExecuteScriptCommand.class);

	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IPluginDbService dbService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {

		ITaskRequest request = context.getRequest();
		Map<String, Object> parameterMap = request.getParameterMap();

		// Find script file
//		Long scriptId = new Long(parameterMap.get("SCRIPT_FILE_ID").toString());
//		ScriptFile script = dbService.find(ScriptFile.class, scriptId);
//		logger.info("Found script file with ID: {}", scriptId);

		// Add contents and type to parameter map
		//parameterMap.put("SCRIPT_CONTENTS", script.getContents());
		//parameterMap.put("SCRIPT_TYPE", script.getScriptType().toString());

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
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
		return "EXECUTE_SCRIPT";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(IPluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setDbService(IPluginDbService dbService) {
		this.dbService = dbService;
	}

}

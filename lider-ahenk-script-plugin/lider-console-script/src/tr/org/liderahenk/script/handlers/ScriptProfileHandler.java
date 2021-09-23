package tr.org.liderahenk.script.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;
import tr.org.liderahenk.script.constants.ScriptConstants;
import tr.org.liderahenk.script.dialogs.ScriptProfileDialog;
import tr.org.liderahenk.script.i18n.Messages;

/**
 * Profile definition handler for script plugin.
 *
 */
public class ScriptProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(ScriptProfileHandler.class);
	
	@Override
	public ProfileEditorInput getEditorInput() {
		// TODO Auto-generated method stub
		return  new ProfileEditorInput(Messages.getString("SCRIPT"), ScriptConstants.PLUGIN_NAME, 
					ScriptConstants.PLUGIN_VERSION, new ScriptProfileDialog());
	}

}

package tr.org.liderahenk.loginmanager.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.loginmanager.i18n.Messages;
import tr.org.liderahenk.loginmanager.constants.LoginManagerConstants;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class LoginManagerTaskDialog extends DefaultTaskDialog {
	
	public LoginManagerTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("END_SESSIONS");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		return null;
	}
	
	@Override
	public void validateBeforeExecution() throws ValidationException {
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		return null;
	}

	@Override
	public String getCommandId() {
		return "MANAGE";
	}

	@Override
	public String getPluginName() {
		return LoginManagerConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return LoginManagerConstants.PLUGIN_VERSION;
	}
	
}

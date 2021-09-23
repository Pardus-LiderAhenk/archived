package tr.org.liderahenk.loginmanager.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.loginmanager.constants.LoginManagerConstants;
import tr.org.liderahenk.loginmanager.dialogs.LoginManagerProfileDialog;
import tr.org.liderahenk.loginmanager.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * Profile definition handler for login-manager plugin.
 *
 */
public class LoginManagerProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(LoginManagerProfileHandler.class);

//	public Object execute(ExecutionEvent event) throws ExecutionException {
//		
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//        IWorkbenchPage page = window.getActivePage();
//        
//        try {
//			// Here we open default profile editor implementation so that all
//			// profiles can be handled by Lider Console Core.
//			// We also pass our profile dialog implementation as parameter to
//			// allow the editor use it dynamically.
//			page.openEditor(new ProfileEditorInput(Messages.getString("LoginManager"), LoginManagerConstants.PLUGIN_NAME, 
//					LoginManagerConstants.PLUGIN_VERSION, new LoginManagerProfileDialog()), 
//					LiderConstants.EDITORS.PROFILE_EDITOR);
//		} catch (PartInitException e) {
//			logger.error(e.getMessage(), e);
//		}
//
//        return null;
//	}
	
	@Override
	public ProfileEditorInput getEditorInput() {
		return new ProfileEditorInput(Messages.getString("LoginManager"), LoginManagerConstants.PLUGIN_NAME, 
			LoginManagerConstants.PLUGIN_VERSION, new LoginManagerProfileDialog());
	}

}

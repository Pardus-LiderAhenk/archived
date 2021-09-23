package tr.org.liderahenk.user.privilege.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.user.privilege.constants.UserPrivilegeConstants;
import tr.org.liderahenk.user.privilege.dialogs.UserPrivilegeProfileDialog;
import tr.org.liderahenk.user.privilege.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * Profile definition handler for user-privilege plugin.
 *
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class UserPrivilegeProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(UserPrivilegeProfileHandler.class);

//	public Object execute(ExecutionEvent event) throws ExecutionException {
//
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		IWorkbenchPage page = window.getActivePage();
//
//		try {
//			// Here we open default profile editor implementation so that all
//			// profiles can be handled by Lider Console Core.
//			// We also pass our profile dialog implementation as parameter to
//			// allow the editor use it dynamically.
//			page.openEditor(
//					new ProfileEditorInput(Messages.getString("UserPrivilege"), UserPrivilegeConstants.PLUGIN_NAME,
//							UserPrivilegeConstants.PLUGIN_VERSION, new UserPrivilegeProfileDialog()),
//					LiderConstants.EDITORS.PROFILE_EDITOR);
//		} catch (PartInitException e) {
//			logger.error(e.getMessage(), e);
//		}
//
//		return null;
//	}
	
	@Override
	public ProfileEditorInput getEditorInput() {
		// TODO Auto-generated method stub
		return new ProfileEditorInput(Messages.getString("UserPrivilege"), UserPrivilegeConstants.PLUGIN_NAME,
				UserPrivilegeConstants.PLUGIN_VERSION, new UserPrivilegeProfileDialog());
	}

}

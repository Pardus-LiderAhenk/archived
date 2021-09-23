package tr.org.liderahenk.ldaplogin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.ldaplogin.constants.LDAPLoginConstants;
import tr.org.liderahenk.ldaplogin.dialogs.LDAPLoginProfileDialog;
import tr.org.liderahenk.ldaplogin.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;

/**
 * Profile definition handler for ldap-login plugin.
 *
 */
public class LDAPLoginProfileHandler extends AbstractHandler {

	private Logger logger = LoggerFactory.getLogger(LDAPLoginProfileHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        IWorkbenchPage page = window.getActivePage();
        
        try {
			// Here we open default profile editor implementation so that all
			// profiles can be handled by Lider Console Core.
			// We also pass our profile dialog implementation as parameter to
			// allow the editor use it dynamically.
			page.openEditor(new ProfileEditorInput(Messages.getString("LDAPLogin"), LDAPLoginConstants.PLUGIN_NAME, 
					LDAPLoginConstants.PLUGIN_VERSION, new LDAPLoginProfileDialog()), 
					LiderConstants.EDITORS.PROFILE_EDITOR);
		} catch (PartInitException e) {
			logger.error(e.getMessage(), e);
		}

        return null;
	}

}

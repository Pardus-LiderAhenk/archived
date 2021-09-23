package tr.org.liderahenk.firewall.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.firewall.constants.FirewallConstants;
import tr.org.liderahenk.firewall.dialogs.FirewallProfileDialog;
import tr.org.liderahenk.firewall.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * Profile definition handler for firewall plugin.
 *
 */
public class FirewallProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(FirewallProfileHandler.class);

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
//			page.openEditor(new ProfileEditorInput(Messages.getString("Firewall"), FirewallConstants.PLUGIN_NAME, 
//					FirewallConstants.PLUGIN_VERSION, new FirewallProfileDialog()), 
//					LiderConstants.EDITORS.PROFILE_EDITOR);
//		} catch (PartInitException e) {
//			logger.error(e.getMessage(), e);
//		}
//
//        return null;
//	}
	@Override
	public ProfileEditorInput getEditorInput() {
		// TODO Auto-generated method stub
		return new ProfileEditorInput(Messages.getString("Firewall"), FirewallConstants.PLUGIN_NAME, 
				FirewallConstants.PLUGIN_VERSION, new FirewallProfileDialog());
	}

}

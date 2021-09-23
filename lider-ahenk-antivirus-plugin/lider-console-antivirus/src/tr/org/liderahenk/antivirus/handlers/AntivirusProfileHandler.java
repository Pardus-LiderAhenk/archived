package tr.org.liderahenk.antivirus.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.antivirus.constants.AntivirusConstants;
import tr.org.liderahenk.antivirus.dialogs.AntivirusProfileDialog;
import tr.org.liderahenk.antivirus.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * Profile definition handler for antivirus plugin.
 *
 */
public class AntivirusProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(AntivirusProfileHandler.class);

//	public Object execute(ExecutionEvent event) throws ExecutionException {
//		
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//        IWorkbenchPage page = window.getActivePage();
//        
//        try {
//			page.openEditor(new ProfileEditorInput(Messages.getString("Antivirus"), AntivirusConstants.PLUGIN_NAME, 
//					AntivirusConstants.PLUGIN_VERSION, new AntivirusProfileDialog()), 
//					LiderConstants.EDITORS.PROFILE_EDITOR);
//		} catch (PartInitException e) {
//			logger.error(e.getMessage(), e);
//		}
//
//        return null;
//	}
	
	@Override
	public ProfileEditorInput getEditorInput() {
		return new ProfileEditorInput(Messages.getString("Antivirus"), AntivirusConstants.PLUGIN_NAME,	AntivirusConstants.PLUGIN_VERSION, new AntivirusProfileDialog());
	}

}

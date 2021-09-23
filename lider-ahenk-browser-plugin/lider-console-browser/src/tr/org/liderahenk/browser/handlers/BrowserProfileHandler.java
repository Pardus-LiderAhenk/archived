package tr.org.liderahenk.browser.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.browser.constants.BrowserConstants;
import tr.org.liderahenk.browser.dialogs.BrowserProfileDialog;
import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class BrowserProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(BrowserProfileHandler.class);
//
//	@Override
//	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//		IWorkbenchPage page = window.getActivePage();
//
//		try {
//			page.openEditor(
//					new ProfileEditorInput(Messages.getString("BROWSER"), BrowserConstants.PLUGIN_NAME,
//							BrowserConstants.PLUGIN_VERSION, new BrowserProfileDialog()),
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
		return new ProfileEditorInput(Messages.getString("BROWSER"), BrowserConstants.PLUGIN_NAME,
				BrowserConstants.PLUGIN_VERSION, new BrowserProfileDialog());
	}
}

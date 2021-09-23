package tr.org.liderahenk.backup.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.dialogs.BackupProfileDialog;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.editorinput.ProfileEditorInput;
import tr.org.liderahenk.liderconsole.core.handlers.LiderAbstractHandler;

/**
 * Profile definition handler for backup plugin.
 *
 */
public class BackupProfileHandler extends LiderAbstractHandler {

	private Logger logger = LoggerFactory.getLogger(BackupProfileHandler.class);
//
//	public Object execute(ExecutionEvent event) throws ExecutionException {
//		
//		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
//        IWorkbenchPage page = window.getActivePage();
//        
//        String selectedProfileId=event.getParameter("selectedProfileId");
//        String action=event.getParameter("action");
//        
//        
//        try {
//			// Here we open default profile editor implementation so that all
//			// profiles can be handled by Lider Console Core.
//			// We also pass our profile dialog implementation as parameter to
//			// allow the editor use it dynamically.,
//        	
//        	
//        	if(action.equals("add")){
//        		ProfileEditorInput editorInput = new ProfileEditorInput(Messages.getString("Backup"), BackupConstants.PLUGIN_NAME, 
//						BackupConstants.PLUGIN_VERSION, new BackupProfileDialog()) ;
//				DefaultProfileDialog dialog = new DefaultProfileDialog(Display.getDefault().getActiveShell(), null,
//						editorInput);
//				dialog.create();
//				dialog.open();
//        	}
//        	else if( action.equals("update") && selectedProfileId!=null){
//        		
//    				ProfileEditorInput editorInput = new ProfileEditorInput(Messages.getString("Backup"), BackupConstants.PLUGIN_NAME, 
//    						BackupConstants.PLUGIN_VERSION, new BackupProfileDialog()) ;
//    				DefaultProfileDialog dialog = new DefaultProfileDialog(Display.getDefault().getActiveShell(), null,	editorInput,selectedProfileId);
//    				dialog.create();
//    				dialog.open();
//        		
//        	}
//        	
//        	
//        
////        	else
////			page.openEditor(new ProfileEditorInput(Messages.getString("Backup"), BackupConstants.PLUGIN_NAME, 
////					BackupConstants.PLUGIN_VERSION, new BackupProfileDialog()), 
////					LiderConstants.EDITORS.PROFILE_EDITOR);
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//
//        return null;
//	}
	
	@Override
	public ProfileEditorInput getEditorInput() {
		// TODO Auto-generated method stub
		return new ProfileEditorInput(Messages.getString("Backup"), BackupConstants.PLUGIN_NAME, 
				BackupConstants.PLUGIN_VERSION, new BackupProfileDialog()) ;
	}

}

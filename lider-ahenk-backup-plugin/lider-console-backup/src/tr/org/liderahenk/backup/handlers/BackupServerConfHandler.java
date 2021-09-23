package tr.org.liderahenk.backup.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import tr.org.liderahenk.backup.dialogs.BackupServerConfDialog;

public class BackupServerConfHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		BackupServerConfDialog dialog = new BackupServerConfDialog(window.getShell());
		dialog.create();
		dialog.open();
		return null;
	}

}

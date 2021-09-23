package tr.org.liderahenk.remote.access.handlers;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.SingleSelectionHandler;
import tr.org.liderahenk.remote.access.dialogs.RemoteAccessTaskDialog;

/**
 * Task execution handler for remote access (VNC) plugin.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class RemoteAccessTaskHandler extends SingleSelectionHandler {

	@Override
	public void executeWithDn(String dn) {
		RemoteAccessTaskDialog dialog = new RemoteAccessTaskDialog(Display.getDefault().getActiveShell(), dn);
		dialog.create();
		dialog.openWithEventBroker();
	}

}

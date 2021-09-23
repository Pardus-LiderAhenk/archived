package tr.org.liderahenk.backup.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.backup.dialogs.BackupWithMonitoringTaskDialog;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class BackupWithMonitoringTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		BackupWithMonitoringTaskDialog dialog = new BackupWithMonitoringTaskDialog(
				Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}

package tr.org.liderahenk.screenshot.handlers;

import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.screenshot.dialogs.ScreenshotTaskDialog;

/**
 * Task execution handler for screenshot plugin.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ScreenshotTaskHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		ScreenshotTaskDialog dialog = new ScreenshotTaskDialog(Display.getDefault().getActiveShell(), dnSet);
		dialog.create();
		dialog.openWithEventBroker();
	}

}

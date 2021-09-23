package tr.org.liderahenk.admigration.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.admigration.wizard.MigrationWizard;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AdMigrationHandler {

	@Execute
	public void execute(Shell shell) {
		WizardDialog wizardDialog = GUIHelper.createDialog(Display.getCurrent().getActiveShell(),
				new MigrationWizard(), new Point(800, 600));
		wizardDialog.open();
	}

}

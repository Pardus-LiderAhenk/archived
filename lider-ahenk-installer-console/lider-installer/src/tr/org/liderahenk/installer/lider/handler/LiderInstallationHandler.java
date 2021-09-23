package tr.org.liderahenk.installer.lider.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.liderahenk.installer.lider.wizard.dialogs.CheckVersionDialog;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class LiderInstallationHandler {

	@Execute
	public void execute(Shell shell) {

		// Check installer version
		CheckVersionDialog versionDialog = new CheckVersionDialog(Display.getCurrent().getActiveShell());
		versionDialog.open();

		if (versionDialog.isContinueInstallation()) {
			WizardDialog wizardDialog = GUIHelper.createDialog(Display.getCurrent().getActiveShell(),
					new LiderSetupWizard(), new Point(1175, 600));
			wizardDialog.open();
		}
	}

}

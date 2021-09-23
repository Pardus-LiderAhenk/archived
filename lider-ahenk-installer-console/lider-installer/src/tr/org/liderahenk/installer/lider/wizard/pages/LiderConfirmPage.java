package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderConfirmPage extends WizardPage implements ILiderPage {

	private LiderSetupConfig config;

	private Label lblIp;

	public LiderConfirmPage(LiderSetupConfig config) {
		super(LiderConfirmPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("5.3 " + Messages.getString("LIDER_INSTALLATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		GridData gd = new GridData();
		gd.widthHint = 200;
		gd.minimumWidth = 200;
		lblIp = GUIHelper.createLabel(container, "localhost");
		lblIp.setLayoutData(gd);

		GUIHelper.createLabel(container,
				"- " + Messages.getString(config.getLiderAccessMethod() == AccessMethod.PRIVATE_KEY
						? "ACCESSING_WITH_PRIVATE_KEY" : "ACCESSING_WITH_USERNAME_AND_PASSWORD"));

		if (config.getLiderInstallMethod() == InstallMethod.TAR_GZ) {
			GUIHelper.createLabel(container, "- " + Messages.getString("USE_TAR_GZ"));
		}

		GUIHelper.createLabel(container, Messages.getString("LIDER_WILL_BE_INSTALLED") + " "
				+ Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {
		// Set the IP info in the opening of page
		lblIp.setText("- IP: " + config.getLiderIp());
		
		((ControlNextEvent) super.getNextPage()).setNextPageEventType(
				NextPageEventType.CLICK_FROM_PREV_PAGE);
		
		// Set page complete to true, otherwise it does not go into getNextPage
		// method of LdapInstallationStatus page.
		((WizardPage) super.getNextPage()).setPageComplete(true);
		
		return super.getNextPage();
	}

}

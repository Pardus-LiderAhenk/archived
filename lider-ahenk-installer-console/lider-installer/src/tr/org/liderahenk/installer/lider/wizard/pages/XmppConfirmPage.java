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
public class XmppConfirmPage extends WizardPage implements IXmppPage {

	private LiderSetupConfig config;

	private Label lblIp;

	public XmppConfirmPage(LiderSetupConfig config) {
		super(XmppConfirmPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("4.3 " + Messages.getString("XMPP_INSTALLATION_CONFIRM"));
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
				"- " + Messages.getString(config.getXmppAccessMethod() == AccessMethod.PRIVATE_KEY
						? "ACCESSING_WITH_PRIVATE_KEY" : "ACCESSING_WITH_USERNAME_AND_PASSWORD"));

		GUIHelper.createLabel(container, "- " + Messages.getString(
				config.getXmppInstallMethod() == InstallMethod.APT_GET ? "USE_REPOSITORY" : "USE_GIVEN_DEB"));

		GUIHelper.createLabel(container,
				Messages.getString("XMPP_WILL_BE_INSTALLED") + " " + Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {
		// Set the IP info in the opening of page
		lblIp.setText("- IP: " + config.getXmppIp());

		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		// Set page complete to true, otherwise it does not go into getNextPage
		// method of XmppInstallationStatus page.
		((WizardPage) super.getNextPage()).setPageComplete(true);

		// Set global variable to false before every installation status page,
		// if it is not set and there are more than one component to be
		// installed, finish button will be enabled directly in the last
		// installation page.
		config.setInstallationFinished(false);
		
		return super.getNextPage();
	}

}

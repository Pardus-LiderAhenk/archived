package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LdapUpdateConfirmPage extends WizardPage implements ILdapPage {
	
	private LiderSetupConfig config;

	private Label lblIp;

	public LdapUpdateConfirmPage(LiderSetupConfig config) {
		super(LdapUpdateConfirmPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.3 " + Messages.getString("LDAP_UPDATE_CONFIRM"));
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
				"- " + Messages.getString(config.getLdapAccessMethod() == AccessMethod.PRIVATE_KEY
						? "ACCESSING_WITH_PRIVATE_KEY" : "ACCESSING_WITH_USERNAME_AND_PASSWORD"));

		GUIHelper.createLabel(container, Messages.getString("LDAP_WILL_BE_UPDATED") + " "
				+ Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {
		
		// Set the IP info in the opening of page
		lblIp.setText("- IP: " + config.getLdapIp());
		
		((ControlNextEvent) super.getNextPage()).setNextPageEventType(
				NextPageEventType.CLICK_FROM_PREV_PAGE);
		
		// Set page complete to true, otherwise it does not go into getNextPage
		// method of DatabaseInstallationStatus page.
		((WizardPage) super.getNextPage()).setPageComplete(true);
		
		return super.getNextPage();
	}
}

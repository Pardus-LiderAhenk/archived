package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LdapUpdateExistingPage extends WizardPage implements ILdapPage {

	private LiderSetupConfig config;

	private Button btnInstallLdap;
	
	public LdapUpdateExistingPage(LiderSetupConfig config) {
		super(LdapUpdateExistingPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.2 " + Messages.getString("LDAP_UPDATE_OR_INSTALL") + " - "
				+ Messages.getString("LDAP_UPDATE_OR_INSTALL_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite compMain = GUIHelper.createComposite(parent, 1);
		setControl(compMain);

		Composite compChild = GUIHelper.createComposite(compMain, 1);

		btnInstallLdap = GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("INSTALL_LDAP"));
		btnInstallLdap.setSelection(true);

		GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("UPDATE_LDAP"));

	}

	@Override
	public IWizardPage getNextPage() {

		if (btnInstallLdap.getSelection()) {
			return getWizard().getPage(LdapInstallMethodPage.class.getName());
		} else {
			config.setLdapUpdate(true);
			LdapUpdateConfPage updateConfPage = (LdapUpdateConfPage) getWizard().getPage(LdapUpdateConfPage.class.getName());
			updateConfPage.setInputValues();
			return updateConfPage;
		}
	}

	@Override
	public IWizardPage getPreviousPage() {

		((ControlNextEvent) super.getPreviousPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getPreviousPage();
	}

}

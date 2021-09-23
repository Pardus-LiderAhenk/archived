package tr.org.liderahenk.installer.ahenk.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class AhenkConfirmPage extends WizardPage {

	private AhenkSetupConfig config;

	private StyledText ipTextArea;
	private Label accessLabel;
	private Label installLabel;

	public AhenkConfirmPage(AhenkSetupConfig config) {
		super(AhenkConfirmPage.class.getName(), Messages.getString("AHENK_INSTALLATION"), null);
		setDescription("4.3 " + Messages.getString("AHENK_INSTALLATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// IP list label
		GUIHelper.createLabel(container, Messages.getString("MACHINES_THAT_AHENK_WILL_BE_INSTALLED"));

		// Add a text area for IP list
		ipTextArea = new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		GridData txtAreaGd = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAreaGd.heightHint = 100;

		ipTextArea.setEditable(false);
		ipTextArea.setLayoutData(txtAreaGd);
		ipTextArea.setText("localhost");

		GridData gd = new GridData();
		gd.widthHint = 500;
		gd.minimumWidth = 500;

		accessLabel = GUIHelper.createLabel(container);
		accessLabel.setLayoutData(gd);

		installLabel = GUIHelper.createLabel(container);
		installLabel.setLayoutData(gd);

		GUIHelper.createLabel(container, Messages.getString("AHENK_WILL_BE_INSTALLED") + " "
				+ Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {
		// Set the IP info in the opening of page
		String allIps = "";
		for (String ip : config.getIpList()) {
			allIps += "-" + ip + "\n";
		}
		ipTextArea.setText(allIps);

		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		return super.getNextPage();
	}

	public Label getAccessLabel() {
		return accessLabel;
	}

	public Label getInstallLabel() {
		return installLabel;
	}

}

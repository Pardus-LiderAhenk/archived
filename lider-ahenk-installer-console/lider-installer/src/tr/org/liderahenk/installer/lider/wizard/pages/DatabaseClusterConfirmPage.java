package tr.org.liderahenk.installer.lider.wizard.pages;

import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.liderahenk.installer.lider.wizard.model.DatabaseNodeInfoModel;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class DatabaseClusterConfirmPage extends WizardPage implements IDatabasePage {
	private LiderSetupConfig config;

	private StyledText ipTextArea;
	
	public DatabaseClusterConfirmPage(LiderSetupConfig config) {
		super(DatabaseClusterConfirmPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("2.3 " + Messages.getString("MARIA_DB_CLUSTER_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		// IP list label
		GUIHelper.createLabel(container, Messages.getString("MACHINES_THAT_DB_CLUSTER_WILL_BE_INSTALLED"));
		// Add a text area for IP list
		ipTextArea = new StyledText(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		GridData txtAreaGd = new GridData(SWT.FILL, SWT.FILL, true, false);
		txtAreaGd.heightHint = 100;

		ipTextArea.setEditable(false);
		ipTextArea.setLayoutData(txtAreaGd);
		
		
		GUIHelper.createLabel(container, "- " + Messages.getString("USE_DEFAULT_REPOSITORY"));

		GUIHelper.createLabel(container, Messages.getString("MARIA_DB_CLUSTER_WILL_BE_INSTALLED") + " "
				+ Messages.getString("WANT_TO_CONTINUE_PRESS_NEXT"));
	}

	@Override
	public IWizardPage getNextPage() {

		// Set the IP info in the opening of page
		String allIps = "";
		for (Iterator<Entry<Integer, DatabaseNodeInfoModel>> iterator = config.getDatabaseNodeInfoMap()
				.entrySet().iterator(); iterator.hasNext();) {

			Entry<Integer, DatabaseNodeInfoModel> entry = iterator.next();
			final DatabaseNodeInfoModel clusterNode = entry.getValue();

			allIps += "-" + clusterNode.getNodeIp() + "\n";
			
		}
		ipTextArea.setText(allIps);
		
		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);

		// Set page complete to true, otherwise it does not go into getNextPage
		// method of DatabaseInstallationStatus page.
		((WizardPage) super.getNextPage()).setPageComplete(true);

		// Set global variable to false before every installation status page,
		// if it is not set and there are more than one component to be
		// installed, finish button will be enabled directly in the last
		// installation page.
		config.setInstallationFinished(false);

		return super.getNextPage();
	}
}

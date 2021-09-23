package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.liderahenk.installer.ahenk.wizard.AhenkSetupWizard;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

/**
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class AhenkSetupLocationPage extends WizardPage {

	private AhenkSetupConfig config;

	// Widgets
	private Button btnGivenIp;
	private Button btnNetworkScan;
	private Button btnLocal;
	private Text txtRemoteIp;

	public AhenkSetupLocationPage(AhenkSetupConfig config) {
		super(AhenkSetupLocationPage.class.getName(), Messages.getString("INSTALLATION_OF_AHENK"), null);
		setDescription(Messages.getString("WHERE_WOULD_YOU_LIKE_TO_INSTALL_AHENK"));
		this.config = config;
	}

	@Override
	public void createControl(final Composite parent) {

		Composite container = GUIHelper.createComposite(parent, new GridLayout(1, false),
				new GridData(GridData.FILL, GridData.FILL, false, false));

		setControl(container);

		Composite containerForButtons = GUIHelper.createComposite(container, new GridLayout(1, false),
				new GridData(GridData.FILL, GridData.FILL, true, false));
		Composite containerForGivenIp = GUIHelper.createComposite(container, new GridLayout(2, false),
				new GridData(GridData.FILL, GridData.FILL, true, false));

		// Perform network scan
		btnNetworkScan = GUIHelper.createButton(containerForButtons, SWT.RADIO,
				Messages.getString("I_WANT_TO_CHOOSE_IP_ADDRESSES_VIA_NETWORK_SCANNING"));
		btnNetworkScan.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnNetworkScan.getSelection()) {
					config.setInstallOnGivenIps(false);
					config.setPerformNetworkScanning(true);
					config.setInstallAhenkLocally(false);
					btnGivenIp.setSelection(false);
					setIpFieldDisabled();
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnNetworkScan.setSelection(true);

		// Install locally
		btnLocal = GUIHelper.createButton(containerForButtons, SWT.RADIO,
				Messages.getString("I_WANT_TO_INSTALL_TO_COMPUTER_WHICH_I_AM_ALREADY_WORKING_ON(LOCAL)"));
		btnLocal.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnLocal.getSelection()) {
					config.setInstallOnGivenIps(false);
					config.setPerformNetworkScanning(false);
					config.setInstallAhenkLocally(true);

					btnGivenIp.setSelection(false);

					setIpFieldDisabled();
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Install to given IPs.
		btnGivenIp = GUIHelper.createButton(containerForGivenIp, SWT.RADIO,
				Messages.getString("INSTALL_TO_THESE_IP_ADDRESSES"));
		btnGivenIp.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnGivenIp.getSelection()) {
					config.setInstallOnGivenIps(true);
					config.setPerformNetworkScanning(false);
					config.setInstallAhenkLocally(false);

					btnLocal.setSelection(false);
					btnNetworkScan.setSelection(false);

					// Enable txtRemoteIp only if btnGivenIp is selected
					txtRemoteIp.setEnabled(btnGivenIp.getSelection());
				}

				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Get remote machine IP if necessary
		txtRemoteIp = GUIHelper.createText(containerForGivenIp);
		txtRemoteIp.setEnabled(false);
		txtRemoteIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageCompleteStatus();
			}
		});

	}

	// Check IP's one by one whether it is valid or not.
	// And return true if all are valid.
	private boolean isRemoteIpListValid(String txtRemoteIpValue) {

		String[] remoteIps = txtRemoteIpValue.split(", ");

		// Create IP list for config
		List<String> remoteIpList = new ArrayList<String>();

		if (remoteIps == null || remoteIps.toString().isEmpty()) {
			// TODO
		} else {
			for (int i = 0; i < remoteIps.length; i++) {
				if (!NetworkUtils.isIpValid(remoteIps[i])) {
					return false;
				}
				// Add to list
				// it will be used for IP list in config
				remoteIpList.add(remoteIps[i]);
			}
		}

		// Set the IP list in config
		config.setIpList(remoteIpList);

		getWizard().getContainer().updateButtons();
		return true;
	}

	protected void updatePageCompleteStatus() {
		// Check if entered ip list is valid.
		setPageComplete(btnLocal.getSelection() || (btnNetworkScan.getSelection())
				|| (btnGivenIp.getSelection() && isRemoteIpListValid(txtRemoteIp.getText())));
	}

	@Override
	public IWizardPage getNextPage() {
		LinkedList<IWizardPage> pagesList = ((AhenkSetupWizard) this.getWizard()).getPagesList();
		if (this.btnNetworkScan.getSelection()) {
			if (!AhenkNetworkScanPage.class.getName().equals(pagesList.get(1).getName())) {
				AhenkNetworkScanPage secondPage = new AhenkNetworkScanPage(config);
				secondPage.setWizard(getWizard());
				pagesList.add(1, secondPage);
			}
		} else if (AhenkNetworkScanPage.class.getName().equals(pagesList.get(1).getName())) {
			pagesList.remove(1);
		}

		if (btnLocal.getSelection()) {
			// Create IP list for config
			List<String> remoteIpList = new ArrayList<String>();
			remoteIpList.add("localhost");
			config.setIpList(remoteIpList);
		}

		return super.getNextPage();
	}

	private void setIpFieldDisabled() {
		txtRemoteIp.setEnabled(false);
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

}
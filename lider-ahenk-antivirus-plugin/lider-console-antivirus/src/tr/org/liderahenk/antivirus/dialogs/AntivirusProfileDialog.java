package tr.org.liderahenk.antivirus.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.antivirus.constants.AntivirusConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.antivirus.i18n.Messages;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;

public class AntivirusProfileDialog implements IProfileDialog {
	private DataBindingContext m_bindingContext;

	private static final Logger logger = LoggerFactory.getLogger(AntivirusProfileDialog.class);
	private Button chkIsRunning;
	private Combo cmbIsRunning;
	private Button chkUsbScanning;
	private Combo cmbUsbScanning;
	private Button chkExecutionFrequency;
	private Spinner spnExecutionFrequency;
	private Button chkUpdatingInterval;
	private Spinner spnUpdatingInterval;
	private Button chkScannedFolders;
	private Text txtScannedFolders;
	private Button chkScanDownloadedFiles;
	private Text txtFolderForDownloadedFiles;
	private Label lblFolderForDownloadedFiles;
	private Label lblForDescription;
	private String[] cmbContent = { "ON", "OFF" };

	@Override
	public void init() {
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Profile recieved: {}", profile != null ? profile.toString() : null);
		createInputs(parent, profile);
	}

	private void createInputs(Composite parent, Profile profile) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd.widthHint = 600;
		composite.setLayoutData(gd);

		chkIsRunning = new Button(composite, SWT.CHECK);
		chkIsRunning.setText(Messages.getString("ANTIVIRUS_STATUS"));
		chkIsRunning.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					cmbIsRunning.setEnabled(true);
				} else {
					cmbIsRunning.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		cmbIsRunning = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbIsRunning.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < cmbContent.length; i++) {
			String i18n = Messages.getString(cmbContent[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbIsRunning.add(i18n);
				cmbIsRunning.setData(i + "", cmbContent[i]);
			}
		}

		selectOption(cmbIsRunning, profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(AntivirusConstants.PARAMETERS.IS_RUNNING) : null);
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.IS_RUNNING) != null) {
			chkIsRunning.setSelection(true);
			cmbIsRunning.setEnabled(true);
		} else {
			chkIsRunning.setSelection(false);
			cmbIsRunning.setEnabled(false);
		}

		chkUsbScanning = new Button(composite, SWT.CHECK);
		chkUsbScanning.setText(Messages.getString("USB_SCANNING"));
		chkUsbScanning.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					cmbUsbScanning.setEnabled(true);
				} else {
					cmbUsbScanning.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		cmbUsbScanning = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbUsbScanning.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < cmbContent.length; i++) {
			String i18n = Messages.getString(cmbContent[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbUsbScanning.add(i18n);
				cmbUsbScanning.setData(i + "", cmbContent[i]);
			}
		}

		selectOption(cmbUsbScanning, profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(AntivirusConstants.PARAMETERS.USB_SCANNING) : null);

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.USB_SCANNING) != null) {
			chkUsbScanning.setSelection(true);
			cmbUsbScanning.setEnabled(true);
		} else {
			chkUsbScanning.setSelection(false);
			cmbUsbScanning.setEnabled(false);
		}

		chkExecutionFrequency = new Button(composite, SWT.CHECK);
		chkExecutionFrequency.setText(Messages.getString("EXECUTION_FREQUENCY"));
		chkExecutionFrequency.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					spnExecutionFrequency.setEnabled(true);
				} else {
					spnExecutionFrequency.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		spnExecutionFrequency = new Spinner(composite, SWT.BORDER);
		spnExecutionFrequency.setMinimum(1);
		spnExecutionFrequency.setMaximum(999);

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.EXECUTION_FREQUENCY) != null) {
			spnExecutionFrequency.setSelection(
					(Integer) profile.getProfileData().get(AntivirusConstants.PARAMETERS.EXECUTION_FREQUENCY));
		}

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.EXECUTION_FREQUENCY) != null) {
			chkExecutionFrequency.setSelection(true);
			spnExecutionFrequency.setEnabled(true);
		} else {
			chkExecutionFrequency.setSelection(false);
			spnExecutionFrequency.setEnabled(false);
		}

		chkUpdatingInterval = new Button(composite, SWT.CHECK);
		chkUpdatingInterval.setText(Messages.getString("UPDATING_INTERVAL"));
		chkUpdatingInterval.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					spnUpdatingInterval.setEnabled(true);
				} else {
					spnUpdatingInterval.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		spnUpdatingInterval = new Spinner(composite, SWT.BORDER);
		spnUpdatingInterval.setMinimum(1);
		spnUpdatingInterval.setMaximum(999);

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.UPDATING_INTERVAL) != null) {
			spnUpdatingInterval.setSelection(
					(Integer) profile.getProfileData().get(AntivirusConstants.PARAMETERS.UPDATING_INTERVAL));
		}

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.UPDATING_INTERVAL) != null) {
			chkUpdatingInterval.setSelection(true);
			spnUpdatingInterval.setEnabled(true);
		} else {
			chkUpdatingInterval.setSelection(false);
			spnUpdatingInterval.setEnabled(false);
		}

		chkScannedFolders = new Button(composite, SWT.CHECK);
		chkScannedFolders.setText(Messages.getString("SCANNED_FOLDERS"));
		chkScannedFolders.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					txtScannedFolders.setEnabled(true);
				} else {
					txtScannedFolders.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		txtScannedFolders = new Text(composite, SWT.BORDER);
		txtScannedFolders.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.SCANNED_FOLDERS) != null) {
			txtScannedFolders
					.setText((String) profile.getProfileData().get(AntivirusConstants.PARAMETERS.SCANNED_FOLDERS));
			chkScannedFolders.setSelection(true);
			txtScannedFolders.setEnabled(true);
		} else {
			txtScannedFolders.setEnabled(false);
		}

		chkScanDownloadedFiles = new Button(composite, SWT.CHECK);
		chkScanDownloadedFiles.setText(Messages.getString("SCAN_DOWNLOADED_FILES"));
		chkScanDownloadedFiles.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					// cmbScanDownloadedFiles.setEnabled(true);
					txtFolderForDownloadedFiles.setEnabled(true);
				} else {
					// cmbScanDownloadedFiles.setEnabled(false);
					txtFolderForDownloadedFiles.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.SCAN_DOWNLOADED_FILES) != null) {
			chkScanDownloadedFiles.setSelection(true);
			// cmbScanDownloadedFiles.setEnabled(true);
;
		} else {
			chkScanDownloadedFiles.setSelection(false);
			// cmbScanDownloadedFiles.setEnabled(false);
		}
				new Label(composite, SWT.NONE);
				
						lblFolderForDownloadedFiles = new Label(composite, SWT.NONE);
						lblFolderForDownloadedFiles.setText(Messages.getString("FILE_FOR_DOWNLOADED_FILES"));
						
								txtFolderForDownloadedFiles = new Text(composite, SWT.BORDER);
								txtFolderForDownloadedFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
								txtFolderForDownloadedFiles.setText(Messages.getString("DOWNLOAD_PATH"));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(AntivirusConstants.PARAMETERS.FOLDER_FOR_DOWNLOADED_FILES) != null) {
			txtFolderForDownloadedFiles.setText(
					(String) profile.getProfileData().get(AntivirusConstants.PARAMETERS.FOLDER_FOR_DOWNLOADED_FILES));
			txtFolderForDownloadedFiles.setEnabled(true);
		} else {
			txtFolderForDownloadedFiles.setEnabled(false);
		}
		
		lblForDescription = new Label(parent, SWT.NONE);
		lblForDescription.setText(Messages.getString("FOLDER_PATH_DESCRIPTION"));
		m_bindingContext = initDataBindings();
		
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {

		Map<String, Object> profileData = new HashMap<String, Object>();
		if (chkIsRunning.getSelection())
			profileData.put(AntivirusConstants.PARAMETERS.IS_RUNNING, cmbIsRunning.getText());
		if (chkUsbScanning.getSelection())
			profileData.put(AntivirusConstants.PARAMETERS.USB_SCANNING, cmbUsbScanning.getText());
		if (chkScannedFolders.getSelection())
			profileData.put(AntivirusConstants.PARAMETERS.SCANNED_FOLDERS, txtScannedFolders.getText());
		if (chkExecutionFrequency.getSelection())
			profileData.put(AntivirusConstants.PARAMETERS.EXECUTION_FREQUENCY, spnExecutionFrequency.getSelection());
		if (chkUpdatingInterval.getSelection())
			profileData.put(AntivirusConstants.PARAMETERS.UPDATING_INTERVAL, spnUpdatingInterval.getSelection());
		if (chkScanDownloadedFiles.getSelection()) {
			profileData.put(AntivirusConstants.PARAMETERS.SCAN_DOWNLOADED_FILES, Messages.getString(cmbContent[0]));
			profileData.put(AntivirusConstants.PARAMETERS.FOLDER_FOR_DOWNLOADED_FILES,
					txtFolderForDownloadedFiles.getText());
		}
		
		return profileData;
		
	}
		
	@Override
	public void validateBeforeSave() throws ValidationException {
		if ((chkScannedFolders.getSelection()
				&& (txtScannedFolders.getText() == null || txtScannedFolders.getText().isEmpty()))
				|| (txtFolderForDownloadedFiles.isEnabled() && (txtFolderForDownloadedFiles.getText() == null
						|| txtFolderForDownloadedFiles.getText().isEmpty()))) {
			throw new ValidationException(Messages.getString("FILL_SELECTED_FIELDS"));
		}
	}

	private boolean selectOption(Combo combo, Object value) {
		if (value == null) {
			combo.select(0);
			return false;
		}
		String[] items = combo.getItems();
		if (items == null) {
			return false;
		}
		for (int i = 0; i < items.length; i++) {
			if (items[i].equalsIgnoreCase(value.toString())) {
				combo.select(i);
				return true;
			}
		}
		combo.select(0); // select first option by default.
		return false;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}

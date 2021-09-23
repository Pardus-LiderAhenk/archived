package tr.org.liderahenk.browser.tabs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.browser.util.BrowserUtil;
import tr.org.liderahenk.browser.util.PreferenceNames;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

public class GeneralSettingsTab implements ISettingsTab {

	private Combo cmbPageMode;
	private Button btnCheckDefaultBrowser;
	private Text txtHomePage;
	private Button btnSaveFilesTo;
	private Text txtDownloadDir;
	private Button btnAlwaysAskWhereToDownload;
	private Button btnOpenNewWindow;
	private Button btnWarnOnClose;
	private Button btnWarnOnOpen;
	private Button btnLoadInBackground;
	private Button btnRestoreOnDemand;
	private Button btnEnableXPInstall;

	private final String[] pageModeArr = new String[] { "HOME_PAGE", "BLANK_PAGE", "LAST_PAGE" };
	private final String[] pageModeValueArr = new String[] { "1", "0", "3" };

	@Override
	public void createInputs(Composite tabComposite, Profile profile) throws Exception {

		Composite group = new Composite(tabComposite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Set<BrowserPreference> preferences = BrowserUtil.getPreferences(profile);

		Label lblStartup = new Label(group, SWT.NONE);
		lblStartup.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblStartup.setText(Messages.getString("STARTUP_LABEL"));
		new Label(group, SWT.NONE);

		btnCheckDefaultBrowser = new Button(group, SWT.CHECK);
		btnCheckDefaultBrowser.setText(Messages.getString("CHECK_DEFAULT_BROWSER_BTN"));
		String val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.CHECK_DEFAULT_BROWSER);
		btnCheckDefaultBrowser.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		Label lblPageMode = new Label(group, SWT.NONE);
		lblPageMode.setText(Messages.getString("PAGE_MODE_LABEL"));

		cmbPageMode = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbPageMode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.PAGE_MODE);
		for (int i = 0; i < pageModeArr.length; i++) {
			String i18n = Messages.getString(pageModeArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbPageMode.add(i18n);
				cmbPageMode.setData(i + "", pageModeValueArr[i]);
				if (val != null && val.equalsIgnoreCase(pageModeValueArr[i].toString())) {
					cmbPageMode.select(i);
				}
			}
		}
		if (cmbPageMode.getSelectionIndex() < 0) {
			cmbPageMode.select(0); // HOME
		}

		Label lblHomePage = new Label(group, SWT.NONE);
		lblHomePage.setText(Messages.getString("HOME_PAGE_LABEL"));

		txtHomePage = new Text(group, SWT.BORDER);
		txtHomePage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.HOMEPAGE);
		if (val != null) {
			txtHomePage.setText(val);
		}

		Label lblDownloads = new Label(group, SWT.NONE);
		lblDownloads.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblDownloads.setText(Messages.getString("DOWNLOADS_LABEL"));
		new Label(group, SWT.NONE);

		btnSaveFilesTo = new Button(group, SWT.RADIO);
		btnSaveFilesTo.setText(Messages.getString("SAVE_FILES_TO_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.USE_DOWNLOAD_DIR);
		btnSaveFilesTo.setSelection("true".equalsIgnoreCase(val));
		btnSaveFilesTo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRadioButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		txtDownloadDir = new Text(group, SWT.BORDER);
		txtDownloadDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.DOWNLOAD_DIR);
		if (val != null) {
			txtDownloadDir.setText(val);
		}

		btnAlwaysAskWhereToDownload = new Button(group, SWT.RADIO);
		btnAlwaysAskWhereToDownload.setText(Messages.getString("ALWAYS_ASK_WHERE_TO_DOWNLOAD_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.USE_DOWNLOAD_DIR);
		btnAlwaysAskWhereToDownload.setSelection(!"true".equalsIgnoreCase(val));
		btnAlwaysAskWhereToDownload.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRadioButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(group, SWT.NONE);

		Label lblTabs = new Label(group, SWT.NONE);
		lblTabs.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblTabs.setText(Messages.getString("TABS_LABEL"));
		new Label(group, SWT.NONE);

		btnOpenNewWindow = new Button(group, SWT.CHECK);
		btnOpenNewWindow.setText(Messages.getString("OPEN_NEW_WINDOWS_IN_TAB_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.OPEN_NEW_WINDOW);
		btnOpenNewWindow.setSelection("3".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnWarnOnClose = new Button(group, SWT.CHECK);
		btnWarnOnClose.setText(Messages.getString("WARN_ON_CLOSE_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.WARN_ON_CLOSE);
		btnWarnOnClose.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnWarnOnOpen = new Button(group, SWT.CHECK);
		btnWarnOnOpen.setText(Messages.getString("WARN_ON_OPEN_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.WARN_ON_OPEN);
		btnWarnOnOpen.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnRestoreOnDemand = new Button(group, SWT.CHECK);
		btnRestoreOnDemand.setText(Messages.getString("RESTORE_ON_DEMAND_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.RESTORE_ON_DEMAND);
		btnRestoreOnDemand.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnLoadInBackground = new Button(group, SWT.CHECK);
		btnLoadInBackground.setText(Messages.getString("LOAD_IN_BACKGROUND_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.LOAD_IN_BACKGROUND);
		btnLoadInBackground.setSelection("false".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		Label lblEnableXPInstall = new Label(group, SWT.NONE);
		lblEnableXPInstall.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblEnableXPInstall.setText(Messages.getString("ENABLE_XP_INSTALL_LABEL"));
		new Label(group, SWT.NONE);

		btnEnableXPInstall = new Button(group, SWT.CHECK);
		btnEnableXPInstall.setText(Messages.getString("ENABLE_XP_INSTALL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ENABLE_XP_INSTALL);
		btnEnableXPInstall.setSelection("false".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		handleRadioButton();

		((ScrolledComposite) tabComposite).setContent(group);
		group.setSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) tabComposite).setExpandVertical(true);
		((ScrolledComposite) tabComposite).setExpandHorizontal(true);
		((ScrolledComposite) tabComposite).setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public Set<BrowserPreference> getValues() {
		Set<BrowserPreference> preferences = new LinkedHashSet<BrowserPreference>();
		preferences.add(new BrowserPreference(PreferenceNames.CHECK_DEFAULT_BROWSER,
				btnCheckDefaultBrowser.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.PAGE_MODE, getSelectedPageMode()));
		if (txtHomePage.getText() != null && !txtHomePage.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.HOMEPAGE, txtHomePage.getText()));
		}
		preferences.add(new BrowserPreference(PreferenceNames.USE_DOWNLOAD_DIR,
				btnSaveFilesTo.getSelection() ? "true" : "false"));
		if(btnSaveFilesTo.getSelection() == true) {
			preferences.add(new BrowserPreference(PreferenceNames.USE_CUSTOM_DOWNLOAD_DIR, "2"));
		}
		else {
			preferences.add(new BrowserPreference(PreferenceNames.USE_CUSTOM_DOWNLOAD_DIR, "1"));
		}
		if (txtDownloadDir.getText() != null && !txtDownloadDir.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.DOWNLOAD_DIR, txtDownloadDir.getText()));
		}
		preferences.add(
				new BrowserPreference(PreferenceNames.OPEN_NEW_WINDOW, btnOpenNewWindow.getSelection() ? "3" : "2"));
		preferences.add(
				new BrowserPreference(PreferenceNames.WARN_ON_CLOSE, btnWarnOnClose.getSelection() ? "true" : "false"));
		preferences.add(
				new BrowserPreference(PreferenceNames.WARN_ON_OPEN, btnWarnOnOpen.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.RESTORE_ON_DEMAND,
				btnRestoreOnDemand.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.LOAD_IN_BACKGROUND,
				btnLoadInBackground.getSelection() ? "false" : "true"));
		preferences.add(new BrowserPreference(PreferenceNames.ENABLE_XP_INSTALL,
				btnEnableXPInstall.getSelection() ? "false" : "true"));
		return preferences;
	}

	private void handleRadioButton() {
		txtDownloadDir.setEnabled(btnSaveFilesTo.getSelection());
	}

	private String getSelectedPageMode() {
		int selectionIndex = cmbPageMode.getSelectionIndex();
		if (selectionIndex > -1 && cmbPageMode.getItem(selectionIndex) != null
				&& cmbPageMode.getData(selectionIndex + "") != null) {
			return cmbPageMode.getData(selectionIndex + "").toString();
		}
		return "1";
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
		if (getSelectedPageMode().equals("1") && (txtHomePage.getText() == null || txtHomePage.getText().isEmpty())) { // HOME
			throw new ValidationException(Messages.getString("FILL_HOME_PAGE"));
		}
	}

}

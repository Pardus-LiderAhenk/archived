package tr.org.liderahenk.browser.tabs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

public class ProxySettingsTab implements ISettingsTab {

	private Combo cmbProxyType;
	private Text txtHttpPort;
	private Text txtHttpProxy;
	private Text txtSslPort;
	private Text txtSslProxy;
	private Text txtFtpPort;
	private Text txtFtpProxy;
	private Text txtSocksPort;
	private Text txtSocksHost;
	private Text txtAutoProxyConfigUrl;
	private Combo cmbSocksVersion;
	private Button btnRemoteDns;
	private Button btnUseThisServerForAllProtocols;
	private Button btnDontPromptForAuth;
	private Text txtNoProxy;

	private final String[] socksTypeArr = new String[] { "4", "5" };
	private final String[] proxyTypeArr = new String[] { "NO_PROXY", "MANUAL_CONFIGURATION", "AUTOMATIC_CONFIGURATION",
			"USE_SYSTEM_SETTINGS", "AUTO_DETECT" };
	private final Integer[] proxyTypeValueArr = new Integer[] { 0, 1, 2, 3, 4 };

	@Override
	public void createInputs(Composite tabComposite, Profile profile) throws Exception {

		Composite group = new Composite(tabComposite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Set<BrowserPreference> preferences = BrowserUtil.getPreferences(profile);

		Label lblConfigureProxies = new Label(group, SWT.NONE);
		lblConfigureProxies.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblConfigureProxies.setText(Messages.getString("CONFIGURE_PROXIES_LABEL"));
		new Label(group, SWT.NONE);

		cmbProxyType = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		String val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.PROXY_TYPE);
		for (int i = 0; i < proxyTypeArr.length; i++) {
			String i18n = Messages.getString(proxyTypeArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbProxyType.add(i18n);
				cmbProxyType.setData(i18n, proxyTypeValueArr[i]);
				if (val != null && val.equals(proxyTypeValueArr[i].toString())) {
					cmbProxyType.select(i);
				}
			}
		}
		if (val == null || val.isEmpty()) {
			cmbProxyType.select(0);
		}

		// Select its option when creating other inputs (e.g. select as
		// MANUAL_CONFIGURATION if txtHttpProxy value is not empty)
		cmbProxyType.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				handleProxyTypeSelection();
			}
		});
		new Label(group, SWT.NONE);

		Label lblManuelConfiguration = new Label(group, SWT.NONE);
		lblManuelConfiguration.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblManuelConfiguration.setText(Messages.getString("MANUAL_CONFIGURATION_LABEL"));
		new Label(group, SWT.NONE);

		// HTTP
		Label lblHttpProxy = new Label(group, SWT.NONE);
		lblHttpProxy.setText(Messages.getString("HTTP_PROXY_IP"));

		Label lblHttpPort = new Label(group, SWT.NONE);
		lblHttpPort.setText(Messages.getString("HTTP_PROXY_PORT"));

		txtHttpProxy = new Text(group, SWT.BORDER);
		GridData gdTxtHttpProxy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtHttpProxy.widthHint = 165;
		txtHttpProxy.setLayoutData(gdTxtHttpProxy);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.HTTP_PROXY);
		if (val != null) {
			txtHttpProxy.setText(val);
		}

		txtHttpPort = new Text(group, SWT.BORDER);
		GridData gdTxtHttpPort = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtHttpPort.widthHint = 70;
		txtHttpPort.setLayoutData(gdTxtHttpPort);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.HTTP_PORT);
		if (val != null) {
			txtHttpPort.setText(val);
		}

		btnUseThisServerForAllProtocols = new Button(group, SWT.CHECK);
		btnUseThisServerForAllProtocols.setText(Messages.getString("USE_THIS_SERVER_FOR_ALL_PROTOCOLS_BTN"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.USE_THIS_SERVER_FOR_ALL_PROTOCOLS);
		btnUseThisServerForAllProtocols.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		// SSL
		Label lblSslProxy = new Label(group, SWT.NONE);
		lblSslProxy.setText(Messages.getString("SSL_PROXY_IP"));

		Label lblSslPort = new Label(group, SWT.NONE);
		lblSslPort.setText(Messages.getString("SSL_PROXY_PORT"));

		txtSslProxy = new Text(group, SWT.BORDER);
		GridData gdTxtSslProxy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtSslProxy.widthHint = 165;
		txtSslProxy.setLayoutData(gdTxtSslProxy);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SSL_PROXY);
		if (val != null) {
			txtSslProxy.setText(val);
		}

		txtSslPort = new Text(group, SWT.BORDER);
		GridData gdTxtSslPort = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtSslPort.widthHint = 70;
		txtSslPort.setLayoutData(gdTxtSslPort);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SSL_PORT);
		if (val != null) {
			txtSslPort.setText(val);
		}

		// FTP
		Label lblFtpProxy = new Label(group, SWT.NONE);
		lblFtpProxy.setText(Messages.getString("FTP_PROXY_IP"));

		Label lblFtpPort = new Label(group, SWT.NONE);
		lblFtpPort.setText(Messages.getString("FTP_PROXY_PORT"));

		txtFtpProxy = new Text(group, SWT.BORDER);
		GridData gdTxtFtpProxy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtFtpProxy.widthHint = 165;
		txtFtpProxy.setLayoutData(gdTxtFtpProxy);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.FTP_PROXY);
		if (val != null) {
			txtFtpProxy.setText(val);
		}

		txtFtpPort = new Text(group, SWT.BORDER);
		GridData gdTxtFtpPort = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtFtpPort.widthHint = 70;
		txtFtpPort.setLayoutData(gdTxtFtpPort);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.FTP_PORT);
		if (val != null) {
			txtFtpPort.setText(val);
		}

		// Socks
		Label lblSocksProxy = new Label(group, SWT.NONE);
		lblSocksProxy.setText(Messages.getString("SOCKS_PROXY_IP"));

		Label lblSocksPort = new Label(group, SWT.NONE);
		lblSocksPort.setText(Messages.getString("SOCKS_PROXY_PORT"));

		txtSocksHost = new Text(group, SWT.BORDER);
		GridData gdTxtSocksProxy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtSocksProxy.widthHint = 165;
		txtSocksHost.setLayoutData(gdTxtSocksProxy);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SOCKS_PROXY);
		if (val != null) {
			txtSocksHost.setText(val);
		}

		txtSocksPort = new Text(group, SWT.BORDER);
		GridData gdTxtSocksPort = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtSocksPort.widthHint = 70;
		txtSocksPort.setLayoutData(gdTxtSocksPort);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SOCKS_PORT);
		if (val != null) {
			txtSocksPort.setText(val);
		}

		Label lblSocksType = new Label(group, SWT.NONE);
		lblSocksType.setText(Messages.getString("SOCKS_TYPE"));
		new Label(group, SWT.NONE);

		cmbSocksVersion = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbSocksVersion.setItems(socksTypeArr);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SOCKS_VERSION);
		cmbSocksVersion.select(val != null && val.equals("4") ? 0 : 1);

		btnRemoteDns = new Button(group, SWT.CHECK);
		btnRemoteDns.setText(Messages.getString("REMOTE_DNS_BTN"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.REMOTE_DNS);
		btnRemoteDns.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);

		// No Proxy
		Label lblNoProxy = new Label(group, SWT.NONE);
		lblNoProxy.setText(Messages.getString("NO_PROXY"));
		new Label(group, SWT.NONE);

		txtNoProxy = new Text(group, SWT.MULTI | SWT.BORDER);
		GridData gdTxtNoProxy = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTxtNoProxy.widthHint = 165;
		gdTxtNoProxy.heightHint = 100;
		txtNoProxy.setLayoutData(gdTxtNoProxy);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.NO_PROXY_ON);
		if (val != null) {
			txtNoProxy.setText(val);
		} else {
			txtNoProxy.setText("localhost, 127.0.0.1");
		}
		new Label(group, SWT.NONE);

		// Automatic proxy config URL
		Label lblAutoProxyConfigUrl = new Label(group, SWT.NONE);
		lblAutoProxyConfigUrl.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblAutoProxyConfigUrl.setText(Messages.getString("AUTO_PROXY_CONFIG_URL"));
		new Label(group, SWT.NONE);

		txtAutoProxyConfigUrl = new Text(group, SWT.BORDER);
		GridData gdtxtAutoProxyConfigUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdtxtAutoProxyConfigUrl.widthHint = 165;
		txtAutoProxyConfigUrl.setLayoutData(gdtxtAutoProxyConfigUrl);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.AUTO_PROXY_CONFIG_URL);
		if (val != null) {
			txtAutoProxyConfigUrl.setText(val);
		}
		new Label(group, SWT.NONE);

		// Do not prompt for authentication if password is saved
		btnDontPromptForAuth = new Button(group, SWT.CHECK);
		btnDontPromptForAuth.setText(Messages.getString("DONT_PROMPT_FOR_AUTH_BTN"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.DONT_PROMPT_FOR_AUTH);
		btnDontPromptForAuth.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);

		handleProxyTypeSelection();

		((ScrolledComposite) tabComposite).setContent(group);
		group.setSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) tabComposite).setExpandVertical(true);
		((ScrolledComposite) tabComposite).setExpandHorizontal(true);
		((ScrolledComposite) tabComposite).setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public Set<BrowserPreference> getValues() {
		Set<BrowserPreference> preferences = new LinkedHashSet<BrowserPreference>();
		preferences.add(new BrowserPreference(PreferenceNames.PROXY_TYPE, getSelectedProxyType()));
		if (txtHttpProxy.getText() != null && !txtHttpProxy.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.HTTP_PROXY, txtHttpProxy.getText()));
		}
		if (txtHttpPort.getText() != null && !txtHttpPort.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.HTTP_PORT, txtHttpPort.getText()));
		}
		preferences.add(new BrowserPreference(PreferenceNames.USE_THIS_SERVER_FOR_ALL_PROTOCOLS,
				btnUseThisServerForAllProtocols.getSelection() ? "true" : "false"));
		if (txtSslProxy.getText() != null && !txtSslProxy.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.SSL_PROXY, txtSslProxy.getText()));
		}
		if (txtSslPort.getText() != null && !txtSslPort.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.SSL_PORT, txtSslPort.getText()));
		}
		if (txtFtpProxy.getText() != null && !txtFtpProxy.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.FTP_PROXY, txtFtpProxy.getText()));
		}
		if (txtFtpPort.getText() != null && !txtFtpPort.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.FTP_PORT, txtFtpPort.getText()));
		}
		if (txtSocksHost.getText() != null && !txtSocksHost.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.SOCKS_PROXY, txtSocksHost.getText()));
		}
		if (txtSocksPort.getText() != null && !txtSocksPort.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.SOCKS_PORT, txtSocksPort.getText()));
		}
		if (cmbSocksVersion.getSelectionIndex() > -1) {
			preferences.add(new BrowserPreference(PreferenceNames.SOCKS_VERSION,
					cmbSocksVersion.getItem(cmbSocksVersion.getSelectionIndex())));
		}
		preferences
				.add(new BrowserPreference(PreferenceNames.REMOTE_DNS, btnRemoteDns.getSelection() ? "true" : "false"));
		if (txtNoProxy.getText() != null && !txtNoProxy.getText().isEmpty()) {
			preferences.add(new BrowserPreference(PreferenceNames.NO_PROXY_ON, txtNoProxy.getText()));
		}
		if (txtAutoProxyConfigUrl.getText() != null && !txtAutoProxyConfigUrl.getText().isEmpty()) {
			preferences
					.add(new BrowserPreference(PreferenceNames.AUTO_PROXY_CONFIG_URL, txtAutoProxyConfigUrl.getText()));
		}
		preferences.add(new BrowserPreference(PreferenceNames.DONT_PROMPT_FOR_AUTH,
				btnDontPromptForAuth.getSelection() ? "true" : "false"));
		return preferences;
	}

	private void handleProxyTypeSelection() {
		if (cmbProxyType.getSelectionIndex() > -1 && cmbProxyType.getSelectionIndex() < proxyTypeArr.length) {
			if (proxyTypeArr[cmbProxyType.getSelectionIndex()].equals("NO_PROXY")) {
				// Disable all inputs
				txtHttpProxy.setEnabled(false);
				txtHttpPort.setEnabled(false);
				btnUseThisServerForAllProtocols.setEnabled(false);
				txtSslProxy.setEnabled(false);
				txtSslPort.setEnabled(false);
				txtFtpProxy.setEnabled(false);
				txtFtpPort.setEnabled(false);
				txtSocksHost.setEnabled(false);
				txtSocksPort.setEnabled(false);
				cmbSocksVersion.setEnabled(false);
				btnRemoteDns.setEnabled(false);
				txtNoProxy.setEnabled(false);
				txtAutoProxyConfigUrl.setEnabled(false);
				btnDontPromptForAuth.setEnabled(false);
			} else if (proxyTypeArr[cmbProxyType.getSelectionIndex()].equals("AUTO_DETECT")) {
				// Enable only 'Remote DNS' and 'Do not prompt for
				// authentication if password is saved'
				txtHttpProxy.setEnabled(false);
				txtHttpPort.setEnabled(false);
				btnUseThisServerForAllProtocols.setEnabled(false);
				txtSslProxy.setEnabled(false);
				txtSslPort.setEnabled(false);
				txtFtpProxy.setEnabled(false);
				txtFtpPort.setEnabled(false);
				txtSocksHost.setEnabled(false);
				txtSocksPort.setEnabled(false);
				cmbSocksVersion.setEnabled(false);
				btnRemoteDns.setEnabled(true);
				txtNoProxy.setEnabled(false);
				txtAutoProxyConfigUrl.setEnabled(false);
				btnDontPromptForAuth.setEnabled(true);
			} else if (proxyTypeArr[cmbProxyType.getSelectionIndex()].equals("USE_SYSTEM_SETTINGS")) {
				// Enable only 'Remote DNS' and 'Do not prompt for
				// authentication if password is saved'
				txtHttpProxy.setEnabled(false);
				txtHttpPort.setEnabled(false);
				btnUseThisServerForAllProtocols.setEnabled(false);
				txtSslProxy.setEnabled(false);
				txtSslPort.setEnabled(false);
				txtFtpProxy.setEnabled(false);
				txtFtpPort.setEnabled(false);
				txtSocksHost.setEnabled(false);
				txtSocksPort.setEnabled(false);
				cmbSocksVersion.setEnabled(false);
				btnRemoteDns.setEnabled(true);
				txtNoProxy.setEnabled(false);
				txtAutoProxyConfigUrl.setEnabled(false);
				btnDontPromptForAuth.setEnabled(true);
			} else if (proxyTypeArr[cmbProxyType.getSelectionIndex()].equals("MANUAL_CONFIGURATION")) {
				// Enable all except for 'Automatic proxy config URL'
				txtHttpProxy.setEnabled(true);
				txtHttpPort.setEnabled(true);
				btnUseThisServerForAllProtocols.setEnabled(true);
				txtSslProxy.setEnabled(true);
				txtSslPort.setEnabled(true);
				txtFtpProxy.setEnabled(true);
				txtFtpPort.setEnabled(true);
				txtSocksHost.setEnabled(true);
				txtSocksPort.setEnabled(true);
				cmbSocksVersion.setEnabled(true);
				btnRemoteDns.setEnabled(true);
				txtNoProxy.setEnabled(true);
				txtAutoProxyConfigUrl.setEnabled(false);
				btnDontPromptForAuth.setEnabled(true);
			} else if (proxyTypeArr[cmbProxyType.getSelectionIndex()].equals("AUTOMATIC_CONFIGURATION")) {
				// Enable only 'Remote DNS' and 'Do not prompt for
				// authentication if password is saved' and 'Auto proxy
				// configuration URL'
				txtHttpProxy.setEnabled(false);
				txtHttpPort.setEnabled(false);
				btnUseThisServerForAllProtocols.setEnabled(false);
				txtSslProxy.setEnabled(false);
				txtSslPort.setEnabled(false);
				txtFtpProxy.setEnabled(false);
				txtFtpPort.setEnabled(false);
				txtSocksHost.setEnabled(false);
				txtSocksPort.setEnabled(false);
				cmbSocksVersion.setEnabled(false);
				btnRemoteDns.setEnabled(true);
				txtNoProxy.setEnabled(false);
				txtAutoProxyConfigUrl.setEnabled(true);
				btnDontPromptForAuth.setEnabled(true);
			} else {
				txtHttpProxy.setEnabled(true);
				txtHttpPort.setEnabled(true);
				btnUseThisServerForAllProtocols.setEnabled(true);
				txtSslProxy.setEnabled(true);
				txtSslPort.setEnabled(true);
				txtFtpProxy.setEnabled(true);
				txtFtpPort.setEnabled(true);
				txtSocksHost.setEnabled(true);
				txtSocksPort.setEnabled(true);
				cmbSocksVersion.setEnabled(true);
				btnRemoteDns.setEnabled(true);
				txtNoProxy.setEnabled(true);
				txtAutoProxyConfigUrl.setEnabled(true);
				btnDontPromptForAuth.setEnabled(true);
			}
		} else {
			txtHttpProxy.setEnabled(true);
			txtHttpPort.setEnabled(true);
			btnUseThisServerForAllProtocols.setEnabled(true);
			txtSslProxy.setEnabled(true);
			txtSslPort.setEnabled(true);
			txtFtpProxy.setEnabled(true);
			txtFtpPort.setEnabled(true);
			txtSocksHost.setEnabled(true);
			txtSocksPort.setEnabled(true);
			cmbSocksVersion.setEnabled(true);
			btnRemoteDns.setEnabled(true);
			txtNoProxy.setEnabled(true);
			txtAutoProxyConfigUrl.setEnabled(true);
			btnDontPromptForAuth.setEnabled(true);
		}
	}

	private String getSelectedProxyType() {
		int selectionIndex = cmbProxyType.getSelectionIndex();
		if (selectionIndex > -1 && cmbProxyType.getItem(selectionIndex) != null
				&& cmbProxyType.getData(cmbProxyType.getItem(selectionIndex)) != null) {
			return cmbProxyType.getData(cmbProxyType.getItem(selectionIndex)).toString();
		}
		return "0";
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
	}

}

package tr.org.liderahenk.browser.tabs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.browser.util.BrowserUtil;
import tr.org.liderahenk.browser.util.PreferenceNames;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

public class PrivacySettingsTab implements ISettingsTab {

	private Combo cmbAcceptThirdPartyCookies;
	private Combo cmbKeepCookiesUntil;
	private Button btnIDontWantToBeTracked;
	private Button btnRememberBrowsingDownloadHistory;
	private Button btnRememberSearchFormHistory;
	private Button btnAcceptCookiesFromSites;
	private Button btnClearHistoryWhenFirefoxCloses;
	private Button btnSuggestHistory;
	private Button btnSuggestBookmarks;
	private Button btnSuggestOpenTabs;

	private final String[] acceptThirdPartyCookiesArr = new String[] { "ALWAYS", "FROM_VISITED", "NEVER" };
	private final Integer[] acceptThirdPartyCookiesValueArr = new Integer[] { 0, 3, 2 };
	private final String[] keepCookiesUntilArr = new String[] { "THEY_EXPIRE", "I_CLOSE_FIREFOX"};
	private final Integer[] keepCookiesUntilValueArr = new Integer[] { 0, 2};

	@Override
	public void createInputs(Composite tabComposite, Profile profile) throws Exception {

		Composite group = new Composite(tabComposite, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Set<BrowserPreference> preferences = BrowserUtil.getPreferences(profile);

		// Tracking
		Label lblTracking = new Label(group, SWT.NONE);
		lblTracking.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblTracking.setText(Messages.getString("TRACKING_LABEL"));
		new Label(group, SWT.NONE);

		btnIDontWantToBeTracked = new Button(group, SWT.CHECK);
		btnIDontWantToBeTracked.setText(Messages.getString("I_DONT_WANT_TO_BE_TRACKED_BTN"));
		btnIDontWantToBeTracked.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		String val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.I_DONT_WANT_TO_BE_TRACKED);
		btnIDontWantToBeTracked.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		// History
		Label lblHistory = new Label(group, SWT.NONE);
		lblHistory.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblHistory.setText(Messages.getString("HISTORY_LABEL"));
		new Label(group, SWT.NONE);

		btnRememberBrowsingDownloadHistory = new Button(group, SWT.CHECK);
		btnRememberBrowsingDownloadHistory.setText(Messages.getString("REMEMBER_BROWSING_DOWNLOAD_HISTORY_BTN"));
		btnRememberBrowsingDownloadHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.REMEMBER_BROWSING_DOWNLOAD_HISTORY);
		btnRememberBrowsingDownloadHistory.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		btnRememberSearchFormHistory = new Button(group, SWT.CHECK);
		btnRememberSearchFormHistory.setText(Messages.getString("REMEMBER_SEARCH_FORM_HISTORY_BTN"));
		btnRememberSearchFormHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.REMEMBER_SEARCH_FORM_HISTORY);
		btnRememberSearchFormHistory.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		btnAcceptCookiesFromSites = new Button(group, SWT.CHECK);
		btnAcceptCookiesFromSites.setText(Messages.getString("ACCEPT_COOKIES_FROM_SITES_BTN"));
		btnAcceptCookiesFromSites.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ACCEPT_COOKIES_FROM_SITES);
		btnAcceptCookiesFromSites.setSelection(val == null || val.isEmpty() || "2".equals(val) ? false : true);
		btnAcceptCookiesFromSites.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCookieSelection();
			}
		});
		new Label(group, SWT.NONE);

		Label lblAcceptThirdPartyCookies = new Label(group, SWT.NONE);
		lblAcceptThirdPartyCookies.setText(Messages.getString("ACCEPT_THIRD_PARTY_COOKIES_LABEL"));

		cmbAcceptThirdPartyCookies = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ACCEPT_COOKIES_FROM_SITES); // Uses
																										// same
																										// preference
																										// with
																										// btnAcceptCookiesFromSites
		for (int i = 0; i < acceptThirdPartyCookiesArr.length; i++) {
			String i18n = Messages.getString(acceptThirdPartyCookiesArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbAcceptThirdPartyCookies.add(i18n);
				cmbAcceptThirdPartyCookies.setData(i + "", acceptThirdPartyCookiesValueArr[i]);
				if (val != null && val.equals(acceptThirdPartyCookiesValueArr[i].toString())) {
					cmbAcceptThirdPartyCookies.select(i);
				}
			}
		}
		if (val == null || val.isEmpty()) { // Default value is: NEVER
			cmbAcceptThirdPartyCookies.select(2);
		}

		Label lblKeepCookiesUntil = new Label(group, SWT.NONE);
		lblKeepCookiesUntil.setText(Messages.getString("KEEP_COOKIES_UNTIL_LABEL"));

		cmbKeepCookiesUntil = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.KEEP_COOKIES_UNTIL);
		for (int i = 0; i < keepCookiesUntilArr.length; i++) {
			String i18n = Messages.getString(keepCookiesUntilArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbKeepCookiesUntil.add(i18n);
				cmbKeepCookiesUntil.setData(i + "", keepCookiesUntilValueArr[i]);
				if (val != null && val.equals(keepCookiesUntilValueArr[i].toString())) {
					cmbKeepCookiesUntil.select(i);
				}
			}
		}
		if (val == null || val.isEmpty()) { // Default value is: THEY_EXPIRE
			cmbKeepCookiesUntil.select(0);
		}

		btnClearHistoryWhenFirefoxCloses = new Button(group, SWT.CHECK);
		btnClearHistoryWhenFirefoxCloses.setText(Messages.getString("CLEAR_HISTORY_ON_CLOSE_BTN"));
		btnClearHistoryWhenFirefoxCloses.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.CLEAR_HISTORY_ON_CLOSE);
		btnClearHistoryWhenFirefoxCloses.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		Label lblLocationBar = new Label(group, SWT.NONE);
		lblLocationBar.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblLocationBar.setText(Messages.getString("LOCATION_BAR_LABEL"));
		new Label(group, SWT.NONE);

		// Location Bar
		Label lblLocationBarInfo = new Label(group, SWT.NONE);
		lblLocationBarInfo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 15, 1));
		lblLocationBarInfo.setText(Messages.getString("LOCATION_BAR_INFO_LABEL"));

		btnSuggestHistory = new Button(group, SWT.CHECK);
		btnSuggestHistory.setText(Messages.getString("SUGGEST_HISTORY_BTN"));
		btnSuggestHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SUGGEST_HISTORY);
		btnSuggestHistory.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		btnSuggestBookmarks = new Button(group, SWT.CHECK);
		btnSuggestBookmarks.setText(Messages.getString("SUGGEST_BOOKMARKS_BTN"));
		btnSuggestBookmarks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SUGGEST_BOOKMARKS);
		btnSuggestBookmarks.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		btnSuggestOpenTabs = new Button(group, SWT.CHECK);
		btnSuggestOpenTabs.setText(Messages.getString("SUGGEST_OPEN_TABS_BTN"));
		btnSuggestOpenTabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.SUGGEST_OPEN_TABS);
		btnSuggestOpenTabs.setSelection(val != null && val.equalsIgnoreCase("true") ? true : false);
		new Label(group, SWT.NONE);

		handleCookieSelection();

		((ScrolledComposite) tabComposite).setContent(group);
		group.setSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) tabComposite).setExpandVertical(true);
		((ScrolledComposite) tabComposite).setExpandHorizontal(true);
		((ScrolledComposite) tabComposite).setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public Set<BrowserPreference> getValues() {
		Set<BrowserPreference> preferences = new LinkedHashSet<BrowserPreference>();
		preferences.add(new BrowserPreference(PreferenceNames.I_DONT_WANT_TO_BE_TRACKED,
				btnIDontWantToBeTracked.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.REMEMBER_BROWSING_DOWNLOAD_HISTORY,
				btnRememberBrowsingDownloadHistory.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.REMEMBER_SEARCH_FORM_HISTORY,
				btnRememberSearchFormHistory.getSelection() ? "true" : "false"));
		preferences
				.add(new BrowserPreference(PreferenceNames.ACCEPT_COOKIES_FROM_SITES, getSelectedCookieAcceptPolicy()));
		preferences.add(new BrowserPreference(PreferenceNames.CLEAR_HISTORY_ON_CLOSE,
				btnClearHistoryWhenFirefoxCloses.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.SUGGEST_HISTORY,
				btnSuggestHistory.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.SUGGEST_BOOKMARKS,
				btnSuggestBookmarks.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.SUGGEST_OPEN_TABS,
				btnSuggestOpenTabs.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.KEEP_COOKIES_UNTIL, getSelectedCookieLifetimePolicy()));
		return preferences;
	}

	private void handleCookieSelection() {
		if (btnAcceptCookiesFromSites.getSelection()) {
			cmbAcceptThirdPartyCookies.setEnabled(true);
			cmbKeepCookiesUntil.setEnabled(true);
		} else {
			// Reset inputs to default values
			cmbAcceptThirdPartyCookies.select(2); // Never
			cmbKeepCookiesUntil.select(0); // Until they expire
			cmbAcceptThirdPartyCookies.setEnabled(false);
			cmbKeepCookiesUntil.setEnabled(false);
		}
	}

	private String getSelectedCookieAcceptPolicy() {
		int selectionIndex = cmbAcceptThirdPartyCookies.getSelectionIndex();
		if (selectionIndex > -1 && cmbAcceptThirdPartyCookies.getItem(selectionIndex) != null
				&& cmbAcceptThirdPartyCookies.getData(selectionIndex + "") != null) {
			return cmbAcceptThirdPartyCookies.getData(selectionIndex + "").toString();
		}
		return "2";
	}

	private String getSelectedCookieLifetimePolicy() {
		int selectionIndex = cmbKeepCookiesUntil.getSelectionIndex();
		if (selectionIndex > -1 && cmbKeepCookiesUntil.getItem(selectionIndex) != null
				&& cmbKeepCookiesUntil.getData(selectionIndex + "") != null) {
			return cmbKeepCookiesUntil.getData(selectionIndex + "").toString();
		}
		return "0";
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
	}

}

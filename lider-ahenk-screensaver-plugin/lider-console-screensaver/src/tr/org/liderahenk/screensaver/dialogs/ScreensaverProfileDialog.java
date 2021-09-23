package tr.org.liderahenk.screensaver.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.screensaver.constants.ScreensaverConstants;
import tr.org.liderahenk.screensaver.i18n.Messages;
import tr.org.liderahenk.screensaver.utils.ScreensaverUtils;

/**
 * Profile definition dialog for Screensaver plugin.
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class ScreensaverProfileDialog implements IProfileDialog {

	private static final Logger logger = LoggerFactory.getLogger(ScreensaverProfileDialog.class);

	// Widgets
	private TabFolder tabFolder;

	private Combo cmbDisplay;

	private Label lblScreen;
	private Label lblBlankMinute;
	private Label lblChange;
	private Label lblCycleMinute;
	private Label lblLockMinute;

	private Spinner spnScreenTime;
	private Spinner spnChangeTime;
	private Spinner spnLockTime;
	private Spinner spnStandby;
	private Spinner spnSuspend;
	private Spinner spnOff;
	private Spinner spnFading;

	private Button btnCheckLock;
	private Button btnCheckGrabImage;
	private Button btnCheckGrabVideo;
	private Button btnCheckPowerManagement;
	private Button btnCheckPowerOff;
	private Button btnCheckFadeToBlack;
	private Button btnCheckFadeFromBlack;
	private Button btnCheckInstallColormap;
	private Button[] btnTextChoices;

	private Text txtText;
	private Text txtURL;

	// Combo values
	private final String[] modeArr = new String[] { "OFF", "BLANK", "ONLY_ONE", "RANDOM", "RANDOM_SAME" };
	private final String[] modeValueArr = new String[] { "off", "blank", "only_one", "random", "random_same" };

	@Override
	public void init() {
	}

	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Profile recieved: {}", profile != null ? profile.toString() : null);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		tabFolder = new TabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		createDisplayModesTab(profile);
		createImageManipulationTab(profile);
		createPowerManagementTab(profile);
		createTextManipulationTab(profile);
		createFadingTab(profile);
	}

	public void createDisplayModesTab(Profile profile) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("MODE"));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 4, 3));

		Label lblDisplayMode = new Label(group, SWT.NONE);
		lblDisplayMode.setText(Messages.getString("DISPLAYING_MODE"));

		cmbDisplay = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		for (int i = 0; i < modeArr.length; i++) {
			String i18n = Messages.getString(modeArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbDisplay.add(i18n);
				cmbDisplay.setData(i + "", modeValueArr[i]);
			}
		}
		cmbDisplay.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (cmbDisplay.getSelectionIndex() == 0) {

					lblScreen.setEnabled(false);
					spnScreenTime.setEnabled(false);
					lblBlankMinute.setEnabled(false);
					lblChange.setEnabled(false);
					spnChangeTime.setEnabled(false);
					lblCycleMinute.setEnabled(false);
					btnCheckLock.setSelection(false);
					btnCheckLock.setEnabled(false);
					spnLockTime.setEnabled(false);
					lblLockMinute.setEnabled(false);
				} else {

					lblScreen.setEnabled(true);
					spnScreenTime.setEnabled(true);
					lblBlankMinute.setEnabled(true);
					lblChange.setEnabled(true);
					spnChangeTime.setEnabled(true);
					lblCycleMinute.setEnabled(true);
					btnCheckLock.setEnabled(true);
					lblLockMinute.setEnabled(true);
				}
			}
		});
		selectOption(cmbDisplay, profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(ScreensaverConstants.PARAMETERS.MODES) : null);

		lblScreen = new Label(group, SWT.NONE);
		lblScreen.setText(Messages.getString("BLACK_SCREEN"));

		spnScreenTime = new Spinner(group, SWT.BORDER);
		spnScreenTime.setMinimum(ScreensaverConstants.MIN_VALUE + 1);
		spnScreenTime.setIncrement(1);
		spnScreenTime.setMaximum(ScreensaverConstants.MAX_VALUE);
		spnScreenTime.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		String minute = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.BLANK_AFTER) : "10");
		if (!minute.equals("")) {
			spnScreenTime.setSelection(Integer.parseInt(minute));
		}

		lblBlankMinute = new Label(group, SWT.NONE);
		lblBlankMinute.setText(Messages.getString("MINUTE"));

		lblChange = new Label(group, SWT.NONE);
		lblChange.setText(Messages.getString("CHANGE"));

		spnChangeTime = new Spinner(group, SWT.BORDER);
		spnChangeTime.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnChangeTime.setIncrement(1);
		spnChangeTime.setMaximum(ScreensaverConstants.MAX_VALUE);
		spnChangeTime.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		minute = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.CYCLE_AFTER) : "10");
		if (!minute.equals("")) {
			spnChangeTime.setSelection(Integer.parseInt(minute));
		}

		lblCycleMinute = new Label(group, SWT.NONE);
		lblCycleMinute.setText(Messages.getString("MINUTE"));

		btnCheckLock = new Button(group, SWT.CHECK);
		btnCheckLock.setText(Messages.getString("LOCK"));
		btnCheckLock.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				spnLockTime.setEnabled(btnCheckLock.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		boolean isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.LOCK_SCREEN_AFTER)) : false);
		btnCheckLock.setSelection(isSelected);

		spnLockTime = new Spinner(group, SWT.BORDER);
		spnLockTime.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnLockTime.setIncrement(1);
		spnLockTime.setMaximum(ScreensaverConstants.MAX_VALUE);
		spnLockTime.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		minute = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.LOCK_SCREEN_AFTER_TIMEOUT)
				: "0");
		if (!minute.equals("")) {
			spnLockTime.setSelection(Integer.parseInt(minute));
		}
		spnLockTime.setEnabled(isSelected);

		lblLockMinute = new Label(group, SWT.NONE);
		lblLockMinute.setText(Messages.getString("MINUTE"));

		tabItem.setControl(group);
	}

	public void createImageManipulationTab(Profile profile) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("MANIPULATION"));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		btnCheckGrabImage = new Button(group, SWT.CHECK);
		btnCheckGrabImage.setText(Messages.getString("GRAB_IMAGE"));
		boolean isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.GRAB_DESKTOP_IMAGE)) : false);
		btnCheckGrabImage.setSelection(isSelected);

		btnCheckGrabVideo = new Button(group, SWT.CHECK);
		btnCheckGrabVideo.setText(Messages.getString("GRAB_VIDEO"));
		isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.GRAB_VIDEO_FRAMES)) : false);
		btnCheckGrabVideo.setSelection(isSelected);

		tabItem.setControl(group);
	}

	public void createPowerManagementTab(Profile profile) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("POWER_MANAGEMENT"));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblPowerMan = new Label(group, SWT.NONE);
		lblPowerMan.setText(Messages.getString("ENABLE_POWER_MANAGEMENT"));
		btnCheckPowerManagement = new Button(group, SWT.CHECK);
		boolean isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.POWER_MANAGEMENT_ENABLED))
				: false);
		btnCheckPowerManagement.setSelection(isSelected);
		btnCheckPowerManagement.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				spnStandby.setEnabled(btnCheckPowerManagement.getSelection());
				spnSuspend.setEnabled(btnCheckPowerManagement.getSelection());
				spnOff.setEnabled(btnCheckPowerManagement.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(group, SWT.NONE);

		Label lblStandby = new Label(group, SWT.NONE);
		lblStandby.setText(Messages.getString("STANDBY"));

		spnStandby = new Spinner(group, SWT.BORDER);
		spnStandby.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnStandby.setIncrement(1);
		spnStandby.setMaximum(ScreensaverConstants.LIMITS.MAX_STAND_VALUE);
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(ScreensaverConstants.PARAMETERS.STANDBY_AFTER) != null
				&& !profile.getProfileData().get(ScreensaverConstants.PARAMETERS.STANDBY_AFTER).equals("")) {
			spnStandby.setSelection(Integer
					.parseInt((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.STANDBY_AFTER)));
		} else {
			spnStandby.setEnabled(false);
			spnStandby.setSelection(ScreensaverConstants.LIMITS.DEFAULT_STAND_VALUE);
		}

		Label lblStandbyMinute = new Label(group, SWT.NONE);
		lblStandbyMinute.setText(Messages.getString("MINUTE"));

		Label lblSuspend = new Label(group, SWT.NONE);
		lblSuspend.setText(Messages.getString("SUSPEND"));

		spnSuspend = new Spinner(group, SWT.BORDER);
		spnSuspend.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnSuspend.setIncrement(1);
		spnSuspend.setMaximum(ScreensaverConstants.LIMITS.MAX_STAND_VALUE);
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(ScreensaverConstants.PARAMETERS.SUSPEND_AFTER) != null
				&& !profile.getProfileData().get(ScreensaverConstants.PARAMETERS.SUSPEND_AFTER).equals("")) {
			spnSuspend.setSelection(Integer
					.parseInt((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.SUSPEND_AFTER)));
		} else {
			spnSuspend.setEnabled(false);
			spnSuspend.setSelection(ScreensaverConstants.LIMITS.DEFAULT_STAND_VALUE);
		}

		Label lblSuspendMinute = new Label(group, SWT.NONE);
		lblSuspendMinute.setText(Messages.getString("MINUTE"));

		Label lblOff = new Label(group, SWT.NONE);
		lblOff.setText(Messages.getString("OFF"));

		spnOff = new Spinner(group, SWT.BORDER);
		spnOff.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnOff.setIncrement(1);
		spnOff.setMaximum(ScreensaverConstants.LIMITS.MAX_STAND_VALUE);
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(ScreensaverConstants.PARAMETERS.OFF_AFTER) != null
				&& !profile.getProfileData().get(ScreensaverConstants.PARAMETERS.OFF_AFTER).equals("")) {
			spnOff.setSelection(
					Integer.parseInt((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.OFF_AFTER)));
		} else {
			spnOff.setEnabled(false);
			spnOff.setSelection(ScreensaverConstants.LIMITS.DEFAULT_STAND_OFF_VALUE);
		}

		Label lblOffMinute = new Label(group, SWT.NONE);
		lblOffMinute.setText(Messages.getString("MINUTE"));

		Label lblPowerOff = new Label(group, SWT.NONE);
		lblPowerOff.setText(Messages.getString("ENABLE_QUICK_POWER_OFF"));
		btnCheckPowerOff = new Button(group, SWT.CHECK);
		isSelected = (profile != null && profile.getProfileData() != null ? Boolean.parseBoolean((String) profile.getProfileData()
				.get(ScreensaverConstants.PARAMETERS.QUICK_POWER_OFF_IN_BLACK_ONLY_MODE)) : false);
		btnCheckPowerOff.setSelection(isSelected);
		new Label(group, SWT.NONE);

		tabItem.setControl(group);
	}

	public void createTextManipulationTab(Profile profile) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("TEXT_MANIPULATION"));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		btnTextChoices = new Button[3];

		btnTextChoices[0] = new Button(group, SWT.RADIO);
		btnTextChoices[0].setSelection(true);
		btnTextChoices[0].setText(Messages.getString("HOST_NAME"));
		btnTextChoices[0].setSelection(profile != null && profile.getProfileData() != null
				&& ScreensaverConstants.PARAMETERS.HOST_NAME_AND_TIME_TYPE
						.equals(profile.getProfileData().get(ScreensaverConstants.PARAMETERS.TEXT_MODE)));
		new Label(group, SWT.NONE);

		btnTextChoices[1] = new Button(group, SWT.RADIO);
		btnTextChoices[1].setText(Messages.getString("TEXT"));
		btnTextChoices[1].addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtText.setEnabled(btnTextChoices[1].getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnTextChoices[1].setSelection(
				profile != null && profile.getProfileData() != null && ScreensaverConstants.PARAMETERS.LITERAL_TYPE
						.equals(profile.getProfileData().get(ScreensaverConstants.PARAMETERS.TEXT_MODE)));

		txtText = new Text(group, SWT.BORDER);
		txtText.setEnabled(btnTextChoices[1].getSelection());
		String txt = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.TEXT) : "");
		txtText.setText(txt);

		btnTextChoices[2] = new Button(group, SWT.RADIO);
		btnTextChoices[2].setText(Messages.getString("URL"));
		btnTextChoices[2].addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtURL.setEnabled(btnTextChoices[2].getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnTextChoices[2].setSelection(
				profile != null && profile.getProfileData() != null && ScreensaverConstants.PARAMETERS.URL_TYPE
						.equals(profile.getProfileData().get(ScreensaverConstants.PARAMETERS.TEXT_MODE)));

		txtURL = new Text(group, SWT.BORDER);
		txtURL.setEnabled(btnTextChoices[2].getSelection());
		txt = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.URL) : "");
		txtURL.setText(txt);

		tabItem.setControl(group);
	}

	public void createFadingTab(Profile profile) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("FADING"));

		Group group = new Group(tabFolder, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true));

		Label lblFadeBlack = new Label(group, SWT.NONE);
		lblFadeBlack.setText(Messages.getString("FADE_TO_BLACK"));
		btnCheckFadeToBlack = new Button(group, SWT.CHECK);
		boolean isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.FADE_TO_BLACK_WHEN_BLANKING))
				: true);
		btnCheckFadeToBlack.setSelection(isSelected);
		btnCheckFadeToBlack.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckFadeFromBlack.getSelection() || btnCheckFadeToBlack.getSelection()) {
					spnFading.setEnabled(true);
				} else if (!btnCheckFadeFromBlack.getSelection() && !btnCheckFadeToBlack.getSelection()) {
					spnFading.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(group, SWT.NONE);

		Label lblFadeFromBlack = new Label(group, SWT.NONE);
		lblFadeFromBlack.setText(Messages.getString("FADE_FROM_BLACK"));
		btnCheckFadeFromBlack = new Button(group, SWT.CHECK);
		isSelected = (profile != null && profile.getProfileData() != null ? Boolean.parseBoolean((String) profile.getProfileData()
				.get(ScreensaverConstants.PARAMETERS.FADE_FROM_BLACK_WHEN_UNBLANKING)) : false);
		btnCheckFadeFromBlack.setSelection(isSelected);
		btnCheckFadeFromBlack.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckFadeFromBlack.getSelection() || btnCheckFadeToBlack.getSelection()) {
					spnFading.setEnabled(true);
				} else if (!btnCheckFadeFromBlack.getSelection() && !btnCheckFadeToBlack.getSelection()) {
					spnFading.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(group, SWT.NONE);

		Label lblTime = new Label(group, SWT.NONE);
		lblTime.setText(Messages.getString("FADING_TIME"));

		spnFading = new Spinner(group, SWT.BORDER);
		spnFading.setMinimum(ScreensaverConstants.MIN_VALUE);
		spnFading.setIncrement(1);
		spnFading.setMaximum(ScreensaverConstants.FADING_MAX_VALUE);
		String minute = (profile != null && profile.getProfileData() != null
				? (String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.FADE_DURATION) : "3");
		if (!minute.equals("")) {
			spnFading.setSelection(Integer.parseInt(minute));
		}

		Label lblSeconds = new Label(group, SWT.NONE);
		lblSeconds.setText(Messages.getString("SECONDS"));

		Label lblColormap = new Label(group, SWT.NONE);
		lblColormap.setText(Messages.getString("INSTALL_COLORMAP"));
		btnCheckInstallColormap = new Button(group, SWT.CHECK);
		isSelected = (profile != null && profile.getProfileData() != null
				? Boolean.parseBoolean((String) profile.getProfileData().get(ScreensaverConstants.PARAMETERS.INSTALL_COLORMAP)) : false);
		btnCheckInstallColormap.setSelection(isSelected);

		tabItem.setControl(group);
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(ScreensaverConstants.PARAMETERS.MODES, ScreensaverUtils.getSelectedValue(cmbDisplay));
		profileData.put(ScreensaverConstants.PARAMETERS.BLANK_AFTER, spnScreenTime.getEnabled() ? spnScreenTime.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.CYCLE_AFTER, spnChangeTime.getEnabled() ? spnChangeTime.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.LOCK_SCREEN_AFTER, Boolean.toString(btnCheckLock.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.LOCK_SCREEN_AFTER_TIMEOUT, spnLockTime.getEnabled() ? spnLockTime.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.GRAB_DESKTOP_IMAGE, Boolean.toString(btnCheckGrabImage.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.GRAB_VIDEO_FRAMES, Boolean.toString(btnCheckGrabVideo.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.POWER_MANAGEMENT_ENABLED,
				Boolean.toString(btnCheckPowerManagement.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.STANDBY_AFTER, spnStandby.getEnabled() ? spnStandby.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.SUSPEND_AFTER, spnSuspend.getEnabled() ? spnSuspend.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.OFF_AFTER, spnOff.getEnabled() ? spnOff.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.QUICK_POWER_OFF_IN_BLACK_ONLY_MODE,
				Boolean.toString(btnCheckPowerOff.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.TEXT_MODE,
				btnTextChoices[0].getSelection() ? ScreensaverConstants.PARAMETERS.HOST_NAME_AND_TIME_TYPE
						: (btnTextChoices[1].getSelection() ? ScreensaverConstants.PARAMETERS.LITERAL_TYPE
								: (btnTextChoices[2].getSelection() ? ScreensaverConstants.PARAMETERS.URL_TYPE : "")));
		profileData.put(ScreensaverConstants.PARAMETERS.TEXT, txtText.getEnabled() ? txtText.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.URL, txtURL.getEnabled() ? txtURL.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.FADE_TO_BLACK_WHEN_BLANKING,
				Boolean.toString(btnCheckFadeToBlack.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.FADE_FROM_BLACK_WHEN_UNBLANKING,
				Boolean.toString(btnCheckFadeFromBlack.getSelection()));
		profileData.put(ScreensaverConstants.PARAMETERS.FADE_DURATION, spnFading.getEnabled() ? spnFading.getText() : "");
		profileData.put(ScreensaverConstants.PARAMETERS.INSTALL_COLORMAP, Boolean.toString(btnCheckInstallColormap.getSelection()));

		return profileData;
	}

	private void selectOption(Combo combo, Object value) {
		if (value != null) {
			for (int i = 0; i < modeValueArr.length; i++) {
				if (modeValueArr[i].equalsIgnoreCase(value.toString())) {
					combo.select(i);
				}
			}
		}
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
	}

}

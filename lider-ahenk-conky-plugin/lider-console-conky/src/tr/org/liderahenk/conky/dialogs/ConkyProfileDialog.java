package tr.org.liderahenk.conky.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.conky.constants.ConkyConstants;
import tr.org.liderahenk.conky.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

public class ConkyProfileDialog implements IProfileDialog {

	private static final Logger logger = LoggerFactory.getLogger(ConkyProfileDialog.class);

	private Combo cmbSampleConfigs;
	private Text txtContents;

	@Override
	public void init() {
	}

	@Override
	public void createDialogArea(Composite parent, Profile profile) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gData.widthHint = 800;
		gData.heightHint = 600;
		composite.setLayoutData(gData);
		// Sample Configs
		Label lblSampleConfigs = new Label(composite, SWT.NONE);
		lblSampleConfigs.setText(Messages.getString("SAMPLE_CONFIGS"));

		cmbSampleConfigs = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbSampleConfigs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		populateConfigs(profile);
		// Hook up listener
		cmbSampleConfigs.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		// If this a new profile dialog, select first sample config by default!
		if (profile == null) {
			cmbSampleConfigs.select(0);
		}

		// Contents
		Label lblContents = new Label(composite, SWT.NONE);
		lblContents.setText(Messages.getString("CONTENTS"));

		txtContents = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 120;
		txtContents.setLayoutData(data);
		String contents = getContents(profile);
		if (contents != null) {
			txtContents.setText(contents);
		}

		handleSelection();
	}

	protected void handleSelection() {
		String content = getSelectedSampleConfig();
		if (content != null && !content.isEmpty()) {
			txtContents.setText(content);
		}
	}

	private void populateConfigs(final Profile profile) {
		try {
			String path = SWTResourceManager.getAbsolutePath(ConkyConstants.PLUGIN_ID.CONKY, "conf/");
			if (path != null) {
				File file = new File(path);
				if (file.isDirectory()) {
					File[] configs = file.listFiles();
					if (configs != null) {
						String savedContents = getContents(profile) != null
								? getContents(profile).replaceAll("\\s+", "") : null;
						for (int i = 0; i < configs.length; i++) {
							File config = configs[i];
							BufferedReader br = null;
							try {
								br = new BufferedReader(new FileReader(config));
								boolean firstLine = true;
								StringBuilder contents = new StringBuilder();
								String line = null;
								while ((line = br.readLine()) != null) {
									if (firstLine) {
										cmbSampleConfigs.add(line.replace("#", "").trim());
										firstLine = false;
									}
									contents.append(line);
									contents.append("\n");
								}
								cmbSampleConfigs.setData(i + "", contents);
								if (savedContents != null
										&& contents.toString().replaceAll("\\s+", "").equals(savedContents)) {
									cmbSampleConfigs.select(i);
								}
							} catch (Exception e1) {
								logger.error(e1.getMessage(), e1);
							} finally {
								if (br != null) {
									try {
										br.close();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected String getContents(Profile profile) {
		return profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(ConkyConstants.PARAMETERS.MESSAGE) != null
						? profile.getProfileData().get(ConkyConstants.PARAMETERS.MESSAGE).toString() : null;
	}

	protected String getSelectedSampleConfig() {
		int selectionIndex = cmbSampleConfigs.getSelectionIndex();
		if (selectionIndex > -1 && cmbSampleConfigs.getItem(selectionIndex) != null
				&& cmbSampleConfigs.getData(selectionIndex + "") != null) {
			return cmbSampleConfigs.getData(selectionIndex + "").toString();
		}
		return null;
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(ConkyConstants.PARAMETERS.MESSAGE, txtContents.getText());
		return parameterMap;
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
		if (txtContents.getText() == null || txtContents.getText().isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_MESSAGE"));
		}
	}

}

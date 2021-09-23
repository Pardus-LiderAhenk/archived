package tr.org.liderahenk.script.dialogs;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.script.constants.ScriptConstants;
import tr.org.liderahenk.script.editors.ScriptDefinitionEditor;
import tr.org.liderahenk.script.i18n.Messages;
import tr.org.liderahenk.script.model.ScriptFile;
import tr.org.liderahenk.script.model.ScriptType;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ScriptDefinitionDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(ScriptDefinitionDialog.class);

	private ScriptFile selectedScript;
	private ScriptDefinitionEditor editor;

	private Combo cmbType;
	private Text txtLabel;
	private Text txtContents;
	
	public ScriptDefinitionDialog(Shell parentShell, ScriptDefinitionEditor editor) {
		super(parentShell);
		this.editor = editor;
	}

	public ScriptDefinitionDialog(Shell parentShell, ScriptFile selectedScript, ScriptDefinitionEditor editor) {
		super(parentShell);
		this.selectedScript = selectedScript;
		this.editor = editor;
	}

	/**
	 * Create script input widgets
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		// Script label
		Label lblLabel = new Label(composite, SWT.NONE);
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblLabel.setText(Messages.getString("SCRIPT_LABEL"));

		txtLabel = new Text(composite, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (selectedScript != null && selectedScript.getLabel() != null) {
			txtLabel.setText(selectedScript.getLabel());
		}

		// Script type
		Label lblType = new Label(composite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblType.setText(Messages.getString("SCRIPT_TYPE"));

		cmbType = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		ScriptType[] values = ScriptType.values();
		boolean selected = false;
		for (int i = 0; i < values.length; i++) {
			String i18n = Messages.getString(values[i].toString().toUpperCase(Locale.ENGLISH));
			cmbType.add(i18n);
			cmbType.setData(i18n, values[i]);
			if (!selected && selectedScript != null && selectedScript.getScriptType() == values[i]) {
				cmbType.select(i);
				selected = true;
			}
		}
		cmbType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getSelectedType() == ScriptType.BASH) {
					txtContents.setText(ScriptConstants.TEMPLATES.BASH);
				} else if (getSelectedType() == ScriptType.PERL) {
					txtContents.setText(ScriptConstants.TEMPLATES.PERL);
				} else if (getSelectedType() == ScriptType.RUBY) {
					txtContents.setText(ScriptConstants.TEMPLATES.RUBY);
				} else if (getSelectedType() == ScriptType.PYTHON) {
					txtContents.setText(ScriptConstants.TEMPLATES.PYTHON);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Contents
		Label lblContents = new Label(composite, SWT.NONE);
		lblContents.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblContents.setText(Messages.getString("CONTENTS"));

		txtContents = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 150;
		gridData.widthHint = 600;
		txtContents.setLayoutData(gridData);
		if (selectedScript != null && selectedScript.getContents() != null) {
			txtContents.setText(selectedScript.getContents());
		}

		if (!selected) {
			cmbType.select(0);
			txtContents.setText(ScriptConstants.TEMPLATES.BASH);
		}

		applyDialogFont(composite);
		if(selectedScript != null && selectedScript.getIsTemplate()) {
			cmbType.setEnabled(false);
			txtLabel.setEnabled(false);
			txtContents.setEditable(false);
		}
		return composite;
	}

	/**
	 * Handle OK button press
	 */
	@Override
	protected void okPressed() {

		setReturnCode(OK);
		if(selectedScript != null && selectedScript.getIsTemplate()) {
			close();
		}
		else {
			if (txtLabel.getText().isEmpty()) {
				Notifier.warning(null, Messages.getString("FILL_LABEL_FIELD"));
				return;
			}
			if (txtContents.getText().isEmpty()) {
				Notifier.warning(null, Messages.getString("FILL_CONTENTS_FIELD"));
				return;
			}
			if (getSelectedType() == null) {
				Notifier.warning(null, Messages.getString("SELECT_TYPE"));
				return;
			}

			ScriptFile script = new ScriptFile();
			if (selectedScript != null) {
				script.setId(selectedScript.getId());
				script.setCreateDate(selectedScript.getCreateDate());
			} else {
				script.setCreateDate(new Date());
			}
			script.setLabel(txtLabel.getText());
			script.setScriptType(getSelectedType());
			script.setContents(txtContents.getText());
			script.setModifyDate(new Date());

			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("SCRIPT", script);
			TaskRequest task = new TaskRequest(null, null, ScriptConstants.PLUGIN_NAME, ScriptConstants.PLUGIN_VERSION,
					"SAVE_SCRIPT", parameterMap, null, null, new Date());
			logger.debug("Script request: {}", task);

			try {
				TaskRestUtils.execute(task);
				editor.refresh();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				Notifier.error(null, Messages.getString("ERROR_ON_SAVE"));
			}

			close();
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 400);
	}

	private ScriptType getSelectedType() {
		int selectionIndex = cmbType.getSelectionIndex();
		if (selectionIndex > -1 && cmbType.getItem(selectionIndex) != null
				&& cmbType.getData(cmbType.getItem(selectionIndex)) != null) {
			return (ScriptType) cmbType.getData(cmbType.getItem(selectionIndex));
		}
		return null;
	}

}


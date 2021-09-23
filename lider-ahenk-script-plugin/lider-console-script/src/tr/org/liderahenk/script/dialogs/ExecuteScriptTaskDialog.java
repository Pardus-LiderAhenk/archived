package tr.org.liderahenk.script.dialogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.script.constants.ScriptConstants;
import tr.org.liderahenk.script.i18n.Messages;
import tr.org.liderahenk.script.model.ScriptFile;
import tr.org.liderahenk.script.model.ScriptType;

/**
 * Task execution dialog for script plugin.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class ExecuteScriptTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteScriptTaskDialog.class);

	private Combo cmbScriptFile;
	private Text txtScriptParams;
	private Text txtContents;
	
	private List<ScriptFile> scripts;
	
	public ExecuteScriptTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("EXECUTE_SCRIPT");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblScript = new Label(composite, SWT.NONE);
		lblScript.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblScript.setText(Messages.getString("SCRIPT_LABEL"));

		cmbScriptFile = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbScriptFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));


		Label lblScriptParams = new Label(composite, SWT.NONE);
		lblScriptParams.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblScriptParams.setText(Messages.getString("SCRIPT_PARAMETERS"));

		txtScriptParams = new Text(composite, SWT.BORDER);
		txtScriptParams.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Contents
		Label lblContents = new Label(composite, SWT.NONE);
		lblContents.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblContents.setText(Messages.getString("CONTENTS"));

		txtContents = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 350;
		txtContents.setLayoutData(gridData);
		populateTemplates();
		try {
			IResponse response = TaskRestUtils.execute(ScriptConstants.PLUGIN_NAME, ScriptConstants.PLUGIN_VERSION,
					"LIST_SCRIPTS");
			if (response != null && response.getResultMap() != null && response.getResultMap().get("SCRIPTS") != null) {
				List<ScriptFile> scriptsUser = new ObjectMapper().readValue(
						response.getResultMap().get("SCRIPTS").toString(), new TypeReference<List<ScriptFile>>() {
						});
				if(scriptsUser != null) {
					scripts.addAll(scriptsUser);
				}
				if (scripts != null && !scripts.isEmpty()) {
					for (int i = 0; i < scripts.size(); i++) {
						ScriptFile script = scripts.get(i);
						cmbScriptFile.add(script.getLabel() + " " + script.getCreateDate());
						cmbScriptFile.setData(i + "", script);
						if (i == 0) {
							txtContents.setText(script.getContents());
						}
					}
					cmbScriptFile.select(0);
				} else {
					txtScriptParams.setEnabled(false);
					txtContents.setEnabled(false);
				}
			} else {
				if (scripts != null && !scripts.isEmpty()) {
					for (int i = 0; i < scripts.size(); i++) {
						ScriptFile script = scripts.get(i);
						cmbScriptFile.add(script.getLabel() + " " + script.getCreateDate());
						cmbScriptFile.setData(i + "", script);
						if (i == 0) {
							txtContents.setText(script.getContents());
						}
					}
					cmbScriptFile.select(0);
				} else {
					txtScriptParams.setEnabled(false);
					txtContents.setEnabled(false);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		
		cmbScriptFile.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtContents.setText(getSelectedScript().getContents());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return composite;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (getSelectedScript() == null) {
			throw new ValidationException(Messages.getString("SELECT_SCRIPT"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("SCRIPT_FILE_ID", getSelectedScript().getId());
		// SCRIPT_PARAMS may contain script parameters or it can be empty string
		parameterMap.put("SCRIPT_PARAMS", txtScriptParams.getText());
		parameterMap.put("SCRIPT_CONTENTS", txtContents.getText());
		parameterMap.put("SCRIPT_TYPE", getSelectedScript().getScriptType().toString());
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return "EXECUTE_SCRIPT";
	}

	@Override
	public String getPluginName() {
		return ScriptConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ScriptConstants.PLUGIN_VERSION;
	}

	private ScriptFile getSelectedScript() {
		int selectionIndex = cmbScriptFile.getSelectionIndex();
		if (selectionIndex > -1 && cmbScriptFile.getItem(selectionIndex) != null
				&& cmbScriptFile.getData(selectionIndex + "") != null) {
			return (ScriptFile) cmbScriptFile.getData(selectionIndex + "");
		}
		return null;
	}

	private void populateTemplates() {
		String path = SWTResourceManager.getAbsolutePath(ScriptConstants.PLUGIN_ID.SCRIPT, "template/");
		ScriptFile scriptFile = null;
		scripts = new ArrayList<ScriptFile>();
		if (path != null) {
			File file = new File(path);
			if (file.isDirectory()) {
				File[] templates = file.listFiles();
				if(templates != null) {
					scripts = new ArrayList<ScriptFile>();
					String fileName;
					for (int i = 0; i < templates.length; i++) {
						scriptFile = new ScriptFile();
						fileName = templates[i].getName();
						
						scriptFile.setCreateDate(new Date());
						if(templates[i].getPath().endsWith(".sh")) {
							scriptFile.setScriptType(ScriptType.BASH);
							fileName = fileName.replace(".sh", "");
						} else if(templates[i].getPath().endsWith(".py")) {
							scriptFile.setScriptType(ScriptType.PYTHON);
							fileName = fileName.replace(".py", "");
						} else if(templates[i].getPath().endsWith(".pl")) {
							scriptFile.setScriptType(ScriptType.PERL);
							fileName = fileName.replace(".pl", "");
						} else if(templates[i].getPath().endsWith(".rb")) {
							scriptFile.setScriptType(ScriptType.RUBY);
							fileName = fileName.replace(".rb", "");
						}
						scriptFile.setLabel(fileName.replace("_", " "));
						scriptFile.setIsTemplate(true);
						scripts.add(scriptFile);
						
						File scriptTemplateFile = templates[i];
						BufferedReader br = null;
						try {
							br = new BufferedReader(new FileReader(scriptTemplateFile));
							StringBuilder contents = new StringBuilder();
							String line = null;
							while ((line = br.readLine()) != null) {
								contents.append(line);
								contents.append("\n");
							}
							scriptFile.setContents(contents.toString());
							
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
	}
}

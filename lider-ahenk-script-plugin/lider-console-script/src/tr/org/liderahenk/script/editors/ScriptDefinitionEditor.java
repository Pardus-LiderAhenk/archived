package tr.org.liderahenk.script.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.script.constants.ScriptConstants;
import tr.org.liderahenk.script.dialogs.ScriptDefinitionDialog;
import tr.org.liderahenk.script.i18n.Messages;
import tr.org.liderahenk.script.model.ScriptFile;
import tr.org.liderahenk.script.model.ScriptType;

/**
 * Editor class for script definitions.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ScriptDefinitionEditor extends EditorPart {

	private static final Logger logger = LoggerFactory.getLogger(ScriptDefinitionEditor.class);

	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	private Button btnAddScript;
	private Button btnEditScript;
	private Button btnDeleteScript;
	private Button btnRefreshScript;

	private ScriptFile selectedScript;
	
	private List<ScriptFile> scripts;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(((DefaultEditorInput) input).getLabel());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createButtonsArea(parent);
		createTableArea(parent);
	}

	/**
	 * Create add, edit, delete button for the table.
	 * 
	 * @param composite
	 */
	private void createButtonsArea(final Composite parent) {

		final Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(4, false));

		btnAddScript = new Button(composite, SWT.NONE);
		btnAddScript.setText(Messages.getString("ADD"));
		btnAddScript.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnAddScript.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/add.png"));
		btnAddScript.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScriptDefinitionDialog dialog = new ScriptDefinitionDialog(Display.getDefault().getActiveShell(),
						getSelf());
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnEditScript = new Button(composite, SWT.NONE);
		btnEditScript.setText(Messages.getString("EDIT"));
		btnEditScript.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/edit.png"));
		btnEditScript.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnEditScript.setEnabled(false);
		btnEditScript.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getSelectedScript()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_SCRIPT"));
					return;
				}
				ScriptDefinitionDialog dialog = new ScriptDefinitionDialog(composite.getShell(), getSelectedScript(),
						getSelf());
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnDeleteScript = new Button(composite, SWT.NONE);
		btnDeleteScript.setText(Messages.getString("DELETE"));
		btnDeleteScript.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/delete.png"));
		btnDeleteScript.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnDeleteScript.setEnabled(false);
		btnDeleteScript.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getSelectedScript()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_SCRIPT"));
					return;
				}
				try {
					Map<String, Object> parameterMap = new HashMap<String, Object>();
					parameterMap.put("SCRIPT_FILE_ID", getSelectedScript().getId());
					TaskRequest task = new TaskRequest(null, null, ScriptConstants.PLUGIN_NAME,
							ScriptConstants.PLUGIN_VERSION, "DELETE_SCRIPT", parameterMap, null, null, new Date());
					TaskRestUtils.execute(task);
					refresh();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
					Notifier.error(null, Messages.getString("ERROR_ON_DELETE"));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRefreshScript = new Button(composite, SWT.NONE);
		btnRefreshScript.setText(Messages.getString("REFRESH"));
		btnRefreshScript.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefreshScript.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefreshScript.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * Create main widget of the editor - table viewer.
	 * 
	 * @param parent
	 */
	private void createTableArea(final Composite parent) {

		createTableFilterArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent);
		createTableColumns();
		//populateTable();
		populateTemplates();
		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof ScriptFile) {
					setSelectedScript((ScriptFile) firstElement);
				}
				if(getSelectedScript().getIsTemplate()) {
					btnEditScript.setEnabled(false);
					btnDeleteScript.setEnabled(false);
				}
				else {
					btnEditScript.setEnabled(true);
					btnDeleteScript.setEnabled(true);
				}
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ScriptDefinitionDialog dialog = new ScriptDefinitionDialog(parent.getShell(), getSelectedScript(),
						getSelf());
				dialog.open();
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	/**
	 * Create table filter area
	 * 
	 * @param parent
	 */
	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_SCRIPT_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	/**
	 * Apply filter to table rows. (Search text can be policy label or
	 * description)
	 *
	 */
	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			ScriptFile script = (ScriptFile) element;
			return script.getLabel().matches(searchString) || script.getContents().matches(searchString);
		}
	}

	/**
	 * Create table columns related to policy database columns.
	 * 
	 */
	private void createTableColumns() {

		// Type
		TableViewerColumn typeColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SCRIPT_TYPE"), 100);
		typeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ScriptFile) {
					return ((ScriptFile) element).getScriptType().getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Label
		TableViewerColumn labelColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SCRIPT_LABEL"), 300);
		labelColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ScriptFile) {
					return ((ScriptFile) element).getLabel();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Create date
		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("CREATE_DATE"), 150);
		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ScriptFile) {
					return ((ScriptFile) element).getCreateDate() != null
							? ((ScriptFile) element).getCreateDate().toString() : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Modify date
		TableViewerColumn modifyDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("MODIFY_DATE"), 150);
		modifyDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ScriptFile) {
					return ((ScriptFile) element).getModifyDate() != null
							? ((ScriptFile) element).getModifyDate().toString() : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

	}

	@Override
	public void setFocus() {
		btnAddScript.setFocus();
	}

	/**
	 * Search script files, then populate specified table with script records.
	 * 
	 */
	private void populateTable() {
		try {
			IResponse response = TaskRestUtils.execute(ScriptConstants.PLUGIN_NAME, ScriptConstants.PLUGIN_VERSION,
					"LIST_SCRIPTS");
			List<ScriptFile> scriptsUser = null;
			if (response != null && response.getResultMap() != null && response.getResultMap().get("SCRIPTS") != null) {
				scriptsUser = new ObjectMapper().readValue(response.getResultMap().get("SCRIPTS").toString(),
						new TypeReference<List<ScriptFile>>() {
						});
			}
			if(scriptsUser != null) {
				scripts.addAll(scriptsUser);
			}
			tableViewer.setInput(scripts != null ? scripts : new ArrayList<ScriptFile>());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	/**
	 * Re-populate table with script files.
	 * 
	 */
	public void refresh() {
		//populateTable();
		populateTemplates();
		tableViewer.refresh();
	}

	public ScriptDefinitionEditor getSelf() {
		return this;
	}

	public ScriptFile getSelectedScript() {
		return selectedScript;
	}

	public void setSelectedScript(ScriptFile selectedScript) {
		this.selectedScript = selectedScript;
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
					
					String fileName;
					for (int i = 0; i < templates.length; i++) {
						scriptFile = new ScriptFile();
						fileName = templates[i].getName();
						
						scriptFile.setCreateDate(new Date());
						if(templates[i].getPath().endsWith(".sh")) {
							scriptFile.setScriptType(ScriptType.BASH);
							fileName = fileName.replace(".sh", "");
						}
						else if(templates[i].getPath().endsWith(".py")) {
							scriptFile.setScriptType(ScriptType.PYTHON);
							fileName = fileName.replace(".py", "");
						}
						else if(templates[i].getPath().endsWith(".pl")) {
							scriptFile.setScriptType(ScriptType.PERL);
							fileName = fileName.replace(".pl", "");
						}
						else if(templates[i].getPath().endsWith(".rb")) {
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
					populateTable();
				}
			}
		}
		else {
			populateTable();
		}
	}

}

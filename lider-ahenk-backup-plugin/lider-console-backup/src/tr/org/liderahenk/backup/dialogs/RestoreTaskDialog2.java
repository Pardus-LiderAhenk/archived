package tr.org.liderahenk.backup.dialogs;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.model.BackupServerConf;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class RestoreTaskDialog2 extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(RestoreTaskDialog2.class);

	private Button btnBack;
	private Button btnUpdateBackupServerConf;
	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;

	private BackupServerConf selectedConfig = null;
	private String currentPath = "/";

	public RestoreTaskDialog2(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet, false, true);
	}

	@Override
	public String createTitle() {
		return Messages.getString("RESTORE_FROM_SERVER");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createRestoreTableArea(parent);
		return null;
	}

	private void createRestoreTableArea(Composite parent) {

		try {
			selectedConfig = getBackupServerConfig();
			
			if(selectedConfig!=null)
			currentPath = getInitialPath(selectedConfig.getDestPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createTableButtonArea(parent);
		createTableFilterArea(parent);
		createTableArea(parent);
	}

	private String getInitialPath(String path) {
		int index = path.lastIndexOf(BackupConstants.IP_ADDRESS_EXPRESSION);
		if (index > -1) {
			path = path.substring(0, index);
		}
		return path.replaceAll("//", "/");
	}

	private void createTableArea(Composite parent) {
		GridData dataSearchGrid = new GridData();
		dataSearchGrid.grabExcessHorizontalSpace = true;
		dataSearchGrid.horizontalAlignment = GridData.FILL;

		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create table columns
		createTableColumns();

		// Configure table layout
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.getVerticalBar().setEnabled(true);
		table.getVerticalBar().setVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());

		if (selectedConfig != null) {
			List<String> items;
			try {
				items = Arrays.asList(getBackupServerDirectories(currentPath));
				tableViewer.setInput(items);
				tableViewer.refresh();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 140;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof String) {
					currentPath = (String) firstElement;
					List<String> items;
					try {
						items = Arrays.asList(getBackupServerDirectories(currentPath));
						tableViewer.setInput(items);
						tableViewer.refresh();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	private void createTableColumns() {
		TableViewerColumn dirColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DIRECTORY"), 800);
		dirColumn.getColumn().setAlignment(SWT.LEFT);
		dirColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
	}

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
		txtSearch.setToolTipText(Messages.getString("SEARCH_AGENT_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	private void createTableButtonArea(Composite parent) {

		final Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(2, false));

		btnBack = new Button(composite, SWT.NONE);
		btnBack.setText(Messages.getString("BACK"));
		btnBack.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnBack.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/arrow-left.png"));
		btnBack.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				currentPath = getPreviousPathFrom(currentPath);
				List<String> items;
				try {
					items = Arrays.asList(getBackupServerDirectories(currentPath));
					tableViewer.setInput(items);
					tableViewer.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnUpdateBackupServerConf = new Button(composite, SWT.NONE);
		btnUpdateBackupServerConf.setText(Messages.getString("BACKUP_SERVER_CONF_BUTTON"));
		btnUpdateBackupServerConf.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnUpdateBackupServerConf.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/services.png"));
		btnUpdateBackupServerConf.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BackupServerConfDialog dialog = new BackupServerConfDialog(Display.getDefault().getActiveShell());
				dialog.create();
				dialog.open();
				selectedConfig = dialog.getSelectedConfig();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	protected String getPreviousPathFrom(String path) {
		path = path.lastIndexOf("/") > 0 ? path.substring(0, path.lastIndexOf("/")) : "/";
		String initialPath = getInitialPath(selectedConfig.getDestPath());
		// Previous path must be a child of the initial path!
		// Users should only select a related path defined by the backup server configuration.
		if (path.startsWith(initialPath)) {
			return path;
		}
		Notifier.warning(null, Messages.getString("BACKUP_PATH_WARNING"), Messages.getString("BACKUP_PATH_WARNING_DESC"));
		return initialPath;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (selectedConfig == null) {
			throw new ValidationException(Messages.getString("BACKUP_SERVER_CONF_NOT_FOUND"));
		}
		if (currentPath == null && currentPath.isEmpty()) {
			throw new ValidationException(Messages.getString("CURRENT_PATH_NOT_FOUND"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(BackupConstants.PARAMETERS.USERNAME, selectedConfig.getUsername());
		profileData.put(BackupConstants.PARAMETERS.PASSWORD, selectedConfig.getPassword());
		profileData.put(BackupConstants.PARAMETERS.DEST_HOST, selectedConfig.getDestHost());
		profileData.put(BackupConstants.PARAMETERS.DEST_PORT, selectedConfig.getDestPort());
		profileData.put(BackupConstants.PARAMETERS.SOURCE_PATH, currentPath.endsWith("/") ? currentPath : currentPath + "/");
		profileData.put(BackupConstants.PARAMETERS.DEST_PATH, getDestPathFrom(currentPath));
		return profileData;
	}

	private String getDestPathFrom(String path) {
		String tmp = path.endsWith("/") ? path : path + "/";
		String base = selectedConfig.getDestPath();
		base = base.endsWith("/") ? base : base + "/";
		int count = base.length() - base.replace("/", "").length();
		int pos = tmp.indexOf("/");
	    while (--count > 0 && pos != -1)
	        pos = tmp.indexOf("/", pos + 1);
	    return tmp.substring(pos);
	}

	@Override
	public String getCommandId() {
		return "RESTORE";
	}

	@Override
	public String getPluginName() {
		return BackupConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return BackupConstants.PLUGIN_VERSION;
	}

	private BackupServerConf getBackupServerConfig() throws Exception {
		IResponse response = null;
		try {
			response = TaskRestUtils.execute(BackupConstants.PLUGIN_NAME, BackupConstants.PLUGIN_VERSION,
					"GET_BACKUP_SERVER_CONFIG", false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		return (BackupServerConf) ((response != null && response.getResultMap() != null
				&& response.getResultMap().get("BACKUP_SERVER_CONFIG") != null)
						? new ObjectMapper().readValue(response.getResultMap().get("BACKUP_SERVER_CONFIG").toString(),
								BackupServerConf.class)
						: null);
	}

	private String[] getBackupServerDirectories(String path) throws Exception {
		IResponse response = null;
		try {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("TARGET_PATH", path != null ? path : "/");
			TaskRequest task = new TaskRequest(null, DNType.AHENK, BackupConstants.PLUGIN_NAME,
					BackupConstants.PLUGIN_VERSION, "LIST_BACKUP_SERVER_DIR", parameterMap, null, null, new Date());
			response = TaskRestUtils.execute(task, false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
		String result = response.getResultMap().get("CHILD_DIRS").toString();
		return result.split(",");
	}

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
			String item = (String) element;
			return item.matches(searchString);
		}
	}
	
	@Override
	public String getMailContent() {
		
		return "cn={ahenk} ahenginde {path} yolu yedek geri alma işlemi tamamlanmıştır.";
	}
	
	@Override
	public String getMailSubject() {
		return "Lider Ahenk Yedekleme ";
	}

}

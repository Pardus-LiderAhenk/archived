package tr.org.liderahenk.backup.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.backup.constants.BackupConstants;
import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.model.BackupParametersListItem;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class BackupDialogBase {

	private Button    btnCheckSSH;
	private Text   	  txtUsername;
	private Text   	  txtPassword;
	private Text   	  txtDestHost;
	private Text   	  txtDestPort;
	private Text   	  txtDestPath;
	private Button 	  btnCheckLVM;
	private Composite   tableComposite;
	private TableViewer tableViewer;
	private BackupParametersListItem item;

	private Button btnAddListItem;
	private Button btnEditListItem;
	private Button btnDeleteListItem;
	
	public void createBackupDialog(Composite parent, Map<String, Object> profileData) 
	{
		createTargetServerInputs(parent, profileData);
		createTableArea(parent, profileData);
	}
	
	
	private void createTargetServerInputs(Composite parent, Map<String, Object> profileData) 
	{
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, true));
		composite.setData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		btnCheckSSH = new Button(composite, SWT.CHECK);
		btnCheckSSH.setText(Messages.getString("SSH"));
		btnCheckSSH.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtPassword.setEnabled(!btnCheckSSH.getSelection());
				txtPassword.setText("");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		if (profileData != null && profileData.get(BackupConstants.PARAMETERS.USE_SSH_KEY) != null) {
			btnCheckSSH.setSelection((boolean) profileData.get(BackupConstants.PARAMETERS.USE_SSH_KEY));
		}
		new Label(composite, SWT.NONE);

		Label labelUN = new Label(composite, SWT.NONE);
		labelUN.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    labelUN.setText(Messages.getString("USERNAME"));
		txtUsername = new Text(composite, SWT.BORDER);
		txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profileData != null) {
			txtUsername.setText(profileData.get(BackupConstants.PARAMETERS.USERNAME).toString());
		}
		
		Label labelPassword = new Label(composite, SWT.NONE);
		labelPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPassword.setText(Messages.getString("PASSWORD"));
		txtPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profileData != null) {
			txtPassword.setText(profileData.get(BackupConstants.PARAMETERS.PASSWORD).toString());
		}
		
		Label labelHost = new Label(composite, SWT.NONE);
		labelHost.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelHost.setText(Messages.getString("DEST_HOST"));
		txtDestHost = new Text(composite, SWT.BORDER);
		txtDestHost.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profileData != null) {
			txtDestHost.setText(profileData.get(BackupConstants.PARAMETERS.DEST_HOST).toString());
		}
		
		Label labelPort = new Label(composite, SWT.NONE);
		labelPort.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelPort.setText(Messages.getString("DEST_PORT"));
		txtDestPort = new Text(composite, SWT.BORDER);
		txtDestPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profileData != null) {
			txtDestPort.setText(profileData.get(BackupConstants.PARAMETERS.DEST_PORT).toString());
		} else {
			txtDestPort.setText(BackupConstants.DEFAULT_PORT);
		}
		
		Label labelDir = new Label(composite, SWT.NONE);
		labelDir.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		labelDir.setText(Messages.getString("DEST_DIR"));
		txtDestPath = new Text(composite, SWT.BORDER);
		txtDestPath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profileData != null) {
			txtDestPath.setText(profileData.get(BackupConstants.PARAMETERS.DEST_PATH).toString());
		}
		
		btnCheckLVM = new Button(composite, SWT.CHECK);
		btnCheckLVM.setText(Messages.getString("USE_LVM"));
		if (profileData != null && profileData.get(BackupConstants.PARAMETERS.USE_LVM) != null) 
		{
			btnCheckLVM.setSelection((boolean) profileData.get(BackupConstants.PARAMETERS.USE_LVM));
		}
		new Label(composite, SWT.NONE);
	}

	/**
	 * Create table area
	 * 
	 * @param parent
	 * @param profileData
	 */
	private void createTableArea(Composite parent, Map<String, Object> profileData) {

		// Table composite contains all table-related widgets and table itself!
		tableComposite = new Composite(parent, SWT.BORDER);
		tableComposite.setLayout(new GridLayout(1, false));

		createButtons(tableComposite);
		createTable(tableComposite, profileData);
	}
	
	/**
	 * Create add, edit, delete buttons for the table
	 * 
	 * @param parent
	 */
	private void createButtons(final Composite parent) {
		final Composite tableButtonComposite = new Composite(parent, SWT.NONE);
		tableButtonComposite.setLayout(new GridLayout(3, false));

		btnAddListItem = new Button(tableButtonComposite, SWT.NONE);
		btnAddListItem.setText(Messages.getString("ADD"));
		btnAddListItem.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnAddListItem.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/add.png"));
		btnAddListItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BackupParametersListItemDialog dialog = new BackupParametersListItemDialog(
						Display.getDefault().getActiveShell(), tableViewer, btnCheckLVM.getSelection());
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnEditListItem = new Button(tableButtonComposite, SWT.NONE);
		btnEditListItem.setText(Messages.getString("EDIT"));
		btnEditListItem.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/edit.png"));
		btnEditListItem.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnEditListItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getItem()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_ITEM"));
					return;
				}
				BackupParametersListItemDialog dialog = new BackupParametersListItemDialog(tableButtonComposite.getShell(),
						getItem(), tableViewer, btnCheckLVM.getSelection());
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnDeleteListItem = new Button(tableButtonComposite, SWT.NONE);
		btnDeleteListItem.setText(Messages.getString("DELETE"));
		btnDeleteListItem.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/delete.png"));
		btnDeleteListItem.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnDeleteListItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getItem()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_ITEM"));
					return;
				}
				@SuppressWarnings("unchecked")
				List<BackupParametersListItem> items = (List<BackupParametersListItem>) tableViewer.getInput();
				items.remove(tableViewer.getTable().getSelectionIndex());
				tableViewer.setInput(items);
				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}
	
	/**
	 * Create table
	 * 
	 * @param parent
	 * @param profile
	 */
	@SuppressWarnings("unchecked")
	private void createTable(final Composite parent, Map<String, Object> profileData) {
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

		if (profileData != null	&& profileData.get(BackupConstants.PARAMETERS.BACKUP_LIST_ITEMS) != null) 
		{	
			ArrayList<LinkedHashMap<String, Object>> list = (ArrayList<LinkedHashMap<String, Object>>) profileData.get(BackupConstants.PARAMETERS.BACKUP_LIST_ITEMS);
			
			if (list != null) {
				List<BackupParametersListItem> items = new ArrayList<BackupParametersListItem>();
				for (LinkedHashMap<String, Object> map : list) {
					// TODO IMPROVEMENT use object mapper instead of this constructor!
					BackupParametersListItem item = new BackupParametersListItem((String) map.get("sourcePath"),
							(String) map.get("excludePattern"), (String) map.get("logicalVolume"), (String) map.get("virtualGroup"),
							(String) map.get("logicalVolumeSize"), (boolean) map.get("recursive"), (boolean) map.get("preserveGroup"),
							(boolean) map.get("preserveOwner"), (boolean) map.get("preservePermissions"), (boolean) map.get("archive"),
							(boolean) map.get("compress"), (boolean) map.get("existingOnly"));
					items.add(item);
				}
				tableViewer.setInput(items);
				tableViewer.refresh();
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

		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				firstElement = (BackupParametersListItem) firstElement;
				if (firstElement instanceof BackupParametersListItem) {
					setItem((BackupParametersListItem) firstElement);
				}
				btnEditListItem.setEnabled(true);
				btnDeleteListItem.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				BackupParametersListItemDialog dialog = new BackupParametersListItemDialog(parent.getShell(), getItem(),
						tableViewer, btnCheckLVM.getSelection());
				dialog.open();
			}
		});
	}

	public BackupParametersListItem getItem() {
		return item;
	}

	public void setItem(BackupParametersListItem item) {
		this.item = item;
	}
	
	/**
	 * Create table columns.
	 * 
	 */
	private void createTableColumns() {

		String[] titles = { Messages.getString("BACKUP_DIR"), Messages.getString("EXCLUDE_FILE_TYPE"), Messages.getString("RECURSIVE"),
				Messages.getString("PROTECT_GROUP"), Messages.getString("PROTECT_OWNER"), Messages.getString("PROTECT_PERMISSIONS"), 
				Messages.getString("ARCHIVE"), Messages.getString("COMPRESS"), Messages.getString("UPDATE_ONLY_EXISTINGS")};
		int[] bounds = { 60, 100, 150, 200};

		TableViewerColumn dirColumn = createTableViewerColumn(titles[0], bounds[3]);
		dirColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).getSourcePath();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn excFileTypesColumn = createTableViewerColumn(titles[1], bounds[2]);
		excFileTypesColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).getExcludePattern();
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn recursiveColumn = createTableViewerColumn(titles[2], bounds[0]);
		recursiveColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isRecursive() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn groupColumn = createTableViewerColumn(titles[3], bounds[1]);
		groupColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isPreserveGroup() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn ownerColumn = createTableViewerColumn(titles[4], bounds[1]);
		ownerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isPreserveOwner() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn permissionsColumn = createTableViewerColumn(titles[5], bounds[1]);
		permissionsColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isPreservePermissions() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn archiveColumn = createTableViewerColumn(titles[6], bounds[0]);
		archiveColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isArchive() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn compressColumn = createTableViewerColumn(titles[7], bounds[0]);
		compressColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isCompress() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn existingColumn = createTableViewerColumn(titles[8], bounds[3]);
		existingColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BackupParametersListItem) {
					return ((BackupParametersListItem) element).isExistingOnly() ? Messages.getString("YES") : Messages.getString("NO");
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	/**
	 * Create new table viewer column instance.
	 * 
	 * @param title
	 * @param bound
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getParameterData() 
	{
		Map<String, Object> profileData = new HashMap<String, Object>();

		profileData.put(BackupConstants.PARAMETERS.PASSWORD, txtPassword.getText());
		profileData.put(BackupConstants.PARAMETERS.USERNAME, txtUsername.getText());
		profileData.put(BackupConstants.PARAMETERS.USE_SSH_KEY, btnCheckSSH.getSelection());
		profileData.put(BackupConstants.PARAMETERS.USE_LVM, btnCheckLVM.getSelection());
		profileData.put(BackupConstants.PARAMETERS.DEST_HOST, txtDestHost.getText());
		profileData.put(BackupConstants.PARAMETERS.DEST_PORT, txtDestPort.getText());
		profileData.put(BackupConstants.PARAMETERS.DEST_PATH, txtDestPath.getText());

		List<BackupParametersListItem> items = (List<BackupParametersListItem>) tableViewer.getInput();
		if (items != null) {
			profileData.put(BackupConstants.PARAMETERS.BACKUP_LIST_ITEMS, items);
		}
		return profileData;
	}
	
	@SuppressWarnings("unchecked")
	public void validateProfile() throws ValidationException 
	{
		if (txtDestHost.getText().isEmpty() || txtUsername.getText().isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_DEST_HOST"));
		}
		if (!btnCheckSSH.getSelection() && txtPassword.getText().isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_PASSWORD"));
		}
		if ( tableViewer.getInput() == null || ((List<BackupParametersListItem>) tableViewer.getInput()).isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_SOURCE"));
		}
	}


}

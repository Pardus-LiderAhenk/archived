package tr.org.liderahenk.user.privilege.dialogs;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.user.privilege.constants.UserPrivilegeConstants;
import tr.org.liderahenk.user.privilege.i18n.Messages;
import tr.org.liderahenk.user.privilege.model.PrivilegeItem;

/**
 * Profile dialog for User Privilege plugin. A User Privilege profile may has
 * many detail items that keep Linux commands and their Polkit and resource
 * limit values. This dialog have a table of these items and buttons for CRUD
 * operations on them.
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class UserPrivilegeProfileDialog implements IProfileDialog {

	private static final Logger logger = LoggerFactory.getLogger(UserPrivilegeProfileDialog.class);

	private Button btnAddListItem;
	private Button btnEditListItem;
	private Button btnDeleteListItem;
	private TableViewer tableViewer;
	private PrivilegeItem item;

	@Override
	public void init() {
	}

	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Profile recieved: {}", profile != null ? profile.toString() : null);

		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		mainComposite.setLayout(layout);

		Composite tableComposite = new Composite(mainComposite, SWT.NONE);
		tableComposite.setLayout(layout);

		// Create table of list items
		createTableArea(tableComposite, profile);

	}

	private void createTableArea(Composite tableComposite, Profile profile) {
		// Create add, edit, delete buttons
		createButtons(tableComposite);

		createTable(tableComposite, profile);
	}

	/**
	 * Create table
	 * 
	 * @param parent
	 * @param profile
	 */
	private void createTable(final Composite parent, Profile profile) {
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

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(UserPrivilegeConstants.PARAMETERS.LIST_ITEMS) != null) {
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<String, Object>> list = (ArrayList<LinkedHashMap<String, Object>>) profile
					.getProfileData().get(UserPrivilegeConstants.PARAMETERS.LIST_ITEMS);
			if (list != null) {
				List<PrivilegeItem> items = new ArrayList<PrivilegeItem>();
				for (LinkedHashMap<String, Object> map : list) {
					PrivilegeItem item = new PrivilegeItem((String) map.get("cmd"), (String) map.get("polkitStatus"),
							(Boolean) map.get("limitResourceUsage"), (Integer) map.get("cpu"),
							(Integer) map.get("memory"));
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
				firstElement = (PrivilegeItem) firstElement;
				if (firstElement instanceof PrivilegeItem) {
					setItem((PrivilegeItem) firstElement);
				}
				btnEditListItem.setEnabled(true);
				btnDeleteListItem.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				PrivilegeItemDialog dialog = new PrivilegeItemDialog(parent.getShell(), getItem(), tableViewer);
				dialog.open();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validateBeforeSave() throws ValidationException {
		Object o = tableViewer.getInput();
		if (o == null) {
			throw new ValidationException(Messages.getString("DEFINE_AT_LEAST_ONE_PRIVILEGE"));
		}
		List<PrivilegeItem> items = (List<PrivilegeItem>) o;
		if (items.size() == 0) {
			throw new ValidationException(Messages.getString("DEFINE_AT_LEAST_ONE_PRIVILEGE"));
		}
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
				PrivilegeItemDialog dialog = new PrivilegeItemDialog(Display.getDefault().getActiveShell(),
						tableViewer);
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
				PrivilegeItemDialog dialog = new PrivilegeItemDialog(tableButtonComposite.getShell(), getItem(),
						tableViewer);
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
				List<PrivilegeItem> items = (List<PrivilegeItem>) tableViewer.getInput();
				items.remove(tableViewer.getTable().getSelectionIndex());
				tableViewer.setInput(items);
				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createTableColumns() {
		TableViewerColumn cmdCol = createTableViewerColumn(tableViewer, Messages.getString("COMMAND_PATH"), 200);
		cmdCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PrivilegeItem) element).getCmd();
			}
		});

		TableViewerColumn privilegeCol = createTableViewerColumn(tableViewer, Messages.getString("PRIVILEGE"), 200);
		privilegeCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				 String status = ((PrivilegeItem) element).getPolkitStatus();
				 if ("privileged".equalsIgnoreCase(status)) {
					 return Messages.getString("PRIVILEGED");
				 } else if ("unprivileged".equalsIgnoreCase(status)) {
					 return Messages.getString("UNPRIVILEGED");
				 } else {
					 return Messages.getString("N/A");
				 }
			}
		});

		TableViewerColumn limitResourceCol = createTableViewerColumn(tableViewer,
				Messages.getString("LIMIT_RESOURCE_USAGE"), 200);
		limitResourceCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PrivilegeItem) element).getLimitResourceUsage().booleanValue() ? Messages.getString("YES")
						: Messages.getString("NO");
			}
		});

		TableViewerColumn cpuCol = createTableViewerColumn(tableViewer, Messages.getString("CPU_LIMIT"), 200);
		cpuCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PrivilegeItem) element).getCpu() != null ? ((PrivilegeItem) element).getCpu().toString()
						: "N/A";
			}
		});

		TableViewerColumn memoryCol = createTableViewerColumn(tableViewer, Messages.getString("MEMORY_LIMIT"), 200);
		memoryCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PrivilegeItem) element).getMemory() != null ? ((PrivilegeItem) element).getMemory().toString()
						: "N/A";
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(final TableViewer tblVwrSetup, String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tblVwrSetup, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		List<PrivilegeItem> items = (List<PrivilegeItem>) tableViewer.getInput();
		if (items != null) {
			profileData.put(UserPrivilegeConstants.PARAMETERS.LIST_ITEMS, items);
		}

		return profileData;
	}

	public PrivilegeItem getItem() {
		return item;
	}

	public void setItem(PrivilegeItem item) {
		this.item = item;
	}

}

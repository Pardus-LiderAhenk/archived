package tr.org.liderahenk.packagemanager.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.packagemanager.constants.PackageManagerConstants;
import tr.org.liderahenk.packagemanager.i18n.Messages;
import tr.org.liderahenk.packagemanager.model.PackageSourceItem;

/**
 * Task execution dialog for package-manager plugin.
 * 
 */
public class PackageSourcesTaskDialog extends DefaultTaskDialog {

	private Composite tableComposite;
	private TableViewer tableViewer;
	private Button btnAdd;
	private Button btnDelete;

	private PackageSourceItem item;
	protected static ArrayList<String> addedSources = new ArrayList<>();
	protected static ArrayList<String> deletedSources = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(PackageSourcesTaskDialog.class);

	public PackageSourcesTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		subscribeEventHandler(eventHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("PackageSources");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		tableComposite = new Composite(parent, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		createButtons(tableComposite);
		createTable(tableComposite);
		getRepositories();
		return tableComposite;
	}

	private void createTable(final Composite parent) {
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

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
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
				firstElement = (PackageSourceItem) firstElement;
				if (firstElement instanceof PackageSourceItem) {
					setItem((PackageSourceItem) firstElement);
				}
				btnDelete.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				PackageSourceItemDialog dialog = new PackageSourceItemDialog(parent.getShell(), getItem(), tableViewer);
				dialog.open();
			}
		});
	}

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

	private void createTableColumns() {

		String[] titles = { Messages.getString("URL") };
		int[] bounds = { 600, 200 };

		TableViewerColumn isLocalColumn = createTableViewerColumn(titles[0], bounds[0]);
		isLocalColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PackageSourceItem) {
					return ((PackageSourceItem) element).getUrl();
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	private void createButtons(final Composite parent) {
		final Composite tableButtonComposite = new Composite(parent, SWT.NONE);
		tableButtonComposite.setLayout(new GridLayout(3, false));

		btnAdd = new Button(tableButtonComposite, SWT.NONE);
		btnAdd.setText(Messages.getString("ADD"));
		btnAdd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnAdd.setImage(SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/add.png"));
		btnAdd.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PackageSourceItemDialog dialog = new PackageSourceItemDialog(Display.getDefault().getActiveShell(),
						tableViewer);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnDelete = new Button(tableButtonComposite, SWT.NONE);
		btnDelete.setText(Messages.getString("DELETE"));
		btnDelete.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/delete.png"));
		btnDelete.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnDelete.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getItem()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_ITEM"));
					return;
				}
				@SuppressWarnings("unchecked")
				List<PackageSourceItemDialog> items = (List<PackageSourceItemDialog>) tableViewer.getInput();
				items.remove(tableViewer.getTable().getSelectionIndex());
				tableViewer.setInput(items);
				tableViewer.refresh();
				if (addedSources.contains(getItem().getUrl())) {
					addedSources.remove(getItem().getUrl());
				} else {
					deletedSources.add(getItem().getUrl());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (tableViewer.getInput() == null || ((List<PackageSourceItem>) tableViewer.getInput()).isEmpty()) {
			throw new ValidationException(Messages.getString("ADD_ITEM"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> taskData = new HashMap<String, Object>();
		taskData.put(PackageManagerConstants.PARAMETERS.ADDED_ITEMS, addedSources);
		taskData.put(PackageManagerConstants.PARAMETERS.DELETED_ITEMS, deletedSources);
		return taskData;
	}

	private void getRepositories() {
		try {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (getProgressBar() != null) {
						getProgressBar().setVisible(true);
					}
				}
			});
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), "REPOSITORIES", null, null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
		}
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("PACKAGE_SOURCES", 100);
					try {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								if (getProgressBar() != null) {
									getProgressBar().setVisible(false);
								}
							}
						});
						addedSources.clear();
						deletedSources.clear();
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = taskStatus.getResult().getResponseData();
						final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
								});
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								String[] result = responseData.containsKey("packageSource")
										? responseData.get("packageSource").toString().split("\\r?\\n") : null;
								if (result != null && result.length > 0) {
									ArrayList<PackageSourceItem> items = new ArrayList<>();
									for (String data : result) {
										PackageSourceItem item = new PackageSourceItem(data);
										items.add(item);
									}
									if (items != null)
										tableViewer.setInput(items);
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_PACKAGE_SOURCES"));
					}
					monitor.worked(100);
					monitor.done();

					return Status.OK_STATUS;
				}
			};

			job.setUser(true);
			job.schedule();
		}
	};

	@Override
	public String getCommandId() {
		return "PACKAGE_SOURCES";
	}

	@Override
	public String getPluginName() {
		return PackageManagerConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return PackageManagerConstants.PLUGIN_VERSION;
	}

	public PackageSourceItem getItem() {
		return item;
	}

	public void setItem(PackageSourceItem item) {
		this.item = item;
	}

}

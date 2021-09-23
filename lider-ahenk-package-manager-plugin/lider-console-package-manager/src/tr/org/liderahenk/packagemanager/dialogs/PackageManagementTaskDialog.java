package tr.org.liderahenk.packagemanager.dialogs;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
import tr.org.liderahenk.liderconsole.core.xmpp.enums.ContentType;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.packagemanager.constants.PackageManagerConstants;
import tr.org.liderahenk.packagemanager.editingsupport.DesiredStatusEditingSupport;
import tr.org.liderahenk.packagemanager.i18n.Messages;
import tr.org.liderahenk.packagemanager.model.DesiredPackageStatus;
import tr.org.liderahenk.packagemanager.model.PackageInfo;

public class PackageManagementTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(PackageManagementTaskDialog.class);

	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	private final Image installImage;
	private final Image uninstallImage;
	private Button btnRefreshPackage;

	private PackageInfo selectedPackage;

	public PackageManagementTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		subscribeEventHandler(taskStatusNotificationHandler);
		installImage = new Image(Display.getDefault(),
				this.getClass().getClassLoader().getResourceAsStream("icons/16/install.png"));
		uninstallImage = new Image(Display.getDefault(),
				this.getClass().getClassLoader().getResourceAsStream("icons/16/uninstall.png"));
	}

	@Override
	public String createTitle() {
		return Messages.getString("PackageManager");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		createButtonsArea(composite);
		createTableArea(composite);
		getPackages();
		return composite;
	}

	private void createButtonsArea(Composite parent) {
		final Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(1, false));

		btnRefreshPackage = new Button(composite, SWT.NONE);
		btnRefreshPackage.setText(Messages.getString("REFRESH"));
		btnRefreshPackage.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefreshPackage.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefreshPackage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPackages();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void createTableArea(Composite parent) {

		createTableFilterArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent);
		createTableColumns();

		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof PackageInfo) {
					setSelectedPackage((PackageInfo) firstElement);
				}
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
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
		txtSearch.setToolTipText(Messages.getString("SEARCH_PACKAGES_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	/**
	 * Apply filter to table rows. (Search text can be package name or version)
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
			PackageInfo packageInfo = (PackageInfo) element;
			return packageInfo.getPackageName().matches(searchString)
					|| (packageInfo.getVersion() != null && packageInfo.getVersion().matches(searchString));
		}

	}

	private void createTableColumns() {

		// Status
		TableViewerColumn statusColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("STATUS"), 120);
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PackageInfo) {
					return ((PackageInfo) element).isInstalled() ? Messages.getString("INSTALLED")
							: Messages.getString("UNINSTALLED");
				}
				return Messages.getString("UNTITLED");
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof PackageInfo) {
					return ((PackageInfo) element).isInstalled() ? installImage : uninstallImage;
				}
				return null;
			}
		});

		// Desired status
		TableViewerColumn desiredStatusColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DESIRED_STATUS"), 120);
		desiredStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PackageInfo) {
					return ((PackageInfo) element).getDesiredStatus().getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});
		desiredStatusColumn.setEditingSupport(new DesiredStatusEditingSupport(tableViewer));

		// Package name
		TableViewerColumn packageNameColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("PACKAGE_NAME"), 200);
		packageNameColumn.getColumn().setAlignment(SWT.LEFT);
		packageNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PackageInfo) {
					return ((PackageInfo) element).getPackageName();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Package version
		TableViewerColumn versionColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("PACKAGE_VERSION"), 100);
		versionColumn.getColumn().setAlignment(SWT.LEFT);
		versionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof PackageInfo) {
					return ((PackageInfo) element).getVersion();
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validateBeforeExecution() throws ValidationException {
		if (tableViewer.getInput() == null || ((List<PackageInfo>) tableViewer.getInput()).isEmpty()
				|| getSelectedPackage() == null) {
			throw new ValidationException(Messages.getString("ADD_ITEM"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> taskData = new HashMap<String, Object>();
		taskData.put(PackageManagerConstants.PACKAGES.PACKAGE_INFO_LIST, getSelectedPackages());
		return taskData;
	}

	private ArrayList<PackageInfo> getSelectedPackages() {
		ArrayList<PackageInfo> selectedPackages = null;
		if (tableViewer.getInput() != null) {
			selectedPackages = new ArrayList<PackageInfo>();
			@SuppressWarnings("unchecked")
			List<PackageInfo> packages = (List<PackageInfo>) tableViewer.getInput();
			for (PackageInfo packageInfo : packages) {
				if (packageInfo.getDesiredStatus() == DesiredPackageStatus.NA) {
					continue;
				}
				packageInfo.setTag(packageInfo.getDesiredStatus() == DesiredPackageStatus.UNINSTALL ? "u" : "i");
				selectedPackages.add(packageInfo);
			}
		}
		return selectedPackages;
	}

	private void getPackages() {
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
					getPluginVersion(), "INSTALLED_PACKAGES", null, null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("PACKAGE_MANAGEMENT", 100);
					try {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								if (getProgressBar() != null) {
									getProgressBar().setVisible(false);
								}
							}
						});
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						if (ContentType.getFileContentTypes().contains(taskStatus.getResult().getContentType())) {
							byte[] data = TaskRestUtils.getResponseData(taskStatus.getResult().getId());
							BufferedReader bufReader = new BufferedReader(
									new StringReader(new String(data, StandardCharsets.UTF_8)));
							String line = null;
							final ArrayList<PackageInfo> packages = new ArrayList<>();
							while ((line = bufReader.readLine()) != null) {
								String[] tokens = line.split(",");
								if (tokens.length >= 2) {
									PackageInfo packageInfo = new PackageInfo();
									packageInfo.setPackageName(tokens[1]);
									if (tokens.length == 3) {
										packageInfo.setVersion(tokens[2]);
									}
									packageInfo.setInstalled("i".equalsIgnoreCase(tokens[0]));
									packageInfo.setDesiredStatus(DesiredPackageStatus.NA);
									packages.add(packageInfo);
								}
							}
							// Refresh table
							if (packages != null && !packages.isEmpty()) {
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										if (tableViewer != null) {
											tableViewer.setInput(packages);
											tableViewer.refresh();
										}
									}
								});
							}
						} else {
							byte[] data = taskStatus.getResult().getResponseData();
							final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
									new TypeReference<HashMap<String, Object>>() {
									});

							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (responseData != null && responseData.containsKey("Result"))
										getPackages();
								}
							});
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
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
		return "PACKAGE_MANAGEMENT";
	}

	@Override
	public String getPluginName() {
		return PackageManagerConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return PackageManagerConstants.PLUGIN_VERSION;
	}

	public PackageInfo getSelectedPackage() {
		return selectedPackage;
	}

	public void setSelectedPackage(PackageInfo selectedPackage) {
		this.selectedPackage = selectedPackage;
	}
}

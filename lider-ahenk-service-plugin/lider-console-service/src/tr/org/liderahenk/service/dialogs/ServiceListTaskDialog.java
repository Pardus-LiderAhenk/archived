package tr.org.liderahenk.service.dialogs;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.model.PdfContent;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.service.constants.ServiceConstants;
import tr.org.liderahenk.service.editingsupport.StartAutoEditingSupport;
import tr.org.liderahenk.service.editingsupport.StatusEditingSupport;
import tr.org.liderahenk.service.i18n.Messages;
import tr.org.liderahenk.service.model.DesiredStatus;
import tr.org.liderahenk.service.model.ServiceListItem;

public class ServiceListTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ServiceListTaskDialog.class);
	private Text txtFilter;
	private Button btnRefreshPackage;
	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private ServiceListItem selectedService;
	private final Image activeImage;
	private final Image inactiveImage;

	public ServiceListTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn,false,true);
		subscribeEventHandler(eventHandler);
		activeImage = new Image(Display.getDefault(),
				this.getClass().getClassLoader().getResourceAsStream("icons/16/active.png"));
		inactiveImage = new Image(Display.getDefault(),
				this.getClass().getClassLoader().getResourceAsStream("icons/16/inactive.png"));

		
	}

	@Override
	public String createTitle() {
		return Messages.getString("SERVICE_LIST_TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createButtonsArea(composite);
		createTableArea(composite);
		
		getServices();

		return null;
	}

	private void createButtonsArea(Composite parent) {

		final Composite composite = new Composite(parent, GridData.FILL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 1300;
		composite.setLayoutData(gd);
		composite.setLayout(new GridLayout(1, false));

		btnRefreshPackage = new Button(composite, SWT.NONE);
		btnRefreshPackage.setText(Messages.getString("REFRESH"));
		btnRefreshPackage.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefreshPackage.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefreshPackage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getServices();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
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
			ServiceListItem packageInfo = (ServiceListItem) element;
			return packageInfo.getServiceName().matches(searchString);
		}

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
				if (firstElement instanceof ServiceListItem) {
					setSelectedService((ServiceListItem) firstElement);
				}
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	private void createTableColumns() {

		// Package name
		TableViewerColumn serviceNameColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SERVICE_NAME"), 250);
		serviceNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return ((ServiceListItem) element).getServiceName();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Package name
		TableViewerColumn serviceStatusColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SERVICE_STAT"), 250);
		serviceStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return Messages.getString(((ServiceListItem) element).getServiceStatus());
				}
				return Messages.getString("UNTITLED");
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof ServiceListItem) {
					if (Messages.getString(((ServiceListItem) element).getServiceStatus()).equals("Aktif")
							|| Messages.getString(((ServiceListItem) element).getServiceStatus()).equals("Active")) {
						return activeImage;
					} else
						return inactiveImage;
				}
				return null;
			}
		});
		// Desired status
		TableViewerColumn desiredStatusColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DESIRED_SERVICE_STAT"), 250);
		desiredStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return ((ServiceListItem) element).getDesiredServiceStatus().getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});
		desiredStatusColumn.setEditingSupport(new StatusEditingSupport(tableViewer));

		// Package name
		TableViewerColumn autoStartColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("START_AUTO"), 250);
		autoStartColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return Messages.getString(((ServiceListItem) element).getStartAuto());
				}
				return Messages.getString("UNTITLED");
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof ServiceListItem) {
					if (Messages.getString(((ServiceListItem) element).getStartAuto()).equals("Aktif")
							|| Messages.getString(((ServiceListItem) element).getStartAuto()).equals("Active")) {
						return activeImage;
					} else
						return inactiveImage;
				}
				return null;
			}
		});

		// Desired status
		TableViewerColumn desiredStartAutoColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DESIRED_START_AT_BEGINNING"), 250);
		desiredStartAutoColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return ((ServiceListItem) element).getDesiredStartAuto().getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});
		desiredStartAutoColumn.setEditingSupport(new StartAutoEditingSupport(tableViewer));

	}

	private void getServices() {
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
					getPluginVersion(), "GET_SERVICES", null, null, null, new Date());
			TaskRestUtils.execute(task);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setText(Messages.getString("FILTER"));

		// Filter table rows
		txtFilter = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtFilter.setToolTipText(Messages.getString("FILTER"));
		txtFilter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtFilter.getText());
				tableViewer.refresh();
			}
		});
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		TableItem[] items = tableViewer.getTable().getItems();
		for (TableItem tableItem : items) {
			if (!tableItem.getText(2).equals(Messages.getString("NA"))) {
				return;
			} else if (!tableItem.getText(4).equals(Messages.getString("NA"))) {
				return;
			}
		}
		throw new ValidationException(Messages.getString("FIRST_YOU_SHOULD_SELECT_AT_LEAST_AN_OPTION"));
	}

	@Override
	public Map<String, Object> getParameterMap() {
		java.util.HashMap<String, Object> parameters = new HashMap<String, Object>();
		List<ServiceListItem> list = new ArrayList<>();
		TableItem[] items = tableViewer.getTable().getItems();
		for (TableItem tableItem : items) {
			if (!tableItem.getText(2).equals(Messages.getString("NA"))
					|| !tableItem.getText(4).equals(Messages.getString("NA"))) {
				ServiceListItem item = new ServiceListItem();
				item.setServiceName(tableItem.getText(0).toString());
				if (!tableItem.getText(2).equals(Messages.getString("NA"))) {// if Desired Servicestatus  exists
					item.setServiceStatus(tableItem.getText(2).toString());
				}
				if (!tableItem.getText(4).equals(Messages.getString("NA"))) {// if DesiredStartAtuo exists
					item.setStartAuto(tableItem.getText(4).toString());
				}
				list.add(item);
			}
		}
		parameters.put(ServiceConstants.SERVICE_REQUESTS_PARAMETERS, list);
		return parameters;
	}

	@Override
	public String getCommandId() {
		return "SERVICE_LIST";
	}

	@Override
	public String getPluginName() {
		return ServiceConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ServiceConstants.PLUGIN_VERSION;
	}

	public ServiceListItem getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(ServiceListItem selectedService) {
		this.selectedService = selectedService;
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("SERVICE_LIST", 100);
					try {
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = TaskRestUtils.getResponseData(taskStatus.getResult().getId());
						final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
								});
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										if (getProgressBar() != null) {
											getProgressBar().setVisible(false);
										}
									}
								});
								
								if (responseData != null && responseData.containsKey("ResultMessage")) {
									getServices();
								} else if (responseData != null && responseData.containsKey("service_list")) {
									List<HashMap<String, Object>> tableItems = cast(responseData.get("service_list"));
									List<ServiceListItem> items = new ArrayList<>();
									for (HashMap<String, Object> map : tableItems) {
										ServiceListItem item = new ServiceListItem();
										item.setServiceName(map.get("serviceName").toString());
										item.setServiceStatus(map.get("serviceStatus").toString());
										item.setStartAuto(map.get("startAuto").toString());
										item.setDesiredServiceStatus(DesiredStatus.NA);
										item.setDesiredStartAuto(DesiredStatus.NA);
										items.add(item);
									}
									if (items != null) {
										tableViewer.setInput(items);
										tableViewer.refresh();
									}
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_SERVICES"));
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

	@SuppressWarnings("unchecked")
	public static <HashMap extends List<?>> HashMap cast(Object obj) {
		return (HashMap) obj;
	}
	
	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(1200,800);
	}
	
	/**
	 * column width number must be same column name 
	 */
	@Override
	public PdfContent getPdfContent() {
		
		String[] columnNames={Messages.getString("SERVICE_NAME"),Messages.getString("SERVICE_STAT"),Messages.getString("START_AUTO")};
		float[] columnWidths={1,1,1};
		
		List<String[]> dataList=new ArrayList<>();
		
		
		for (TableItem tItem : tableViewer.getTable().getItems()) {
			
			ServiceListItem item=(ServiceListItem) tItem.getData();
			
			String[] row={item.getServiceName(),Messages.getString(item.getServiceStatus()),Messages.getString(item.getStartAuto())};
			dataList.add(row);
		} 
		
		PdfContent cont= new PdfContent("Servis Listesi","Servis Listesi",columnNames,columnWidths,dataList);
		
		return cont;
		
	}
}
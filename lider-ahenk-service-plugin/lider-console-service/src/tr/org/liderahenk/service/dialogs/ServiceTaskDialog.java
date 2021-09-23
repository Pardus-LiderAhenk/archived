package tr.org.liderahenk.service.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.model.Command;
import tr.org.liderahenk.liderconsole.core.model.CommandExecution;
import tr.org.liderahenk.liderconsole.core.model.CommandExecutionResult;
import tr.org.liderahenk.liderconsole.core.model.PdfContent;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.StatusCode;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskNotification;
import tr.org.liderahenk.service.constants.ServiceConstants;
import tr.org.liderahenk.service.editingsupport.StatusEditingSupport;
import tr.org.liderahenk.service.i18n.Messages;
import tr.org.liderahenk.service.model.DesiredStatus;
import tr.org.liderahenk.service.model.ServiceListItem;

/**
 * Task execution dialog for service plugin.
 * 
 */
public class ServiceTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ServiceTaskDialog.class);

	private Label lblServiceName;
	private Text txtServiceName;
	// private static final String[] serviceStatArray = new String[] { "NA",
	// "Start", "Stop" };
	// private Composite compositeServiceList;
	// private TableViewer tableViewerServiceMonitor;
	private TableViewer tableViewerServiceManage;
	private Table table;
	private Button btnAddService;
	private List<ServiceListItem> serviceListForScreen;
	private List<ServiceListItem> serviceListForTask;
	private TabFolder tabFolder;
	private TabItem tbtmServiceManage;
	private Composite compositeServiceListManage;
//	private Button btnDeleteServiceManage;
	private Button btnManageService;
	private List<String> dnList;
	private Button btnStartAll;
	private Button btnStopAll;
	private TableFilter tableFilter;
//	private Button btnRefresh;
	private Text textFilter;
	private Button buttonNone;
	
	private Timer timer = null;

	private boolean monitoringOnly=false;

	public ServiceTaskDialog(Shell parentShell, Set<String> dnSet) {
		
		super(parentShell, dnSet, false, true,false,true);
		serviceListForScreen = new ArrayList<>();
		serviceListForTask= new ArrayList<>();

		//subscribeEventHandler(taskStatusNotificationHandler);
		
		subscribeEventHandler(getPluginName().toUpperCase(Locale.ENGLISH) + "_TASK_NOTIFICATION", taskStatusNotificationHandler);
		
		parentShell.addListener(SWT.CLOSE, new Listener() {
			
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if (timer != null) {
					timer.cancel();
					timer.purge();
				}
				
			}
		});

		dnList = new ArrayList<String>(dnSet);
	}

	public ServiceTaskDialog( Shell shell, Command command) 
	{
		super(shell, new HashSet<String>(command.getDnList()), true,false,false,true);
		timer = new Timer();
		timer.schedule(new CheckResults(command.getTask().getId()), 0, 30000);
		monitoringOnly = true;
	}
	

	@Override
	public String createTitle() {
		return Messages.getString("SERVICE_MANAGEMENT_TITLE");
	}


	private void getServices() {
		try {

			TaskRequest task = new TaskRequest(dnList, DNType.AHENK, getPluginName(), getPluginVersion(),
					"GET_SERVICES_FROM_DB", null, null, null, new Date());
			IResponse response = TaskRestUtils.execute(task);
			Map<String, Object> resultMap = response.getResultMap();
			ObjectMapper mapper = new ObjectMapper();
			if (resultMap != null) {
				List<ServiceListItem> services = mapper.readValue(
						mapper.writeValueAsString(resultMap.get("serviceList")),
						new TypeReference<List<ServiceListItem>>() {
						});

				if (services != null) {
					serviceListForScreen = services;
					if (!tableViewerServiceManage.getTable().isDisposed()) {
						tableViewerServiceManage.setInput(serviceListForScreen);
						tableViewerServiceManage.refresh();
					}
					// tableViewerServiceMonitor.setInput(serviceList);
					// tableViewerServiceMonitor.refresh();
				}
			}
		} catch (Exception e) {
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
			e.printStackTrace();
		}
	}

	
	@Override
	public Control createTaskDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		// gd.widthHint = 900;
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(5, false));

		lblServiceName = new Label(composite, SWT.NONE);
		lblServiceName.setText(Messages.getString("SERVICE_NAME"));

		txtServiceName = new Text(composite, SWT.BORDER);
		GridData gd_txtServiceName = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd_txtServiceName.widthHint = 139;
		txtServiceName.setLayoutData(gd_txtServiceName);
		txtServiceName.setToolTipText("Örn: ssh");

		btnAddService = new Button(composite, SWT.NONE);
		btnAddService.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String serviceName = txtServiceName.getText().toString();
				
				if(serviceName.equals("")){
					return;
					
				}
				
				boolean isExst = false;
				for (ServiceListItem serviceListItem : serviceListForTask) {
					if (serviceListItem.getServiceName().equals(serviceName))
						isExst = true;
				}

				if (!isExst){
					ServiceListItem serviceItemForTask = new ServiceListItem();
					serviceItemForTask.setServiceName(serviceName);
					serviceItemForTask.setDesiredServiceStatus(DesiredStatus.NA);
					serviceListForTask.add(serviceItemForTask);
				}
				
				for (int i = 0; i < dnList.size(); i++) {

					ServiceListItem item = new ServiceListItem();
					item.setServiceName(serviceName);
					item.setAgentDn(dnList.get(i));
					item.setDesiredServiceStatus(DesiredStatus.NA);

					boolean isExist = false;
					for (ServiceListItem serviceListItem : serviceListForScreen) {
						if (serviceListItem.getServiceName().equals(serviceName)
								&& serviceListItem.getAgentDn().equals(dnList.get(i)))
							isExist = true;
					}

					if (!isExist)
						serviceListForScreen.add(item);
				}

				
//				List<String> mockDnList=new ArrayList<>();
//				for (String string : dnList) {
//					mockDnList.add(string);
//				}
//				
//				for (int i = 0; i < 500; i++) {
//					
//					mockDnList.add("Ahenk_"+i);
//				}
//				
//				for (int i = 0; i < mockDnList.size(); i++) {
//
//					ServiceListItem item = new ServiceListItem();
//					item.setServiceName(serviceName);
//					item.setAgentDn(mockDnList.get(i));
//					item.setDesiredServiceStatus(DesiredStatus.NA);
//
//					boolean isExist = false;
//					for (ServiceListItem serviceListItem : serviceList) {
//						if (serviceListItem.getServiceName().equals(serviceName)
//								&& serviceListItem.getAgentDn().equals(mockDnList.get(i)))
//							isExist = true;
//					}
//
//					if (!isExist)
//						serviceList.add(item);
//				}

				if (serviceListForScreen != null) {
					tableViewerServiceManage.setInput(serviceListForScreen);
					tableViewerServiceManage.refresh();
				}

			}
		});
		btnAddService.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnAddService.setText(Messages.getString("ADD_SERVICE_BTN"));

//		btnRefresh = new Button(composite, SWT.NONE);
//		btnRefresh.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				getServices();
//			}
//		});
//		btnRefresh.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
//		btnRefresh.setText(Messages.getString("refresh_services")); //$NON-NLS-1$

//		btnDeleteServiceManage = new Button(composite, SWT.NONE);
//		btnDeleteServiceManage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
//		btnDeleteServiceManage.setAlignment(SWT.RIGHT);
//		btnDeleteServiceManage.setText(Messages.getString("DELETE_SERVICE_BTN"));
//
//		btnDeleteServiceManage.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				List<ServiceListItem> 	deletedServiceList= new ArrayList<>();
//
//				TableItem[] selection = tableViewerServiceManage.getTable().getSelection();
//				if (selection.length > 0) {
//
//					for (int j = 0; j < selection.length; j++) {
//						
//						ServiceListItem item = (ServiceListItem) selection[j].getData();
//						item.setDeleted(true);
//						deletedServiceList.add(item);
//
//						if (serviceList != null) {
//							serviceList.remove(item);
//							
//						}
//					}
//					tableViewerServiceManage.setInput(serviceList);
//					tableViewerServiceManage.refresh();
//					
//					
//					try {
//						
//						Map<String, Object> pMap= new HashMap<>();
//						pMap.put("deletedServices", deletedServiceList);
//						
//
//						TaskRequest task = new TaskRequest(null, DNType.AHENK, getPluginName(), getPluginVersion(),
//								"DELETE_SERVICES", pMap, null, null, new Date());
//						IResponse response = TaskRestUtils.execute(task);
//						
//					} catch (Exception ex) {
//						Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
//						ex.printStackTrace();
//					}
//					
//					
//					
//				}
//
//			}
//		});

		// compositeServiceList = new Composite(composite, SWT.NONE);
		// compositeServiceList.setLayout(new GridLayout(1, false));
		// compositeServiceList.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
		// true, false, 4, 1));

		// tabFolder = new TabFolder(compositeServiceList, SWT.NONE);
		// tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
		// 1, 1));
		// tbtmServiceMonitor.setControl(compositeServiceListMonitor);

		// tbtmServiceManage = new TabItem(tabFolder, SWT.NONE);
		// tbtmServiceManage.setText(Messages.getString("DESIRED_SERVICE_STATE"));
		// //$NON-NLS-1$

		compositeServiceListManage = new Composite(composite, SWT.NONE);
		// tbtmServiceManage.setControl(compositeServiceListManage);

		// compositeServiceListMonitor.setLayout(new GridLayout(1, false));

		// btnDeleteServiceMonitor = new Button(compositeServiceListMonitor,
		// SWT.NONE);
		// btnDeleteServiceMonitor.setLayoutData(new GridData(SWT.RIGHT,
		// SWT.CENTER, true, false, 1, 1));
		// btnDeleteServiceMonitor.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// TableItem[] selection =
		// tableViewerServiceMonitor.getTable().getSelection();
		// if (selection.length > 0) {
		//
		// ServiceListItem item = (ServiceListItem) selection[0].getData();
		// item.setDeleted(true);
		//
		// if (serviceList != null) {
		// serviceList.remove(item);
		// tableViewerServiceMonitor.setInput(serviceList);
		// tableViewerServiceMonitor.refresh();
		// }
		// }
		//
		// }
		// });
		// btnDeleteServiceMonitor.setText(Messages.getString("DELETE_SERVICE_BTN"));
		//
		// createServiceMonitorArea(compositeServiceListMonitor);

		compositeServiceListManage.setLayout(new GridLayout(8, false));
		GridData gd_compositeServiceListManage = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_compositeServiceListManage.horizontalSpan = 5;
		compositeServiceListManage.setLayoutData(gd_compositeServiceListManage);

		textFilter = new Text(compositeServiceListManage, SWT.BORDER);
		textFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		
		textFilter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(textFilter.getText());
				tableViewerServiceManage.refresh();
			}
		});

		createServiceManageArea(compositeServiceListManage);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);
		new Label(compositeServiceListManage, SWT.NONE);

		btnManageService = new Button(compositeServiceListManage, SWT.NONE);
		btnManageService.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnManageService.setText(Messages.getString("MANAGE_SERVICES_BTN"));

		btnManageService.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Map<String, Object> parameters = getServiceParams();
				
				TableItem[] items = tableViewerServiceManage.getTable().getSelection();
				
				List<String> dnList=new ArrayList<>();
				
				for (TableItem tableItem : items) {

					ServiceListItem listItem = (ServiceListItem) tableItem.getData();
					dnList.add(listItem.getAgentDn());
				}

				TaskRequest task = new TaskRequest(dnList, DNType.AHENK, getPluginName(),
						getPluginVersion(), "SERVICE_LIST", parameters, null, null, new Date());
				try {
					TaskRestUtils.execute(task);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});

		//getServices();

		return parent;
	}

	public Map<String, Object> getServiceParams() {
		java.util.HashMap<String, Object> parameters = new HashMap<String, Object>();
		List<ServiceListItem> list = new ArrayList<>();
		TableItem[] items = tableViewerServiceManage.getTable().getSelection();
		for (TableItem tableItem : items) {

			ServiceListItem listItem = (ServiceListItem) tableItem.getData();

			if (!(listItem.getDesiredServiceStatus() == DesiredStatus.NA)) {
				ServiceListItem item = new ServiceListItem();
				item.setServiceName(listItem.getServiceName());
				item.setServiceStatus(listItem.getDesiredServiceStatus().toString());
				item.setAgentDn(listItem.getAgentDn());
			//	item.setAgentId(listItem.getAgentId());
				item.setId(listItem.getId());
				item.setServiceMonitoring(listItem.isServiceMonitoring());

				list.add(item);
			}
		}
		parameters.put(ServiceConstants.SERVICE_REQUESTS_PARAMETERS, list);
		return parameters;
	}

	private void createServiceManageArea(Composite parent) {
		new Label(compositeServiceListManage, SWT.NONE);

		btnStopAll = new Button(compositeServiceListManage, SWT.NONE);
		btnStopAll.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnStopAll.setText(Messages.getString("STOP_ALL"));

		btnStopAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableViewerServiceManage.getTable().selectAll();

				TableItem[] items = tableViewerServiceManage.getTable().getItems();
				if (items.length > 0) {

					for (TableItem tableItem : items) {

						ServiceListItem item = (ServiceListItem) tableItem.getData();
						item.setDesiredServiceStatus(DesiredStatus.STOP);

					}
					
					if(genericServiceList!=null && genericServiceList.size()>0){
						tableViewerServiceManage.setInput(genericServiceList);
						tableViewerServiceManage.refresh();
					}

					else if (serviceListForScreen != null) {
						tableViewerServiceManage.setInput(serviceListForScreen);
						tableViewerServiceManage.refresh();
					}

				}

			}
		});

		btnStartAll = new Button(compositeServiceListManage, SWT.NONE);
		btnStartAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnStartAll.setText(Messages.getString("START_ALL"));

		btnStartAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableViewerServiceManage.getTable().selectAll();

				TableItem[] items = tableViewerServiceManage.getTable().getItems();
				if (items.length > 0) {

					for (TableItem tableItem : items) {

						ServiceListItem item = (ServiceListItem) tableItem.getData();
						item.setDesiredServiceStatus(DesiredStatus.START);

					}
					
					if(genericServiceList!=null && genericServiceList.size()>0){
						tableViewerServiceManage.setInput(genericServiceList);
						tableViewerServiceManage.refresh();
					}

					else if (serviceListForScreen != null) {
						tableViewerServiceManage.setInput(serviceListForScreen);
						tableViewerServiceManage.refresh();
					}

				}

			}
		});

		buttonNone = new Button(compositeServiceListManage, SWT.NONE);
		GridData gd_buttonNone = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_buttonNone.widthHint = 75;
		buttonNone.setLayoutData(gd_buttonNone);
		buttonNone.setText(Messages.getString("DEFAULT_ALL"));

		buttonNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewerServiceManage.getTable().selectAll();

				TableItem[] items = tableViewerServiceManage.getTable().getItems();
				if (items.length > 0) {

					for (TableItem tableItem : items) {

						ServiceListItem item = (ServiceListItem) tableItem.getData();
						item.setDesiredServiceStatus(DesiredStatus.NA);

					}

					if(genericServiceList!=null && genericServiceList.size()>0){
						tableViewerServiceManage.setInput(genericServiceList);
						tableViewerServiceManage.refresh();
					}

					else if (serviceListForScreen != null) {
						tableViewerServiceManage.setInput(serviceListForScreen);
						tableViewerServiceManage.refresh();
					}

				}

			}
		});

		tableViewerServiceManage = new TableViewer(parent,
				 SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER| SWT.MULTI);
		configureTableLayout(tableViewerServiceManage);
		Table table_1 = tableViewerServiceManage.getTable();
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 8, 1));

		// Hook up listeners
		tableViewerServiceManage.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// IStructuredSelection selection = (IStructuredSelection)
				// tableViewer.getSelection();
				// Object firstElement = selection.getFirstElement();
				// if (firstElement instanceof ServiceListItem) {
				// setSelectedService((ServiceListItem) firstElement);
				// }
			}
		});
		tableViewerServiceManage.refresh();
		// tableViewerServiceManage =
		// SWTResourceManager.createTableViewer(parent);
		createServiceManageTableColumns();
		
		tableFilter = new TableFilter();
		tableViewerServiceManage.addFilter(tableFilter);
		tableViewerServiceManage.refresh();

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
			
			String status= ((ServiceListItem) element).isServiceMonitoring() ? Messages.getString("IS_MONTORING")
					: Messages.getString("IS_NOT_MONITORING");
			return packageInfo.getServiceName().matches(searchString) 
					|| packageInfo.getAgentDn().matches(searchString)
					|| status.matches(searchString)
					|| packageInfo.getServiceStatus().matches(searchString)
					;
		}

	}

	// private void createServiceMonitorArea(Composite parent) {
	// // createTableFilterArea(parent);
	//
	// tableViewerServiceMonitor = SWTResourceManager.createTableViewer(parent);
	// createServiceMonitorTableColumns();
	//
	// // Hook up listeners
	// tableViewerServiceMonitor.addSelectionChangedListener(new
	// ISelectionChangedListener() {
	// @Override
	// public void selectionChanged(SelectionChangedEvent event) {
	// // IStructuredSelection selection = (IStructuredSelection)
	// // tableViewer.getSelection();
	// // Object firstElement = selection.getFirstElement();
	// // if (firstElement instanceof ServiceListItem) {
	// // setSelectedService((ServiceListItem) firstElement);
	// // }
	// }
	// });
	// tableViewerServiceMonitor.refresh();
	//
	// }

	// private void createServiceMonitorTableColumns() {
	// // Package name
	// TableViewerColumn serviceNameColumn =
	// SWTResourceManager.createTableViewerColumn(tableViewerServiceMonitor,
	// Messages.getString("SERVICE_NAME"), 200);
	// serviceNameColumn.setLabelProvider(new ColumnLabelProvider() {
	// @Override
	// public String getText(Object element) {
	// if (element instanceof ServiceListItem) {
	// return ((ServiceListItem) element).getServiceName();
	// }
	// return Messages.getString("UNTITLED");
	// }
	// });
	//
	// // // Desired status
	// // TableViewerColumn desiredStatusColumn =
	// // SWTResourceManager.createTableViewerColumn(tableViewerServiceMonitor,
	// // Messages.getString("SERVICE_STAT"), 250);
	// // desiredStatusColumn.setLabelProvider(new ColumnLabelProvider() {
	// // @Override
	// // public String getText(Object element) {
	// // if (element instanceof ServiceListItem) {
	// // return ((ServiceListItem)
	// // element).getDesiredServiceStatus().getMessage();
	// // }
	// // return Messages.getString("UNTITLED");
	// // }
	// // });
	// // desiredStatusColumn.setEditingSupport(new
	// // StatusEditingSupport(tableViewerServiceMonitor));
	//
	// // // Desired status
	// // TableViewerColumn desiredStartAutoColumn =
	// // SWTResourceManager.createTableViewerColumn(tableViewer,
	// // Messages.getString("START_AT_BEGINNING"), 250);
	// // desiredStartAutoColumn.setLabelProvider(new ColumnLabelProvider() {
	// // @Override
	// // public String getText(Object element) {
	// // if (element instanceof ServiceListItem) {
	// // return ((ServiceListItem)
	// // element).getDesiredStartAuto().getMessage();
	// // }
	// // return Messages.getString("UNTITLED");
	// // }
	// // });
	// // desiredStartAutoColumn.setEditingSupport(new
	// // StartAutoEditingSupport(tableViewer));
	//
	// }

	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(1200,800);
	}
	private void createServiceManageTableColumns() {
		TableViewerColumn ahenkDnColumn = SWTResourceManager.createTableViewerColumn(tableViewerServiceManage,
				Messages.getString("agent"), 380);
		ahenkDnColumn.getColumn().setAlignment(SWT.LEFT);
		ahenkDnColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {

					String dn = ((ServiceListItem) element).getAgentDn();

					String dnName = "";

					if (dn != null) {

						String[] dnArr = dn.split(",");
						if (dnArr.length > 0)
							dnName = dnArr[0];

					}

					return dnName;
				}
				return Messages.getString("UNTITLED");
			}
		});
		

		// Service name
		TableViewerColumn serviceNameColumn = SWTResourceManager.createTableViewerColumn(tableViewerServiceManage,
				Messages.getString("SERVICE_NAME"), 120);
		serviceNameColumn.getColumn().setAlignment(SWT.LEFT);
		serviceNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return ((ServiceListItem) element).getServiceName();
				}
				return Messages.getString("UNTITLED");
			}
		});


		TableViewerColumn serviceMonitorStatusColumn = SWTResourceManager
				.createTableViewerColumn(tableViewerServiceManage, Messages.getString("SERVICE_MONITOR"), 160);
		serviceMonitorStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {

					return ((ServiceListItem) element).isServiceMonitoring() ? Messages.getString("IS_MONTORING")
							: Messages.getString("IS_NOT_MONITORING");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn serviceStatusColumn = SWTResourceManager.createTableViewerColumn(tableViewerServiceManage,
				Messages.getString("SERVICE_STAT"), 120);

		serviceStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {
					return ((ServiceListItem) element).getServiceStatus();
				}
				return Messages.getString("UNTITLED");
			}
			
			@Override
			public Color getBackground(Object element) {
				return element instanceof ServiceListItem && ((ServiceListItem) element).getServiceStatus() != null
						&& ((ServiceListItem) element).getServiceStatus().equals("Stopped")
								? SWTResourceManager.getErrorColor() : null;
			}
		});

		// Desired status
		TableViewerColumn desiredStatusColumn = SWTResourceManager.createTableViewerColumn(tableViewerServiceManage,
				Messages.getString("SERVICE_MANAGE"), 120);
		desiredStatusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ServiceListItem) {

					if (((ServiceListItem) element).getDesiredServiceStatus() == null)
						((ServiceListItem) element).setDesiredServiceStatus(DesiredStatus.NA);

					return ((ServiceListItem) element).getDesiredServiceStatus().getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});
		desiredStatusColumn.setEditingSupport(new StatusEditingSupport(tableViewerServiceManage));

	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
//		if (tableViewerServiceManage.getTable().getItems().length == 0) {
//			throw new ValidationException(Messages.getString("PLEASE_ENTER_SERVICE_NAME"));
//		}
	}

	@Override
	public Map<String, Object> getParameterMap() {

		java.util.HashMap<String, Object> parameters = new HashMap<String, Object>();
//		List<ServiceListItem> list = new ArrayList<>();
//		TableItem[] items = tableViewerServiceManage.getTable().getItems();
//		for (TableItem tableItem : items) {
//			ServiceListItem item = (ServiceListItem) tableItem.getData();
//			list.add(item);
//		}
		
		parameters.put(ServiceConstants.SERVICE_MANAGE_PARAM, serviceListForTask);
		return parameters;

	}

	@Override
	public String getCommandId() {
		return "SERVICE_MANAGEMENT";
	}

	@Override
	public String getPluginName() {
		return ServiceConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ServiceConstants.PLUGIN_VERSION;
	}

	@Override
	public String getMailSubject() {

		return "Servis Alarm";
	}

	private static void configureTableLayout(TableViewer tableViewer) {
		// Configure table properties
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.getVerticalBar().setEnabled(true);
		table.getVerticalBar().setVisible(true);
		// Set content provider
		tableViewer.setContentProvider(new ArrayContentProvider());
		// Configure table layout
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 420;
		gridData.widthHint = 600;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("SERVICE_MANAGEMENT", 100);
					
					try {
						final TaskNotification task = (TaskNotification) event.getProperty("org.eclipse.e4.data");
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								Long taskId = task.getCommand().getTask().getId();
								// Dispose previous timer if exists
								onClose();
								timer = new Timer();
								timer.schedule(new CheckResults(taskId), 0, 30000);
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR"));
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
					
					
//					final TaskStatusNotification taskStatusNotification = (TaskStatusNotification) event.getProperty("org.eclipse.e4.data");
//					
//					try {
//						Display.getDefault().asyncExec(new Runnable() {
//
//							@Override
//							public void run() {
//
//								//getServices();
//								
//								CommandExecutionResult commandExecutionResult = taskStatusNotification.getResult();
//							String responseMessage=	commandExecutionResult.getResponseMessage();
//							byte[]	 data= commandExecutionResult.getResponseData();
//							
//							Map<String, Object> responseData = null;
//							try {
//								responseData = new ObjectMapper().readValue(data, 0, data.length,
//										new TypeReference<HashMap<String, Object>>() {
//										});
//								
//								if (responseData != null && responseData.containsKey("services")) {
//									List<HashMap<String, Object>> tableItems = (List<HashMap<String, Object>>) responseData.get("services");
//									List<ServiceListItem> items = new ArrayList<>();
//									for (HashMap<String, Object> map : tableItems) {
//										ServiceListItem item = new ServiceListItem();
//										item.setAgentDn(map.get("agentDn").toString());
//										item.setServiceName(map.get("serviceName").toString());
//										item.setServiceStatus(map.get("serviceStatus").toString());
//										//item.setStartAuto(map.get("startAuto").toString());
//										item.setDesiredServiceStatus(DesiredStatus.NA);
//										//item.setDesiredStartAuto(DesiredStatus.NA);
//										items.add(item);
//									}
//									if (items != null) {
//										tableViewerServiceManage.setInput(items);
//										tableViewerServiceManage.refresh();
//									}
//								
//							} 
//							
//							}catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//								// Dispose previous timer if exists
////								onClose();
////								timer = new Timer();
////								timer.schedule(new CheckResults(taskId), 0, 15000);
//
//							}
//						});
//					} catch (Exception e) {
//						logger.error(e.getMessage(), e);
//						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_SERVICES"));
//					}
//					monitor.worked(100);
//					monitor.done();
//
//					return Status.OK_STATUS;
//				}
//			};
//
//			job.setUser(true);
//			job.schedule();
//		}
//	};


	@Override
	public String getMailContent() {

		return "cn={ahenk} tanımlamış olduğunuz aşağıdaki servisler durmuştur. \n {stopped_services} ";
	}
	
	
	@Override
	protected void onClose() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			System.out.println("TIMER CLOSED");
		}
	}
	
	List<ServiceListItem> genericServiceList;
	
	String operation="";
	
	protected class CheckResults extends TimerTask {

		Long taskId = null;

		public CheckResults(Long taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			try {
				
				final Command command = TaskRestUtils.getCommand(this.taskId);
				if(command.getTask()!=null){
					operation=command.getTask().getCommandClsId();
					System.out.println("OPERATION" + operation);
				}
				if(operation.equals(("SERVICE_MANAGEMENT"))){
				
				if (command != null && command.getCommandExecutions() != null) 
					genericServiceList = new ArrayList<ServiceListItem>();
			
					for (CommandExecution exec : command.getCommandExecutions()) {
						List<CommandExecutionResult> results = exec.getCommandExecutionResults();
						
						if (results != null && !results.isEmpty()) {
							
							for (CommandExecutionResult result : results) {
								
								if (result!=null && result.getResponseCode()!=null && result.getResponseCode() == StatusCode.TASK_PROCESSED) {
									
									byte[] data = result.getResponseData();
									Map<String, Object> responseData = null;
									
									if(data!=null) 
									{
											responseData = new ObjectMapper().readValue(data, 0, data.length,
													new TypeReference<HashMap<String, Object>>() {
													});
											
											if (responseData != null && responseData.containsKey("services")) {
											List<HashMap<String, Object>> tableItems = (List<HashMap<String, Object>>) responseData.get("services");
											for (HashMap<String, Object> map : tableItems) {
												ServiceListItem item = new ServiceListItem();
												item.setAgentDn(map.get("agentDn")!=null ? map.get("agentDn").toString(): "");
												item.setServiceName(map.get("serviceName")!=null ? map.get("serviceName").toString() :  "");
												item.setServiceStatus(map.get("serviceStatus").toString());
												item.setServiceMonitoring(map.get("isServiceMonitoring")!=null ? (boolean)map.get("isServiceMonitoring") : true);
												//item.setStartAuto(map.get("startAuto").toString()); 
												item.setDesiredServiceStatus(DesiredStatus.NA);
												item.setCreateDate(result.getCreateDate());
												//item.setDesiredStartAuto(DesiredStatus.NA); 
												
												ServiceListItem lastServiceItem=null;
												
												for (int i = 0; i < genericServiceList.size(); i++) {
													ServiceListItem itemTmp= genericServiceList.get(i);
													
													if(item.getAgentDn().equals(itemTmp.getAgentDn()) && item.getServiceName().equals(itemTmp.getServiceName())){
														
														if(item.getCreateDate().before(itemTmp.getCreateDate())){
															lastServiceItem=itemTmp;
															genericServiceList.remove(itemTmp);
															
														}
														else lastServiceItem=item;
														
													}
												}
												if(lastServiceItem!=null){
												
												genericServiceList.add(lastServiceItem);
												}
												else
												genericServiceList.add(item);
											}
											}
										
									}
								} 
								
							}
						}
					
						serviceListForScreen=genericServiceList;
					
//					if(completedCount==command.getCommandExecutions().size()){
//						timer.cancel();
//					}
					}
				
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						
						if (operation.equals(("SERVICE_MANAGEMENT")) &&genericServiceList != null && tableViewerServiceManage!=null && !tableViewerServiceManage.getTable().isDisposed()) {
							System.out.println("table refreshing");
							tableViewerServiceManage.setInput(genericServiceList);
							tableViewerServiceManage.refresh();
						}
						
					}
				});
				}
			
			} 
			catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
	};
	
	
	/**
	 * column width number must be same column name 
	 */
	@Override
	public PdfContent getPdfContent() {
		
		String[] columnNames={Messages.getString("agent"),Messages.getString("SERVICE_NAME"),Messages.getString("SERVICE_MONITOR"),Messages.getString("SERVICE_STAT")};
		float[] columnWidths={1,1,1,1};
		
		List<String[]> dataList=new ArrayList<>();
		
		
		for (TableItem tItem : tableViewerServiceManage.getTable().getItems()) {
			
			ServiceListItem item=(ServiceListItem) tItem.getData();
			
			String dn = item.getAgentDn();

			String dnName = "";

			if (dn != null) {

				String[] dnArr = dn.split(",");
				if (dnArr.length > 0)
					dnName = dnArr[0];

			}
			
			String[] row={dnName,item.getServiceName(), item.isServiceMonitoring() ? Messages.getString("IS_MONTORING")
					: Messages.getString("IS_NOT_MONITORING") ,item.getServiceStatus()};
			dataList.add(row);
		} 
		
		PdfContent cont= new PdfContent("Servis İzleme Listesi","Servis İzleme Listesi",columnNames,columnWidths,dataList);
		
		return cont;
		
	}
}

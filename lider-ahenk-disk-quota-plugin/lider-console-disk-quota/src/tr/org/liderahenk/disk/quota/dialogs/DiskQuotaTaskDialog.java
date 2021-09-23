package tr.org.liderahenk.disk.quota.dialogs;

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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class DiskQuotaTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(DiskQuotaTaskDialog.class);
	
	private TableViewer viewer;
	private TableItem item;
	
	private String user;
	private String softQuota;
	private String hardQuota;
	private String diskUsage;
	
	private final String[] columnTitles = new String[] { "USER", "SOFT_QUOTA", "HARD_QUOTA", "DISK_USAGE" };

	public DiskQuotaTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		subscribeEventHandler(eventHandler);
		getData();
	}

	@Override
	public String createTitle() {
		return Messages.getString("CURRENT_QUOTA");
	}
	
	private void getData() {

		try {
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), getCommandId(), null, null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			Notifier.error(null, Messages.getString("ERROR_WHEN_GET_DATA"));
		}
	}
	
	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("DISK-QUOTA", 100);
					try {
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = taskStatus.getResult().getResponseData();
						final Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
						});
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								if (responseData != null && !responseData.isEmpty() && responseData.containsKey("users")) {
									
									@SuppressWarnings({ "unchecked" })
									List<Map<String, Object>> usersList = (List<Map<String, Object>>) responseData.get("users");
									
									for (Map<String, Object> userMap : usersList) {
										user = (String) userMap.get("user");
										softQuota = (String) userMap.get("soft_quota");
										hardQuota = (String) userMap.get("hard_quota");
										diskUsage = (String) userMap.get("disk_usage");
										
										item = new TableItem(viewer.getTable(), SWT.NONE);
									    item.setText(0, user);
									    item.setText(1, softQuota);
									    item.setText(2, hardQuota);
									    item.setText(3, diskUsage);
									}
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("ERROR_WHEN_GET_DATA"));
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
	private Text textSearch;

	private TableFilter tableFilter;

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		textSearch = new Text(composite, SWT.BORDER);
		textSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		
		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL
		        | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		createColumns(composite, viewer);
		
		Table table = viewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    // define layout for the viewer
	    GridData gridData = new GridData();
	    gridData.horizontalSpan = 5;
	    gridData.verticalAlignment = GridData.FILL;
	    gridData.grabExcessHorizontalSpace = true;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.horizontalAlignment = GridData.FILL;
	    gridData.heightHint = 300;
	    viewer.getControl().setLayoutData(gridData);
	    
	   tableFilter=new TableFilter();
	    
	    viewer.addFilter(new TableFilter());
	    
	    textSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(textSearch.getText());
				viewer.refresh();
			}
		});
	    
		return null;
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
			
			return ((String) element).matches(searchString);
		}

	}
	
	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		int[] bounds = { 120, 120, 120, 120 };

		for (int i = 0; i < columnTitles.length; i++) {
			createTableViewerColumn(Messages.getString(columnTitles[i]), bounds[i], i);
		}
	}
	
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		 final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
			        SWT.NONE);
		 final TableColumn column = viewerColumn.getColumn();
		 column.setText(title);
		 column.setWidth(bound);
		 column.setResizable(true);
		 column.setMoveable(true);
		 return viewerColumn;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		return null;
	}

	@Override
	public String getCommandId() {
		return "GET_QUOTA";
	}

	@Override
	public String getPluginName() {
		return DiskQuotaConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return DiskQuotaConstants.PLUGIN_VERSION;
	}

}

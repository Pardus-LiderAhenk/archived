package tr.org.liderahenk.resourceusage.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.resourceusage.constants.ResourceUsageConstants;
import tr.org.liderahenk.resourceusage.i18n.Messages;
import tr.org.liderahenk.resourceusage.model.ResourceUsageTableItem;
import tr.org.liderahenk.resourceusage.tabs.AlertListTab;
import tr.org.liderahenk.resourceusage.tabs.DataListTab;

/**
 * Task execution dialog for resource-usage plugin.
 * 
 */
public class ResourceUsageAlertTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ResourceUsageAlertTaskDialog.class);

	private DataListTab dataList;
	private AlertListTab alarmList;

	public ResourceUsageAlertTaskDialog(Shell parentShell, String dnString) {
		super(parentShell, dnString);
		this.dataList = new DataListTab();
		this.alarmList = new AlertListTab();
		subscribeEventHandler(eventHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("COMPLEX_EVENT_PROCESSING");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		try {

			Composite composite = new Composite(parent, SWT.NONE);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
			gridData.widthHint = 1400;
			gridData.heightHint = 600;
			composite.setLayoutData(gridData);
			composite.setLayout(new GridLayout(1, false));
			CTabFolder tabFolder = createTabFolder(composite);
			tabFolder.setSize(1300, 500);
			dataList.createTab(createInputTab(tabFolder, Messages.getString("DATA_LIST"), true), getDnSet(),
					getPluginName(), getPluginVersion());

			alarmList.createTab(createInputTab(tabFolder, Messages.getString("ALERT_LIST"), true), getDnSet(),
					getPluginName(), getPluginVersion());

			tabFolder.setSelection(0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	private CTabFolder createTabFolder(final Composite composite) {
		CTabFolder tabFolder = new CTabFolder(composite, SWT.BORDER);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		return tabFolder;
	}

	private Composite createInputTab(CTabFolder tabFolder, String label, boolean isScrolledComposite) {
		CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
		tab.setText(label);
		Composite composite = isScrolledComposite ? new ScrolledComposite(tabFolder, SWT.V_SCROLL)
				: new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		tab.setControl(composite);
		return composite;
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
		return null;
	}

	@Override
	public String getPluginName() {
		return ResourceUsageConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ResourceUsageConstants.PLUGIN_VERSION;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		Button exitButton = createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CANCEL"), true);
		exitButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataList.finishTaskIfExecuting();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("RESOURCE_INFO_ALERT", 100);
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

								if (responseData != null && !responseData.isEmpty()) {
									if(!responseData.containsKey("shutdown")){
										if (responseData.containsKey(
												"status")) { /*
																 * New request started
																 */
											alarmList.removeTableItems();
										}
										if (responseData.containsKey("memoryUsage") && responseData.containsKey("diskUsage")
												&& responseData.containsKey("cpuPercentage")) {
											ResourceUsageTableItem item = new ResourceUsageTableItem();
											item.setCpuUsed(responseData.get("cpuPercentage").toString());
											item.setMemUsed(responseData.get("memoryUsage").toString());
											List<Object> alerts = dataList.addTableItem(item);
											if (alerts != null && !alerts.isEmpty()) {
												for (Object object : alerts) {
													alarmList.addTableItem(object);
												}
											}
										}
									}
								}
							}
						});
					} catch (Exception e) {
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_RESOURCE_USAGE"));
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
}

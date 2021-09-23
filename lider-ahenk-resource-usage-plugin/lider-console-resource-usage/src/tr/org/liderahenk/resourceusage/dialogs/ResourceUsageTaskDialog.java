package tr.org.liderahenk.resourceusage.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
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
import tr.org.liderahenk.liderconsole.core.widgets.LiderConfirmBox;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.resourceusage.constants.ResourceUsageConstants;
import tr.org.liderahenk.resourceusage.i18n.Messages;

/**
 * Task execution dialog for resource-usage plugin.
 * 
 */
public class ResourceUsageTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ResourceUsageTaskDialog.class);

	private Label lblRecordDate;
	private Label lblRecordDateInfo;
	private Label lblPlatformInfo;
	private Label lblSystem;
	private Label lblSystemInfo;
	private Label lblRelease;
	private Label lblReleaseInfo;
	private Label lblVersion;
	private Label lblVersionInfo;
	private Label lblMachine;
	private Label lblMachineInfo;
	private Label lblProcessor;
	private Label lblProcessorInfo;
	private Label lblCPUInfo;
	private Label lblCPUPhysicalCoreCount;
	private Label lblCPUPhysicalCoreCountInfo;
	private Label lblCPULogicalCoreCount;
	private Label lblCPULogicalCoreCountInfo;
	private Label lblCPUActualHz;
	private Label lblCPUActualHzInfo;
	private Label lblCPUAdvertisedHz;
	private Label lblCPUAdvertisedHzInfo;
	private Label lblMemoryInfo;
	private Label lblTotalMemory;
	private Label lblTotalMemoryInfo;
	private Label lblMemoryUsage;
	private Label lblMemoryUsageInfo;
	private Label lblDiscInfo;
	private Label lblPartitionName;
	private Label lblPartitionNameInfo;
	private Label lblTotalDisc;
	private Label lblTotalDiscInfo;
	private Label lblUsageDisc;
	private Label lblUsageDiscInfo;

	public ResourceUsageTaskDialog(Shell parentShell, String dnString, boolean activation) {
		super(parentShell, dnString, activation);
		subscribeEventHandler(taskStatusNotificationHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("RESOURCE_USAGE");
	}

	private void getData() {

		try {
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), getCommandId(), null, null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
		}
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
		gridData.widthHint = 600;
		composite.setLayoutData(gridData);

		Composite recordInfoComposite = new Composite(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		recordInfoComposite.setLayout(gridLayout);

		lblRecordDate = new Label(recordInfoComposite, SWT.NONE);
		lblRecordDate.setText(Messages.getString("RECORD_DATE"));

		lblRecordDateInfo = new Label(recordInfoComposite, SWT.NONE);
		lblRecordDateInfo.setText(new Date().toString());

		lblPlatformInfo = new Label(composite, SWT.BOLD);
		lblPlatformInfo.setText(Messages.getString("PLATFORM_INFO"));

		Composite platformComposite = new Composite(composite, SWT.BORDER);
		gridLayout.marginWidth = 0;
		platformComposite.setLayout(gridLayout);

		platformComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lblSystem = new Label(platformComposite, SWT.NONE);
		lblSystem.setText(Messages.getString("SYSTEM"));
		lblSystem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblSystemInfo = new Label(platformComposite, SWT.NONE);

		lblRelease = new Label(platformComposite, SWT.NONE);
		lblRelease.setText(Messages.getString("RELEASE"));
		lblRelease.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblReleaseInfo = new Label(platformComposite, SWT.NONE);

		lblVersion = new Label(platformComposite, SWT.NONE);
		lblVersion.setText(Messages.getString("VERSION"));
		lblVersion.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblVersionInfo = new Label(platformComposite, SWT.NONE);

		lblMachine = new Label(platformComposite, SWT.NONE);
		lblMachine.setText(Messages.getString("MACHINE"));
		lblMachine.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblMachineInfo = new Label(platformComposite, SWT.NONE);

		lblProcessor = new Label(platformComposite, SWT.NONE);
		lblProcessor.setText(Messages.getString("PROCESSOR"));
		lblProcessor.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblProcessorInfo = new Label(platformComposite, SWT.NONE);

		lblCPUInfo = new Label(composite, SWT.BOLD);
		lblCPUInfo.setText(Messages.getString("CPU_INFO"));

		Composite cpuComposite = new Composite(composite, SWT.BORDER);
		cpuComposite.setLayout(gridLayout);

		cpuComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lblCPUPhysicalCoreCount = new Label(cpuComposite, SWT.NONE);
		lblCPUPhysicalCoreCount.setText(Messages.getString("CPU_PHYSICAL"));
		lblCPUPhysicalCoreCount.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblCPUPhysicalCoreCountInfo = new Label(cpuComposite, SWT.NONE);

		lblCPULogicalCoreCount = new Label(cpuComposite, SWT.NONE);
		lblCPULogicalCoreCount.setText(Messages.getString("CPU_LOGICAL"));
		lblCPULogicalCoreCount.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblCPULogicalCoreCountInfo = new Label(cpuComposite, SWT.NONE);

		lblCPUActualHz = new Label(cpuComposite, SWT.NONE);
		lblCPUActualHz.setText(Messages.getString("CPU_ACTUAL"));
		lblCPUActualHz.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblCPUActualHzInfo = new Label(cpuComposite, SWT.NONE);

		lblCPUAdvertisedHz = new Label(cpuComposite, SWT.NONE);
		lblCPUAdvertisedHz.setText(Messages.getString("CPU_ADVERTISED"));
		lblCPUAdvertisedHz.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblCPUAdvertisedHzInfo = new Label(cpuComposite, SWT.NONE);

		lblMemoryInfo = new Label(composite, SWT.BOLD);
		lblMemoryInfo.setText(Messages.getString("MEMORY_INFO"));

		Composite memoryComposite = new Composite(composite, SWT.BORDER);
		memoryComposite.setLayout(gridLayout);

		memoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lblTotalMemory = new Label(memoryComposite, SWT.NONE);
		lblTotalMemory.setText(Messages.getString("TOTAL_MEMORY"));
		lblTotalMemory.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblTotalMemoryInfo = new Label(memoryComposite, SWT.NONE);

		lblMemoryUsage = new Label(memoryComposite, SWT.NONE);
		lblMemoryUsage.setText(Messages.getString("USAGE_DISC"));
		lblMemoryUsage.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblMemoryUsageInfo = new Label(memoryComposite, SWT.NONE);

		lblDiscInfo = new Label(composite, SWT.BOLD);
		lblDiscInfo.setText(Messages.getString("DISC_INFO"));

		Composite diskComposite = new Composite(composite, SWT.BORDER);
		diskComposite.setLayout(gridLayout);

		diskComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		lblPartitionName = new Label(diskComposite, SWT.NONE);
		lblPartitionName.setText(Messages.getString("PARTITION_NAME"));
		lblPartitionName.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblPartitionNameInfo = new Label(diskComposite, SWT.NONE);

		lblTotalDisc = new Label(diskComposite, SWT.NONE);
		lblTotalDisc.setText(Messages.getString("TOTAL_DISC"));
		lblTotalDisc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblTotalDiscInfo = new Label(diskComposite, SWT.NONE);

		lblUsageDisc = new Label(diskComposite, SWT.NONE);
		lblUsageDisc.setText(Messages.getString("USAGE"));
		lblUsageDisc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN));

		lblUsageDiscInfo = new Label(diskComposite, SWT.NONE);

		getData();
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {

	}

	@Override
	public Map<String, Object> getParameterMap() {
		return new HashMap<String, Object>();
	}

	@Override
	public String getCommandId() {
		return "RESOURCE_INFO_FETCHER";
	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("RESOURCE_USAGE", 100);
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
								lblSystemInfo.setText(responseData.containsKey("System")
										? responseData.get("System").toString() : "");
								lblSystemInfo.pack();
								lblReleaseInfo.setText(responseData.containsKey("Release")
										? responseData.get("Release").toString() : "");
								lblReleaseInfo.pack();
								lblVersionInfo.setText(responseData.containsKey("Version")
										? responseData.get("Version").toString() : "");
								lblVersionInfo.pack();
								lblMachineInfo.setText(responseData.containsKey("Machine")
										? responseData.get("Machine").toString() : "");
								lblMachineInfo.pack();
								lblProcessorInfo.setText(responseData.containsKey("Processor")
										? responseData.get("Processor").toString() : "");
								lblProcessorInfo.pack();
								lblCPUPhysicalCoreCountInfo.setText(responseData.containsKey("CPU Physical Core Count")
										? responseData.get("CPU Physical Core Count").toString() : "");
								lblCPUPhysicalCoreCountInfo.pack();
								lblCPULogicalCoreCountInfo.setText(responseData.containsKey("CPU Logical Core Count")
										? responseData.get("CPU Logical Core Count").toString() : "");
								lblCPULogicalCoreCountInfo.pack();
								lblCPUActualHzInfo.setText(responseData.containsKey("CPU Actual Hz")
										? responseData.get("CPU Actual Hz").toString() : "");
								lblCPUActualHzInfo.pack();
								lblCPUAdvertisedHzInfo.setText(responseData.containsKey("CPU Advertised Hz")
										? responseData.get("CPU Advertised Hz").toString() : "");
								lblCPUAdvertisedHzInfo.pack();
								lblTotalMemoryInfo.setText(responseData.containsKey("Total Memory")
										? responseData.get("Total Memory").toString() + " MB" : "");
								lblTotalMemoryInfo.pack();
								lblMemoryUsageInfo.setText(responseData.containsKey("Usage")
										? responseData.get("Usage").toString() + " MB" : "");
								lblMemoryUsageInfo.pack();
								lblTotalDiscInfo.setText(responseData.containsKey("Total Disc")
										? responseData.get("Total Disc").toString() + " MB" : "");
								lblTotalDiscInfo.pack();
								lblUsageDiscInfo.setText(responseData.containsKey("Usage Disc")
										? responseData.get("Usage Disc").toString() + " MB" : "");
								lblUsageDiscInfo.pack();
								lblPartitionNameInfo.setText(responseData.containsKey("Device")
										? responseData.get("Device").toString() : "");
								lblPartitionNameInfo.pack();
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
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

	@Override
	public String getPluginName() {
		return ResourceUsageConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ResourceUsageConstants.PLUGIN_VERSION;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Execute task now
		Button btnExecuteNow = createButton(parent, 5000, Messages.getString("REFRESH"), false);
		btnExecuteNow.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/task-play.png"));
		btnExecuteNow.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Validation of task data
				if (validateTaskData()) {
					if (LiderConfirmBox.open(Display.getDefault().getActiveShell(),
							Messages.getString("TASK_EXEC_TITLE"), Messages.getString("TASK_EXEC_MESSAGE"))) {
						try {
							TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK,
									getPluginName(), getPluginVersion(), getCommandId(), getParameterMap(), null, null,
									new Date());
							TaskRestUtils.execute(task);
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
							Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		// Close
				Button closeButton = createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CANCEL"), true);
				closeButton.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						unsubscribeEventHandlers();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
	}

}

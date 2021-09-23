package tr.org.liderahenk.disklimit.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
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

import tr.org.liderahenk.disklimit.constants.DiskLimitConstants;
import tr.org.liderahenk.disklimit.i18n.Messages;
import tr.org.liderahenk.disklimit.model.DiskUsageEntity;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;

/**
 * Task execution dialog for disk-limit plugin.
 * 
 */
public class DiskLimitTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(DiskLimitTaskDialog.class);
	private Text textDiskLimitPercentage;
	private Button btnExecuteNow;
	//private Button btnCheckButtonSave;
	
	private DiskUsageEntity diskUsageEntity;
	
	// TODO do not forget to change this constructor if SingleSelectionHandler is used!
	public DiskLimitTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet,false,true);
		diskUsageEntity=new DiskUsageEntity();
		subscribeEventHandler(taskStatusNotificationHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData  gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		 gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData( gd);
		
//		btnCheckButtonSave = new Button(composite, SWT.CHECK);
//		btnCheckButtonSave.setText(Messages.getString("SAVE"));
		
		
		Label lblDiskLimit = new Label(composite, SWT.NONE);
		
		lblDiskLimit.setText(Messages.getString("disk_limit_percentage"));
		
		textDiskLimitPercentage = new Text(composite, SWT.BORDER);
		GridData gd_textDiskLimitPercentage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textDiskLimitPercentage.widthHint = 135;
		textDiskLimitPercentage.setLayoutData(gd_textDiskLimitPercentage);
		
		
		getDiskUsageList();
		
		return composite;
	}
	
	
	private void getDiskUsageList() {
			try {
				TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
						getPluginVersion(), "GET_DISK_LIMIT", null, null, null, new Date());
				
				IResponse response = TaskRestUtils.execute(task);
				Map<String, Object> resultMap = response.getResultMap();
				ObjectMapper mapper = new ObjectMapper();

				List<DiskUsageEntity> diskUsageEntityList = mapper.readValue(mapper.writeValueAsString(resultMap.get("diskUsageList")),
						new TypeReference<List<DiskUsageEntity>>() {
						});

				if(diskUsageEntityList!=null && diskUsageEntityList.size()>0){
					diskUsageEntity=diskUsageEntityList.get(0);
					textDiskLimitPercentage.setText(Double.toString(diskUsageEntity.getLimitation()));
				}

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
			}
		
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		
		if(textDiskLimitPercentage.getText().equals("")) throw new ValidationException(Messages.getString("FILL_FIELDS"));
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		
		
		Map<String, Object> map = new HashMap<>();
		
		String diskLimitPercentage = textDiskLimitPercentage.getText();
		diskUsageEntity.setLimitation(new Double(diskLimitPercentage));
		
		
		//boolean saveActive= btnCheckButtonSave.getSelection();
		
		map.put("diskUsageEntity", diskUsageEntity );
		
		
		return map;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public String getCommandId() {
		return "SET_DISK_LIMIT";
	}

	@Override
	public String getPluginName() {
		return DiskLimitConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return DiskLimitConstants.PLUGIN_VERSION;
	}
	
	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("GET_DISK_LIMIT", 100);
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
								
								String result=(String) responseData.get("Result");
								System.out.println(result);
								
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("","Hata Oluştu");
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
	public String getMailSubject() {
		
		return "Disk Doluluk Alarm";
	}

	@Override
	public String getMailContent() {
		
		return "cn={ahenk} ahenkde tanımlamış olduğunuz {limit} disk doluluk oranı aşılmıştır. Disk {usage} oranında kullanılmıştır.";
	}
	
	public boolean isMailSendMust() {
		return true;
	}
	
}

package tr.org.liderahenk.remote.access.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.remote.access.constants.RemoteAccessConstants;
import tr.org.liderahenk.remote.access.i18n.Messages;
import tr.org.liderahenk.remote.access.listeners.RemoteAccessConnection;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:tuncay.colak@tubitak.gov.tr">Tuncay ÇOLAK</a>
 */
public class RemoteAccessTaskDialog extends DefaultTaskDialog {
	
	private Combo cmbPerClick;

	private static final Logger logger = LoggerFactory.getLogger(RemoteAccessTaskDialog.class);

	public RemoteAccessTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		subscribeEventHandler(taskStatusNotificationHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("ESTABLISH_REMOTE_ACCESS");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		GridData gData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gData.widthHint = SWT.DEFAULT;
		gData.heightHint = 50;
		composite.setLayoutData(gData);
		
		Label lblUserPermission = new Label(composite, SWT.NONE);
		
		lblUserPermission.setText(Messages.getString("USER_PERMISSION"));
		cmbPerClick = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbPerClick.setSize(10, 10 );
		String items [] = {Messages.getString("YES"), Messages.getString("NO"), Messages.getString("CONNECT_WITHOUT_NOTIFY")};
		cmbPerClick.setItems(items);
		cmbPerClick.select(0);
		return composite;
	}

		
	@Override
	public void validateBeforeExecution() throws ValidationException {

	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> params= new HashMap<>();
		
//		params.put("file-content", textMessage.getText());
		if (cmbPerClick.getSelectionIndex() == 0) {
			
			params.put(RemoteAccessConstants.PARAMETERS.USER_PERMISSION, "yes");
		}
		else if (cmbPerClick.getSelectionIndex() == 1) {
			params.put(RemoteAccessConstants.PARAMETERS.USER_PERMISSION, "no");
		} 
		else {
			params.put(RemoteAccessConstants.PARAMETERS.USER_PERMISSION, "without_notify");
		}
		return params;
			
	}

	@Override
	public String getCommandId() {
		return "SETUP-VNC-SERVER";
	}

	@Override
	public String getPluginName() {
		return RemoteAccessConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return RemoteAccessConstants.PLUGIN_VERSION;
	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("VNC", 100);
					try {
						TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						byte[] data = taskStatus.getResult().getResponseData();
						Map<String, Object> responseData = new ObjectMapper().readValue(data, 0, data.length,
								new TypeReference<HashMap<String, Object>>() {
								});

						// Host may have contain multiple IP addresses
						String[] ipAddresses = ((String) responseData.get("host")).split(",");
						for (int i = 0; i < ipAddresses.length; i++) {
							String ipAddress = ipAddresses[i];
							try {
								// Try to connect VNC via
								// {ipAddress,port,password}
								// provided from agent itself.
								RemoteAccessConnection.invoke(ipAddress, (String) responseData.get("port"),
										(String) responseData.get("password"));
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								if (i == ipAddresses.length - 1) {
									Notifier.error("", Messages.getString("COULD_NOT_CONNECT_VNC"));
								} else {
									Notifier.error("", Messages.getString("COULD_NOT_CONNECT_VNC_TRY_ANOTHER"));
								}
							}
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_CONNECTING_VNC"));
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

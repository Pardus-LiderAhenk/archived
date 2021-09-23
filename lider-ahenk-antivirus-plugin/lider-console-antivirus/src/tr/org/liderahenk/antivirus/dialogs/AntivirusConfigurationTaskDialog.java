package tr.org.liderahenk.antivirus.dialogs;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import tr.org.liderahenk.antivirus.constants.AntivirusConstants;
import tr.org.liderahenk.antivirus.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;

/**
 * Task execution dialog for antivirus plugin.
 * 
 */
public class AntivirusConfigurationTaskDialog extends DefaultTaskDialog {

	private Label lblConfiguration;
	private Text txtConfiguration;

	public AntivirusConfigurationTaskDialog(Shell parentShell, String dn, boolean activation) {
		super(parentShell, dn, activation);
		subscribeEventHandler(eventHandler);
		getData();
	}

	private void getData() {
		try {
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), "ANTIVIRUS_CONFIGURATION", null, null, null, new Date());
			TaskRestUtils.execute(task);
		} catch (Exception e1) {
			Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
		}
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("ANTIVIRUS_CONFIGURATION", 100);
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
								if (responseData != null && responseData.containsKey("antivirusConfiguration")) {
									txtConfiguration.setText(responseData.get("antivirusConfiguration").toString());
									txtConfiguration.pack();
								}
							}
						});
					} catch (Exception e) {
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_ANTIVIRUS_CONFIGURATION_INFO"));
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
	public String createTitle() {
		return Messages.getString("ANTIVIRUS_CONFIGURATION_TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 600;
		gridData.heightHint = 500;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));

		lblConfiguration = new Label(composite, SWT.NONE);
		lblConfiguration.setText(Messages.getString("ANTIVIRUS_CONFIGURATION"));

		txtConfiguration = new Text(composite, SWT.BORDER | SWT.MULTI  |SWT.H_SCROLL | SWT.V_SCROLL);
		txtConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if(txtConfiguration == null || txtConfiguration.getText() == null ||  txtConfiguration.getText().isEmpty()){
			throw new ValidationException(Messages.getString("ERROR_EMPTY_CONFIGURATION"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(AntivirusConstants.CONFIGURATION_PARAMETER, txtConfiguration.getText().toString());
		return parameters;
	}

	@Override
	public String getCommandId() {
		return "ANTIVIRUS_CHANGE_CONFIGURATION";
	}

	@Override
	public String getPluginName() {
		return AntivirusConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return AntivirusConstants.PLUGIN_VERSION;
	}

}

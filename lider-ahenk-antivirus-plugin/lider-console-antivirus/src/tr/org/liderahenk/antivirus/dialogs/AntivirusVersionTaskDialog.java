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
public class AntivirusVersionTaskDialog extends DefaultTaskDialog {

	private Label lblVersion;
	private Text txtVersion;

	public AntivirusVersionTaskDialog(Shell parentShell, String dn, boolean activation) {
		super(parentShell, dn, activation);
		subscribeEventHandler(eventHandler);
		getData();
	}

	private void getData() {
		try {
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), "ANTIVIRUS_VERSION", null, null, null, new Date());
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
					monitor.beginTask("ANTIVIRUS_VERSION", 100);
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
								if (responseData != null && responseData.containsKey("antivirusVersion")) {
									txtVersion.setText(responseData.get("antivirusVersion").toString());
									txtVersion.pack();
								}
							}
						});
					} catch (Exception e) {
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_ACCESSING_PACKAGE_INFO"));
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
		return Messages.getString("ANTIVIRUS_VERSION_TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = SWT.DEFAULT;
		gd.heightHint = SWT.DEFAULT;
		composite.setLayoutData(gd);

		lblVersion = new Label(composite, SWT.NONE);
		lblVersion.setText(Messages.getString("ANTIVIRUS_VERSION"));

		txtVersion = new Text(composite, SWT.BORDER);
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		txtVersion.setEnabled(false);

		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("EXIT"), true);
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
		return "ANTIVIRUS_VERSION";
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

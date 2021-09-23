package tr.org.liderahenk.firewall.dialogs;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.firewall.constants.FirewallConstants;
import tr.org.liderahenk.firewall.i18n.Messages;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class FirewallTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(FirewallTaskDialog.class);
	
	private Text txtRule;
	
	public FirewallTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);
		subscribeEventHandler(eventHandler);
		getData();
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
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("EXIT"), true);
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("FIREWALL", 100);
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
								if (responseData != null && !responseData.isEmpty()
										&& responseData.containsKey("firewallRules")) {
									@SuppressWarnings("unchecked")
									List<String> rules = (List<String>) responseData.get("firewallRules");
									txtRule.setEnabled(true);
									
									String txt = "";
									for (String data : rules) {
										txt = txt.concat(data);
									}
									txtRule.setText(txt);
									txtRule.setEditable(false);
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_WHEN_GET_RULES"));
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
		return Messages.getString("FIREWALL");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Label lblRule = new Label(composite, SWT.NONE);
		lblRule.setText(Messages.getString("EXIST_RULES"));
		
		txtRule = new Text(parent,  SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 200;
		txtRule.setLayoutData(gridData);
		txtRule.setEnabled(false);
		
		return null;
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
		return "GET-RULES";
	}

	@Override
	public String getPluginName() {
		return FirewallConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return FirewallConstants.PLUGIN_VERSION;
	}
	
}

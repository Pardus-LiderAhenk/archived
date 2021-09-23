package tr.org.liderahenk.file.management.dialogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.file.management.constants.FileManagementConstants;
import tr.org.liderahenk.file.management.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.TaskRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
	
/**
 * Task execution dialog for file plugin.
 * 
 */
public class FileManagementTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(FileManagementTaskDialog.class);
	
	ViewerFilter filter;
	
	private Label lblFilePath;
	private Button btnGetFile;
	
	private Text txtFilePath;
	private Text txtFileContent;
	
	private Shell shell;
	
	// TODO do not forget to change this constructor if SingleSelectionHandler is used!
	public FileManagementTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
		shell = parentShell;
		subscribeEventHandler(eventHandler);
	}

	@Override
	public String createTitle() {
		// TODO dialog title
		return Messages.getString("File");
	}

	private EventHandler eventHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("FİLE_TASK", 100);
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
										&& responseData.containsKey("file_exists")) {
									
									if((Boolean)responseData.get("file_exists") == true) {
										String fileContent = (String)responseData.get("file_content");
										txtFileContent.setText(fileContent);
									}
									else {
										MessageDialog.openWarning(shell, Messages.getString("FILE_NOT_FOUND_HEADER"), Messages.getString("FILE_NOT_FOUND_CONTENT"));
									}
								}
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_WHILE_GETTING_FILE"));
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
	public Control createTaskDialogArea(Composite parent) {
		Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayout(new GridLayout(1, false));
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 100;
		composite.setLayout(gridLayout);
		
		GridData data= new GridData(SWT.FILL, SWT.FILL, true, true,1,1);
        data.widthHint=600;
        data.heightHint=500;
		
		composite.setLayoutData(data);
		
        //File Path Label
		lblFilePath = new Label(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 100;
		lblFilePath.setLayoutData(gridData);
		lblFilePath.setText(Messages.getString("FILE_PATH")); //$NON-NLS-1$
        
        //File Path Input
		txtFilePath=new Text(composite, SWT.BORDER);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 95;
		gridData.heightHint = 20;
		txtFilePath.setLayoutData(gridData);
		
		//get file button
		btnGetFile = new Button(composite, SWT.CENTER);
		btnGetFile.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/search.png"));
		//File Name Label
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 5;
		gridData.heightHint = 25;
		btnGetFile.setLayoutData(gridData);
		
		btnGetFile.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Get file if exist
				if(txtFilePath.getText().equals("")) {
					MessageDialog.openWarning(shell, Messages.getString("MISSING_PART"), Messages.getString("FILL_FILE_PATH"));
					throw new ValidationException(Messages.getString("FILL_FILE_PATH"));
				}
				else if(txtFilePath.getText().endsWith("/")) {
					MessageDialog.openWarning(shell, Messages.getString("VALID_FILE_PATH_HEADER"), Messages.getString("VALID_FILE_PATH_CONTENT"));
					throw new ValidationException(Messages.getString("VALID_FILE_PATH_CONTENT"));
				}
				else {
					getFileContent();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				MessageDialog.openWarning(shell, "widgetDefaultSelected", "widgetDefaultSelected");
			}
		});
			
        //File Content Label
		lblFilePath = new Label(composite, SWT.NONE);
		gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gridData.horizontalSpan = 100;
		lblFilePath.setLayoutData(gridData);
		lblFilePath.setText(Messages.getString("FILE_CONTENT")); //$NON-NLS-1$
		
		//File Content 
		txtFileContent = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gridData.horizontalSpan = 100;
		gridData.heightHint = 500;
		txtFileContent.setLayoutData(gridData);
		
		return composite;
	}
	
	@Override
	public void validateBeforeExecution() throws ValidationException {
		// Checking input areas
		if(txtFilePath.getText().equals("")) {
			MessageDialog.openWarning(shell, Messages.getString("MISSING_PART"), Messages.getString("FILL_FILE_PATH"));
			throw new ValidationException(Messages.getString("FILL_FILE_PATH"));
		}
		else if(txtFilePath.getText().endsWith("/")) {
			MessageDialog.openWarning(shell, Messages.getString("VALID_FILE_PATH_HEADER"), Messages.getString("VALID_FILE_PATH_CONTENT"));
			throw new ValidationException(Messages.getString("VALID_FILE_PATH_CONTENT"));
		}
		else if(txtFileContent.getText().equals("")) {
			MessageDialog.openWarning(shell, Messages.getString("MISSING_PART"), Messages.getString("FILL_FILE_CONTENT"));
			throw new ValidationException(Messages.getString("FILL_FILE_CONTENT"));
		}
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> params= new HashMap<>();
		params.put("file-path", txtFilePath.getText());
		params.put("file-content", txtFileContent.getText());
		return params;
	}

	private void getFileContent() {

		try {
			Map<String, Object> taskData = new HashMap<String, Object>();
			taskData.put(FileManagementConstants.FILE_PARAMETERS.FILE_PATH, txtFilePath.getText());
			TaskRequest task = new TaskRequest(new ArrayList<String>(getDnSet()), DNType.AHENK, getPluginName(),
					getPluginVersion(), "GET_FILE_CONTENT", taskData, null, null, new Date());
			TaskRestUtils.execute(task);
			System.err.println(task.getTimestamp());
		} catch (Exception e1) {
			logger.error(e1.getMessage(), e1);
			Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
		}
	}
	
	@Override
	public String getCommandId() {
		// TODO command id which is used to match tasks with ICommand class in the corresponding Lider plugin
		return "WRITE_TO_FILE";
	}

	@Override
	public String getPluginName() {
		return FileManagementConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return FileManagementConstants.PLUGIN_VERSION;
	}
	
}

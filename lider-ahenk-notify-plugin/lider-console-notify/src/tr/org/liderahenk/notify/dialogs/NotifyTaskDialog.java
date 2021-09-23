package tr.org.liderahenk.notify.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.notify.constants.NotifyConstants;
import tr.org.liderahenk.notify.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * @author <a href="mailto:tuncay.colak@tubitak.gov.tr">Tuncay ÇOLAK</a>
 * 
 */
public class NotifyTaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(NotifyTaskDialog.class);
	
	private Label lblMessage;
	private Text textMessage;
	
	private Label lblDuration;
	private Text textDuration;
	
	private Label lblSize;
	private Combo cmbSize;
	
	// TODO do not forget to change this constructor if SingleSelectionHandler is used!
	public NotifyTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		// TODO dialog title
		return Messages.getString("ETAP_NOTIFY");	
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		// TODO create your task-related widgets here
		Composite composite = new Composite(parent, GridData.FILL);
		composite.setLayout(new GridLayout(4, false));
		
		GridData data= new GridData(SWT.FILL, SWT.FILL, true, true,1,1);
		composite.setLayoutData(data);
                
        lblMessage = new Label(composite, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblMessage.setText(Messages.getString("NOTIFY_MESSAGE_CONTENT")); //$NON-NLS-1$
        
        textMessage = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, true, 4,1);
        gd_text.widthHint=200;
        gd_text.heightHint=300;
        textMessage.setLayoutData(gd_text);
        
        lblDuration = new Label(composite, SWT.NONE);
        lblDuration.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblDuration.setText(Messages.getString("DURATION"));
       
        textDuration = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.CENTER);
        GridData dudarion = new GridData(SWT.NONE, SWT.NONE, true, true, 1, 1);
        dudarion.widthHint=50;
        dudarion.heightHint=20;	
        textDuration.setText("10");
        textDuration.setLayoutData(dudarion);
        
        lblSize = new Label(composite, SWT.NONE);
        lblSize.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblSize.setText(Messages.getString("SIZE"));
        
        cmbSize = new Combo(composite, SWT.TOP | SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        cmbSize.setSize(10, 10 );
		String items [] = {Messages.getString("SMALL"), Messages.getString("FULLSCREEN")};
		cmbSize.setItems(items);
		cmbSize.select(1);
		cmbSize.setEnabled(true);

		return composite;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		// TODO triggered before task execution
		if(textMessage.getText().equals("")) throw new ValidationException(Messages.getString("NOTIFY_MESSAGE_CONTENT_ERROR"));	
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> params= new HashMap<>();
		params.put(NotifyConstants.PARAMETERS.NOTIFY_CONTENT, textMessage.getText());
		if (cmbSize.getSelectionIndex() == 0) {
			params.put(NotifyConstants.PARAMETERS.SIZE, "small");
		}
		else {
			params.put(NotifyConstants.PARAMETERS.SIZE, "fullscreen");
		}
		params.put(NotifyConstants.PARAMETERS.DURATION, textDuration.getText());
		return params;
	}

	@Override
	public String getCommandId() {
		// TODO command id which is used to match tasks with ICommand class in the corresponding Lider plugin
		return "ETA_NOTIFY";
	}

	@Override
	public String getPluginName() {
		return NotifyConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return NotifyConstants.PLUGIN_VERSION;
	}
	
}

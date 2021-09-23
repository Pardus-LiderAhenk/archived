package tr.org.liderahenk.packagemanager.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderDialog;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.packagemanager.i18n.Messages;

public class SelectVersionDialog extends DefaultLiderDialog {

	CheckPackageTaskDialog dialog;
	String param;
	Text txtVersion;
	
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gData);
		
		Label lblVersion = new Label(composite, SWT.NONE);
		lblVersion.setText(Messages.getString("PACKAGE_VERSION"));

		txtVersion = new Text(composite, SWT.BORDER);
		txtVersion.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Composite descComposite = new Composite(composite, SWT.NONE);
		descComposite.setLayout(new GridLayout(1, false));
		
		Label lblDesc = new Label(composite, SWT.NONE);
		lblDesc.setText(Messages.getString("DESC_VERSION_ENTERANCE"));
		
		
		return super.createDialogArea(parent);
	}

	public SelectVersionDialog(Shell activeShell) {
		super(activeShell);
	}

	@Override
	protected void okPressed() {
		if(txtVersion.getText() == null || txtVersion.getText().isEmpty()){
			Notifier.error("", Messages.getString("ENTER_VERSION"));
			return;
		}
		setReturnCode(OK);
		setParam(txtVersion.getText().toString());
		this.close();
	}

	



}

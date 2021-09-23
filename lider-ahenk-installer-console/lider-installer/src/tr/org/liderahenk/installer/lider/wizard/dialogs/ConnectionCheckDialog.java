package tr.org.liderahenk.installer.lider.wizard.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

/**
 * Creates a dialog which executes an authorization check with provided when it
 * pops up.
 * 
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class ConnectionCheckDialog extends Dialog {

	private Label image;
	private Label message;
	private ProgressBar progBar;
	private Button okBtn;

	private String ip;
	private String username;
	private String password;
	private String keyAbsPath;
	private String passphrase;
	private AccessMethod method;

	private boolean canAuthorize;

	public ConnectionCheckDialog(Shell parentShell, String ip, String username, String password, String keyAbsPath,
			String passphrase, AccessMethod method) {
		super(parentShell);
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.keyAbsPath = keyAbsPath;
		this.passphrase = passphrase;
		this.method = method;

		// Do not show close on the title bar and lock parent window.
		super.setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);

	}

	@Override
	protected Control createContents(Composite parent) {

		// Disable ESC key in this dialog
		getShell().addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});

		Composite mainContainer = GUIHelper.createComposite(parent, 1);

		Composite container = GUIHelper.createComposite(mainContainer, 2);

		// Wait-Success-Fail image
		image = GUIHelper.createLabel(container);
		image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/wait.png")));

		// Message to user
		message = GUIHelper.createLabel(container, Messages.getString("CHECKING_AUTHENTICATION_PARAMETERS_PLEASE_WAIT"), SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 350;
		message.setLayoutData(gridData);

		// Progress bar while process going on
		Composite secondCon = GUIHelper.createComposite(mainContainer, 1);

		progBar = new ProgressBar(secondCon, SWT.INDETERMINATE);
		GridData barGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		progBar.setLayoutData(barGridData);

		// Ok button to close dialog
		okBtn = GUIHelper.createButton(secondCon, SWT.PUSH, Messages.getString("OK"));
		okBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		okBtn.setVisible(false);
		GridData gdButton = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gdButton.widthHint = 100;
		okBtn.setLayoutData(gdButton);

		startAuthorizationCheck();

		return mainContainer;
	}

	private void startAuthorizationCheck() {
		final Display display = Display.getCurrent();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (method == AccessMethod.USERNAME_PASSWORD) {
					canAuthorize = SetupUtils.canConnectViaSsh(ip, username, password, passphrase);
				} else {
					canAuthorize = SetupUtils.canConnectViaSshWithoutPassword(ip, username, keyAbsPath, passphrase);
				}
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						if (canAuthorize) {
							image.setImage(new Image(Display.getCurrent(),
									this.getClass().getResourceAsStream("/icons/success.png")));
							message.setText(Messages.getString("AUTHENTICATION_SUCCESSFULL"));
							progBar.setVisible(false);
							okBtn.setVisible(true);
						} else {
							image.setImage(new Image(Display.getCurrent(),
									this.getClass().getResourceAsStream("/icons/fail.png")));
							message.setText(Messages.getString("AUTHENTICATION_FAILED"));
							progBar.setVisible(false);
							okBtn.setVisible(true);
						}
					}
				});
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 155);
	}

	public boolean getCanAuthorize() {
		return canAuthorize;
	}

	public void setCanAuthorize(boolean canAuthorize) {
		this.canAuthorize = canAuthorize;
	}
}

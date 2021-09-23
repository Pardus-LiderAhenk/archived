package tr.org.liderahenk.installer.lider.wizard.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class CheckVersionDialog extends Dialog {

	private Label image;
	private Label message;
	private ProgressBar progBar;
	private Button btnYes;
	private Button btnNo;
	private boolean isUpToDate;
	private boolean continueInstallation;

	public CheckVersionDialog(Shell parentShell) {
		super(parentShell);

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

		Composite cmpMain = GUIHelper.createComposite(parent, 1);

		Composite cmpInfo = GUIHelper.createComposite(cmpMain, 2);

		// Wait-Success-Fail image
		image = GUIHelper.createLabel(cmpInfo);
		image.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/wait.png")));

		// Message to user
		message = GUIHelper.createLabel(cmpInfo, Messages.getString("CHECKING_INSTALLER_VERSION"), SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 350;
		gridData.heightHint = 85;
		message.setLayoutData(gridData);

		// Progress bar while process going on
		Composite cmpProgBar = GUIHelper.createComposite(cmpMain, 1);

		progBar = new ProgressBar(cmpProgBar, SWT.INDETERMINATE);
		GridData barGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		progBar.setLayoutData(barGridData);

		Composite cmpButtons = GUIHelper.createComposite(cmpMain, 2);

		// Ok button to close dialog
		btnYes = GUIHelper.createButton(cmpButtons, SWT.PUSH, Messages.getString("YES"));
		btnYes.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				continueInstallation = true;
				close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnYes.setVisible(false);
		GridData gdYesBtn = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		gdYesBtn.widthHint = 100;
		btnYes.setLayoutData(gdYesBtn);

		// Ok button to close dialog
		btnNo = GUIHelper.createButton(cmpButtons, SWT.PUSH, Messages.getString("NO_DOWNLOAD_LATEST_VERSION"));
		btnNo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				continueInstallation = false;
				try {
					openDownloadUrl();
					close();
				} catch (IOException ex) {
					ex.printStackTrace();
					message.setText(Messages.getString("CANNOT_OPEN_BROWSER_PLEASE_GO_TO") + "\n" + PropertyReader.property("download.url"));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		btnNo.setVisible(false);
		GridData gdNoBtn = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gdNoBtn.widthHint = 300;
		btnNo.setLayoutData(gdNoBtn);

		startVersionCheck();

		return cmpMain;
	}

	private void openDownloadUrl() throws IOException {
		Runtime.getRuntime().exec("xdg-open " + PropertyReader.property("download.url"));
	}

	private void startVersionCheck() {
		final Display display = Display.getDefault();

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// Let user read the text
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				final String latestVersion = getVersionFromUrl();
				final String currentVersion = PropertyReader.property("installer.version");
				if (latestVersion != null) {
					if (latestVersion.equals(currentVersion)) {
						isUpToDate = true;
					} else {
						isUpToDate = false;
					}

					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (isUpToDate) {
								image.setImage(
										new Image(display, this.getClass().getResourceAsStream("/icons/success.png")));
								message.setText(Messages.getString("INSTALLER_IS_UP_TO_DATE") + "\n"
										+ Messages.getString("STARTING_APPLICATION"));
								progBar.setVisible(false);
								continueInstallation = true;
								display.asyncExec(new Runnable() {
									@Override
									public void run() {
										try {
											Thread.sleep(3000);
											close();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										
									}
								});
								
							} else {
								image.setImage(new Image(display, this.getClass().getResourceAsStream("/icons/fail.png")));
								message.setText(Messages.getString("INSTALLER_IS_OLD") + "\n"
										+ Messages.getString("LATEST_VERSION") + " " + latestVersion + "\n"
										+ Messages.getString("CURRENT_VERSION") + " " + currentVersion);
								progBar.setVisible(false);
								btnYes.setVisible(true);
								btnNo.setVisible(true);
							}
						}
					});
				} else {
					continueInstallation = true;
					
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							close();
							// TODO
							// TODO set message to dialog about internet connection or url
							// TODO
						}
					});
				}

			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private String getVersionFromUrl() {

		URL url;
		StringBuffer urlContent = null;
		BufferedReader br = null;
		URLConnection conn;
		String inputLine;

		// get URL content
		try {
			url = new URL(PropertyReader.property("installer.version.check.address"));
			conn = url.openConnection();

			// open the stream and put it into BufferedReader
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			urlContent = new StringBuffer();

			while ((inputLine = br.readLine()) != null) {
				urlContent.append(inputLine);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (urlContent != null) {
			return urlContent.toString().substring(urlContent.indexOf("<html>") + 6, urlContent.indexOf("</html>"));
		} else {
			return null;
		}

	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

	public boolean isContinueInstallation() {
		return continueInstallation;
	}

	public void setContinueInstallation(boolean continueInstallation) {
		this.continueInstallation = continueInstallation;
	}

}

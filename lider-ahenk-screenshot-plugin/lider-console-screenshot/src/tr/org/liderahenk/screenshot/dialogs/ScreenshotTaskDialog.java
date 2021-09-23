package tr.org.liderahenk.screenshot.dialogs;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.rest.utils.AgentRestUtils;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.xmpp.enums.ContentType;
import tr.org.liderahenk.liderconsole.core.xmpp.notifications.TaskStatusNotification;
import tr.org.liderahenk.screenshot.constants.ScreenshotConstants;
import tr.org.liderahenk.screenshot.i18n.Messages;
import tr.org.liderahenk.screenshot.widgets.OnlineUsersCombo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ScreenshotTaskDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(ScreenshotTaskDialog.class);

	private ScrolledComposite sc;
	private List<OnlineUsersCombo> comboList;
	List<String> onlineUsers = null;

	private OnlineUsersCombo cmbOnlineUsers;

	public ScreenshotTaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
		subscribeEventHandler(taskStatusNotificationHandler);
	}

	@Override
	public String createTitle() {
		return Messages.getString("TAKE_SCREENSHOT");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		sc = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
		sc.setLayout(new GridLayout(1, false));
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite mainComposite = new Composite(sc, SWT.NONE);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		comboList = new ArrayList<OnlineUsersCombo>();

		// For each DN, user may indicate whose screenshot should be taken:
		for (String dn : getDnSet()) {
			// DN
			Label lblDn = new Label(mainComposite, SWT.NONE);
			lblDn.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
			lblDn.setText((dn.length() > 100 ? dn.substring(0, 100) : dn) + ":");

			Composite innerComposite = new Composite(mainComposite, SWT.NONE);
			innerComposite.setLayout(new GridLayout(2, false));
			innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			Composite composite = new Composite(innerComposite, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			
			textSearch = new Text(composite, SWT.BORDER);
			textSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			
			Button btnNewButton = new Button(composite, SWT.NONE);
			btnNewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					String srcText=textSearch.getText();
					
					if(srcText.equals("")) return;
					
					if(onlineUsers!=null){
						int searchIndex=-1;
						
						for (int i = 0; i < onlineUsers.size(); i++) {
							String user=onlineUsers.get(i);
							
							if(user.contains(srcText)){
								searchIndex=i;
							}
						}
						
						cmbOnlineUsers.select(searchIndex);
						
					}
				}
			});
			btnNewButton.setText(Messages.getString("search")); //$NON-NLS-1$

			// Online users
			Label lblOnlineUsers = new Label(innerComposite, SWT.NONE);
			lblOnlineUsers.setText(Messages.getString("ONLINE_USERS"));

			// Find online users of agent specified by current DN
			
			try {
				onlineUsers = AgentRestUtils.getOnlineUsers(dn);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			cmbOnlineUsers = new OnlineUsersCombo(innerComposite,
					SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY, dn);
			cmbOnlineUsers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			comboList.add(cmbOnlineUsers);
			if (onlineUsers != null && !onlineUsers.isEmpty()) {
				onlineUsers.add(""); // User selection is optional!
				cmbOnlineUsers.setItems(onlineUsers.toArray(new String[onlineUsers.size()]));
			}
		}

		sc.setContent(mainComposite);
		mainComposite.setSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setMinSize(new Point(600, 400));

		return sc;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		for (OnlineUsersCombo cmbOnlineUsers : comboList) {
			if (cmbOnlineUsers.getSelectionIndex() > -1) {
				String onlineUser = cmbOnlineUsers.getItem(cmbOnlineUsers.getSelectionIndex());
				if (onlineUser.isEmpty())
					continue;
				parameterMap.put(cmbOnlineUsers.getDn(), onlineUser);
			}
		}
		return parameterMap;
	}

	@Override
	public String getCommandId() {
		return "TAKE-SCREENSHOT";
	}

	@Override
	public String getPluginName() {
		return ScreenshotConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return ScreenshotConstants.PLUGIN_VERSION;
	}

	private EventHandler taskStatusNotificationHandler = new EventHandler() {
		@Override
		public void handleEvent(final Event event) {
			Job job = new Job("TASK") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("SCREENSHOT", 100);
					try {
						final TaskStatusNotification taskStatus = (TaskStatusNotification) event
								.getProperty("org.eclipse.e4.data");
						if (ContentType.getImageContentTypes().contains(taskStatus.getResult().getContentType())) {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									try {
										// Agent DN
										final String dn = taskStatus.getCommandExecution().getDn();
										final ContentType contentType = taskStatus.getResult().getContentType();
										final byte[] data = TaskRestUtils
												.getResponseData(taskStatus.getResult().getId());

										for (OnlineUsersCombo cmbOnlineUsers : comboList) {

											// Find correct line to display the
											// image
											if (dn.equalsIgnoreCase(cmbOnlineUsers.getDn())) {

												// Draw image!
												Label lblImage = new Label(cmbOnlineUsers.getParent(), SWT.BORDER);
												lblImage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
												lblImage.setImage(createImage(data));

												// File button to download image
												final DirectoryDialog dialog = new DirectoryDialog(
														cmbOnlineUsers.getParent().getShell(), SWT.OPEN);
												dialog.setMessage(Messages.getString("SELECT_DOWNLOAD_DIR"));
												Button btnDirSelect = new Button(cmbOnlineUsers.getParent(), SWT.PUSH);
												btnDirSelect.setText(Messages.getString("DOWNLOAD_FILE"));
												btnDirSelect.setImage(new Image(cmbOnlineUsers.getParent().getDisplay(),
														this.getClass().getResourceAsStream("/icons/16/download.png")));
												btnDirSelect.addSelectionListener(new SelectionListener() {
													@Override
													public void widgetSelected(SelectionEvent e) {
														String path = dialog.open();
														if (path == null || path.isEmpty()) {
															return;
														}
														if (!path.endsWith("/")) {
															path += "/";
														}
														// Save image
														ImageLoader loader = new ImageLoader();
														loader.data = new ImageData[] {
																new ImageData(new ByteArrayInputStream(data)) };
														loader.save(
																path + "sc" + new Date().getTime() + "."
																		+ ContentType.getFileExtension(contentType),
																ContentType.getSWTConstant(contentType));
													}

													@Override
													public void widgetDefaultSelected(SelectionEvent e) {
													}
												});

												// Refresh dialog
												cmbOnlineUsers.getParent().layout(true);
												sc.layout(true);
												sc.setMinSize(sc.getContent().computeSize(600, SWT.DEFAULT));

												break;
											}
										}
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						Notifier.error("", Messages.getString("UNEXPECTED_ERROR_TAKING_SCREENSHOT"));
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
	private Text textSearch;

	/**
	 * Create image from given response data, resize if necessary.
	 * 
	 * @param responseData
	 * @return
	 */
	private Image createImage(byte[] responseData) {
		int width = 400;
		int height = 400;
		Image image = new Image(Display.getDefault(), new ByteArrayInputStream(responseData));
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		image.dispose();
		return scaled;
	}
}

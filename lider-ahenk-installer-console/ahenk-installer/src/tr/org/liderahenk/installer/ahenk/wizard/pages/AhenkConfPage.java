package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.AccessMethod;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Volkan Şahin <bm.volkansahin@gmail.com>
 * @author Caner Feyzullahoglu <caner.feyzullahoglu@agem.com.tr>
 */
public class AhenkConfPage extends WizardPage implements ControlNextEvent {

	private AhenkSetupConfig config;

	private StyledText stMainConfig;

	// XMPP configuration
	private Text xmppHost;
	private Text xmppServiceName;
	private Text liderJid;

	private NextPageEventType nextPageEventType;

	private Text receiverResource;

	private Text receiveFile;

	private Combo cmbUseSsl;

	public AhenkConfPage(AhenkSetupConfig config) {
		super(AhenkConfPage.class.getName(), Messages.getString("AHENK_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("AHENK_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		container = new ScrolledComposite(container, SWT.V_SCROLL);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Composite innerContainer = new Composite(container, SWT.NONE);
		innerContainer.setLayout(new GridLayout(1, false));
		innerContainer.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Composite lineCont = GUIHelper.createComposite(innerContainer, 2);

		GUIHelper.createLabel(lineCont, Messages.getString("XMPP_SERVER_HOST_ADDRESS"));
		xmppHost = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		xmppHost.setText(config.getHost() != null ? config.getHost() : "");
		xmppHost.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		xmppHost.setMessage(Messages.getString("EG_XMPP_HOST"));

		GUIHelper.createLabel(lineCont, Messages.getString("XMPP_SERVER_SERVICE_NAME"));
		xmppServiceName = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		xmppServiceName.setText(config.getServiceName() != null ? config.getServiceName() : "");
		xmppServiceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		xmppServiceName.setMessage(Messages.getString("EG_XMPP_SERVICE_NAME"));

		GUIHelper.createLabel(lineCont, Messages.getString("LIDER_XMPP_USERNAME"));
		liderJid = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		liderJid.setText(config.getLiderJid() != null ? config.getLiderJid() : "");
		liderJid.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		liderJid.setMessage(Messages.getString("EG_LIDER_JID"));

		GUIHelper.createLabel(lineCont, Messages.getString("RECEIVER_RESOURCE"));
		receiverResource = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		receiverResource.setText("Smack");
		receiverResource.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		receiverResource.setMessage(Messages.getString("EG_RECEIVER_RESOURCE"));

		GUIHelper.createLabel(lineCont, Messages.getString("RECEIVE_FILE"));
		receiveFile = GUIHelper.createText(lineCont, new GridData(GridData.FILL, GridData.FILL, true, false));
		receiveFile.setText("/tmp/");
		receiveFile.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		receiveFile.setMessage(Messages.getString("EG_RECEIVE_FILE"));

		GUIHelper.createLabel(lineCont, Messages.getString("AHENK_USE_TLS"));
		cmbUseSsl = new Combo(lineCont, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbUseSsl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		cmbUseSsl.setItems(new String[] { Messages.getString("FALSE"), Messages.getString("TRUE") });
		cmbUseSsl.select(0);

		Composite infoComposite = GUIHelper.createComposite(innerContainer, 1);
		GUIHelper.createLabel(infoComposite, Messages.getString("AHENK_INSTALLATION_XMPP_CONF_HINT"));

		GUIHelper.createLabel(innerContainer, Messages.getString("AHENK_CONF"));

		// Add a text area for configuration.
		stMainConfig = new StyledText(innerContainer, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData layoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		layoutData.heightHint = 200;
		stMainConfig.setLayoutData(layoutData);
		stMainConfig.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// If config content is entered user can click next.
				if (!"".equals(stMainConfig.getText()) && stMainConfig.getText() != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		// Read from file and bring default configuration
		// in the opening of page
		readFile("ahenk.conf", stMainConfig);

		((ScrolledComposite) container).setContent(innerContainer);
		innerContainer.setSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) container).setExpandVertical(true);
		((ScrolledComposite) container).setExpandHorizontal(true);
		((ScrolledComposite) container).setMinSize(innerContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setPageComplete(false);
		updatePageCompleteStatus();
	}

	@Override
	public IWizardPage getNextPage() {

		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
			updatePageCompleteStatus();
		}

		AhenkConfirmPage confPage = (AhenkConfirmPage) super.getNextPage();

		// Set config variables and confirm page labels.
		if (config.getAhenkInstallMethod() == InstallMethod.APT_GET) {
			confPage.getInstallLabel().setText("- " + Messages.getString("USE_REPOSITORY"));
		} else if (config.getAhenkInstallMethod() == InstallMethod.PROVIDED_DEB) {
			confPage.getInstallLabel().setText("- " + Messages.getString("USE_GIVEN_DEB"));
		} else {
			confPage.getInstallLabel().setText("- " + Messages.getString("USE_GIVEN_URL"));
		}

		if (config.getAhenkAccessMethod() == AccessMethod.USERNAME_PASSWORD) {
			confPage.getAccessLabel().setText("- " + Messages.getString("ACCESSING_WITH_USERNAME_AND_PASSWORD"));
		} else {
			confPage.getAccessLabel().setText("- " + Messages.getString("ACCESSING_WITH_PRIVATE_KEY"));
		}

		// Set config variables before going to next page
		String text = stMainConfig.getText();
		Map<String, String> map = new HashMap<>();

		// XMPP configuration
		map.put("#HOST", xmppHost.getText());
		map.put("#LIDERJID", liderJid.getText());
		map.put("#SERVICENAME", xmppServiceName.getText());
		map.put("#RECEIVER_RESOURCE", receiverResource.getText());
		map.put("#RECEIVE_FILE", receiveFile.getText());
		map.put("#USE_TLS", cmbUseSsl.getText());

		text = LiderAhenkUtils.replace(map, text);
		config.setAhenkConfContent(text);
		config.setAhenkAbsPathConfFile(LiderAhenkUtils.writeToFileReturnPath(text, "ahenk.conf"));
		return confPage;
	}

	/**
	 * Reads file from classpath location for current project and sets it to a
	 * text in a GUI.
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName, final StyledText guiText) {

		BufferedReader br = null;
		InputStream inputStream = null;

		try {
			String currentLine;

			inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			String readingText = "";

			while ((currentLine = br.readLine()) != null) {
				// Platform independent line separator.
				readingText += currentLine + System.getProperty("line.separator");
			}

			final String tmpText = readingText;
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					guiText.setText(tmpText);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updatePageCompleteStatus() {
		if (!xmppHost.getText().isEmpty() && !xmppServiceName.getText().isEmpty() && !liderJid.getText().isEmpty()
				&& !receiveFile.getText().isEmpty()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return this.nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}

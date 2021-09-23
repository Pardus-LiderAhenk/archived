package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.LiderAhenkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LdapUpdateConfPage extends WizardPage implements ILdapPage, ControlNextEvent {

	private LiderSetupConfig config;

	private Button btnUpdateConfigUser;

	private Button btnDoNotUpdateConfigUser;

	private Text txtCnConfigDn;

	private Text txtCnConfigPwd;

	private Text txtBaseDn;

	private Text txtLdapDbAdminDn;

	private Text txtLdapDbAdminPwd;

	private Text txtLiderIp;

	private Text txtLiderAdminPwd;

	private StyledText st;
	
	private NextPageEventType nextPageEventType = NextPageEventType.CLICK_FROM_PREV_PAGE;
	
	public LdapUpdateConfPage(LiderSetupConfig config) {
		super(LdapUpdateConfPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.2 " + Messages.getString("LDAP_UPDATE_CONF_INFO"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite compMain = GUIHelper.createComposite(parent, 1);
		setControl(compMain);

		Composite compChild = GUIHelper.createComposite(compMain, 2);

		btnUpdateConfigUser = GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("UPDATE_CONFIG_USER"));
		btnUpdateConfigUser.setSelection(true);
		final ControlDecoration decUpdate= new ControlDecoration(btnUpdateConfigUser, SWT.TOP | SWT.RIGHT);
		// TODO change icon
		decUpdate.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/info.png")));
		decUpdate.setDescriptionText(Messages.getString("UPDATE_DESC"));

		btnDoNotUpdateConfigUser = GUIHelper.createButton(compChild, SWT.RADIO, Messages.getString("DO_NOT_UPDATE_CONFIG_USER"));
		final ControlDecoration decDoNotUpdate = new ControlDecoration(btnDoNotUpdateConfigUser, SWT.TOP | SWT.RIGHT);
		// TODO change icon
		decDoNotUpdate.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/info.png")));
		decDoNotUpdate.setDescriptionText(Messages.getString("DO_NOT_UPDATE_DESC"));
		
		
		GUIHelper.createLabel(compChild, Messages.getString("CN_CONFIG_ADMIN_DN"));
		txtCnConfigDn = GUIHelper.createText(compChild);
		txtCnConfigDn.setMessage(Messages.getString("EG_CN_CONFIG_ADMIN_DN"));
		txtCnConfigDn.setText("cn=admin,cn=config");
		txtCnConfigDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("CN_CONFIG_ADMIN_PWD"));
		txtCnConfigPwd = GUIHelper.createText(compChild);
		txtCnConfigPwd.setMessage(Messages.getString("EG_CN_CONFIG_ADMIN_PWD"));
		txtCnConfigPwd.setText("secret");
		txtCnConfigPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("BASE_DN"));
		txtBaseDn = GUIHelper.createText(compChild);
		txtBaseDn.setMessage(Messages.getString("EG_BASE_DN"));
		txtBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("LDAP_DB_ADMIN_DN"));
		txtLdapDbAdminDn = GUIHelper.createText(compChild);
		txtLdapDbAdminDn.setMessage(Messages.getString("EG_LDAP_DB_ADMIN_DN"));
		txtLdapDbAdminDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("LDAP_DB_ADMIN_PWD"));
		txtLdapDbAdminPwd = GUIHelper.createText(compChild);
		txtLdapDbAdminPwd.setMessage(Messages.getString("EG_LDAP_DB_ADMIN_PWD"));
		txtLdapDbAdminPwd.setText("secret");
		txtLdapDbAdminPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("LIDER_SERVER_ADDRESS"));
		txtLiderIp = GUIHelper.createText(compChild);
		txtLiderIp.setMessage(Messages.getString("EG_LIDER_SERVER_ADDRESS"));
		txtLiderIp.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(compChild, Messages.getString("LIDER_ADMIN_PWD"));
		txtLiderAdminPwd = GUIHelper.createText(compChild);
		txtLiderAdminPwd.setMessage(Messages.getString("EG_LIDER_ADMIN_PWD"));
		txtLiderAdminPwd.setText("secret");
		txtLiderAdminPwd.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});

		// Add a text area for configuration.
		st = new StyledText(compMain, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		st.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Add a menu which pops up when right clicked.
		final Menu rightClickMenu = new Menu(st);

		// Add items to new menu
		MenuItem copy = new MenuItem(rightClickMenu, SWT.PUSH);
		copy.setText(Messages.getString("COPY"));
		copy.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.copy();
			}
		});

		MenuItem paste = new MenuItem(rightClickMenu, SWT.PUSH);
		paste.setText(Messages.getString("PASTE"));
		paste.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.paste();
			}
		});

		MenuItem cut = new MenuItem(rightClickMenu, SWT.PUSH);
		cut.setText(Messages.getString("CUT"));
		cut.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.cut();
			}
		});

		MenuItem selectAll = new MenuItem(rightClickMenu, SWT.PUSH);
		selectAll.setText(Messages.getString("SELECT_ALL"));
		selectAll.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				st.selectAll();
			}
		});

		// Set menu for text area
		st.setMenu(new Menu(compMain));
		// Listen for right clicks only.
		st.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				rightClickMenu.setVisible(true);
			}
		});

		// Add CTRL+A select all key binding.
		st.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent event) {
				if ((event.stateMask & SWT.CTRL) == SWT.CTRL && (event.keyCode == 'a')) {
					st.selectAll();
				}
			}
		});

		st.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				// If config content is entered user can click next.
				if (!"".equals(st.getText()) && st.getText() != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		});

		// Read from file and bring default configuration
		// in the opening of page
		readFile("update_ldap", st);

		setPageComplete(false);

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

	@Override
	public IWizardPage getNextPage() {
		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			setInputValues();
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
			updatePageCompleteStatus();
		}
		
		setConfigVariables();
		
		createConfFile();

		return super.getNextPage();
	}

	private void createConfFile() {
		String text = st.getText();
		Map<String, String> map = new HashMap<>();
		map.put("#UPDATE_CONFIG_USER", btnUpdateConfigUser.getSelection() ? "1" : "0" );
		map.put("#CN_CONFIG_ADMIN_DN", txtCnConfigDn.getText());
		map.put("#CN_CONFIG_ADMIN_PWD", txtCnConfigPwd.getText());
		map.put("#BASE_DN", txtBaseDn.getText());
		map.put("#LDAP_DB_ADMIN_DN", txtLdapDbAdminDn.getText());
		map.put("#LDAP_DB_ADMIN_PWD", txtLdapDbAdminPwd.getText());
		map.put("#LIDER_SERVER_ADDR", txtLiderIp.getText());
		map.put("#LADMIN_PWD", txtLiderAdminPwd.getText());
		
		text = LiderAhenkUtils.replace(map, text);

		// Set config variables before going to next page
		config.setLdapConfContent(text);
		config.setLdapAbsPathConfFile(LiderAhenkUtils.writeToFileReturnPath(text, "update_ldap"));
	}
	
	public void updatePageCompleteStatus() {
		if (!txtCnConfigDn.getText().isEmpty() &&
				!txtCnConfigPwd.getText().isEmpty() &&
				!txtBaseDn.getText().isEmpty() &&
				!txtLdapDbAdminDn.getText().isEmpty() &&
				!txtLdapDbAdminPwd.getText().isEmpty() &&
				!txtLiderIp.getText().isEmpty() &&
				!txtLiderAdminPwd.getText().isEmpty()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}
	
	private void setConfigVariables() {
		config.setLdapBaseDn(txtBaseDn.getText());
		config.setLdapAdminDn(txtLdapDbAdminDn.getText());
		config.setLdapAdminDnPwd(txtLdapDbAdminPwd.getText());
	}

	public void setInputValues() {
		if (nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {
			txtBaseDn.setText(config.getLdapBaseDn());
			txtLdapDbAdminDn.setText("cn=admin," + config.getLdapBaseDn());
			txtLiderIp.setText(config.getLiderIp() != null ? config.getLiderIp() : "lider." + config.getLdapOrgCn());
			nextPageEventType = NextPageEventType.NEXT_BUTTON_CLICK;
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

package tr.org.liderahenk.admigration.wizard.pages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.admigration.config.MigrationConfig;
import tr.org.liderahenk.admigration.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MigrationConfPage extends WizardPage {

	private MigrationConfig config;

	private Text adHost;

	private Text adPort;

	private Text adUsername;

	private Text adPassword;

	private Text adUserSearchBaseDn;

	private Text adUserObjectClasses;

	private Text adGroupSearchBaseDn;

	private Text adGroupObjectClasses;

	private Text ldapHost;

	private Text ldapPort;

	private Text ldapUsername;

	private Text ldapPassword;

	private Text ldapUserSearchBaseDn;

	private Text ldapUserObjectClasses;

	private Text ldapGroupSearchBaseDn;

	private Text ldapGroupObjectClasses;

	private Text ldapNewUserEntrySuffix;

	private Text ldapNewUserEntryPrefixAttr;

	private Text ldapNewGroupEntrySuffix;

	private Text ldapNewGroupEntryPrefixAttr;

	public MigrationConfPage(MigrationConfig config) {
		super(MigrationConfPage.class.getName(), Messages.getString("AD_MIGRATION"), null);
		setDescription("1.1 " + Messages.getString("MIGRATION_CONF"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		final Composite cmpRoot = GUIHelper.createComposite(parent, 1);
		cmpRoot.setLayout(GridLayoutFactory.fillDefaults().create());

		final ScrolledComposite sc = new ScrolledComposite(cmpRoot, SWT.BORDER | SWT.V_SCROLL);
		sc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		sc.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).create());
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		final Composite cmpMain = new Composite(sc, SWT.NULL);
		cmpMain.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		cmpMain.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_HOST"));
		adHost = GUIHelper.createText(cmpMain);
		adHost.setMessage(Messages.getString("AD_HOST_EG"));
		adHost.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_PORT"));
		adPort = GUIHelper.createText(cmpMain);
		adPort.setMessage(Messages.getString("AD_PORT_EG"));
		adPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		adPort.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_USERNAME"));
		adUsername = GUIHelper.createText(cmpMain);
		adUsername.setMessage(Messages.getString("AD_USERNAME_EG"));
		adUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_PASSWORD"));
		adPassword = GUIHelper.createText(cmpMain);
		adPassword.setMessage(Messages.getString("AD_PASSWORD_EG"));
		adPassword.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_USER_SEARCH_BASE_DN"));
		adUserSearchBaseDn = GUIHelper.createText(cmpMain);
		adUserSearchBaseDn.setMessage(Messages.getString("AD_USER_SEARCH_BASE_DN_EG"));
		adUserSearchBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_USER_OBJECT_CLASSES"));
		adUserObjectClasses = GUIHelper.createText(cmpMain);
		adUserObjectClasses.setMessage(Messages.getString("AD_USER_OBJECT_CLASSES_EG"));
		adUserObjectClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_GROUP_SEARCH_BASE_DN"));
		adGroupSearchBaseDn = GUIHelper.createText(cmpMain);
		adGroupSearchBaseDn.setMessage(Messages.getString("AD_GROUP_SEARCH_BASE_DN_EG"));
		adGroupSearchBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("AD_GROUP_OBJECT_CLASSES"));
		adGroupObjectClasses = GUIHelper.createText(cmpMain);
		adGroupObjectClasses.setMessage(Messages.getString("AD_GROUP_OBJECT_CLASSES_EG"));
		adGroupObjectClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_HOST"));
		ldapHost = GUIHelper.createText(cmpMain);
		ldapHost.setMessage(Messages.getString("LDAP_HOST_EG"));
		ldapHost.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_PORT"));
		ldapPort = GUIHelper.createText(cmpMain);
		ldapPort.setMessage(Messages.getString("LDAP_PORT_EG"));
		ldapPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});
		ldapPort.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_USERNAME"));
		ldapUsername = GUIHelper.createText(cmpMain);
		ldapUsername.setMessage(Messages.getString("LDAP_USERNAME_EG"));
		ldapUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_PASSWORD"));
		ldapPassword = GUIHelper.createText(cmpMain);
		ldapPassword.setMessage(Messages.getString("LDAP_PASSWORD_EG"));
		ldapPassword.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_USER_SEARCH_BASE_DN"));
		ldapUserSearchBaseDn = GUIHelper.createText(cmpMain);
		ldapUserSearchBaseDn.setMessage(Messages.getString("LDAP_USER_SEARCH_BASE_DN_EG"));
		ldapUserSearchBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_USER_OBJECT_CLASSES"));
		ldapUserObjectClasses = GUIHelper.createText(cmpMain);
		ldapUserObjectClasses.setMessage(Messages.getString("LDAP_USER_OBJECT_CLASSES_EG"));
		ldapUserObjectClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_GROUP_SEARCH_BASE_DN"));
		ldapGroupSearchBaseDn = GUIHelper.createText(cmpMain);
		ldapGroupSearchBaseDn.setMessage(Messages.getString("LDAP_GROUP_SEARCH_BASE_DN_EG"));
		ldapGroupSearchBaseDn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_GROUP_OBJECT_CLASSES"));
		ldapGroupObjectClasses = GUIHelper.createText(cmpMain);
		ldapGroupObjectClasses.setMessage(Messages.getString("LDAP_GROUP_OBJECT_CLASSES_EG"));
		ldapGroupObjectClasses.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_NEW_USER_ENTRY_SUFFIX"));
		ldapNewUserEntrySuffix = GUIHelper.createText(cmpMain);
		ldapNewUserEntrySuffix.setMessage(Messages.getString("LDAP_NEW_USER_ENTRY_SUFFIX_EG"));
		ldapNewUserEntrySuffix.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_NEW_USER_ENTRY_PREFIX_ATTR"));
		ldapNewUserEntryPrefixAttr = GUIHelper.createText(cmpMain);
		ldapNewUserEntryPrefixAttr.setMessage(Messages.getString("LDAP_NEW_USER_ENTRY_PREFIX_ATTR_EG"));
		ldapNewUserEntryPrefixAttr.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_NEW_GROUP_ENTRY_SUFFIX"));
		ldapNewGroupEntrySuffix = GUIHelper.createText(cmpMain);
		ldapNewGroupEntrySuffix.setMessage(Messages.getString("LDAP_NEW_GROUP_ENTRY_SUFFIX_EG"));
		ldapNewGroupEntrySuffix.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		GUIHelper.createLabel(cmpMain, Messages.getString("LDAP_NEW_GROUP_ENTRY_PREFIX_ATTR"));
		ldapNewGroupEntryPrefixAttr = GUIHelper.createText(cmpMain);
		ldapNewGroupEntryPrefixAttr.setMessage(Messages.getString("LDAP_NEW_GROUP_ENTRY_PREFIX_ATTR_EG"));
		ldapNewGroupEntryPrefixAttr.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				updatePageCompleteStatus();
			}
		});

		sc.setContent(cmpMain);
		sc.setMinSize(cmpMain.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(cmpRoot);

		setPageComplete(false);
	}

	private void updatePageCompleteStatus() {
		if (!adHost.getText().isEmpty() && !adPort.getText().isEmpty() && !adUsername.getText().isEmpty()
				&& !adPassword.getText().isEmpty() && !adUserSearchBaseDn.getText().isEmpty()
				&& !adUserObjectClasses.getText().isEmpty() && !adGroupSearchBaseDn.getText().isEmpty()
				&& !adGroupObjectClasses.getText().isEmpty() && !ldapHost.getText().isEmpty()
				&& !ldapPort.getText().isEmpty() && !ldapUsername.getText().isEmpty()
				&& !ldapPassword.getText().isEmpty() && !ldapUserSearchBaseDn.getText().isEmpty()
				&& !ldapUserObjectClasses.getText().isEmpty() && !ldapGroupSearchBaseDn.getText().isEmpty()
				&& !ldapGroupObjectClasses.getText().isEmpty() && !ldapNewUserEntrySuffix.getText().isEmpty()
				&& !ldapNewUserEntryPrefixAttr.getText().isEmpty() && !ldapNewGroupEntrySuffix.getText().isEmpty()
				&& !ldapNewGroupEntryPrefixAttr.getText().isEmpty()) {

			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	@Override
	public IWizardPage getNextPage() {

		setConfig();

		return super.getNextPage();
	}

	private void setConfig() {

		config.setAdHost(adHost.getText());
		config.setAdPort(!adPort.getText().isEmpty() ? Integer.parseInt(adPort.getText()) : null);
		config.setAdUsername(adUsername.getText());
		config.setAdPassword(adPassword.getText());
		config.setAdUserSearchBaseDn(adUserSearchBaseDn.getText());
		config.setAdUserObjectClasses(adUserObjectClasses.getText().replaceAll("\\s", "").split(","));
		config.setAdGroupSearchBaseDn(adGroupSearchBaseDn.getText());
		config.setAdGroupObjectClasses(adGroupObjectClasses.getText().replaceAll("\\s", "").split(","));
		config.setLdapHost(ldapHost.getText());
		config.setLdapPort(!ldapPort.getText().isEmpty() ? Integer.parseInt(ldapPort.getText()) : null);
		config.setLdapUsername(ldapUsername.getText());
		config.setLdapPassword(ldapPassword.getText());
		config.setLdapUserSearchBaseDn(ldapUserSearchBaseDn.getText());
		config.setLdapUserObjectClasses(ldapUserObjectClasses.getText().replaceAll("\\s", "").split(","));
		config.setLdapGroupSearchBaseDn(ldapGroupSearchBaseDn.getText());
		config.setLdapGroupObjectClasses(ldapGroupObjectClasses.getText().replaceAll("\\s", "").split(","));
		config.setLdapNewUserEntrySuffix(ldapNewUserEntrySuffix.getText());
		config.setLdapNewUserEntryPrefixAttr(ldapNewUserEntryPrefixAttr.getText());
		config.setLdapNewGroupEntrySuffix(ldapNewGroupEntrySuffix.getText());
		config.setLdapNewGroupEntryPrefixAttr(ldapNewGroupEntryPrefixAttr.getText());
	}

}

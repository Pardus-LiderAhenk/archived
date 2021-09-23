package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LiderOrganizationPage extends WizardPage {

	private LiderSetupConfig config;

	private Text txtOrgCn;
	private Text txtOrgName;

	public LiderOrganizationPage(LiderSetupConfig config) {
		super(LiderOrganizationPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("1.1 " + Messages.getString("CHOOSING_ORGANIZATION_CN"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		GUIHelper.createLabel(mainContainer, Messages.getString("ORGANIZATION_PAGE_DESCRIPTION"));

		GUIHelper.createLabel(mainContainer, Messages.getString("EG_ORG_DESC"));

		Composite cmpOrgCn = GUIHelper.createComposite(mainContainer, new GridLayout(2, false),
				new GridData(SWT.LEFT, SWT.CENTER, false, false));

		GUIHelper.createLabel(cmpOrgCn, Messages.getString("ORGANIZATION_NAME"));

		GridData gd = new GridData();
		gd.widthHint = 250;
		gd.horizontalIndent = 10;

		txtOrgName = GUIHelper.createText(cmpOrgCn);
		txtOrgName.setLayoutData(gd);
		txtOrgName.setMessage(Messages.getString("EG_ORG_NAME"));
		txtOrgName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});
		
		final ControlDecoration decOrgName = new ControlDecoration(txtOrgName, SWT.TOP | SWT.LEFT);
		// TODO change icon
		decOrgName.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/info.png")));
		decOrgName.setDescriptionText(Messages.getString("ORG_NAME_DESC"));

		GUIHelper.createLabel(cmpOrgCn, Messages.getString("ORGANIZATION_CN"));

		txtOrgCn = GUIHelper.createText(cmpOrgCn);
		txtOrgCn.setLayoutData(gd);
		txtOrgCn.setMessage(Messages.getString("EG_ORG_CN"));
		txtOrgCn.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				updatePageCompleteStatus();
			}
		});
		txtOrgCn.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// Validate inputs for letter and dot only
				char c = e.character;
				if (Character.isSpaceChar(c)) {
					e.doit = false;
					return;
				} else {
					e.doit = true;
					return;
				}
			}
		});
		final ControlDecoration decOrgCn = new ControlDecoration(txtOrgCn, SWT.TOP | SWT.LEFT);
		// TODO change icon
		decOrgCn.setImage(new Image(Display.getCurrent(), this.getClass().getResourceAsStream("/icons/info.png")));
		decOrgCn.setDescriptionText(Messages.getString("ORG_CN_DESC"));

		updatePageCompleteStatus();
	}

	@Override
	public IWizardPage getNextPage() {

		prepareDefaults(txtOrgName.getText(), txtOrgCn.getText());

		// Update next page according to selections of this page
		IWizardPage nextPage = super.getNextPage();
		if (nextPage instanceof LiderLocationOfComponentsPage) {
			((LiderLocationOfComponentsPage) nextPage).updatePage();
		}

		return super.getNextPage();
	}

	private void updatePageCompleteStatus() {
		if (!txtOrgName.getText().isEmpty() && !txtOrgCn.getText().isEmpty()) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	private void prepareDefaults(String orgName, String orgCn) {
		String[] organizationArr = orgCn.split("\\.");

		String orgBaseDn = "";
		String orgBaseCn = "";
		if (organizationArr.length > 0) {
			orgBaseCn = organizationArr[0];
			for (int i = 0; i < organizationArr.length; i++) {
				if (i != organizationArr.length - 1) {
					orgBaseDn += "dc=" + organizationArr[i] + ",";
				} else {
					orgBaseDn += "dc=" + organizationArr[i];
				}
			}
		}

		config.setLdapOrgName(orgName);
		config.setLdapBaseDn(orgBaseDn);
		config.setLdapOrgCn(orgCn);
		config.setLdapBaseCn(orgBaseCn);
	}

}

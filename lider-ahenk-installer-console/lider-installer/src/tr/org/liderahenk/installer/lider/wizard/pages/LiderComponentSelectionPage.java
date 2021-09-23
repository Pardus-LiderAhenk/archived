package tr.org.liderahenk.installer.lider.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class LiderComponentSelectionPage extends WizardPage {

	private LiderSetupConfig config;

	private Button btnCheckAll;
	private Button btnCheckDatabase;
	private Button btnCheckLdap;
	private Button btnCheckXmpp;
	private Button btnCheckLider;

	public LiderComponentSelectionPage(LiderSetupConfig config) {
		super(LiderComponentSelectionPage.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("1.1 " + Messages.getString("CHOOSING_COMPONENTS_THAT_WILL_BE_INSTALLED"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		GUIHelper.createLabel(mainContainer, Messages.getString("BELOW_COMPONENTS_MUST_BE_INSTALLED_FOR_LIDER"));
		GUIHelper.createLabel(mainContainer, Messages.getString("WHICH_COMPONENTS_WILL_BE_INSTALLED"));
		GUIHelper.createLabel(mainContainer, "");

		btnCheckAll = GUIHelper.createButton(mainContainer, SWT.CHECK | SWT.BORDER, Messages.getString("ALL_RECOMMENDED"));

		btnCheckDatabase = GUIHelper.createButton(mainContainer, SWT.CHECK | SWT.BORDER, Messages.getString("DATABASE"));

		btnCheckLdap = GUIHelper.createButton(mainContainer, SWT.CHECK | SWT.BORDER, Messages.getString("LDAP"));

		btnCheckXmpp = GUIHelper.createButton(mainContainer, SWT.CHECK | SWT.BORDER, Messages.getString("XMPP"));

		btnCheckLider = GUIHelper.createButton(mainContainer, SWT.CHECK | SWT.BORDER, Messages.getString("LIDER"));

		// Adding "select all" functionality to checkAll button.
		// And updating page complete status.
		btnCheckAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Toggle all checkboxes
				btnCheckDatabase.setSelection(btnCheckAll.getSelection());
				btnCheckLdap.setSelection(btnCheckAll.getSelection());
				btnCheckXmpp.setSelection(btnCheckAll.getSelection());
				btnCheckLider.setSelection(btnCheckAll.getSelection());
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Select/Deselect checkAll button
		// according to other components' checks.
		// And updating page complete status.
		btnCheckDatabase.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnCheckLdap.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnCheckXmpp.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnCheckLider.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionOfCheckAll();
				updatePageCompleteStatus();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// All components should come as selected
		// in the opening of page.
		btnCheckAll.setSelection(true);

		btnCheckDatabase.setSelection(true);

		btnCheckLdap.setSelection(true);

		btnCheckXmpp.setSelection(true);

		btnCheckLider.setSelection(true);
		
		updatePageCompleteStatus();
	}

	private void setSelectionOfCheckAll() {
		// Set selection of checkAll button to true
		// if all components are selected else set to false.
		if (btnCheckDatabase.getSelection() && btnCheckLdap.getSelection() && btnCheckXmpp.getSelection()
				&& btnCheckLider.getSelection()) {

			btnCheckAll.setSelection(true);
		} else {
			btnCheckAll.setSelection(false);
		}
	}

	/**
	 * This method decides to next button's status
	 */
	private void updatePageCompleteStatus() {
		// If any of the components is selected then enable the next button.
		setPageComplete(btnCheckDatabase.getSelection() || btnCheckLdap.getSelection()
				|| btnCheckXmpp.getSelection() || btnCheckLider.getSelection());
	}

	/**
	 * Setting component selections to the map in LiderSetupConfig
	 */
	private void setConfigVariables() {
		config.setInstallDatabase(btnCheckDatabase.getSelection());
		config.setInstallLdap(btnCheckLdap.getSelection());
		config.setInstallXmpp(btnCheckXmpp.getSelection());
		config.setInstallLider(btnCheckLider.getSelection());
	}

	@Override
	public IWizardPage getNextPage() {
		setConfigVariables();
		// Update next page according to selections of this page
		IWizardPage nextPage = super.getNextPage();
		if (nextPage instanceof LiderLocationOfComponentsPage) {
			((LiderLocationOfComponentsPage) nextPage).updatePage();
		}
		return super.getNextPage();
	}

}

package tr.org.liderahenk.admigration.wizard.pages;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.admigration.config.MigrationConfig;
import tr.org.liderahenk.admigration.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 */
public class MigrationConfirmPage extends WizardPage {

	private MigrationConfig config;
	private Label lblInfo;

	public MigrationConfirmPage(MigrationConfig config) {
		super(MigrationConfirmPage.class.getName(), Messages.getString("AD_MIGRATION"), null);
		setDescription("1.2 " + Messages.getString("MIGRATION_CONFIRM"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {

		Composite cmpMain = GUIHelper.createComposite(parent, 1);
		setControl(cmpMain);

		lblInfo = GUIHelper.createLabel(cmpMain, Messages.getString("AD_WILL_BE_MIGRATED_", "localhost", "localhost"));
		GUIHelper.createLabel(cmpMain, Messages.getString("WOULD_YOU_LIKE_TO_CONTINUE"));
	}

	@Override
	public IWizardPage getNextPage() {
		// Set the IP info in the opening of page
		lblInfo.setText(Messages.getString("AD_WILL_BE_MIGRATED_", config.getAdHost(), config.getLdapHost()));
		((ControlNextEvent) super.getNextPage()).setNextPageEventType(NextPageEventType.CLICK_FROM_PREV_PAGE);
		return super.getNextPage();
	}

}

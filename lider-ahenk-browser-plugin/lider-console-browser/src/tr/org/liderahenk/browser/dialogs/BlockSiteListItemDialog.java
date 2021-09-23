package tr.org.liderahenk.browser.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.browser.model.BlockSiteURL;
import tr.org.liderahenk.browser.tabs.BlockSiteSettingsTab;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class BlockSiteListItemDialog extends TitleAreaDialog {

	private Text txtURL;
	private Text txtDescription;
	private boolean editMode;
	private BlockSiteSettingsTab blockSiteTab;
	private BlockSiteURL url;

	public BlockSiteListItemDialog(Shell parentShell, BlockSiteSettingsTab blockSiteTab) {
		super(parentShell);
		this.blockSiteTab = blockSiteTab;
		this.editMode = false;
	}

	public BlockSiteListItemDialog(Shell parentShell, BlockSiteURL url, BlockSiteSettingsTab blockSiteTab,
			boolean editMode) {
		super(parentShell);
		this.editMode = editMode;
		this.blockSiteTab = blockSiteTab;
		this.url = url;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("BLOCK_SITE_LIST_ITEM_DIALOG_TITLE"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(area, GridData.FILL);
		composite.setLayout(new GridLayout(2, false));

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createURL(composite);
		createDescription(composite);

		applyDialogFont(composite);

		return composite;
	}

	private void createURL(Composite composite) {
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.getString("URL"));
		GridData grdData = new GridData();
		grdData.grabExcessHorizontalSpace = true;
		grdData.horizontalAlignment = GridData.FILL;
		txtURL = new Text(composite, SWT.BORDER);
		txtURL.setLayoutData(grdData);
		if (url != null && url.getURL() != null && url.getURL().length() > 0) {
			txtURL.setText(url.getURL());
		}
	}

	private void createDescription(Composite composite) {
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.getString("DESCRIPTION"));
		GridData grdData = new GridData();
		grdData.grabExcessHorizontalSpace = true;
		grdData.horizontalAlignment = GridData.FILL;
		txtDescription = new Text(composite, SWT.BORDER);
		txtDescription.setLayoutData(grdData);
		if (url != null && url.getDescription() != null && url.getDescription().length() > 0) {
			txtDescription.setText(url.getDescription());
		}
	}

	@Override
	protected void okPressed() {

		setReturnCode(OK);
		List<BlockSiteURL> list = blockSiteTab.getBtnBlacklist().getSelection() ? blockSiteTab.getBlockedUrlList()
				: blockSiteTab.getAllowedUrlList();

		if (txtURL == null || txtURL.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_URL_VALUE_FIELD"));
			return;
		}

		if (editMode) {
			int selectionIndex = blockSiteTab.getTblVwrUrl().getTable().getSelectionIndex();
			list.get(selectionIndex).setURL(txtURL.getText());
			list.get(selectionIndex).setDescription(txtDescription.getText());
		} else {
			if (null == list) {
				list = new ArrayList<BlockSiteURL>();
			}
			list.add(new BlockSiteURL(txtURL.getText(), txtDescription.getText()));
		}

		blockSiteTab.addRecordToURLTable(list);
		close();
	}

	protected Point getInitialSize() {
		return new Point(600, 300);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CANCEL"), false);
	}

}

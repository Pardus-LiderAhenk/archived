package tr.org.liderahenk.packagemanager.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.packagemanager.i18n.Messages;
import tr.org.liderahenk.packagemanager.model.PackageSourceItem;

public class PackageSourceItemDialog extends DefaultLiderTitleAreaDialog {

	// Model
	private PackageSourceItem item;
	// Table
	private TableViewer tableViewer;
	// Widgets
	private Label lblUrl;
	private Text txtUrl;

	public PackageSourceItemDialog(Shell parentShell, TableViewer tableViewer) {
		super(parentShell);
		this.tableViewer = tableViewer;
	}

	public PackageSourceItemDialog(Shell parentShell, PackageSourceItem item, TableViewer tableViewer) {
		super(parentShell);
		this.item = item;
		this.tableViewer = tableViewer;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("PACKAGE_SOURCE_ITEM"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		GridData gData = new GridData(SWT.FILL, SWT.FILL, false, true);
		gData.widthHint = 520;
		composite.setLayoutData(gData);

		Composite mailComposite = new Composite(composite, SWT.NONE);
		mailComposite.setLayout(new GridLayout(2, false));

		lblUrl = new Label(mailComposite, SWT.NONE);
		lblUrl.setText(Messages.getString("URL"));

		txtUrl = new Text(mailComposite, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = 400;
		txtUrl.setLayoutData(gridData);

		if (item != null && item.getUrl() != null) {
			txtUrl.setText(item.getUrl());
			txtUrl.setEditable(false);
		}

		return composite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (txtUrl.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_ALL_FIELDS"));
			return;
		}
		else if(!txtUrl.getText().startsWith("deb ")) {
			Notifier.error(null, Messages.getString("PACKAGE_ARCHIVE_ERROR"));
			return;
		}

		boolean editMode = true;
		if (item == null) {
			item = new PackageSourceItem();
			editMode = false;
		}
		// Set values
		item.setUrl(txtUrl.getText());

		// Get previous items...
		List<PackageSourceItem> items = (List<PackageSourceItem>) tableViewer.getInput();
		if (items == null) {
			items = new ArrayList<PackageSourceItem>();
		}

		if (editMode) {
			int index = tableViewer.getTable().getSelectionIndex();
			if (index > -1) {
				// Override previous item!
				items.set(index, item);
			}
		} else {
			// New item!
			items.add(item);
			PackageSourcesTaskDialog.addedSources.add(item.getUrl());
		}

		tableViewer.setInput(items);
		tableViewer.refresh();
		close();
	}

}

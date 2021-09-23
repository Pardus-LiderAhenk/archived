package tr.org.liderahenk.resourceusage.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.resourceusage.i18n.Messages;
import tr.org.liderahenk.resourceusage.model.ResourceUsageAlertItem;

public class ResourceUsageAlertItemDialog extends DefaultLiderTitleAreaDialog {

	// Model
	private ResourceUsageAlertItem item;
	// Table
	private TableViewer tableViewer;
	// Widgets
	private Combo cmbAlertType;
	private Label lblAlertType;
	private Label lblAlertLimit;
	private Spinner spinnerAlertLimit;
	private Label lblEMail;
	private Text txtEMail;

	private final String[] alertTypes = new String[] { "MEM", "DISC" };

	public ResourceUsageAlertItemDialog(Shell parentShell, TableViewer tableViewer) {
		super(parentShell);
		this.tableViewer = tableViewer;
	}

	public ResourceUsageAlertItemDialog(Shell parentShell, ResourceUsageAlertItem item, TableViewer tableViewer) {
		super(parentShell);
		this.item = item;
		this.tableViewer = tableViewer;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("RESOURCE_USAGE_ALERT_ITEM"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		GridData gData = new GridData(SWT.FILL, SWT.FILL, false, true);
		gData.widthHint = 520;
		composite.setLayoutData(gData);

		Composite alertComposite = new Composite(composite, SWT.NONE);
		alertComposite.setLayout(new GridLayout(4, false));

		alertComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblAlertType = new Label(alertComposite, SWT.NONE);
		lblAlertType.setText(Messages.getString("ALERT_TYPE"));

		cmbAlertType = new Combo(alertComposite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbAlertType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < alertTypes.length; i++) {
			String i18n = Messages.getString(alertTypes[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbAlertType.add(i18n);
				cmbAlertType.setData(i + "", alertTypes[i]);
			}
		}
		selectOption(cmbAlertType, item != null && item.getType() != null ? item.getType() : null);

		lblAlertLimit = new Label(alertComposite, SWT.NONE);
		lblAlertLimit.setText(Messages.getString("ALERT_LIMIT_VALUE"));

		spinnerAlertLimit = new Spinner(alertComposite, SWT.BORDER);
		spinnerAlertLimit.setMinimum(0);
		spinnerAlertLimit.setMaximum(100);
		if (item != null && item.getEmail() != null) {
			spinnerAlertLimit.setSelection(Integer.parseInt(item.getLimit()));
		}

		Composite mailComposite = new Composite(composite, SWT.NONE);
		mailComposite.setLayout(new GridLayout(2, false));

		lblEMail = new Label(mailComposite, SWT.NONE);
		lblEMail.setText(Messages.getString("EMAIL"));

		txtEMail = new Text(mailComposite, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint=230;
		txtEMail.setLayoutData(gridData);
		
		if (item != null && item.getEmail() != null) {
			txtEMail.setText(item.getEmail());
		}

		return composite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (txtEMail.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_ALL_FIELDS"));
			return;
		}

		boolean editMode = true;
		if (item == null) {
			item = new ResourceUsageAlertItem();
			editMode = false;
		}
		// Set values
		item.setType(cmbAlertType.getText());
		item.setLimit(String.valueOf(spinnerAlertLimit.getSelection()));
		item.setEmail(txtEMail.getText());

		// Get previous items...
		List<ResourceUsageAlertItem> items = (List<ResourceUsageAlertItem>) tableViewer.getInput();
		if (items == null) {
			items = new ArrayList<ResourceUsageAlertItem>();
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
		}

		tableViewer.setInput(items);
		tableViewer.refresh();

		close();
	}

	private boolean selectOption(Combo combo, Object value) {
		if (value == null) {
			combo.select(0);
			return false;
		}
		String[] items = combo.getItems();
		if (items == null) {
			return false;
		}
		for (int i = 0; i < items.length; i++) {
			if (items[i].equalsIgnoreCase(value.toString())) {
				combo.select(i);
				return true;
			}
		}
		combo.select(0); // select first option by default.
		return false;
	}

}

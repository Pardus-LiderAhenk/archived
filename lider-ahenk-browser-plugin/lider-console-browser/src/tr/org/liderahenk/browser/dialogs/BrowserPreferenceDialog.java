package tr.org.liderahenk.browser.dialogs;

import java.util.ArrayList;
import java.util.LinkedHashSet;

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
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

public class BrowserPreferenceDialog extends TitleAreaDialog {

	private Text txtPreferenceName;
	private Text txtValue;
	private boolean editMode;
	private BrowserProfileDialog browserPreferenceDialog;
	private BrowserPreference preference;

	public BrowserPreferenceDialog(Shell parentShell, BrowserProfileDialog browserPreferenceDialog) {
		super(parentShell);
		this.browserPreferenceDialog = browserPreferenceDialog;
		this.editMode = false;
	}

	public BrowserPreferenceDialog(Shell parentShell, BrowserPreference preference,
			BrowserProfileDialog browserPreferenceDialog, boolean editMode) {
		super(parentShell);
		this.editMode = editMode;
		this.browserPreferenceDialog = browserPreferenceDialog;
		this.preference = preference;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("BROWSER_PREF_DIALOG_TITLE"));
	}

	protected Point getInitialSize() {
		return new Point(600, 300);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite area = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(area, GridData.FILL);
		composite.setLayout(new GridLayout(2, false));

		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createPreferenceName(composite);
		createValue(composite);

		applyDialogFont(composite);
		return composite;
	}

	private void createPreferenceName(Composite composite) {
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.getString("PREFERENCE_NAME"));
		GridData grdData = new GridData();
		grdData.grabExcessHorizontalSpace = true;
		grdData.horizontalAlignment = GridData.FILL;
		txtPreferenceName = new Text(composite, SWT.BORDER);
		txtPreferenceName.setLayoutData(grdData);
		if (preference != null && preference.getPreferenceName() != null
				&& preference.getPreferenceName().length() > 0) {
			txtPreferenceName.setText(preference.getPreferenceName());
		}
	}

	private void createValue(Composite composite) {
		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(Messages.getString("PREFERENCE_VALUE"));
		GridData grdData = new GridData();
		grdData.grabExcessHorizontalSpace = true;
		grdData.horizontalAlignment = GridData.FILL;
		txtValue = new Text(composite, SWT.BORDER);
		txtValue.setLayoutData(grdData);
		if (preference != null && preference.getValue() != null && preference.getValue().length() > 0) {
			txtValue.setText(preference.getValue());
		}
	}

	@Override
	protected void okPressed() {

		setReturnCode(OK);
		LinkedHashSet<BrowserPreference> list = browserPreferenceDialog.getPreferenceList();

		if (txtPreferenceName == null || txtPreferenceName.getText().isEmpty() || txtValue == null
				|| txtValue.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_PREF_NAME_VALUE_FIELD"));
			return;
		}

		if (editMode) {
			int selectionIndex = browserPreferenceDialog.getTableViewer().getTable().getSelectionIndex();
			BrowserPreference pref = new ArrayList<BrowserPreference>(list).get(selectionIndex);
			pref.setPreferenceName(txtPreferenceName.getText());
			pref.setValue(txtValue.getText());
		} else {
			if (null == list) {
				list = new LinkedHashSet<BrowserPreference>();
			}
			list.add(new BrowserPreference(txtPreferenceName.getText(), txtValue.getText()));
		}

		browserPreferenceDialog.addRecordToPreferenceTable(list);
		close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, Messages.getString("OK"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.getString("CANCEL"), false);
	}

}

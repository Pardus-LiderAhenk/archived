package tr.org.liderahenk.rsyslog.dialogs;

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
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.rsyslog.i18n.Messages;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.rsyslog.model.LogFileListItem;

public class LogFileListItemDialog  extends DefaultLiderTitleAreaDialog {

	// Model
	private LogFileListItem item;
	// Table
	private TableViewer tableViewer;
	// Widgets
	private Combo cmbIsLocal;
	private Text txtRecordDescription;
	private Text txtLogFilePath;
	
	private final String[] isLocalArray = new String[] { "YES", "NO" };

	public LogFileListItemDialog(Shell parentShell, TableViewer tableViewer) {
		super(parentShell);
		this.tableViewer = tableViewer;
	}

	public LogFileListItemDialog(Shell parentShell, LogFileListItem item, TableViewer tableViewer) {
		super(parentShell);
		this.item = item;
		this.tableViewer = tableViewer;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("LOG_FILE_LIST_ITEM"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		
		Label lblIsLocal = new Label(composite, SWT.NONE);
		lblIsLocal.setText(Messages.getString("IS_LOCAL"));
		
		cmbIsLocal = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbIsLocal.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < isLocalArray.length; i++) {
			String i18n = Messages.getString(isLocalArray[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbIsLocal.add(i18n);
				cmbIsLocal.setData(i + "", isLocalArray[i]);
			}
		}

		selectOption(cmbIsLocal, item != null && item.getIsLocal() != null ? item.getIsLocal() : null);
		
		Label lblRecordDescription = new Label(composite, SWT.NONE);
		lblRecordDescription.setText(Messages.getString("RECORD_DESCRIPTION"));

		txtRecordDescription = new Text(composite, SWT.BORDER);
		txtRecordDescription.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if (item != null && item.getRecordDescription() != null) {
			txtRecordDescription.setText(item.getRecordDescription());
		}

		Label lblLogFilePath = new Label(composite, SWT.NONE);
		lblLogFilePath.setText(Messages.getString("LOG_FILE_PATH"));

		txtLogFilePath = new Text(composite, SWT.BORDER);
		txtLogFilePath.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if (item != null && item.getLogFilePath() != null) {
			txtLogFilePath.setText(item.getLogFilePath());
		}

		return composite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (txtRecordDescription.getText().isEmpty() || txtLogFilePath.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_ALL_FIELDS"));
			return;
		}

		boolean editMode = true;
		if (item == null) {
			item = new LogFileListItem();
			editMode = false;
		}
		// Set values
		item.setIsLocal(cmbIsLocal.getText());
		item.setRecordDescription(txtRecordDescription.getText());
		item.setLogFilePath(txtLogFilePath.getText());

		// Get previous items...
		List<LogFileListItem> items = (List<LogFileListItem>) tableViewer.getInput();
		if (items == null) {
			items = new ArrayList<LogFileListItem>();
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

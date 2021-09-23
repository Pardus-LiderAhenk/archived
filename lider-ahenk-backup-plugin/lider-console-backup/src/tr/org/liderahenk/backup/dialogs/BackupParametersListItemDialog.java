package tr.org.liderahenk.backup.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.backup.i18n.Messages;
import tr.org.liderahenk.backup.model.BackupParametersListItem;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

/**
 * Model class for backup parameter items.
 * 
 * @author <a href="mailto:seren.unal@agem.com.tr">Seren Ünal</a>
 *
 */
public class BackupParametersListItemDialog extends TrayDialog {

	// Model
	private BackupParametersListItem item;
	// Table
	private TableViewer tableViewer;
	// Widgets
	private Text   txtSourcePath;
	private Text   txtExcludeFileTypes;
	private Button btnCheckRecursive;
	private Button btnCheckPreserveGroup;
	private Button btnCheckPreserveOwner;
	private Button btnCheckPreservePermissions;
	private Button btnCheckArchive;
	private Button btnCheckCompress;
	private Button btnCheckUpdateOnlyExistings;
	private Text   txtLogicalVolume;
	private Text   txtVirtualGroup;
	private Text   txtLogicalVolumeSize;
	
	private boolean LVMChecked;
	
	public BackupParametersListItemDialog(Shell parentShell, TableViewer tableViewer, boolean LVMChecked) {
		super(parentShell);
		this.tableViewer = tableViewer;
		this.LVMChecked = LVMChecked;
	}

	public BackupParametersListItemDialog(Shell parentShell, BackupParametersListItem item, TableViewer tableViewer, boolean LVMChecked) {
		super(parentShell);
		this.item = item;
		this.tableViewer = tableViewer;
		this.LVMChecked = LVMChecked;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 400;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));

		Label lblDir = new Label(composite, SWT.NONE);
		lblDir.setText(Messages.getString("BACKUP_DIR"));
		txtSourcePath = new Text(composite, SWT.BORDER);
		txtSourcePath.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if (item != null && item.getSourcePath() != null) {
			txtSourcePath.setText(item.getSourcePath());
		}
		new Label(composite, SWT.NONE);

		Label lblExcFileType = new Label(composite, SWT.NONE);
		lblExcFileType.setText(Messages.getString("EXCLUDE_FILE_TYPE"));
		txtExcludeFileTypes = new Text(composite, SWT.BORDER);
		txtExcludeFileTypes.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if (item != null && item.getExcludePattern() != null) {
			txtExcludeFileTypes.setText(item.getExcludePattern());
		}
		new Label(composite, SWT.NONE);
		
		btnCheckRecursive = new Button(composite, SWT.CHECK);
		btnCheckRecursive.setText(Messages.getString("RECURSIVE"));
		if (item != null) {
			btnCheckRecursive.setSelection(item.isRecursive());
		}
		
		btnCheckPreserveGroup = new Button(composite, SWT.CHECK);
		btnCheckPreserveGroup.setText(Messages.getString("PROTECT_GROUP"));
		if (item != null) {
			btnCheckPreserveGroup.setSelection(item.isPreserveGroup());
		}
		new Label(composite, SWT.NONE);
		
		btnCheckPreserveOwner = new Button(composite, SWT.CHECK);
		btnCheckPreserveOwner.setText(Messages.getString("PROTECT_OWNER"));
		if (item != null) {
			btnCheckPreserveOwner.setSelection(item.isPreserveOwner());
		}
		
		btnCheckPreservePermissions = new Button(composite, SWT.CHECK);
		btnCheckPreservePermissions.setText(Messages.getString("PROTECT_PERMISSIONS"));
		if (item != null) {
			btnCheckPreservePermissions.setSelection(item.isPreservePermissions());
		}
		new Label(composite, SWT.NONE);
		
		btnCheckArchive = new Button(composite, SWT.CHECK);
		btnCheckArchive.setText(Messages.getString("ARCHIVE"));
		if (item != null) {
			btnCheckArchive.setSelection(item.isArchive());
		}
		
		btnCheckCompress = new Button(composite, SWT.CHECK);
		btnCheckCompress.setText(Messages.getString("COMPRESS"));
		if (item != null) {
			btnCheckCompress.setSelection(item.isCompress());
		}
		new Label(composite, SWT.NONE);
		
		btnCheckUpdateOnlyExistings = new Button(composite, SWT.CHECK);
		btnCheckUpdateOnlyExistings.setText(Messages.getString("UPDATE_ONLY_EXISTINGS"));
		if (item != null) {
			btnCheckUpdateOnlyExistings.setSelection(item.isExistingOnly());
		}
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);

		Label lblLogicalVol = new Label(composite, SWT.NONE);
		lblLogicalVol.setText(Messages.getString("LOGICAL_VOLUME"));
		txtLogicalVolume = new Text(composite, SWT.BORDER);
		txtLogicalVolume.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txtLogicalVolume.setEnabled(LVMChecked);
		if (item != null && item.getLogicalVolume() != null) {
			txtLogicalVolume.setText(item.getLogicalVolume());
		}
		new Label(composite, SWT.NONE);
		
		Label lblVirtualGrp = new Label(composite, SWT.NONE);
		lblVirtualGrp.setText(Messages.getString("VIRTUAL_GROUP"));
		txtVirtualGroup = new Text(composite, SWT.BORDER);
		txtVirtualGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txtVirtualGroup.setEnabled(LVMChecked);
		if (item != null && item.getVirtualGroup() != null) {
			txtVirtualGroup.setText(item.getVirtualGroup());
		}
		new Label(composite, SWT.NONE);
		
		Label lblLogicalVolSize = new Label(composite, SWT.NONE);
		lblLogicalVolSize.setText(Messages.getString("LOGICAL_VOLUME_SIZE"));
		txtLogicalVolumeSize = new Text(composite, SWT.BORDER);
		txtLogicalVolumeSize.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txtLogicalVolumeSize.setEnabled(LVMChecked);
		if (item != null && item.getLogicalVolumeSize() != null) {
			txtLogicalVolumeSize.setText(item.getLogicalVolumeSize());
		}
		new Label(composite, SWT.NONE);

		return composite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		if (txtSourcePath.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_BACKUP_DIR"));
			return;
		}
		if (LVMChecked && (txtLogicalVolume.getText().isEmpty() || txtLogicalVolumeSize.getText().isEmpty()
				|| txtVirtualGroup.getText().isEmpty())) {
			Notifier.warning(null, Messages.getString("FILL_LVM_PARAMS"));
			return;
		}

		boolean editMode = true;
		if (item == null) {
			item = new BackupParametersListItem();
			editMode = false;
		}
		// Set values
		item.setSourcePath(txtSourcePath.getText());
		item.setExcludePattern(txtExcludeFileTypes.getText());
		item.setRecursive(btnCheckRecursive.getSelection());
		item.setPreserveGroup(btnCheckPreserveGroup.getSelection());
		item.setPreserveOwner(btnCheckPreserveOwner.getSelection());
		item.setPreservePermissions(btnCheckPreservePermissions.getSelection());
		item.setArchive(btnCheckArchive.getSelection());
		item.setCompress(btnCheckCompress.getSelection());
		item.setExistingOnly(btnCheckUpdateOnlyExistings.getSelection());
		item.setLogicalVolume(txtLogicalVolume.getText());
		item.setVirtualGroup(txtVirtualGroup.getText());
		item.setLogicalVolumeSize(txtLogicalVolumeSize.getText());
		
		// Get previous items...
		List<BackupParametersListItem> items = (List<BackupParametersListItem>) tableViewer.getInput();
		if (items == null) {
			items = new ArrayList<BackupParametersListItem>();
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

}

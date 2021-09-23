package tr.org.liderahenk.user.privilege.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultLiderTitleAreaDialog;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.user.privilege.constants.UserPrivilegeConstants;
import tr.org.liderahenk.user.privilege.i18n.Messages;
import tr.org.liderahenk.user.privilege.model.PrivilegeItem;

/**
 * Dialog for adding new items to a User Privilege profile. Here user can
 * specify all the details of a Linux command such as Polkit (Policy Kit)
 * choices and resource limit values (aka "ulimit" and "cpulimit" parameters in
 * Linux).
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class PrivilegeItemDialog extends DefaultLiderTitleAreaDialog {
	// Model
	private PrivilegeItem item;
	// Table
	private TableViewer tableViewer;
	// Widgets
	// private Text txtCmd;
	private Combo cmbPrivilege;
	private Button btnLimitUsage;
	private Button btnNoLimit;
	private Button btnCheckBoxAhenkLimit;
	private Text txtCpu;
	private Text txtMemory;
	private Combo cmbCommand;

	// Combo values & i18n labels
	private final String[] statusArr = new String[] { "Privileged", "Unprivileged", "N/A" };
	private final String[] statusValueArr = new String[] { "0", "1", "2" };

	public PrivilegeItemDialog(Shell parentShell, TableViewer tableViewer) {
		super(parentShell);
		this.tableViewer = tableViewer;
	}

	public PrivilegeItemDialog(Shell parentShell, PrivilegeItem item, TableViewer tableViewer) {
		super(parentShell);
		this.item = item;
		this.tableViewer = tableViewer;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("PRIVILEGE_ITEM"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite cmpMain = new Composite(parent, SWT.NONE);
		cmpMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cmpMain.setLayout(new GridLayout(1, false));

		Composite cmpCommand = new Composite(cmpMain, SWT.NONE);
		cmpCommand.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cmpCommand.setLayout(new GridLayout(2, false));

		Label lblVendor = new Label(cmpCommand, SWT.NONE);
		lblVendor.setText(Messages.getString("COMMAND_PATH"));

		cmbCommand = new Combo(cmpCommand, SWT.DROP_DOWN);
		cmbCommand.setItems(new String[] { "/usr/bin/firefox", "/opt/google/chrome/chrome", "/usr/bin/thunderbird",
				"/usr/lib/libreoffice/program/soffice.bin", "/opt/master-pdf-editor-3/masterpdfeditor3", "/usr/bin/xfburn", "/usr/bin/vlc" });
		cmbCommand.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		if (item != null && item.getCmd() != null) {
			cmbCommand.setText(item.getCmd());
		} else {
			cmbCommand.select(0);
		}
		cmbCommand.setToolTipText(Messages.getString("EG_FIREFOX"));

		Label lblPrivilege = new Label(cmpCommand, SWT.NONE);
		lblPrivilege.setText(Messages.getString("PRIVILEGE"));

		cmbPrivilege = new Combo(cmpCommand, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbPrivilege.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < statusArr.length; i++) {
			String i18n = Messages.getString(statusArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbPrivilege.add(i18n);
				cmbPrivilege.setData(i + "", statusValueArr[i]);
			}
		}
		if (item != null && item.getPolkitStatus() != null) {
			if (item.getPolkitStatus().equals(UserPrivilegeConstants.PARAMETERS.PRIVILEGED)) {
				cmbPrivilege.select(0);
			} else if (item.getPolkitStatus().equals(UserPrivilegeConstants.PARAMETERS.UNPRIVILEGED)) {
				cmbPrivilege.select(1);
			} else {
				cmbPrivilege.select(2);
			}
		} else {
			cmbPrivilege.select(0);
		}

		cmbPrivilege.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (((Combo) e.getSource()).getSelectionIndex() == 1) {

					btnLimitUsage.setSelection(false);
					btnNoLimit.setSelection(true);

					btnLimitUsage.setEnabled(false);
					btnNoLimit.setEnabled(false);

					txtCpu.setEnabled(false);
					txtMemory.setEnabled(false);

					txtCpu.setText("");
					txtMemory.setText("");
				} else {

					btnLimitUsage.setSelection(true);
					btnNoLimit.setSelection(false);

					btnLimitUsage.setEnabled(true);
					btnNoLimit.setEnabled(true);

					txtCpu.setEnabled(true);
					txtMemory.setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Composite cmpRadio = new Composite(cmpMain, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		cmpRadio.setLayout(layout);

		btnLimitUsage = new Button(cmpRadio, SWT.RADIO);
		btnLimitUsage.setText(Messages.getString("LIMIT_RESOURCE_USAGE"));
		if (item != null && item.getLimitResourceUsage()) {
			btnLimitUsage.setSelection(true);
		}
		if (item == null) {
			btnLimitUsage.setSelection(true);
		}

		btnNoLimit = new Button(cmpRadio, SWT.RADIO);
		btnNoLimit.setText(Messages.getString("DO_NOT_LIMIT"));
		if (item != null && !item.getLimitResourceUsage()) {
			btnNoLimit.setSelection(true);
		}
		btnNoLimit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (btnNoLimit.getSelection()) {
					txtCpu.setEnabled(false);
					txtMemory.setEnabled(false);

					txtCpu.setText("");
					txtMemory.setText("");

				} else {
					txtCpu.setEnabled(true);
					txtMemory.setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		// limit only ahenk service

		Composite ahenkResource = new Composite(cmpMain, SWT.NONE);
		ahenkResource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ahenkResource.setLayout(new GridLayout(2, false));

		Label ahenkResourceUsage = new Label(ahenkResource, SWT.NONE);
		ahenkResourceUsage.setText(Messages.getString("LIMIT_AHENK"));

		btnCheckBoxAhenkLimit = new Button(ahenkResource, SWT.CHECK);
		btnCheckBoxAhenkLimit.setText("");

		btnCheckBoxAhenkLimit.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if (((Button) e.getSource()).getSelection() == true) {
					restrictAhenkSelect();
				} else {
					restrictAhenkDeselect();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Composite cmpResource = new Composite(cmpMain, SWT.NONE);
		cmpResource.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cmpResource.setLayout(new GridLayout(2, false));

		Label resourceUsage = new Label(cmpResource, SWT.NONE);
		resourceUsage.setText(Messages.getString("RESOURCE_USAGE_VALUES"));

		// empty label to fill composite
		new Label(cmpResource, SWT.NONE);

		Label lblCpu = new Label(cmpResource, SWT.NONE);
		lblCpu.setText(Messages.getString("CPU_IN_PERCENT"));

		txtCpu = new Text(cmpResource, SWT.BORDER);
		txtCpu.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txtCpu.setMessage(Messages.getString("FROM_1_UP_TO_200_EG_65"));
		if (item != null && item.getCpu() != null) {
			txtCpu.setText(item.getCpu().toString());
		}
		txtCpu.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// Validate inputs for integer only
				char c = e.character;
				if (!Character.isDigit(c) && !(c == SWT.DEL || e.keyCode == SWT.ARROW_LEFT
						|| e.keyCode == SWT.ARROW_RIGHT || c == SWT.BS)) {
					e.doit = false;
					return;
				} else {
					e.doit = true;
					return;
				}
			}
		});

		Label lblMemory = new Label(cmpResource, SWT.NONE);
		lblMemory.setText(Messages.getString("MEMORY_IN_KILOBYTES"));

		txtMemory = new Text(cmpResource, SWT.BORDER);
		txtMemory.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		txtMemory.setMessage(Messages.getString("EG_500000"));
		if (item != null && item.getMemory() != null) {
			txtMemory.setText(item.getMemory().toString());
		}
		txtMemory.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// Validate inputs for integer only
				char c = e.character;
				if (!Character.isDigit(c) && !(c == SWT.DEL || e.keyCode == SWT.ARROW_LEFT
						|| e.keyCode == SWT.ARROW_RIGHT || c == SWT.BS)) {
					e.doit = false;
					return;
				} else {
					e.doit = true;
					return;
				}
			}
		});

		// Disable resource limit text fields if item's limit resource button is
		// not selected
		if (item != null && !item.getLimitResourceUsage()) {
			txtCpu.setEnabled(false);
			txtMemory.setEnabled(false);
		}

		if (item != null && item.getCmd() != null) {
			cmbCommand.setText(item.getCmd());
			if ("/opt/ahenk/ahenkd".equals(item.getCmd())) {
				btnCheckBoxAhenkLimit.setSelection(true);
				restrictAhenkSelect();
			}
		}

		return cmpMain;
	}

	private void restrictAhenkSelect() {
		cmbCommand.setText("/opt/ahenk/ahenkd");
		cmbCommand.setEnabled(false);

		cmbPrivilege.select(2);
		cmbPrivilege.setEnabled(false);

		btnLimitUsage.setSelection(true);
		btnNoLimit.setSelection(false);

		btnLimitUsage.setEnabled(false);
		btnNoLimit.setEnabled(false);

		txtCpu.setEnabled(true);
		txtMemory.setEnabled(true);
	}

	private void restrictAhenkDeselect() {
		cmbCommand.setText("");
		cmbCommand.setEnabled(true);

		cmbPrivilege.select(2);
		cmbPrivilege.setEnabled(true);

		btnLimitUsage.setSelection(false);
		btnNoLimit.setSelection(true);

		btnLimitUsage.setEnabled(true);
		btnNoLimit.setEnabled(true);

		txtCpu.setEnabled(false);
		txtMemory.setEnabled(false);

		txtCpu.setText("");
		txtMemory.setText("");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {

		if (cmbCommand.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("PLEASE_ENTER_CMD_PATH"));
			return;
		}

		if (btnLimitUsage.getSelection() && txtCpu.getText().isEmpty() && txtMemory.getText().isEmpty()) {
			Notifier.error(null, Messages.getString("FILL_AT_LEAST_ONE_RESOURCE_USAGE_VALUE"));
			return;
		}

		if (!cmbCommand.getText().isEmpty() && "/opt/ahenk/ahenkd".equals(cmbCommand.getText())
				&& (btnCheckBoxAhenkLimit.getSelection() == false)) {
			Notifier.error(null, Messages.getString("PLEASE_ENTER_VALID_CMD_PATH"));
			return;
		}

		boolean editMode = true;
		if (item == null) {
			item = new PrivilegeItem();
			editMode = false;
		}
		// Set values
		item.setCmd(cmbCommand.getText());
		if (cmbPrivilege.getSelectionIndex() == 0) {
			item.setPolkitStatus(UserPrivilegeConstants.PARAMETERS.PRIVILEGED);
		} else if (cmbPrivilege.getSelectionIndex() == 1) {
			item.setPolkitStatus(UserPrivilegeConstants.PARAMETERS.UNPRIVILEGED);
		} else {
			item.setPolkitStatus(UserPrivilegeConstants.PARAMETERS.NA);
		}

		item.setLimitResourceUsage(btnLimitUsage.getSelection());

		if (btnLimitUsage.getSelection()) {
			if (!txtCpu.getText().isEmpty()) {
				item.setCpu(Integer.parseInt(txtCpu.getText()));
			} else {
				item.setCpu(null);
			}
			if (!txtMemory.getText().isEmpty()) {
				item.setMemory(Integer.parseInt(txtMemory.getText()));
			} else {
				item.setMemory(null);
			}
		}
		else{
			item.setCpu(null);
			item.setMemory(null);
		}

		// Get previous items...
		List<PrivilegeItem> items = (List<PrivilegeItem>) tableViewer.getInput();
		if (items == null) {
			items = new ArrayList<PrivilegeItem>();
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

		setReturnCode(OK);
		close();
	}

}

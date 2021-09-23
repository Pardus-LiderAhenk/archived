package tr.org.liderahenk.rsyslog.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.rsyslog.constants.RsyslogConstants;
import tr.org.liderahenk.rsyslog.i18n.Messages;
import tr.org.liderahenk.rsyslog.model.LogFileListItem;
import tr.org.liderahenk.rsyslog.utils.RsyslogUtils;

public class RsyslogProfileDialog implements IProfileDialog {

	private static final Logger logger = LoggerFactory.getLogger(RsyslogProfileDialog.class);

	// Widgets

	private Label lblLogRotation;
	private Label lblLogFreq;
	private Label lblKeptLogCount;
	private Text txtlogFileSize;
	private Label lblLogFileSize;
	private Combo cmbRotationFreq;
	private Button btnCheckNewLogFileAfterRotation;
	private Button btnCheckCompressOldLogFiles;
	private Button btnCheckSkipWithoutError;
	private Spinner spinnerLogRotationCount;
	private Label lblRemoteServerInfo;
	private Label lblRemoteServerAddress;
	private Label lblGate;
	private Label lblProtocol;
	private Combo cmbProtocol;
	private Text txtRemoteServerAddress;
	private Text txtPort;
	private Label lblManagedLogs;
	private Button btnAdd;
	private Button btnDelete;
	private Button btnEdit;

	private TableViewer tableViewer;
	private LogFileListItem item;

	private final String[] rotationFreqArray = new String[] { "DAILY", "WEEKLY", "MONTHLY", "YEARLY" };
	private final String[] protocolArray = new String[] { "UDP", "TCP" };

	@Override
	public void init() {
	}

	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		logger.debug("Profile recieved: {}", profile != null ? profile.toString() : null);
		createLogFileInfoInputs(parent, profile);
		createButtons(parent);
		createTable(parent, profile);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	private void createTableColumns() {

		String[] titles = { Messages.getString("IS_LOCAL"), Messages.getString("RECORD_DESCRIPTION"), Messages.getString("LOG_FILE_PATH") };
		int[] bounds = { 200, 200 };

		TableViewerColumn isLocalColumn = createTableViewerColumn(titles[0], bounds[0]);
		isLocalColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogFileListItem) {
					return ((LogFileListItem) element).getIsLocal();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn recordDescriptionColumn = createTableViewerColumn(titles[1], bounds[0]);
		recordDescriptionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogFileListItem) {
					return ((LogFileListItem) element).getRecordDescription();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn logFilePathColumn = createTableViewerColumn(titles[2], bounds[1]);
		logFilePathColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LogFileListItem) {
					return ((LogFileListItem) element).getLogFilePath();
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	private void createLogFileInfoInputs(Composite parent, Profile profile) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		lblLogRotation = new Label(composite, SWT.BOLD);
		lblLogRotation.setText(Messages.getString("LOG_ROTATION"));
		FontDescriptor descriptor = FontDescriptor.createFrom(lblLogRotation.getFont());
		// setStyle method returns a new font descriptor for the given style
		descriptor = descriptor.setStyle(SWT.BOLD);
		lblLogRotation.setFont(descriptor.createFont(lblLogRotation.getDisplay()));

		new Label(composite, SWT.NONE);

		lblLogFreq = new Label(composite, SWT.NONE);
		lblLogFreq.setText(Messages.getString("ROTATION_FREQUENCY"));

		cmbRotationFreq = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbRotationFreq.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < rotationFreqArray.length; i++) {
			String i18n = Messages.getString(rotationFreqArray[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbRotationFreq.add(i18n);
				cmbRotationFreq.setData(i + "", rotationFreqArray[i]);
			}
		}

		selectOption(cmbRotationFreq, profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(RsyslogConstants.PARAMETERS.ROTATION_FREQUENCY) : null);

		lblKeptLogCount = new Label(composite, SWT.NONE);
		lblKeptLogCount.setText(Messages.getString("KEPT_LOG_COUNT"));

		spinnerLogRotationCount = new Spinner(composite, SWT.BORDER);
		spinnerLogRotationCount.setMinimum(1);
		spinnerLogRotationCount.setMaximum(99999);

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.OLD_LOG_COUNT) != null) {
			spinnerLogRotationCount
					.setSelection((Integer) profile.getProfileData().get(RsyslogConstants.PARAMETERS.OLD_LOG_COUNT));
		}

		lblLogFileSize = new Label(composite, SWT.NONE);
		lblLogFileSize.setText(Messages.getString("LOG_FILE_SIZE"));

		txtlogFileSize = new Text(composite, SWT.BORDER);
		txtlogFileSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtlogFileSize.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (!(('0' <= e.character && e.character <= '9') || e.keyCode == 8))
					e.doit = false;
			}
		});
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.LOG_FILE_SIZE) != null) {
			txtlogFileSize.setText((String) profile.getProfileData().get(RsyslogConstants.PARAMETERS.LOG_FILE_SIZE));
		}

		btnCheckNewLogFileAfterRotation = new Button(composite, SWT.CHECK);
		btnCheckNewLogFileAfterRotation.setText(Messages.getString("NEW_LOG_FILE_AFTER_ROTATION"));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.NEW_LOG_FILE_AFTER_ROTATION) != null) {
			btnCheckNewLogFileAfterRotation.setSelection(
					(boolean) profile.getProfileData().get(RsyslogConstants.PARAMETERS.NEW_LOG_FILE_AFTER_ROTATION));
		}

		new Label(composite, SWT.NONE);

		btnCheckCompressOldLogFiles = new Button(composite, SWT.CHECK);
		btnCheckCompressOldLogFiles.setText(Messages.getString("COMPRESS_OLD_LOG_FILES"));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.COMPRESS_OLD_LOG_FILE) != null) {
			btnCheckCompressOldLogFiles.setSelection(
					(boolean) profile.getProfileData().get(RsyslogConstants.PARAMETERS.COMPRESS_OLD_LOG_FILE));
		}

		new Label(composite, SWT.NONE);

		btnCheckSkipWithoutError = new Button(composite, SWT.CHECK);
		btnCheckSkipWithoutError.setText(Messages.getString("SKIP_WITHOUT_ERROR"));
		if (profile != null && profile.getProfileData() != null && profile.getProfileData()
				.get(RsyslogConstants.PARAMETERS.PASS_AWAY_WITHOUT_ERROR_IF_FILE_NOT_EXIST) != null) {
			btnCheckSkipWithoutError.setSelection((boolean) profile.getProfileData()
					.get(RsyslogConstants.PARAMETERS.PASS_AWAY_WITHOUT_ERROR_IF_FILE_NOT_EXIST));
		}

		new Label(composite, SWT.NONE);

		lblRemoteServerInfo = new Label(composite, SWT.BOLD);
		lblRemoteServerInfo.setText(Messages.getString("REMOTE_SERVER_INFO"));
		// setStyle method returns a new font descriptor for the given style
		descriptor = descriptor.setStyle(SWT.BOLD);
		lblRemoteServerInfo.setFont(descriptor.createFont(lblRemoteServerInfo.getDisplay()));

		new Label(composite, SWT.NONE);

		lblRemoteServerAddress = new Label(composite, SWT.NONE);
		lblRemoteServerAddress.setText(Messages.getString("ADDRESS"));

		txtRemoteServerAddress = new Text(composite, SWT.BORDER);
		txtRemoteServerAddress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.ADDRESS) != null) {
			txtRemoteServerAddress.setText((String) profile.getProfileData().get(RsyslogConstants.PARAMETERS.ADDRESS));
		}

		lblGate = new Label(composite, SWT.NONE);
		lblGate.setText(Messages.getString("PORT"));

		txtPort = new Text(composite, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.PORT) != null) {
			txtPort.setText((String) profile.getProfileData().get(RsyslogConstants.PARAMETERS.PORT));
		}

		lblProtocol = new Label(composite, SWT.NONE);
		lblProtocol.setText(Messages.getString("PROTOCOL"));

		cmbProtocol = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbProtocol.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < protocolArray.length; i++) {
			String i18n = Messages.getString(protocolArray[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbProtocol.add(i18n);
				cmbProtocol.setData(i + "", protocolArray[i]);
			}
		}

		selectOption(cmbProtocol, profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(RsyslogConstants.PARAMETERS.PROTOCOL) : null);

		lblManagedLogs = new Label(composite, SWT.NONE);
		lblManagedLogs.setText(Messages.getString("MANAGED_LOGS"));
		descriptor = descriptor.setStyle(SWT.BOLD);
		lblManagedLogs.setFont(descriptor.createFont(lblManagedLogs.getDisplay()));

		new Label(composite, SWT.NONE);

	}

	/**
	 * Create table
	 * 
	 * @param parent
	 * @param profile
	 */
	private void createTable(final Composite parent, Profile profile) {
		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create table columns
		createTableColumns();

		// Configure table layout
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.getVerticalBar().setEnabled(true);
		table.getVerticalBar().setVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());

		if (profile != null && profile.getProfileData() != null
				&& profile.getProfileData().get(RsyslogConstants.PARAMETERS.LIST_ITEMS) != null) {
			@SuppressWarnings("unchecked")
			ArrayList<LinkedHashMap<String, String>> list = (ArrayList<LinkedHashMap<String, String>>) profile
					.getProfileData().get(RsyslogConstants.PARAMETERS.LIST_ITEMS);
			if (list != null) {
				List<LogFileListItem> items = new ArrayList<LogFileListItem>();
				for (LinkedHashMap<String, String> map : list) {
					LogFileListItem item = new LogFileListItem((String) map.get("isLocal"),
							(String) map.get("recordDescription"), (String) map.get("logFilePath"));
					items.add(item);
				}
				tableViewer.setInput(items);
				tableViewer.refresh();
			}
		}

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 140;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);

		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				firstElement = (LogFileListItem) firstElement;
				if (firstElement instanceof LogFileListItem) {
					setItem((LogFileListItem) firstElement);
				}
				btnEdit.setEnabled(true);
				btnDelete.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				LogFileListItemDialog dialog = new LogFileListItemDialog(parent.getShell(), getItem(), tableViewer);
				dialog.open();
			}
		});
	}

	/**
	 * Create add, edit, delete buttons for the table
	 * 
	 * @param parent
	 */

	private void createButtons(final Composite parent) {
		final Composite tableButtonComposite = new Composite(parent, SWT.NONE);
		tableButtonComposite.setLayout(new GridLayout(3, false));

		btnAdd = new Button(tableButtonComposite, SWT.NONE);
		btnAdd.setText(Messages.getString("ADD"));
		btnAdd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnAdd.setImage(SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/add.png"));
		btnAdd.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogFileListItemDialog dialog = new LogFileListItemDialog(Display.getDefault().getActiveShell(),
						tableViewer);
				dialog.create();
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnEdit = new Button(tableButtonComposite, SWT.NONE);
		btnEdit.setText(Messages.getString("EDIT"));
		btnEdit.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/edit.png"));
		btnEdit.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getItem()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_ITEM"));
					return;
				}
				LogFileListItemDialog dialog = new LogFileListItemDialog(tableButtonComposite.getShell(), getItem(),
						tableViewer);
				dialog.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnDelete = new Button(tableButtonComposite, SWT.NONE);
		btnDelete.setText(Messages.getString("DELETE"));
		btnDelete.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/delete.png"));
		btnDelete.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnDelete.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getItem()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_ITEM"));
					return;
				}
				@SuppressWarnings("unchecked")
				List<LogFileListItemDialog> items = (List<LogFileListItemDialog>) tableViewer.getInput();
				items.remove(tableViewer.getTable().getSelectionIndex());
				tableViewer.setInput(items);
				tableViewer.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(RsyslogConstants.PARAMETERS.ROTATION_FREQUENCY, RsyslogUtils.getSelectedValue(cmbRotationFreq));
		profileData.put(RsyslogConstants.PARAMETERS.OLD_LOG_COUNT, spinnerLogRotationCount.getSelection());
		if (txtlogFileSize != null && !"".equals(txtlogFileSize.getText())) {
			profileData.put(RsyslogConstants.PARAMETERS.LOG_FILE_SIZE, txtlogFileSize.getText());
		}
		profileData.put(RsyslogConstants.PARAMETERS.NEW_LOG_FILE_AFTER_ROTATION,
				btnCheckNewLogFileAfterRotation.getSelection());
		profileData.put(RsyslogConstants.PARAMETERS.COMPRESS_OLD_LOG_FILE, btnCheckCompressOldLogFiles.getSelection());
		profileData.put(RsyslogConstants.PARAMETERS.PASS_AWAY_WITHOUT_ERROR_IF_FILE_NOT_EXIST,
				btnCheckSkipWithoutError.getSelection());
		if (txtRemoteServerAddress != null && !"".equals(txtRemoteServerAddress.getText())) {
			profileData.put(RsyslogConstants.PARAMETERS.ADDRESS, txtRemoteServerAddress.getText());
		}
		if (txtPort != null && !"".equals(txtPort.getText())) {
			profileData.put(RsyslogConstants.PARAMETERS.PORT, txtPort.getText());
		}
		profileData.put(RsyslogConstants.PARAMETERS.PROTOCOL, RsyslogUtils.getSelectedValue(cmbProtocol));

		@SuppressWarnings("unchecked")
		List<LogFileListItem> items = (List<LogFileListItem>) tableViewer.getInput();
		if (items != null) {
			profileData.put(RsyslogConstants.PARAMETERS.LIST_ITEMS, items);
		}
		return profileData;
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
		if (txtPort.getText() == null || txtPort.getText().isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_PORT"));
		}
		if (txtRemoteServerAddress.getText() == null || txtRemoteServerAddress.getText().isEmpty()) {
			throw new ValidationException(Messages.getString("FILL_SERVER_ADDRESS"));
		}
		if (cmbProtocol.getSelectionIndex() == -1) {
			throw new ValidationException(Messages.getString("SELECT_PROTOCOL"));
		}
	}

	public LogFileListItem getItem() {
		return item;
	}

	public void setItem(LogFileListItem item) {
		this.item = item;
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
			if (items[i].equalsIgnoreCase(Messages.getString(value.toString()))) {
				combo.select(i);
				return true;
			}
		}
		combo.select(0); // select first option by default.
		return false;
	}

}

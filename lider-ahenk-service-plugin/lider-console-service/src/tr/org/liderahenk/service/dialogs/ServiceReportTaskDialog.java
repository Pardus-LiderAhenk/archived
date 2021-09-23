package tr.org.liderahenk.service.dialogs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Agent;
import tr.org.liderahenk.liderconsole.core.xmpp.XMPPClient;
import tr.org.liderahenk.service.i18n.Messages;



public class ServiceReportTaskDialog extends DefaultTaskDialog {

	private TableViewer tableViewer;
	private Label labelRecordDate;
	// private List<AgentService> list = new ArrayList<AgentService>();
	private String agentDn;
	private Table table;
	private Text textServiceName;
	private Text textAgent;
	private TableColumn columnAhenk;
	private TableColumn columnServices;
	private Composite composite;
	private Composite mainPanel;
	private TabFolder tabFolder;
	private Combo combo;
	private Hashtable<String, Boolean> agentList;

	public ServiceReportTaskDialog(Shell parentShell, String dn) {
		super(parentShell, dn);

	}

	@Override
	public String createTitle() {
		 return Messages.getString("SERVICE_REPORT_TITLE");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {

		mainPanel = parent;
		mainPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainPanel.setLayout(new GridLayout(1, false));

		tabFolder = new TabFolder(mainPanel, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		TabItem tbtmCreateFile = new TabItem(tabFolder, SWT.NONE);
		tbtmCreateFile.setText(Messages.getString("service_list")); //$NON-NLS-1$

		Group grpOnlineUsr = new Group(tabFolder, SWT.BORDER | SWT.SHADOW_IN);
		tbtmCreateFile.setControl(grpOnlineUsr);
		grpOnlineUsr.setLayout(new GridLayout(4, false));

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		labelRecordDate = new Label(grpOnlineUsr, SWT.NONE);
		labelRecordDate.setText(Messages.getString("DATE") + " " + format.format(new Date()));
		new Label(grpOnlineUsr, SWT.NONE);
		new Label(grpOnlineUsr, SWT.NONE);
		new Label(grpOnlineUsr, SWT.NONE);

		Label lblAgent = new Label(grpOnlineUsr, SWT.NONE);
		lblAgent.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAgent.setText(Messages.getString("agent")); //$NON-NLS-1$

		textAgent = new Text(grpOnlineUsr, SWT.BORDER);
		GridData gd_textAgent = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textAgent.widthHint = 324;
		textAgent.setLayoutData(gd_textAgent);
		new Label(grpOnlineUsr, SWT.NONE);
		new Label(grpOnlineUsr, SWT.NONE);

		Label label = new Label(grpOnlineUsr, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText(Messages.getString("service_name"));

		textServiceName = new Text(grpOnlineUsr, SWT.BORDER);
		GridData gd_textPackageName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textPackageName.widthHint = 324;
		textServiceName.setLayoutData(gd_textPackageName);
		new Label(grpOnlineUsr, SWT.NONE);
		new Label(grpOnlineUsr, SWT.NONE);

		Label lblStatus = new Label(grpOnlineUsr, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStatus.setText(Messages.getString("status")); //$NON-NLS-1$

		combo = new Combo(grpOnlineUsr, SWT.NONE);
		combo.setItems(new String[] { "", "Started", "Stopped" });
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 258;
		combo.setLayoutData(gd_combo);

		Button btnFilter = new Button(grpOnlineUsr, SWT.NONE);
		GridData gd_btnFilter = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_btnFilter.widthHint = 114;
		btnFilter.setLayoutData(gd_btnFilter);
		btnFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				

				String agent = textAgent.getText();
				String serviceName = textServiceName.getText();
				String status = combo.getText();

				status = status.equals("Started") ? "started" : status.equals("Stopped") ? "stopped" : "";
				// filter(agent, serviceName, status);

			}
		});
		btnFilter.setText(Messages.getString("filter"));

		Button btnExportToPDF = new Button(grpOnlineUsr, SWT.NONE);
		btnExportToPDF.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnExportToPDF.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// exportToPdf();
			}
		});
		btnExportToPDF.setText(Messages.getString("EXPORT"));

		composite = new Composite(grpOnlineUsr, SWT.BORDER | SWT.SHADOW_IN);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 2));

		tableViewer = new TableViewer(composite,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// createTableColumns();
		table = tableViewer.getTable();

		// list = getList();

		// table = new Table(composite, SWT.BORDER | SWT.MULTI);

		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		//
		// table.addListener(SWT.MeasureItem, new Listener() {
		// @Override
		// public void handleEvent(org.eclipse.swt.widgets.Event event) {
		//
		// event.height = 90;
		// }
		// });

		// columnAhenk = new TableColumn(table, SWT.CENTER);
		// columnAhenk.setText("Ahenk");
		// columnAhenk.setWidth(100);
		//
		// columnServices = new TableColumn(table, SWT.CENTER);
		// columnServices.setText("Services");
		// columnServices.setWidth(200);

		tableViewer.setContentProvider(new ArrayContentProvider());
		// tableViewer.setInput(list);
		tableViewer.refresh();
		tableViewer.getTable().pack();

		// fillDataToTable(list);

		agentList = XMPPClient.getInstance().getOnlineAgentPresenceMap();
		
		
		
		return mainPanel;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		if(agentList==null || agentList.size()==0) throw new ValidationException(Messages.getString("FILL_FIELDS"));

	}

	@Override
	public Map<String, Object> getParameterMap() {
		
		
		
		return null;
	}

	@Override
	public String getCommandId() {
		return null;
	}

	@Override
	public String getPluginName() {
		return null;
	}

	@Override
	public String getPluginVersion() {
		return null;
	}
	
}

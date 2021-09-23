package tr.org.liderahenk.installer.ahenk.wizard.pages;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.installer.ahenk.config.AhenkSetupConfig;
import tr.org.liderahenk.installer.ahenk.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.network.MapContentProvider;
import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;
import tr.org.pardus.mys.liderahenksetup.utils.network.NmapParameters;
import tr.org.pardus.mys.liderahenksetup.utils.network.RunnableNmap4j;
import tr.org.pardus.mys.liderahenksetup.utils.network.TableThreadHelper;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class AhenkNetworkScanPage extends WizardPage {

	private static final Logger logger = LoggerFactory.getLogger(AhenkNetworkScanPage.class);

	private AhenkSetupConfig config = null;

	// Widgets
	private TableViewer tblVwrSetup;
	private ProgressBar bar;
	private Combo cmbTimingTemplate;
	private Text txtPorts;
	private Button btnOsGuess;
	private Text txtSudoUsername;
	private Text txtSudoPassword;
	private Text txtIpRange;
	private Button btnScan;
	private Button btnSelectAll;
	private Button btnDeselectAll;
	private Button btnSelectOnlines;

	// Thread pool executor
	ThreadPoolExecutor executor = null;

	// Host colours
	Color HOST_UP_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	Color HOST_DOWN_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
	// Progress bar colour
	Color PROGRESS_BAR_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);

	private LinkedHashMap<String, Host> hosts;

	private static int NUM_THREADS = 50;

	static {
		String maxThreadSize = PropertyReader.property("max.thread.size");
		NUM_THREADS = maxThreadSize != null ? Integer.parseInt(maxThreadSize) : NUM_THREADS;
	}

	// Used by Timing Template combo.
	private final String[] timingTemplateArr = new String[] { "PARANOID", "SNEAKY", "POLITE", "NORMAL", "AGGRESSIVE",
			"INSANE" };
	private final String[] timingTemplateValArr = new String[] { "0", "1", "2", "3", "4", "5" };

	public AhenkNetworkScanPage(AhenkSetupConfig config) {
		super(AhenkNetworkScanPage.class.getName(), Messages.getString("INSTALLATION_OF_AHENK"), null);
		setDescription(Messages.getString("WHERE_WOULD_YOU_LIKE_TO_INSTALL_AHENK_(NETWORK_SCAN)"));
		this.config = config;
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {

		// Create main container
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		setControl(container);

		// Create nmap parameters inputs
		createInputs(container);

		tblVwrSetup = new TableViewer(container,
				SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CHECK);

		final Table table = tblVwrSetup.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		tblVwrSetup.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		tblVwrSetup.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					updatePageStatus(tblVwrSetup, btnOsGuess);
				}
			}
		});

		createTableColumns(tblVwrSetup);

		hosts = findIpRange();

		// Populate table with only IP addresses!
		tblVwrSetup.setContentProvider(new MapContentProvider());
		tblVwrSetup.setInput(hosts);
		tblVwrSetup.refresh();

		setPageComplete(false);

	}

	/**
	 * Creates inputs for network scanner (such as timing template, IP range
	 * etc.)
	 * 
	 * @param container
	 */

	private void createInputs(final Composite container) {

		final Composite inputContainer = new Composite(container, SWT.NONE);
		inputContainer.setLayout(new GridLayout(4, false));

		// Timing template
		Label lblTimingTemplate = new Label(inputContainer, SWT.NONE);
		lblTimingTemplate.setText(Messages.getString("TIMING_TEMPLATE"));

		cmbTimingTemplate = new Combo(inputContainer, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbTimingTemplate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < timingTemplateArr.length; i++) {
			String i18n = Messages.getString(timingTemplateArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbTimingTemplate.add(i18n);
				cmbTimingTemplate.setData(i + "", timingTemplateValArr[i]);
			}
		}
		cmbTimingTemplate.select(3); // Select 'normal' template as default

		// Ports
		Label lblPorts = new Label(inputContainer, SWT.NONE);
		lblPorts.setText(Messages.getString("PORTS"));

		txtPorts = new Text(inputContainer, SWT.BORDER);
		txtPorts.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Operating System Guessing
		btnOsGuess = new Button(inputContainer, SWT.CHECK);
		btnOsGuess.setText(Messages.getString("OS_GUESS"));
		btnOsGuess.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePageStatus(tblVwrSetup, btnOsGuess);
				organizeSudoFields();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		txtSudoUsername = new Text(inputContainer, SWT.BORDER);
		txtSudoUsername.setToolTipText(Messages.getString("USERNAME"));
		txtSudoUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtSudoUsername.setText("root");
		txtSudoUsername.setEnabled(false);

		txtSudoPassword = new Text(inputContainer, SWT.BORDER);
		txtSudoPassword.setToolTipText(Messages.getString("PASSWORD"));
		txtSudoPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtSudoPassword.setEnabled(false);
		txtSudoPassword.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageStatus(tblVwrSetup, btnOsGuess);
			}
		});

		new Label(inputContainer, SWT.NONE);

		// IP Range
		Label lblIpRange = new Label(inputContainer, SWT.NONE);
		lblIpRange.setText(Messages.getString("IP_RANGE"));

		txtIpRange = new Text(inputContainer, SWT.BORDER);
		txtIpRange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		GridData gd = new GridData();
		gd.widthHint = 150;

		// Network Scan Button
		btnScan = new Button(inputContainer, SWT.NONE);
		btnScan.setText(Messages.getString("START_SCAN"));
		btnScan.setLayoutData(gd);
		btnScan.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Start a new scan!
				if (executor == null || executor.getActiveCount() == 0) {

					Display.getCurrent().asyncExec(new Runnable() {
						@Override
						public void run() {
							btnScan.setText(Messages.getString("STOP_SCAN"));
						}
					});

					// If user provides an IP range, scan only it!
					// otherwise find all IP addresses on the connected networks
					List<String> ipAddresses = null;
					try {
						if (txtIpRange.getText() != null && !txtIpRange.getText().isEmpty()) {
							ipAddresses = NetworkUtils.convertToIpList(txtIpRange.getText());
						} else {
							ipAddresses = NetworkUtils.findIpAddresses();
						}
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (SocketException e1) {
						e1.printStackTrace();
					}

					// Gather network info via threads
					// Each thread is responsible for a limited number of hosts!
					if (ipAddresses != null && ipAddresses.size() > 0) {

						// Create thread pool executor!
						LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
						@SuppressWarnings({ "unchecked", "rawtypes" })
						final List<Runnable> running = Collections.synchronizedList(new ArrayList());
						executor = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 0L, TimeUnit.MILLISECONDS,
								taskQueue, Executors.defaultThreadFactory()) {

							@Override
							protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, T value) {
								return new FutureTask<T>(runnable, value) {
									@Override
									public String toString() {
										return runnable.toString();
									}
								};
							}

							@Override
							protected void beforeExecute(Thread t, Runnable r) {
								super.beforeExecute(t, r);
								running.add(r);
							}

							@Override
							protected void afterExecute(Runnable r, Throwable t) {
								super.afterExecute(r, t);
								final int selection = 100 - (executor.getActiveCount() / NUM_THREADS * 100);
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										bar.setSelection(selection);
									}
								});
								running.remove(r);
								logger.info("Running threads: {0}", running);
							}
						};

						// Threads use this helper class to modify IP table.
						TableThreadHelper tableHelper = new TableThreadHelper(tblVwrSetup, hosts);

						// Calculate number of the hosts a thread can process
						int numberOfHosts = ipAddresses.size();
						int hostsPerThread;
						if (numberOfHosts < NUM_THREADS) {
							hostsPerThread = 1;
						} else {
							hostsPerThread = numberOfHosts / NUM_THREADS;
						}

						logger.info("Hosts: {}, Threads:{}, Host per Thread: {}",
								new Object[] { numberOfHosts, NUM_THREADS, hostsPerThread });

						for (int i = 0; i < numberOfHosts; i += hostsPerThread) {
							List<String> ipSubList;
							if (numberOfHosts < NUM_THREADS) {
								ipSubList = ipAddresses.subList(i, i + 1);
							} else {
								int toIndex = i + hostsPerThread;
								ipSubList = ipAddresses.subList(i,
										toIndex < ipAddresses.size() ? toIndex : ipAddresses.size() - 1);
							}

							NmapParameters params = new NmapParameters();
							params.setIpList(ipSubList);
							params.setPorts(getPorts());
							params.setSudoPassword(getSudoPasswdIfExists());
							params.setSudoUsername(getSudoUsernameIfExists());
							params.setTimingTemplate(getSelectedTimingTemplate());

							RunnableNmap4j nmap4jThread = new RunnableNmap4j(tableHelper, params);
							executor.execute(nmap4jThread);
						}

						// Initiate shutdown (waits for already-running threads
						// to finish)
						executor.shutdown();
					}
				}
				// Executor already running! Force stop.
				else {

					Display.getCurrent().asyncExec(new Runnable() {
						@Override
						public void run() {
							btnScan.setText(Messages.getString("START_SCAN"));
						}
					});

					BusyIndicator.showWhile(btnScan.getDisplay(), new Thread() {
						@Override
						public void run() {
							executor.shutdownNow();
						}
					});

					btnScan.setText(Messages.getString("START_SCAN"));
				}

			}

			private String getSelectedTimingTemplate() {
				int selectionIndex = cmbTimingTemplate.getSelectionIndex();
				if (selectionIndex > -1 && cmbTimingTemplate.getItem(selectionIndex) != null
						&& cmbTimingTemplate.getData(selectionIndex + "") != null) {
					return cmbTimingTemplate.getData(selectionIndex + "").toString();
				}
				return "4";
			}

			private String getPorts() {
				if (txtPorts.getText() != null && !txtPorts.getText().isEmpty()) {
					return txtPorts.getText();
				}
				return null;
			}

			private String getSudoUsernameIfExists() {
				if (btnOsGuess.getSelection()) {
					return (txtSudoUsername.getText() == null || txtSudoUsername.getText().isEmpty()) ? "root"
							: txtSudoUsername.getText();
				}
				return null;
			}

			private String getSudoPasswdIfExists() {
				if (btnOsGuess.getSelection() && txtSudoPassword.getText() != null
						&& !txtSudoPassword.getText().isEmpty()) {
					return txtSudoPassword.getText();
				}
				return null;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		new Label(inputContainer, SWT.NONE);

		// Create New Composite for Select All and Select Onlines Buttons
		final Composite selectBtnContainer = new Composite(container, SWT.NONE);
		selectBtnContainer.setLayout(new GridLayout(3, false));

		// Select All Button
		btnSelectAll = new Button(selectBtnContainer, SWT.PUSH);
		btnSelectAll.setText(Messages.getString("SELECT_ALL"));
		btnSelectAll.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDeselectAll("S");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Deselect All Button
		btnDeselectAll = new Button(selectBtnContainer, SWT.PUSH);
		btnDeselectAll.setText(Messages.getString("DESELECT_ALL"));
		btnDeselectAll.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDeselectAll("D");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Select Online IPs
		btnSelectOnlines = new Button(selectBtnContainer, SWT.PUSH);
		btnSelectOnlines.setText(Messages.getString("SELECT_ONLINE_IPS"));
		btnSelectOnlines.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectOnlineIps();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * Finds IP addresses of all connected networks.
	 * 
	 * @return a collection of Host instances
	 */
	private LinkedHashMap<String, Host> findIpRange() {

		LinkedHashMap<String, Host> items = new LinkedHashMap<String, Host>();

		try {
			List<String> ipAddresses = NetworkUtils.findIpAddresses();
			if (ipAddresses != null && ipAddresses.size() > 0) {
				for (String ipAddress : ipAddresses) {
					Host host = new Host();
					Address address = new Address();
					address.setAddr(ipAddress);
					address.setAddrtype(NetworkUtils.IPV4);
					host.addAddress(address);
					items.put(ipAddress, host);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return items;
	}

	/**
	 * Creates table columns (such as IP address, MAC address, MAC vendor,
	 * Hostname, Open ports/services etc.)
	 * 
	 * @param tblVwrSetup
	 */
	private void createTableColumns(TableViewer tblVwrSetup) {

		TableViewerColumn ipCol = createTableViewerColumn(tblVwrSetup, Messages.getString("IP_ADDRESS"), 100);
		ipCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String ip = NetworkUtils.getIpV4((Host) element);
					return ip != null ? ip : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}

			@Override
			public Color getForeground(Object element) {
				if (element instanceof Host && NetworkUtils.isHostUp((Host) element)) {
					return HOST_UP_COLOR;
				} else {
					return HOST_DOWN_COLOR;
				}
			}
		});

		TableViewerColumn hostnameCol = createTableViewerColumn(tblVwrSetup, Messages.getString("HOST_NAME"), 50);
		hostnameCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String hostname = NetworkUtils.getHostname((Host) element);
					return hostname != null ? hostname : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn portsCol = createTableViewerColumn(tblVwrSetup, Messages.getString("PORTS"), 150);
		portsCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String openPorts = NetworkUtils.getOpenPorts((Host) element);
					return openPorts != null ? openPorts : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn osCol = createTableViewerColumn(tblVwrSetup, Messages.getString("OS_INFO"), 250);
		osCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String osGuess = NetworkUtils.getOsGuess((Host) element);
					return osGuess != null ? osGuess : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn distanceCol = createTableViewerColumn(tblVwrSetup, Messages.getString("DISTANCE"), 30);
		distanceCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String distance = NetworkUtils.getDistance((Host) element);
					return distance != null ? distance : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn uptimeCol = createTableViewerColumn(tblVwrSetup, Messages.getString("UPTIME"), 50);
		uptimeCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String uptime = NetworkUtils.getUptime((Host) element);
					return uptime != null ? uptime : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn macAddressCol = createTableViewerColumn(tblVwrSetup, Messages.getString("MAC_ADDRESS"), 100);
		macAddressCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String mac = NetworkUtils.getMac((Host) element);
					return mac != null ? mac : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn macVendorCol = createTableViewerColumn(tblVwrSetup, Messages.getString("MAC_VENDOR"), 100);
		macVendorCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Host) {
					String vendor = NetworkUtils.getMacVendor((Host) element);
					return vendor != null ? vendor : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

	}

	/**
	 * Helper method to create table columns
	 * 
	 * @param tblVwrSetup
	 * @param title
	 * @param bound
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(final TableViewer tblVwrSetup, String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tblVwrSetup, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	private void updatePageStatus(TableViewer tblVwrSetup, Button btnOsGuess) {

		// At least one IP should be selected
		boolean ipSelected = false;

		TableItem[] items = tblVwrSetup.getTable().getItems();

		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				ipSelected = true;
				// If one of the IP's is selected, that's enough
				// do not iterate over all items
				i = items.length;
			}
		}

		// If show OS info checkbox is selected, sudo info should be entered.
		boolean sudoInfoEntered = false;

		if (btnOsGuess.getSelection()) {
			if ((!"".equals(txtSudoUsername.getText()) && txtSudoUsername.getText() != null)
					&& (!"".equals(txtSudoPassword.getText()) && txtSudoPassword.getText() != null)) {
				sudoInfoEntered = true;
			} else {
				sudoInfoEntered = false;
			}
		} else {
			sudoInfoEntered = true;
		}

		// If required info is entered and at least one IP is selected
		// then set page complete.
		setPageComplete(ipSelected && sudoInfoEntered);
	}

	// Select or deselect all checkboxes
	private void selectDeselectAll(String selectOrDeselect) {
		TableItem[] items = tblVwrSetup.getTable().getItems();

		if ("S".equals(selectOrDeselect)) {
			for (int i = 0; i < items.length; i++) {
				items[i].setChecked(true);
			}
		} else {
			for (int i = 0; i < items.length; i++) {
				items[i].setChecked(false);
			}
		}
		updatePageStatus(tblVwrSetup, btnOsGuess);
	}

	private void selectOnlineIps() {
		TableItem[] items = tblVwrSetup.getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			if (NetworkUtils.isHostUp((Host) items[i].getData())) {
				items[i].setChecked(true);
			} else {
				items[i].setChecked(false);
			}
		}
		updatePageStatus(tblVwrSetup, btnOsGuess);
	}

	private void organizeSudoFields() {
		if (btnOsGuess.getSelection()) {
			txtSudoUsername.setEnabled(true);
			txtSudoPassword.setEnabled(true);
		} else {
			txtSudoUsername.setEnabled(false);
			txtSudoPassword.setEnabled(false);
		}
	}

	@Override
	public IWizardPage getNextPage() {

		TableItem[] items = tblVwrSetup.getTable().getItems();

		List<String> selectedIpList = new ArrayList<String>();

		for (int i = 0; i < items.length; i++) {
			if (items[i].getChecked()) {
				selectedIpList.add(items[i].getText());
			}
		}

		config.setIpList(selectedIpList);

		return super.getNextPage();
	}
}

package tr.org.liderahenk.browser.dialogs;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
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
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.browser.constants.BrowserConstants;
import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.browser.tabs.GeneralSettingsTab;
import tr.org.liderahenk.browser.tabs.PrivacySettingsTab;
import tr.org.liderahenk.browser.tabs.ProxySettingsTab;
import tr.org.liderahenk.browser.util.BrowserUtil;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

public class BrowserProfileDialog implements IProfileDialog {

	private static final Logger logger = LoggerFactory.getLogger(BrowserProfileDialog.class);

	private TableViewer tableViewer;
	private LinkedHashSet<BrowserPreference> preferenceList; // need ordered set
	private BrowserPreference selectedPreference;

	private GeneralSettingsTab generalSettings;
	private ProxySettingsTab proxySettings;
	private PrivacySettingsTab privacySettings;
	
	// Adding blacklist and whitelist is not possible with newer version of Blocksite extension 
	//private BlockSiteSettingsTab blockSiteSettingsTab;

	@Override
	public void init() {
		this.generalSettings = new GeneralSettingsTab();
		this.proxySettings = new ProxySettingsTab();
		this.privacySettings = new PrivacySettingsTab();
		//this.blockSiteSettingsTab = new BlockSiteSettingsTab();
	}

	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		try {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(1, false));
			CTabFolder tabFolder = createTabFolder(composite);

			// General Settings Tab
			generalSettings.createInputs(createInputTab(tabFolder, Messages.getString("GENERAL_SETTINGS")), profile);

			// Web Browser Proxy Settings Tab
			proxySettings.createInputs(createInputTab(tabFolder, Messages.getString("PROXY_SETTINGS")), profile);

			// Privary Settings Tab
			privacySettings.createInputs(createInputTab(tabFolder, Messages.getString("PRIVACY_SETTINGS")), profile);

			// Block Site Tab
			// blockSiteSettingsTab.createInputs(createInputTab(tabFolder, Messages.getString("BLOCK_SITE_SETTINGS")),
			//		profile);

			// Web Browser Preferences Tab - Shows all preferences here!
			Composite tabComposite = createInputTab(tabFolder, Messages.getString("PREFERENCES"), false);
			createPreferenceButtons(tabComposite);
			createPreferenceTable(tabComposite, profile);

			tabFolder.setSelection(0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create new tab folder instance that can be used to contain tabs
	 * 
	 * @param composite
	 * @return
	 */
	private CTabFolder createTabFolder(final Composite composite) {
		CTabFolder tabFolder = new CTabFolder(composite, SWT.BORDER);
		GridData gdTabFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		tabFolder.setLayoutData(gdTabFolder);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		return tabFolder;
	}

	/**
	 * Create input tab with specified label in a specified folder
	 * 
	 * @param tabFolder
	 * @param label
	 * @return
	 */
	private Composite createInputTab(CTabFolder tabFolder, String label) {
		return createInputTab(tabFolder, label, true);
	}

	/**
	 * Overloaded method which can be used to create scrolled composite inside
	 * tab.
	 * 
	 * @param tabFolder
	 * @param label
	 * @param isScrolledComposite
	 * @return
	 */
	private Composite createInputTab(CTabFolder tabFolder, String label, boolean isScrolledComposite) {
		CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
		tab.setText(label);
		Composite composite = isScrolledComposite ? new ScrolledComposite(tabFolder, SWT.V_SCROLL)
				: new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		tab.setControl(composite);
		return composite;
	}

	/**
	 * Create buttons that can be used to manage preference table
	 * 
	 * @param compositePreferences
	 */
	private void createPreferenceButtons(Composite compositePreferences) {
		Composite group = new Composite(compositePreferences, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		Button btnAddPref = new Button(group, SWT.NONE);
		GridData gdBtnAddPref = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdBtnAddPref.widthHint = 77;
		btnAddPref.setLayoutData(gdBtnAddPref);
		btnAddPref.setText(Messages.getString("ADD"));
		btnAddPref.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/add.png"));
		btnAddPref.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BrowserPreferenceDialog frd = new BrowserPreferenceDialog(Display.getCurrent().getActiveShell(),
						getSelf());
				frd.create();
				frd.open();
			}
		});

		Button btnRemovePref = new Button(group, SWT.NONE);
		GridData gdBtnRemovePref = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gdBtnRemovePref.widthHint = 82;
		btnRemovePref.setLayoutData(gdBtnRemovePref);
		btnRemovePref.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/delete.png"));
		btnRemovePref.setText(Messages.getString("REMOVE"));
		btnRemovePref.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Set<BrowserPreference> list = getPreferenceList();
				if (tableViewer.getTable().getSelectionIndex() > -1) {
					BrowserPreference prefToBeRemoved = (BrowserPreference) list.toArray()[tableViewer.getTable()
							.getSelectionIndex()];
					list.remove(prefToBeRemoved);
					tableViewer.setInput(list);
					tableViewer.refresh();
				}
			}
		});
	}

	/**
	 * Create preference table
	 * 
	 * @param composite
	 * @param profile
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private void createPreferenceTable(final Composite composite, Profile profile)
			throws JsonParseException, JsonMappingException, IOException {
		tableViewer = new TableViewer(composite,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		data.heightHint = 100;
		table.setLayoutData(data);

		createPreferenceTableColumns();

		Set<BrowserPreference> temp = BrowserUtil.getPreferences(profile);
		if (profile != null && temp != null) {
			LinkedHashSet<BrowserPreference> preferences = new LinkedHashSet<BrowserPreference>(temp);
			setPreferenceList(preferences);
			tableViewer.setInput(preferences);
		} else {
			setPreferenceList(new LinkedHashSet<BrowserPreference>());
			tableViewer.setInput(new BrowserPreference[] {});
		}

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof BrowserPreference) {
					setSelectedPreference((BrowserPreference) firstElement);
				}
			}
		});

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				BrowserPreferenceDialog dialog = new BrowserPreferenceDialog(composite.getShell(),
						getSelectedPreference(), getSelf(), true);
				dialog.create();
				dialog.open();
			}
		});
	}

	/**
	 * Create preference table columns
	 */
	private void createPreferenceTableColumns() {
		String[] titles = { Messages.getString("PREFERENCE_NAME"), Messages.getString("PREFERENCE_VALUE") };
		int[] bounds = { 300, 100 };

		TableViewerColumn preferenceNameColumn = createTableViewerColumn(titles[0], bounds[0], this.tableViewer);
		preferenceNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BrowserPreference)
					return ((BrowserPreference) element).getPreferenceName();
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn valueColumn = createTableViewerColumn(titles[1], bounds[1], this.tableViewer);
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BrowserPreference)
					return ((BrowserPreference) element).getValue();
				return Messages.getString("UNTITLED");
			}
		});
	}

	/**
	 * Create new table viewer column
	 * 
	 * @param title
	 * @param bound
	 * @param viewer
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound, TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	/**
	 * Add specified preference list to current list and refresh table
	 * accordingly.
	 * 
	 * @param list
	 */
	public void addRecordToPreferenceTable(LinkedHashSet<BrowserPreference> list) {
		setPreferenceList(list);
		tableViewer.setInput(list);
		tableViewer.refresh();
	}

	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		LinkedHashSet<BrowserPreference> set = new LinkedHashSet<BrowserPreference>();

		// Block site settings
		//Set<BrowserPreference> temp = blockSiteSettingsTab.getValues();
		//if (temp != null) {
		//	set.addAll(temp);
		//}
		// General settings
		Set<BrowserPreference> temp = generalSettings.getValues();
		if (temp != null) {
			set.addAll(temp);
		}
		// Proxy settings
		temp = proxySettings.getValues();
		if (temp != null) {
			set.addAll(temp);
		}
		// Privacy settings
		temp = privacySettings.getValues();
		if (temp != null) {
			set.addAll(temp);
		}
		// Add preference list, it may be modified via preference dialog
		if (preferenceList != null) {
			set.addAll(preferenceList);
		}

		profileData.put(BrowserConstants.PREFERENCES_MAP_KEY, set);
		return profileData;
	}

	@Override
	public void validateBeforeSave() throws ValidationException {
		generalSettings.validateBeforeSave();
		proxySettings.validateBeforeSave();
		privacySettings.validateBeforeSave();
		//blockSiteSettingsTab.validateBeforeSave();
	}

	public BrowserProfileDialog getSelf() {
		return this;
	}

	public BrowserPreference getSelectedPreference() {
		return selectedPreference;
	}

	public void setSelectedPreference(BrowserPreference selectedPreference) {
		this.selectedPreference = selectedPreference;
	}

	public LinkedHashSet<BrowserPreference> getPreferenceList() {
		return preferenceList;
	}

	public void setPreferenceList(LinkedHashSet<BrowserPreference> preferenceList) {
		this.preferenceList = preferenceList;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

}


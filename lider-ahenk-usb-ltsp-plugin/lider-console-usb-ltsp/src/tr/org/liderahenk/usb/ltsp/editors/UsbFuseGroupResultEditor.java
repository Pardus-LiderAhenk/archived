package tr.org.liderahenk.usb.ltsp.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;
import tr.org.liderahenk.liderconsole.core.rest.responses.IResponse;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.IExportableTableViewer;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.usb.ltsp.constants.UsbLtspConstants;
import tr.org.liderahenk.usb.ltsp.enums.StatusCode;
import tr.org.liderahenk.usb.ltsp.i18n.Messages;
import tr.org.liderahenk.usb.ltsp.model.UsbFuseGroupResult;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class UsbFuseGroupResultEditor extends EditorPart {
	public UsbFuseGroupResultEditor() {
	}

	private static final Logger logger = LoggerFactory.getLogger(UsbFuseGroupResultEditor.class);

	private TableViewer tableViewer;
	private TableFilter tablePriviligeFilter;
	private TableFilter tableTextFilter;
	private Text txtSearch;
	private Button btnRefresh;
	private Composite buttonComposite;

	private Button btnPrivileged;
	private Button btnUnprivileged;


	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(((DefaultEditorInput) input).getLabel());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createButtonsArea(parent);
		createTableArea(parent);
	}

	private void createTableArea(final Composite parent) {

		createTableFilterArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent, new IExportableTableViewer() {
			@Override
			public Composite getButtonComposite() {
				return buttonComposite;
			}

			@Override
			public String getSheetName() {
				return Messages.getString("USB_FUSE_GROUP_RESULTS");
			}

			@Override
			public String getReportName() {
				return Messages.getString("USB_FUSE_GROUP_RESULTS");
			}
		});
		createTableColumns();
		populateTable();

		tableTextFilter = new TableFilter();
		tableViewer.addFilter(tableTextFilter);

		tablePriviligeFilter = new TableFilter();
		tableViewer.addFilter(tablePriviligeFilter);
		
		tableViewer.refresh();
	}

	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_USB_FUSE_GROUP_RESULT_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filterTable(null);
			}
		});

		btnPrivileged = new Button(filterContainer, SWT.CHECK);
		btnPrivileged.setText(Messages.getString("SHOW_AUTHORIZED"));
		btnPrivileged.setSelection(true);
		btnPrivileged.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				filterTable(btnPrivileged);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

		btnUnprivileged = new Button(filterContainer, SWT.CHECK);
		btnUnprivileged.setText(Messages.getString("SHOW_UNAUTHORIZED"));
		btnUnprivileged.setSelection(true);
		btnUnprivileged.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				filterTable(btnUnprivileged);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});

	}

	private void createTableColumns() {

		// Username
		TableViewerColumn usernameColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("USERNAME"), 300);
		usernameColumn.getColumn().setAlignment(SWT.LEFT);
		usernameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UsbFuseGroupResult) {
					return ((UsbFuseGroupResult) element).getUsername();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Agent UID
		TableViewerColumn ipColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("AGENT"), 300);
		ipColumn.getColumn().setAlignment(SWT.LEFT);
		ipColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UsbFuseGroupResult) {
					return ((UsbFuseGroupResult) element).getUid();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Status code
		TableViewerColumn macColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("STATUS_CODE"), 150);
		macColumn.getColumn().setAlignment(SWT.LEFT);
		macColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UsbFuseGroupResult) {
					StatusCode statusCode = ((UsbFuseGroupResult) element).getStatusCode();
					return Messages.getString(statusCode.toString());
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Create date
		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("CREATE_DATE"), 100);
		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UsbFuseGroupResult) {
					return ((UsbFuseGroupResult) element).getCreateDate() != null
							? SWTResourceManager.formatDate(((UsbFuseGroupResult) element).getCreateDate())
							: Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

		// End date
		TableViewerColumn endColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("END_DATE"), 100);
		endColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UsbFuseGroupResult) {
					return ((UsbFuseGroupResult) element).getEndDate() != null
							? SWTResourceManager.formatDate(((UsbFuseGroupResult) element).getEndDate())
									: Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});

	}

	private void createButtonsArea(final Composite parent) {

		buttonComposite = new Composite(parent, GridData.FILL);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		buttonComposite.setLayout(new GridLayout(2, false));

		btnRefresh = new Button(buttonComposite, SWT.NONE);
		btnRefresh.setText(Messages.getString("REFRESH"));
		btnRefresh.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefresh.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefresh.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void refresh() {
		populateTable();
		tableViewer.refresh();
	}

	private void populateTable() {
		try {
			IResponse response = TaskRestUtils.execute(UsbLtspConstants.PLUGIN_NAME, UsbLtspConstants.PLUGIN_VERSION,
					UsbLtspConstants.TASKS.LIST_USB_FUSE_GROUP_STATUS, false);
			List<UsbFuseGroupResult> results = null;
			if (response.getResultMap().get("fuse-group-results") != null) {
				ObjectMapper mapper = new ObjectMapper();
				String str = response.getResultMap().get("fuse-group-results").toString();
				results = Arrays.asList(mapper.readValue(str, UsbFuseGroupResult[].class));
			}
			tableViewer.setInput(results != null ? results : new ArrayList<UsbFuseGroupResult>());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	/**
	 * Apply filter to table rows. (Search text can be agent DN, hostname, JID,
	 * IP address or MAC address)
	 *
	 */
	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s, boolean useRegex) {
			if (s == null) {
				searchString = null;
			} else if (useRegex) {
				this.searchString = ".*" + s + ".*";
				
			} else {
				this.searchString = s;
			}
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			UsbFuseGroupResult result = (UsbFuseGroupResult) element;
			return result.getUsername().matches(searchString) || result.getUid().matches(searchString) || result.getStatusCode().toString().equals(searchString);
		}
	}

	@Override
	public void setFocus() {
	}
	
	private void filterTable(Button btnPrivilegeFilter) {
		tableTextFilter.setSearchText(txtSearch.getText(), true);
		
		if (btnPrivileged.getSelection() && btnUnprivileged.getSelection()) {
			tablePriviligeFilter.setSearchText(null, false);
		} else if (!btnPrivileged.getSelection() && btnUnprivileged.getSelection()) {
			tablePriviligeFilter.setSearchText(StatusCode.UNPRIVILEGED.toString(), false);
		} else if (btnPrivileged.getSelection() && !btnUnprivileged.getSelection()) {
			tablePriviligeFilter.setSearchText(StatusCode.PRIVILEGED.toString(), false);
		} else {
			if (btnPrivilegeFilter != null) {
				btnPrivilegeFilter.setSelection(true);
			}
		}
		
		tableViewer.refresh();
	}

}

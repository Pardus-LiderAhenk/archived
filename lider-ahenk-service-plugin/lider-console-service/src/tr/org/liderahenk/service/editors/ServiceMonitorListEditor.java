package tr.org.liderahenk.service.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.config.ConfigProvider;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;
import tr.org.liderahenk.liderconsole.core.model.Command;
import tr.org.liderahenk.liderconsole.core.model.ExecutedTask;
import tr.org.liderahenk.liderconsole.core.rest.utils.TaskRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.IExportableTableViewer;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.service.constants.ServiceConstants;
import tr.org.liderahenk.service.dialogs.ServiceTaskDialog;
import tr.org.liderahenk.service.i18n.Messages;


public class ServiceMonitorListEditor extends EditorPart {
	public ServiceMonitorListEditor() {
	}

	private static final Logger logger = LoggerFactory.getLogger(ServiceMonitorListEditor.class);

	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	private Composite buttonComposite;
	private Button btnViewDetail;
	private Button btnRefreshExecutedTask;

	private ExecutedTask selectedTask;

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

	/**
	 * Create main widget of the editor - table viewer.
	 * 
	 * @param parent
	 */
	private void createTableArea(final Composite parent) {

		createTableFilterArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent, new IExportableTableViewer() {
			@Override
			public Composite getButtonComposite() {
				return buttonComposite;
			}

			@Override
			public String getSheetName() {
				return Messages.getString("AGENT_INFO");
			}

			@Override
			public String getReportName() {
				return Messages.getString("AGENT_INFO");
			}
		});
		createTableColumns();
		populateTable();

		// Hook up listeners
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof ExecutedTask) {
					setSelectedTask((ExecutedTask) firstElement);
				}
				btnViewDetail.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					Command command = TaskRestUtils.getCommand(selectedTask.getId());
					ServiceTaskDialog dialog = new ServiceTaskDialog(parent.getShell(),	command);
					dialog.create();
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	private void createTableColumns() {

		TableViewerColumn taskColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("TASK"), 250);
		taskColumn.getColumn().setAlignment(SWT.LEFT);
		taskColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ExecutedTask) {
					return Messages.getString("LABEL") + " "
							+ SWTResourceManager.formatDate(((ExecutedTask) element).getCreateDate());
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn jidColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("TASK_TYPE"), 200);
		jidColumn.getColumn().setAlignment(SWT.LEFT);
		jidColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ExecutedTask) {
					return ((ExecutedTask) element).getScheduled() ? Messages.getString("SCHEDULED")
							: Messages.getString("ONE_TIME");
				}
				return Messages.getString("UNTITLED");
			}
		});
		
		TableViewerColumn backupType = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SERVICE_TYPE"), 200);
		backupType.getColumn().setAlignment(SWT.LEFT);
		backupType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ExecutedTask) {
					return Messages.getString(((ExecutedTask) element).getCommandClsId());
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("LAST_EXECUTION_DATE"), 100);
		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ExecutedTask) {
					return ((ExecutedTask) element).getLastExecutionDate() != null
							? SWTResourceManager.formatDate(((ExecutedTask) element).getLastExecutionDate())
							: Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	/**
	 * Create table filter area
	 * 
	 * @param parent
	 */
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
		txtSearch.setToolTipText(Messages.getString("SEARCH_TASK_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	/**
	 * Create add, edit, delete button for the table.
	 * 
	 * @param composite
	 */
	private void createButtonsArea(final Composite parent) {

		buttonComposite = new Composite(parent, GridData.FILL);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		buttonComposite.setLayout(new GridLayout(3, false));

		btnViewDetail = new Button(buttonComposite, SWT.NONE);
		btnViewDetail.setText(Messages.getString("VIEW_DETAIL"));
		btnViewDetail.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnViewDetail.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/report.png"));
		btnViewDetail.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getSelectedTask()) {
					Notifier.warning(null, Messages.getString("PLEASE_SELECT_RECORD"));
					return;
				}
				try {
					Command command = TaskRestUtils.getCommand(selectedTask.getId());
					ServiceTaskDialog dialog = new ServiceTaskDialog(
							Display.getDefault().getActiveShell(), command);
					dialog.create();
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnRefreshExecutedTask = new Button(buttonComposite, SWT.NONE);
		btnRefreshExecutedTask.setText(Messages.getString("REFRESH"));
		btnRefreshExecutedTask.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/refresh.png"));
		btnRefreshExecutedTask.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnRefreshExecutedTask.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			// TODO
			ExecutedTask task = (ExecutedTask) element;
			return task.getCreateDate().toString().matches(searchString);
		}
	}

	/**
	 * Get agents and populate the table with them.
	 */
	private void populateTable() {
		try {
			List<ExecutedTask> tasks = null;
			tasks = TaskRestUtils.listExecutedTasks(ServiceConstants.PLUGIN_NAME, false, false, null, null, null,
					ConfigProvider.getInstance().getInt(LiderConstants.CONFIG.EXECUTED_TASKS_MAX_SIZE));
			
			List<ExecutedTask> filteredTasks = new ArrayList<>();
			
			for (ExecutedTask task : tasks) {
				
				if(task.getCommandClsId().equals("SERVICE_MANAGEMENT")){
					filteredTasks.add(task);
				}
			}
			
			tableViewer.setInput(filteredTasks != null ? filteredTasks : new ArrayList<ExecutedTask>());
			tableViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	/**
	 * Re-populate table with policies.
	 * 
	 */
	public void refresh() {
		populateTable();
		tableViewer.refresh();
	}

	@Override
	public void setFocus() {
	}

	public ExecutedTask getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(ExecutedTask selectedTask) {
		this.selectedTask = selectedTask;
	}

}

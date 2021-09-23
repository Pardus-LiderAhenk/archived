package tr.org.liderahenk.resourceusage.tabs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.resourceusage.i18n.Messages;
import tr.org.liderahenk.resourceusage.model.ResourceUsageAlertTableItem;

public class AlertListTab implements IUsageTab {

	private Label lblAlertList;
	private TableViewer tableViewer;
	private String pluginVersion;
	private String pluginName;
	private Set<String> dnSet;

	public String getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public Set<String> getDnSet() {
		return dnSet;
	}

	public void setDnSet(Set<String> dnSet) {
		this.dnSet = dnSet;
	}

	public void createInputs(Composite tabComposite) throws Exception {

		Composite alertListcomposite = new Composite(tabComposite, SWT.BORDER);
		alertListcomposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		alertListcomposite.setLayout(new GridLayout(1, false));

		lblAlertList = new Label(alertListcomposite, SWT.NONE);
		lblAlertList.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblAlertList.setText(Messages.getString("ALERT_LIST"));

		createTable(alertListcomposite);

		((ScrolledComposite) tabComposite).setContent(alertListcomposite);
		alertListcomposite.setSize(alertListcomposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		((ScrolledComposite) tabComposite).setExpandVertical(true);
		((ScrolledComposite) tabComposite).setExpandHorizontal(true);
		((ScrolledComposite) tabComposite).setMinSize(alertListcomposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	private void createTable(final Composite parent) {
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

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 5;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 500;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
	}

	private void createTableColumns() {

		String[] titles = { Messages.getString("DATE"), Messages.getString("USAGE"), Messages.getString("PATTERN"),
				Messages.getString("ACTION"), Messages.getString("MESSAGE") };
		int[] bounds = { 240, 240, 240, 240, 300 };

		TableViewerColumn dateColumn = createTableViewerColumn(titles[0], bounds[0]);
		dateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ResourceUsageAlertTableItem) {
					return ((ResourceUsageAlertTableItem) element).getDate();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn usageColumn = createTableViewerColumn(titles[1], bounds[1]);
		usageColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ResourceUsageAlertTableItem) {
					return ((ResourceUsageAlertTableItem) element).getUsage();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn patternColumn = createTableViewerColumn(titles[2], bounds[2]);
		patternColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ResourceUsageAlertTableItem) {
					return ((ResourceUsageAlertTableItem) element).getPattern();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn actionColumn = createTableViewerColumn(titles[3], bounds[3]);
		actionColumn.getColumn().setAlignment(SWT.LEFT);
		actionColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ResourceUsageAlertTableItem) {
					return ((ResourceUsageAlertTableItem) element).getAction();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn messageColumn = createTableViewerColumn(titles[4], bounds[4]);
		messageColumn.getColumn().setAlignment(SWT.LEFT);
		messageColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ResourceUsageAlertTableItem) {
					return ((ResourceUsageAlertTableItem) element).getMessage();
				}
				return Messages.getString("UNTITLED");
			}
		});
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

	@Override
	public void validateBeforeSave() throws ValidationException {

	}

	@Override
	public void createTab(Composite tabComposite, Set<String> dnSet, String pluginName, String pluginVersion)
			throws Exception {
		setDnSet(dnSet);
		setPluginName(pluginName);
		setPluginVersion(pluginVersion);
		createInputs(tabComposite);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> addTableItem(Object tableItem) {
		ArrayList<ResourceUsageAlertTableItem> listItems = (ArrayList<ResourceUsageAlertTableItem>) tableViewer
				.getInput();
		if (listItems == null) {
			listItems = new ArrayList<>();
		}
		listItems.add((ResourceUsageAlertTableItem) tableItem);
		tableViewer.setInput(listItems);
		tableViewer.refresh();

		return null;
	}

	@Override
	public void removeTableItems() {
		tableViewer.setInput(null);
		tableViewer.refresh();
	}

}

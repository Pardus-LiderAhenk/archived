package tr.org.liderahenk.disk.quota.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.osgi.service.runnable.ApplicationLauncher;
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

import tr.org.liderahenk.disk.quota.constants.DiskQuotaConstants;
import tr.org.liderahenk.disk.quota.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.config.ConfigProvider;
import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.dialogs.AppliedPolicyDialog;
import tr.org.liderahenk.liderconsole.core.editorinput.DefaultEditorInput;
import tr.org.liderahenk.liderconsole.core.ldap.enums.DNType;
import tr.org.liderahenk.liderconsole.core.ldap.model.LdapEntry;
import tr.org.liderahenk.liderconsole.core.ldap.utils.LdapUtils;
import tr.org.liderahenk.liderconsole.core.model.AppliedPolicy;
import tr.org.liderahenk.liderconsole.core.model.Command;
import tr.org.liderahenk.liderconsole.core.model.CommandExecution;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.rest.utils.PolicyRestUtils;
import tr.org.liderahenk.liderconsole.core.utils.IExportableTableViewer;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;


public class DiskQutaPolicyListEditor extends EditorPart {
	public DiskQutaPolicyListEditor() {
	}

	private static final Logger logger = LoggerFactory.getLogger(DiskQutaPolicyListEditor.class);

	private TableViewer tableViewer;
	private TableViewer tableLdapViewer;
	private Text txtSearch;
	private Composite buttonComposite;
	private Button btnViewDetail;

	private AppliedPolicy selectedPolicy;

	private Button btnSearch;
	private Text textFilter;
	private TableFilter tableFilter;

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
		createTableArea(parent);
	}

	private void createTableArea(final Composite parent) {

		createTableFilterArea(parent);

		// LDAP viewer

		Composite cont = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 200;
		cont.setLayoutData(data);
		cont.setLayout(new GridLayout(1, false));
		tableLdapViewer = SWTResourceManager.createTableViewer(cont);
		TableViewerColumn dnColumn = SWTResourceManager.createTableViewerColumn(tableLdapViewer,
				Messages.getString("DN"), 300);
		dnColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LdapEntry) {
					return ((LdapEntry) element).getDistinguishedName();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn uidColumn = SWTResourceManager.createTableViewerColumn(tableLdapViewer,
				Messages.getString("USER_UID"), 100);
		uidColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LdapEntry) {
					return ((LdapEntry) element).getAttributes().get("uid") != null
							? ((LdapEntry) element).getAttributes().get("uid") : "-";
				}
				return Messages.getString("UNTITLED");
			}
		});
		tableLdapViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableLdapViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof LdapEntry) {
					clearPolicyTable();
					populateTable((LdapEntry) firstElement);
				}
				btnViewDetail.setEnabled(true);
			}
		});
		//

		// Policy viewer
		Label lblTable = new Label(parent, SWT.NONE);
		lblTable.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblTable.setText(Messages.getString("POLICY_TABLE"));

		createButtonsArea(parent);

		tableViewer = SWTResourceManager.createTableViewer(parent, new IExportableTableViewer() {
			@Override
			public Composite getButtonComposite() {
				return buttonComposite;
			}

			@Override
			public String getSheetName() {
				return Messages.getString("QUOTA_REPORT");
			}

			@Override
			public String getReportName() {
				return Messages.getString("QUOTA_REPORT");
			}
		});
		createTableColumns();
		// populateTable();

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof AppliedPolicy) {
					setSelectedPolicy((AppliedPolicy) firstElement);
				}
				btnViewDetail.setEnabled(true);
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				try {
					AppliedPolicy policy = getSelectedPolicy();
					List<Command> commands = PolicyRestUtils.listCommands(policy.getId());
					AppliedPolicyDialog dialog = new AppliedPolicyDialog(parent.getShell(), policy, commands);
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
		
		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		//
	}

	private void createTableColumns() {

		TableViewerColumn polColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				"POLİTİKA TİPİ", 300);
		polColumn.getColumn().setAlignment(SWT.LEFT);
		polColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					return ((AppliedPolicy) element).getPolicyType() == 0 ? "Kullanici" : "Grup";
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn uidColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("APPLIED_ENTRIES"), 240);
		uidColumn.getColumn().setAlignment(SWT.LEFT);
		uidColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getUidList() != null
						&& !((AppliedPolicy) element).getUidList().isEmpty()) {
					return StringUtils.join(((AppliedPolicy) element).getUidList(), ",");
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn softQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SOFT_QUOTA") + " (MB)", 140);
		softQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		softQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.SOFT_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn hardQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("HARD_QUOTA") + " (MB)", 140);
		hardQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		hardQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.HARD_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn defaultQuotaColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("DEFAULT_QUOTA") + " (MB)", 140);
		defaultQuotaColumn.getColumn().setAlignment(SWT.LEFT);
		defaultQuotaColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					Set<Profile> profiles = ((AppliedPolicy) element).getPolicyAsObj().getProfiles();
					for (Profile profile : profiles) {
						if (profile.getProfileData() != null
								&& profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.DEFAULT_QUOTA) != null) {
							return profile.getProfileData().get(DiskQuotaConstants.PARAMETERS.DEFAULT_QUOTA).toString();
						}
					}
					return null;
				}
				return Messages.getString("UNTITLED");
			}
		});

//		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
//				Messages.getString("CREATE_DATE"), 100);
//		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
//			@Override
//			public String getText(Object element) {
//				if (element instanceof AppliedPolicy) {
//					return SWTResourceManager.formatDate(((AppliedPolicy) element).getCreateDate());
//				}
//				return Messages.getString("UNTITLED");
//			}
//		});

		TableViewerColumn applyDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("APPLY_DATE"), 100);
		applyDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getApplyDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getApplyDate());
				}
				return "-";
			}
		});

		TableViewerColumn activationDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("ACTIVATION_DATE"), 100);
		activationDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getActivationDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getActivationDate());
				}
				return "-";
			}
		});

		TableViewerColumn expirationDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("EXPIRATION_DATE"), 100);
		expirationDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy && ((AppliedPolicy) element).getExpirationDate() != null) {
					return SWTResourceManager.formatDate(((AppliedPolicy) element).getExpirationDate());
				}
				return "-";
			}
		});
		
		TableViewerColumn appliedQouta = SWTResourceManager.createTableViewerColumn(tableViewer,
				"UYGULANMA DURUMU", 140);
		appliedQouta.getColumn().setAlignment(SWT.LEFT);
		appliedQouta.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AppliedPolicy) {
					if (((AppliedPolicy) element).getAppliedToUser()){
						return "EVET";
					}else {
						return "HAYIR";
					}
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	private void createTableFilterArea(Composite parent) {

		Label lblTable = new Label(parent, SWT.NONE);
		lblTable.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblTable.setText(Messages.getString("SELECT_USER_OU_TIP"));

		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(3, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_TASK_TOOLTIP"));
		
		
		btnSearch = new Button(filterContainer, SWT.NONE);
		btnSearch.setText("ARA");
		btnSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btnSearch.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					// Clear policy table
					clearPolicyTable();

					List<LdapEntry> entries = new ArrayList<LdapEntry>();
					if (txtSearch.getText() == null || txtSearch.getText().trim().isEmpty()) {
						tableLdapViewer.setInput(entries);
						tableLdapViewer.refresh();
						return;
					}
					// Create filter for cn, mail, uid
					StringBuffer filter = new StringBuffer();
					filter.append("(|");
					filter.append("(cn=*").append(txtSearch.getText().trim()).append("*)");
					filter.append("(mail=*").append(txtSearch.getText().trim()).append("*)");
					filter.append("(uid=*").append(txtSearch.getText().trim()).append("*)");
					filter.append(")");

					// Do search
					List<LdapEntry> temp = LdapUtils.getInstance().findUsers(filter.toString(),
							new String[] { "uid", "cn" });
					if (temp != null) {
						entries.addAll(temp);
					}

					temp = LdapUtils.getInstance().findOUs("(ou=*" + txtSearch.getText().trim() + "*)",
							new String[] { LdapUtils.OBJECT_CLASS, "ou" });
					if (temp != null) {
						entries.addAll(temp);
					}

					tableLdapViewer.setInput(entries);
					tableLdapViewer.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		
//		txtSearch.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				// tableFilter.setSearchText(txtSearch.getText());
//				
//			}
//		});

		Label lblDesc = new Label(filterContainer, SWT.NONE);
		lblDesc.setText(Messages.getString("SELECT_USER_OU_DESC"));
		new Label(filterContainer, SWT.NONE);
		new Label(filterContainer, SWT.NONE);
	}

	protected void clearPolicyTable() {
		List<AppliedPolicy> dummy = new ArrayList<AppliedPolicy>();
		tableViewer.setInput(dummy);
		tableViewer.refresh();
	}

	private void createButtonsArea(final Composite parent) {

		buttonComposite = new Composite(parent, GridData.FILL);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		buttonComposite.setLayout(new GridLayout(4, false));

		
		btnViewDetail = new Button(buttonComposite, SWT.NONE);
		btnViewDetail.setEnabled(false);
		btnViewDetail.setText(Messages.getString("VIEW_DETAIL"));
		btnViewDetail.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		btnViewDetail.setImage(
				SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE, "icons/16/report.png"));
		
		textFilter = new Text(buttonComposite, SWT.BORDER);
		GridData gd_textFilter = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_textFilter.widthHint = 158;
		textFilter.setLayoutData(gd_textFilter);
		textFilter.setMessage(Messages.getString("search"));
		
		textFilter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(textFilter.getText());
				tableViewer.refresh();
			}
		});
		
		
		new Label(buttonComposite, SWT.NONE);
		btnViewDetail.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (null == getSelectedPolicy()) {
					Notifier.warning(null, Messages.getString("NO_POLICY_APPLIED"));
					return;
				}
				try {
					AppliedPolicy policy = getSelectedPolicy();
					List<Command> commands = PolicyRestUtils.listCommands(policy.getId());
					AppliedPolicyDialog dialog = new AppliedPolicyDialog(parent.getShell(), policy, commands);
					dialog.open();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	/**
	 * Get agents and populate the table with them.
	 * 
	 * @param selectedEntry
	 */
	private void populateTable(LdapEntry selectedEntry) {
		try {
			List<AppliedPolicy> policiesList = new ArrayList<AppliedPolicy>();
			List<AppliedPolicy> policies = null;
			policies = PolicyRestUtils.listAppliedPolicies(null, null, null, null,
					ConfigProvider.getInstance().getInt(LiderConstants.CONFIG.APPLIED_POLICIES_MAX_SIZE),
					DiskQuotaConstants.PLUGIN_NAME, selectedEntry.getType(), selectedEntry.getDistinguishedName());
			
			for (AppliedPolicy appliedPolicy : policies) {
				if (appliedPolicy.getUidList().size() > 1){
					
					List<Command> commands = PolicyRestUtils.listCommands(appliedPolicy.getId());
					
					if (selectedEntry.getType() == DNType.USER) {
						
						List<String> cloneList = new ArrayList<String>(1);
						cloneList.add(selectedEntry.getDistinguishedName());
						appliedPolicy.setUidList(cloneList);
						appliedPolicy.setPolicyType(1);
						policiesList.add(appliedPolicy);
						
						
					}else {
						for(String uid : appliedPolicy.getUidList()){
							AppliedPolicy clone = appliedPolicy.clone();
							
							
							for(Command co : commands) {
								List<CommandExecution> commandExecutions = co.getCommandExecutions();
								for(CommandExecution ce : commandExecutions){
									if (ce.getUid().equalsIgnoreCase(uid)){
										if(ce.getCommandExecutionResults() != null && ce.getCommandExecutionResults().size() > 0){
											clone.setAppliedToUser(true);
											
										}
									}
								}
							}
							
							List<String> cloneList = new ArrayList<String>(1);
							cloneList.add(uid);
							clone.setUidList(cloneList);
							clone.setPolicyType(1);
							policiesList.add(clone);
						}
					}
					
				}else {
					List<Command> commands = PolicyRestUtils.listCommands(appliedPolicy.getId());
					for(Command co : commands) {
						List<CommandExecution> commandExecutions = co.getCommandExecutions();
						for(CommandExecution ce : commandExecutions){
							if (ce.getUid().equalsIgnoreCase(appliedPolicy.getUidList().get(0))){
								if(ce.getCommandExecutionResults() != null && ce.getCommandExecutionResults().size() > 0){
									appliedPolicy.setAppliedToUser(true);
								}
							}
						}
					}
					policiesList.add(appliedPolicy);
				}
			}
			
			
			Collections.sort(policiesList, new Comparator<AppliedPolicy>() {

				@Override
				public int compare(AppliedPolicy o1, AppliedPolicy o2) {
					
					return o2.getApplyDate().compareTo(o1.getApplyDate());
				}
			});
			
			tableViewer.setInput(policiesList != null ? policiesList : new ArrayList<AppliedPolicy>());
			tableViewer.refresh();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_LIST"));
		}
	}

	@Override
	public void setFocus() {
	}

	public AppliedPolicy getSelectedPolicy() {
		return selectedPolicy;
	}

	public void setSelectedPolicy(AppliedPolicy selectedPolicy) {
		this.selectedPolicy = selectedPolicy;
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
			
			String uids=StringUtils.join(((AppliedPolicy) element).getUidList(), ",");
			
			return uids.matches(searchString);
		}

	}

}

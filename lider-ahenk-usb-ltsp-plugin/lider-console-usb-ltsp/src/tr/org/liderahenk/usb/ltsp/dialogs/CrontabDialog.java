package tr.org.liderahenk.usb.ltsp.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.usb.ltsp.enums.ScheduleOperation;
import tr.org.liderahenk.usb.ltsp.i18n.Messages;
import tr.org.liderahenk.usb.ltsp.model.CrontabExpression;

public class CrontabDialog extends TitleAreaDialog {

//	private static final String HELP = Messages_NLS.CrontabDialog_HELP;
	private Combo cmbCronSelection;
	private Text txtMinute;
	private Text txtHour;
	private Text txtDayOfMonth;
	private Text txtMonth;
	private Text txtDayOfWeek;
	private CrontabExpression crontabExpr;
	private DateTime dateTime;
	private Boolean dateTimeChanged;
	private final String[] CRON_SHORTCUT_LIST = { Messages.getString("CUSTOM"), Messages.getString("YEARLY"),
			Messages.getString("MONTHLY"), Messages.getString("WEEKLY"), Messages.getString("DAILY"),
			Messages.getString("HOURLY"), Messages.getString("ONCE") };
	
	/**
	 * Fix for Crontab/Quartz syntax differences!
	 * By default, all CrontabDialog instances uses Quartz syntax (in order to ensure backward-compatibility)
	 * 
	 * @see tr.org.pardus.mys.liderconsole.usb.dialogs.UsbFuseGroupDialog
	 * 
	 */
	private boolean useCrontabSyntax;
	
	public CrontabDialog(Shell shell) {
		super(shell);
		useCrontabSyntax = false;
	}
	
	public CrontabDialog(Shell shell, boolean useCrontabSyntax) {
		super(shell);
		this.useCrontabSyntax = useCrontabSyntax;
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.getString("SCHEDULER_PARAMS"));
		setMessage(Messages.getString("SCHEDULER_MESSAGE"), IMessageProvider.INFORMATION);
	}
	
	/**
	 * 
	 * @return crontab expression which user provided in the dialog. 
	 */
	public CrontabExpression getExpression() { 
		return crontabExpr;
	}
	
	private String getSeconds() {
		return "0"; //$NON-NLS-1$
	}
	private String getMinutes() {
		return txtMinute.getText().trim();
	}
	private String getHours() {
		return txtHour.getText().trim();
	}
	private String getDayOfMonth() {
		return txtDayOfMonth.getText().trim();
	}
	private String getMonth(){ 
		return txtMonth.getText().trim();
	}
	private String getDayOfWeek(){
		return txtDayOfWeek.getText().trim();
	}
	
	private void clearAll() {
		txtDayOfMonth.setText(""); //$NON-NLS-1$
		txtDayOfWeek.setText(""); //$NON-NLS-1$
		txtHour.setText(""); //$NON-NLS-1$
		txtMinute.setText(""); //$NON-NLS-1$
		txtMonth.setText(""); //$NON-NLS-1$
	}
	
	private void setEnabled(final boolean enabled) {
		txtDayOfMonth.setEditable(enabled);
		txtDayOfWeek.setEditable(enabled);
		txtHour.setEditable(enabled);
		txtMinute.setEditable(enabled);
		txtMonth.setEditable(enabled);
	}
	
	private void setValues(final String[] values) {
		txtMinute.setText(values[0]);
		txtHour.setText(values[1]);
		txtDayOfMonth.setText(values[2]);
		txtMonth.setText(values[3]);
		txtDayOfWeek.setText(values[4]);
	}	
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		// setting container layout
		// 2-columns
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridLayout layout = new GridLayout(2, false);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//		data.heightHint = 300;
		container.setLayoutData(data);
		container.setLayout(layout);
		// create dialog items
		createShortcuts(container);		
		createMinute(container);
		createHour(container);
		createDayOfMonth(container);
		createMonth(container);
		createDayOfWeek(container);
		createDateTime(container);
		return container;
	}
	
	private void createShortcuts(final Composite parent) {
		final Label lblCronShortcuts = new Label(parent, SWT.NONE);
		lblCronShortcuts.setText(Messages.getString("SCHEDULING"));

		cmbCronSelection = new Combo(parent, SWT.READ_ONLY);
		cmbCronSelection.setItems(CRON_SHORTCUT_LIST);
		cmbCronSelection.select(0);
		
		ComboViewer cmbVwCronSelection = new ComboViewer(cmbCronSelection);
		cmbVwCronSelection.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				switch(cmbCronSelection.getSelectionIndex()) {
				// user defined
				case 0: clearAll(); setEnabled( true ); break;
				// yearly
				case 1: clearAll(); setEnabled( true ); dateTimeChanged = false;  dateTime.setEnabled(false);setValues(new String[]{"0", "0", "1", "1", useCrontabSyntax ? "*" : "?"}); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				// monthly
				case 2: clearAll(); setEnabled( true ); dateTimeChanged = false;  dateTime.setEnabled(false);setValues(new String[]{"0", "0", "1", "*", useCrontabSyntax ? "*" : "?"}); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				// weekly
				case 3: clearAll(); setEnabled( true ); dateTimeChanged = false;  dateTime.setEnabled(false); setValues(new String[]{"0", "0", "*", "*", "0"}); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				// daily
				case 4: clearAll(); setEnabled( true ); dateTimeChanged = false;  dateTime.setEnabled(false);setValues(new String[]{"0", "0", "*", "*", useCrontabSyntax ? "*" : "?"}); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				// hourly
				case 5: clearAll(); setEnabled( true ); dateTimeChanged = false; dateTime.setEnabled(false); setValues(new String[]{"0", "*", "*", "*", useCrontabSyntax ? "*" : "?"}); break; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				// once
				case 6: 
					{
						clearAll(); setEnabled( true ); dateTime.setEnabled(true); setValues(new String[]{"", "", "", "", ""});  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						txtHour.setText(dateTime.getHours() + "");
						txtMonth.setText((dateTime.getMonth() + 1) + "");
						txtDayOfWeek.setText(useCrontabSyntax ? "*" : "?");
						txtDayOfMonth.setText(dateTime.getDay() + "");	
						txtMinute.setText(dateTime.getMinutes() + "");
						break;
					}
				default: break;
				}
				
			}
		});
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		cmbCronSelection.setLayoutData( gridData );
	}
	

	private void createMinute(Composite parent) {
		Label lblMinute = new Label(parent, SWT.NONE);
		lblMinute.setText(Messages.getString("MINUTE"));
		txtMinute = new Text( parent, SWT.BORDER );
		txtMinute.setLayoutData( createTextGridData() );
		
		txtMinute.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {			
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
		});
	}
	
	private void createDateTime(Composite parent) {
		Label lblMinute = new Label(parent, SWT.NONE);
		lblMinute.setText(Messages.getString("DATE"));
		dateTime = new DateTime( parent, SWT.DROP_DOWN );
		dateTime.setLayoutData( createTextGridData() );
		dateTime.setEnabled(false);
		
		dateTimeChanged = false;
		
		dateTime.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				
				txtHour.setText(dateTime.getHours() + "");
				txtMonth.setText((dateTime.getMonth() + 1) + "");
				txtDayOfWeek.setText("?");
				txtDayOfMonth.setText(dateTime.getDay() + "");	
				txtMinute.setText(dateTime.getMinutes() + "");
				
				
				dateTimeChanged = true;
				
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
			
			@Override
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent event) {
				//Auto generated.
			}
		});		
	}

	private void createHour(Composite parent) {
		Label lblHour = new Label(parent, SWT.NONE);
		lblHour.setText(Messages.getString("HOUR"));
		txtHour = new Text( parent, SWT.BORDER );
		txtHour.setLayoutData( createTextGridData() );
		
		txtHour.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {			
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
		});
	}
	
    protected Point getInitialSize() {
        return new Point(360, 440);
    }

	private void createDayOfMonth(Composite parent) {
		Label lblDayOfMonth = new Label(parent, SWT.NONE);
		lblDayOfMonth.setText(Messages.getString("DAY_OF_MONTH"));
		txtDayOfMonth = new Text( parent, SWT.BORDER );
		txtDayOfMonth.setLayoutData( createTextGridData() );
		
		txtDayOfMonth.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {			
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
		});
	}

	private void createMonth(Composite parent) {
		Label lblMonth = new Label(parent, SWT.NONE);
		lblMonth.setText(Messages.getString("MONTH"));
		txtMonth = new Text( parent, SWT.BORDER );
		txtMonth.setLayoutData( createTextGridData() );
		
		txtMonth.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {			
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
		});
	}

	private void createDayOfWeek(Composite parent) {
		Label lblDayOfWeek = new Label(parent, SWT.NONE);
		lblDayOfWeek.setText(Messages.getString("DAY_OF_WEEK"));
		txtDayOfWeek = new Text( parent, SWT.BORDER );
		txtDayOfWeek.setLayoutData( createTextGridData() );
		
		txtDayOfWeek.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {			
				boolean isOkButtonEnabled = false;
//				if(CronExpression.isValidExpression(getCrontabStr()) || useCrontabSyntax) {
					isOkButtonEnabled = true;
//				}
				getButton(IDialogConstants.OK_ID).setEnabled(isOkButtonEnabled);
			}
		});
	}
	
	private GridData createTextGridData() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment= GridData.FILL;
		return gridData;
	}
	
	
	private String getCrontabStr() {
		// build a crontab string
		StringBuilder exprStringBuilder = new StringBuilder();
		exprStringBuilder.append( getSeconds() )
							.append(" ") //$NON-NLS-1$
							.append( getMinutes() )
							.append(" ") //$NON-NLS-1$
							.append( getHours() )
							.append(" ") //$NON-NLS-1$
							.append( getDayOfMonth() )
							.append(" ") //$NON-NLS-1$
							.append( getMonth() )
							.append(" ") //$NON-NLS-1$
							.append( getDayOfWeek() );
		
		
		return exprStringBuilder.toString();
	}
	
	@Override
	protected void okPressed() {
		crontabExpr = new CrontabExpression();
		crontabExpr.setActive(true);
		crontabExpr.setOperation(ScheduleOperation.ADD);
		if( dateTimeChanged )
		{
			crontabExpr.setCrontabStr( getCrontabStr() + " " + dateTime.getYear() );	
		}
		else
		{
			crontabExpr.setCrontabStr( getCrontabStr() );
		}
		
		super.okPressed();
	}
	
	@Override
	protected Control createButtonBar(final Composite parent) {
		final Composite buttonBar = new Composite(parent, SWT.NONE);

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonBar.setLayout(layout);

		final GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		buttonBar.setLayoutData(data);

		buttonBar.setFont(parent.getFont());

		// place a button on the left
//		Button btnHelp = new Button(buttonBar, SWT.PUSH);
//		btnHelp.setImage( grabImage("icons/help.gif") ); //$NON-NLS-1$
//		btnHelp.setText(HELP);
//		btnHelp.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//			}
//		});
		
		final GridData testConnectionButtonData = new GridData(SWT.LEFT,
				SWT.CENTER, true, true);
		testConnectionButtonData.grabExcessHorizontalSpace = true;
		testConnectionButtonData.horizontalIndent = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
//		btnHelp.setLayoutData(testConnectionButtonData);

		// add the dialog's button bar to the right
		final Control buttonControl = super.createButtonBar(buttonBar);
		buttonControl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true,
				false));
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return buttonBar;
	}
	
//	private Image grabImage(String path) {
//		Bundle bundle = FrameworkUtil.getBundle(getClass());
//		URL url = FileLocator.find(bundle, new Path(path), null);
//	    ImageDescriptor imageDcr = ImageDescriptor.createFromURL(url);
//	    return imageDcr.createImage();
//	}
}

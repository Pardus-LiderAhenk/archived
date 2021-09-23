package tr.org.liderahenk.loginmanager.dialogs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import tr.org.liderahenk.liderconsole.core.dialogs.IProfileDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.loginmanager.constants.LoginManagerConstants;
import tr.org.liderahenk.loginmanager.i18n.Messages;
import tr.org.liderahenk.loginmanager.utils.LoginManagerUtils;

/**
 * 
 * @author <a href="mailto:mine.dogan@agem.com.tr">Mine Dogan</a>
 *
 */
public class LoginManagerProfileDialog implements IProfileDialog {
	
	private Button btnDays;
	private DateTime startTime;
	private DateTime endTime;
	private DateTime date;
	private Combo cmbDuration;
	
	private final String[] days = new String[] {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
	private final String[] daysValues = new String[] {"0", "1", "2", "3", "4", "5", "6"};
	
	private List<String> chosenDays;
	
	// Combo values & i18n labels
	private final String[] statusArr = new String[] { "1M", "5M" };
	private final String[] statusValueArr = new String[] { "1", "5" };
	
	@Override
	public void init() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void createDialogArea(Composite parent, Profile profile) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Label lblTitle = new Label(composite, SWT.NONE);
		lblTitle.setText(Messages.getString("TITLE"));
		FontData fontData = lblTitle.getFont().getFontData()[0];
		Font font = new Font(composite.getDisplay(), new FontData(fontData.getName(), fontData
		    .getHeight(), SWT.BOLD));
		lblTitle.setFont(font);
		
		Label lblDays = new Label(composite, SWT.NONE);
		lblDays.setText(Messages.getString("DAYS"));
		
		Composite compDays = new Composite(composite, SWT.NONE);
		compDays.setLayout(new GridLayout(5, false));
		
		chosenDays = new ArrayList<String>();
		
		List<String> data = (List<String>) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(LoginManagerConstants.PARAMETERS.DAYS) : null);
		
		if (data != null) {
			chosenDays = data;
		}
		
		for (int i = 0; i < days.length; i++) {
			String i18n = Messages.getString(days[i]);
			if (i18n != null && !i18n.isEmpty()) {
				btnDays = new Button(compDays, SWT.CHECK);
				btnDays.setData(daysValues[i]);
				btnDays.setText(i18n);
				btnDays.addSelectionListener(new SelectionAdapter() {
					
					@Override
			        public void widgetSelected(SelectionEvent event) {
			            Button btn = (Button) event.getSource();
			            if (btn.getSelection()) {
							chosenDays.add((String) btn.getData());
						}
			            else {
			            	if(chosenDays.contains((String) btn.getData())) {
			            		chosenDays.remove((String) btn.getData());
			            	}
			            }
			        }
				});
			}
			if (data != null && data.contains(daysValues[i])) {
				btnDays.setSelection(true);
			}
	    }
		
		Label lblTimeDate = new Label(composite, SWT.NONE);
		lblTimeDate.setText(Messages.getString("TIME_DATE"));
		
		Composite compOptions = new Composite(composite, SWT.NONE);
		compOptions.setLayout(new GridLayout(2, false));
		
		Label lblStartTime = new Label(compOptions, SWT.NONE);
		lblStartTime.setText(Messages.getString("START_TIME"));
		
		startTime = new DateTime(compOptions, SWT.TIME | SWT.SHORT);
		String start = (String) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(LoginManagerConstants.PARAMETERS.START_TIME) : null);
		
		if(start != null) {
			String[] arrStart = start.split(":");
			startTime.setHours(Integer.valueOf(arrStart[0]));
			startTime.setMinutes(Integer.valueOf(arrStart[1]));
		}
		
		Label lblEndTime = new Label(compOptions, SWT.NONE);
		lblEndTime.setText(Messages.getString("END_TIME"));
		
		endTime = new DateTime(compOptions, SWT.TIME | SWT.SHORT);
		String end = (String) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(LoginManagerConstants.PARAMETERS.END_TIME) : null);
		
		if(end != null) {
			String[] arrEnd = end.split(":");
			endTime.setHours(Integer.valueOf(arrEnd[0]));
			endTime.setMinutes(Integer.valueOf(arrEnd[1]));
		}
		
		Label lblDate = new Label(compOptions, SWT.NONE);
		lblDate.setText(Messages.getString("LAST_AVAILABILITY_DATE"));
		
		date = new DateTime(compOptions, SWT.DATE);
		String strDate = (String) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(LoginManagerConstants.PARAMETERS.LAST_DATE) : null);
		
		if(strDate != null) {
			String[] arrDate = strDate.split("/");
			date.setDay(Integer.valueOf(arrDate[0]));
			date.setMonth(Integer.valueOf(arrDate[1])-1);
			date.setYear(Integer.valueOf(arrDate[2]));
		}
		
		Composite compNotify = new Composite(composite, SWT.NONE);
		compNotify.setLayout(new GridLayout(2, false));
		
		Label lblNotify = new Label(compNotify, SWT.NONE);
		lblNotify.setText(Messages.getString("NOTIFY_BEFORE_LOGOUT"));
		
		cmbDuration = new Combo(compNotify, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbDuration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < statusArr.length; i++) {
			String i18n = Messages.getString(statusArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbDuration.add(i18n);
				cmbDuration.setData(i + "", statusValueArr[i]);
			}
		}
		cmbDuration.select(0);
		
		String duration = (String) (profile != null && profile.getProfileData() != null
				? profile.getProfileData().get(LoginManagerConstants.PARAMETERS.DURATION) : null);
		
		for (int i = 0; i < statusValueArr.length; i++) {
			if (statusValueArr[i].equalsIgnoreCase(duration)) {
				cmbDuration.select(i);
			}
		}
	}
	
	public String convertDateToString(DateTime date) {
		
		int day = date.getDay();
	    int month = date.getMonth() + 1;
	    int year = date.getYear();

	    String strDate = (day < 10) ? "0" + day + "/" : day + "/";
	    strDate += (month < 10) ? "0" + month + "/" : month + "/";
	    strDate += year;
	    
	    return strDate;
	}
	
	public Date convertTimeToDate(DateTime time) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(Calendar.HOUR, time.getHours());
		calendar.set(Calendar.MINUTE, time.getMinutes());
		
		return calendar.getTime();
	}
	
	@Override
	public Map<String, Object> getProfileData() throws Exception {
		Map<String, Object> profileData = new HashMap<String, Object>();
		profileData.put(LoginManagerConstants.PARAMETERS.DAYS, chosenDays);
		profileData.put(LoginManagerConstants.PARAMETERS.START_TIME, startTime.getHours() + ":" + startTime.getMinutes());
		profileData.put(LoginManagerConstants.PARAMETERS.END_TIME, endTime.getHours() + ":" + endTime.getMinutes());
		profileData.put(LoginManagerConstants.PARAMETERS.LAST_DATE, convertDateToString(date));
		profileData.put(LoginManagerConstants.PARAMETERS.DURATION, LoginManagerUtils.getSelectedValue(cmbDuration));
		return profileData;
	}
	
	@Override
	public void validateBeforeSave() throws ValidationException {
		Date start = convertTimeToDate(startTime);
		Date end = convertTimeToDate(endTime);
		
		if(start.after(end)) {
			throw new ValidationException(Messages.getString("START_TIME_NOT_BIGGER_THAN_END_TIME"));
		}
	}
}

/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.report;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.constants.LiderConstants;
import tr.org.liderahenk.lider.core.api.mail.IMailService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.report.exporters.HTMLReportExporter;

/**
 * Manages report views defined with alarm by executing their report query
 * periodically and checking if number of resulting records exceeds defined
 * threshold. If it does, query results are sent to defined e-mail addresses.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AlarmManager implements EventHandler {

	private Logger logger = LoggerFactory.getLogger(AlarmManager.class);

	private IReportDao reportDao;
	private IConfigurationService configurationService;
	private IMailService mailService;
	private LinkedHashMap<Long, Timer> timers;

	private static final long TIMER_DELAY = 10000;

	public void init() {
		logger.info("Initializing alarm manager...");
		timers = new LinkedHashMap<Long, Timer>();
		if (configurationService.getAlarmCheckReport()) {
			List<? extends IReportView> views = reportDao.findViewsWithAlarm();
			if (views != null && !views.isEmpty()) {
				logger.info("Found report views defined with alarm: {}", views.size());
				for (IReportView view : views) {
					try {
						Timer timer = new Timer();
						timer.schedule(new ReportAlarmListener(view), TIMER_DELAY, view.getAlarmCheckPeriod());
						timers.put(view.getId(), timer);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	public void destroy() {
		logger.info("Destroying alarm manager...");
		if (timers != null) {
			for (Timer timer : timers.values()) {
				try {
					timer.cancel();
					timer.purge();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			timers.clear();
		}
	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		// Report view deleted, find and delete its timer as well
		if (LiderConstants.EVENTS.REPORT_VIEW_DELETED.equalsIgnoreCase(topic)) {
			logger.info("Report view deleted event received. Deleting related timer if exists.");
			Long id = (Long) event.getProperty("viewId");
			if (timers != null) {
				Timer timer = timers.get(id);
				if (timer != null) {
					timer.cancel();
					timer.purge();
					timers.remove(id);
				}
			}
		}
		// Report view created, if it has defined alarm create timer for it
		else if (LiderConstants.EVENTS.REPORT_VIEW_CREATED.equalsIgnoreCase(topic)) {
			logger.info("Report view created event received. Creating timer if needed.");
			IReportView view = (IReportView) event.getProperty("view");
			if (view.getAlarmMail() != null && timers != null) {
				Timer timer = new Timer();
				timer.schedule(new ReportAlarmListener(view), TIMER_DELAY, view.getAlarmCheckPeriod());
				timers.put(view.getId(), timer);
			}
		}
		// Report view update, if it has defined alarm update/create timer
		else if (LiderConstants.EVENTS.REPORT_VIEW_UPDATED.equalsIgnoreCase(topic)) {
			logger.info("Report view updated event received. Updating timer if needed.");
			IReportView view = (IReportView) event.getProperty("view");
			if (timers != null) {
				Timer timer = timers.get(view.getId());
				// It may have timer, try to find it and cancel it.
				if (timer != null) {
					timer.cancel();
					timer.purge();
					timers.remove(view.getId());
				}
				if (view.getAlarmMail() != null) {
					Timer newTimer = new Timer();
					newTimer.schedule(new ReportAlarmListener(view), TIMER_DELAY, view.getAlarmCheckPeriod());
					timers.put(view.getId(), newTimer);
				}
			}
		}
	}

	/**
	 * Generate report data from report view and check if it exceeds defined
	 * alert threshold. If it does, send mail to defined mail recipients.
	 *
	 */
	protected class ReportAlarmListener extends TimerTask {

		private IReportView view;

		public ReportAlarmListener(IReportView view) {
			this.view = view;
		}

		@Override
		public void run() {
			try {
				////////////////////////////
				// FIXME This is a workaround for Karaf cluster!
				// DONT FORGET TO SET THIS PROPERTY IN ALL NODES:
				// alarm.check.report = true
				//
				// The alarm manager class listens to report-related events and
				// creates/updates/deletes report-related timer object if
				// necessary...
				// ...but when it comes to Karaf cluster, the node which created
				// the timer MAY NEVER receive the delete/update event.
				//
				// To overcome this, we can check if the view object received is
				// the same view object in the database.
				IReportView actualView = reportDao.findView(view.getId());
				// Alarm is removed from the view OR report is completely
				// deleted:
				if (actualView == null || actualView.getAlarmCheckPeriod() == null || actualView.getAlarmMail() == null
						|| actualView.getAlarmRecordNumThreshold() == null) {
					return;
				}
				// Alarm is updated, it will be handled by another Karaf node:
				if (!actualView.getAlarmMail().equals(view.getAlarmMail())
						|| !actualView.getAlarmCheckPeriod().equals(view.getAlarmCheckPeriod())
						|| !actualView.getAlarmRecordNumThreshold().equals(view.getAlarmRecordNumThreshold())) {
					return;
				}
				////////////////////////////
				List<Object[]> resultList = reportDao.generateView(view, null);
				if (view.getAlarmRecordNumThreshold() <= resultList.size()) {
					String htmlContents = HTMLReportExporter.export(view, resultList);
					String[] mails = view.getAlarmMail().split(",");
					mailService.sendMail(Arrays.asList(mails), view.getDescription(), htmlContents,
							"text/html; charset=ISO-8859-9");
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param reportDao
	 */
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}

	/**
	 * 
	 * @param configurationService
	 */
	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * 
	 * @param mailService
	 */
	public void setMailService(IMailService mailService) {
		this.mailService = mailService;
	}

	/**
	 * 
	 * @param timers
	 */
	public void setTimers(LinkedHashMap<Long, Timer> timers) {
		this.timers = timers;
	}

}

package tr.org.liderahenk.rsyslog.report.templates;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class RsyslogReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = -4121593523951121045L;

	@Override
	public String getName() {
		return "Ahenk Log Kayıtları";
	}

	@Override
	public String getDescription() {
		return "Ahenk kurulu bilgisayarlardan toplanan log kayıtları";
	}

	@Override
	public String getQuery() {
		return "SELECT s.fromHost as fromhost,s.eventUser as eventuser, s.eventSource as eventsource,s.eventLogType as eventlogtype , "
				+ "s.genericFileName as genericfilename, s.message as message, s.receivedAt as receivedat, s.sysLogTag as syslogtag "
				+ "FROM SystemEventsImpl s WHERE s.fromHost LIKE :fromhostparam ORDER BY s.deviceReportedTime DESC";
	}

	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		Set<IReportTemplateParameter> params = new HashSet<IReportTemplateParameter>();
		params.add(new IReportTemplateParameter() {

			private static final long serialVersionUID = -6579501320904978340L;

			@Override
			public ParameterType getType() {
				return ParameterType.STRING;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Makina İsmi";
			}

			@Override
			public String getKey() {
				return "fromhostparam";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public String getDefaultValue() {
				return null;
			}

			@Override
			public boolean isMandatory() {
				return false;
			}
		});
		return params;
	}

	@Override
	public Set<? extends IReportTemplateColumn> getTemplateColumns() {
		Set<IReportTemplateColumn> columns = new HashSet<IReportTemplateColumn>();

		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "From Host";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 1;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Event User";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 2;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Event Source";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 3;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Event Log Type";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 4;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Generic File Name";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 5;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Message";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 6;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Received At";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 7;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});
		// Plugin name
		columns.add(new IReportTemplateColumn() {

			private static final long serialVersionUID = -7169058053952522305L;

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Sys Log Tag";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 8;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}

		});
		return columns;
	}

	protected RsyslogReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "RSYSLOG-REPORT";
	}

}

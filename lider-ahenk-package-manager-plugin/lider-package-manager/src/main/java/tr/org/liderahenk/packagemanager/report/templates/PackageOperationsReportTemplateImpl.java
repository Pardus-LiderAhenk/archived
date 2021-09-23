package tr.org.liderahenk.packagemanager.report.templates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

/**
 * Default report template for executed tasks.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre Alpsoy</a>
 *
 */
public class PackageOperationsReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = -8026043224671892836L;

	@Override
	public String getName() {
		return "Paket İşlemleri Raporu";
	}

	@Override
	public String getDescription() {
		return "Paket işlemleri hakkında istatistiksel rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT cer.responseMessage, cer.createDate "
				+ "FROM CommandImpl c LEFT JOIN c.commandExecutions ce INNER JOIN ce.commandExecutionResults cer INNER JOIN c.task t INNER JOIN t.plugin p "
				+ "WHERE p.name = 'package-manager' AND ce.dn LIKE :dn AND (t.commandClsId = 'PACKAGES') AND t.createDate BETWEEN :startDate AND :endDate AND cer.responseCode <> 8";
	}

	@SuppressWarnings("serial")
	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		Set<IReportTemplateParameter> params = new HashSet<IReportTemplateParameter>();
		// Plugin name
		params.add(new IReportTemplateParameter() {
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
				return "Dn";
			}

			@Override
			public String getKey() {
				return "dn";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getDefaultValue() {
				return null;
			}

			@Override
			public boolean isMandatory() {
				return true;
			}

			@Override
			public Date getCreateDate() {
				return new Date();
			}
		});// Start date
		params.add(new IReportTemplateParameter() {

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public boolean isMandatory() {
				return true;
			}

			@Override
			public ParameterType getType() {
				return ParameterType.DATE;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Başlangıç tarih";
			}

			@Override
			public String getKey() {
				return "startDate";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getDefaultValue() {
				Calendar prevYear = Calendar.getInstance();
				prevYear.setTime(new Date());
				prevYear.add(Calendar.DAY_OF_YEAR, -1);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return format.format(prevYear.getTime());
			}
		});
		// End year
		params.add(new IReportTemplateParameter() {

			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public boolean isMandatory() {
				return true;
			}

			@Override
			public ParameterType getType() {
				return ParameterType.DATE;
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getLabel() {
				return "Bitiş tarihi";
			}

			@Override
			public String getKey() {
				return "endDate";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public String getDefaultValue() {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return format.format(new Date());
			}
		});
		return params;
	}

	@SuppressWarnings("serial")
	@Override
	public Set<? extends IReportTemplateColumn> getTemplateColumns() {
		Set<IReportTemplateColumn> columns = new HashSet<IReportTemplateColumn>();
		columns.add(new IReportTemplateColumn() {
			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Değişiklik Tarihi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 2;
			}
		});
		columns.add(new IReportTemplateColumn() {
			@Override
			public Date getCreateDate() {
				return new Date();
			}

			@Override
			public IReportTemplate getTemplate() {
				return getSelf();
			}

			@Override
			public String getName() {
				return "Yanıt Mesajı";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 1;
			}
		});
		return columns;
	}

	protected PackageOperationsReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "PACKAGE-OPERATIONS-REPORT";
	}

}

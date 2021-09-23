package tr.org.liderahenk.packagemanager.report.templates;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

/**
 * Default report template for executed tasks.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cemre ALPSOY</a>
 *
 */
public class MostUsedCommandsReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = -8026043224671892836L;
	@Override
	public String getName() {
		return "En Sık Kullanılan Komutlar";
	}

	@Override
	public String getDescription() {
		return "En sık kullanılan 10 komut için detaylı rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT DISTINCT c.command, count(c.command) "
				+ "FROM CommandExecutionStatistics c "
				+ "WHERE c.isActive = '1' GROUP BY c.command ORDER BY COUNT(c.command) DESC LIMIT 10";
	}

	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
	
		return new HashSet<IReportTemplateParameter>();
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
				return "Command";
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
				return "Execution Count";
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
		return columns;
	}

	protected MostUsedCommandsReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "MOST-USED-COMMANDS-REPORT";
	}

}

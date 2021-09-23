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
package tr.org.liderahenk.lider.report.templates;

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

public class AgentInfoReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = 4168546979440386632L;

	@Override
	public String getName() {
		return "Ahenk Bilgisi";
	}

	@Override
	public String getDescription() {
		return "Ahenk yüklü bilgisayarlar hakkında rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT a.dn, a.hostname, a.ipAddresses, a.macAddresses, a.createDate " + "FROM AgentImpl a "
				+ "WHERE a.createDate BETWEEN :startDate AND :endDate " + "ORDER BY a.createDate DESC";
	}

	@SuppressWarnings("serial")
	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		Set<IReportTemplateParameter> params = new HashSet<IReportTemplateParameter>();
		// Start date
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
				return "Ahenk LDAP DN";
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
				return "Makina adı";
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
				return "IP adres(ler)i";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 3;
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
				return "MAC adres(ler)i";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 4;
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
				return "Oluşturulma tarihi";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 5;
			}
		});
		return columns;
	}

	protected AgentInfoReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "AGENT-INFO-REPORT";
	}

}

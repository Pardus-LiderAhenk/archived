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

public class SessionActivityReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = 6854143789818012538L;

	@Override
	public String getName() {
		return "Kullanıcı Giriş-Çıkış Logları";
	}

	@Override
	public String getDescription() {
		return "Ahenk yüklü bilgisayarlardaki kullanıcı giriş-çıkışlarına dair rapor";
	}

	@Override
	public String getQuery() {
		return "SELECT us.username, "
				+ "CASE WHEN us.sessionEvent = 1 THEN 'Giriş' ELSE 'Çıkış' END, "
				+ "us.createDate, a.ipAddresses, a.dn  "
				+ "FROM UserSessionImpl us INNER JOIN us.agent a "
				+ "WHERE us.createDate BETWEEN :startDate AND :endDate "
				+ "ORDER BY us.createDate, us.username DESC";
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
				return "Kullanıcı adı";
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
				return "Oturum işlemi";
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
				return "İşlem tarihi";
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
				return "IP adres(ler)i";
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
				return "Ahenk LDAP DN";
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

	protected SessionActivityReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "SESSION-ACTIVITY-REPORT";
	}

}

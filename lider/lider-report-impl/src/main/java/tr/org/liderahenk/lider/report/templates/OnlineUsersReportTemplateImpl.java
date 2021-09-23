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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.plugin.BaseReportTemplate;

public class OnlineUsersReportTemplateImpl extends BaseReportTemplate {

	private static final long serialVersionUID = 8330754495877362709L;

	@Override
	public String getName() {
		return "Çevrimiçi Kullanıcılar";
	}

	@Override
	public String getDescription() {
		return "Anlık olarak sisteme bağlı bulunan tüm kullanıcılara ait bilgiler içeren rapor.";
	}

	@Override
	public String getQuery() {
		return "SELECT a.id, a.jid, us.username, us.createDate, a.ipAddresses, a.dn, us.userIp as USER_IP "
				+ "  FROM UserSessionImpl us INNER JOIN us.agent a" 
				+ " WHERE us.sessionEvent = 1 "
				+ "	AND NOT EXISTS (select 1 from UserSessionImpl logout where logout.sessionEvent = 2 and logout.agent = us.agent "
				+ " 			and logout.username = us.username and logout.createDate > us.createDate)"
				+ " ORDER BY us.createDate, us.username DESC";
	}

	@Override
	public Set<? extends IReportTemplateParameter> getTemplateParams() {
		return null;
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
				return "Ahenk JID";
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
				return "Kullanıcı Adı";
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
				return "Sisteme Giriş Tarihi";
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
				return "IP Adresleri";
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
				return "Ahenk DN";
			}

			@Override
			public Long getId() {
				return null;
			}

			@Override
			public Integer getColumnOrder() {
				return 6;
			}
		});
		return columns;
	}

	protected OnlineUsersReportTemplateImpl getSelf() {
		return this;
	}

	@Override
	public String getCode() {
		return "ONLINE-USERS-REPORT";
	}
}

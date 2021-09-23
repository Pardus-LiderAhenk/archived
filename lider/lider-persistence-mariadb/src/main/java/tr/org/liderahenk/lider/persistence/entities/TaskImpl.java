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
package tr.org.liderahenk.lider.persistence.entities;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.lider.core.api.persistence.entities.ITask;

@JsonIgnoreProperties({ "parameterMapBlob" })
@Entity
@Table(name = "C_TASK")
public class TaskImpl implements ITask {

	private static final long serialVersionUID = 843694316079776849L;

	@Id
	@GeneratedValue
	@Column(name = "TASK_ID", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PLUGIN_ID", nullable = false)
	private PluginImpl plugin; // unidirectional

	@Column(name = "COMMAND_CLS_ID")
	private String commandClsId;

	@Lob
	@Column(name = "PARAMETER_MAP")
	private byte[] parameterMapBlob;

	@Transient
	private Map<String, Object> parameterMap;

	@Column(name = "DELETED")
	private boolean deleted = false;

	@Column(name = "CRON_EXPRESSION")
	private String cronExpression;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	
	@Column(name = "IS_MAIL_SEND")
	private boolean isMailSend = false;
	
	public TaskImpl() {
	}

	public TaskImpl(Long id, PluginImpl plugin, String commandClsId, Map<String, Object> parameterMap, boolean deleted,
			String cronExpression, Date createDate, Date modifyDate) {
		this.id = id;
		this.plugin = plugin;
		this.commandClsId = commandClsId;
		setParameterMap(parameterMap);
		this.deleted = deleted;
		this.cronExpression = cronExpression;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public TaskImpl(ITask task) {
		this.id = task.getId();
		this.commandClsId = task.getCommandClsId();
		setParameterMap(task.getParameterMap());
		this.deleted = task.isDeleted();
		this.cronExpression = task.getCronExpression();
		this.createDate = task.getCreateDate();
		this.modifyDate = task.getModifyDate();
		if (task.getPlugin() instanceof PluginImpl) {
			this.plugin = (PluginImpl) task.getPlugin();
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public PluginImpl getPlugin() {
		return plugin;
	}

	public void setPlugin(PluginImpl plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getCommandClsId() {
		return commandClsId;
	}

	public void setCommandClsId(String commandClsId) {
		this.commandClsId = commandClsId;
	}

	@Override
	public byte[] getParameterMapBlob() {
		if (parameterMapBlob == null && parameterMap != null) {
			try {
				this.parameterMapBlob = new ObjectMapper().writeValueAsBytes(parameterMap);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return parameterMapBlob;
	}

	public void setParameterMapBlob(byte[] parameterMapBlob) {
		this.parameterMapBlob = parameterMapBlob;
		try {
			this.parameterMap = new ObjectMapper().readValue(parameterMapBlob,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		if (parameterMap == null && parameterMapBlob != null) {
			try {
				this.parameterMap = new ObjectMapper().readValue(parameterMapBlob,
						new TypeReference<Map<String, Object>>() {
						});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return parameterMap;
	}

	public void setParameterMap(Map<String, Object> parameterMap) {
		this.parameterMap = parameterMap;
		try {
			this.parameterMapBlob = new ObjectMapper().writeValueAsBytes(parameterMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	@Override
	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "TaskImpl [id=" + id + ", plugin=" + plugin + ", commandClsId=" + commandClsId + ", parameterMap="
				+ parameterMap + "]";
	}

	public boolean isMailSend() {
		return isMailSend;
	}

	public void setMailSend(boolean isMailSend) {
		this.isMailSend = isMailSend;
	}

}

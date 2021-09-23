package tr.org.liderahenk.packagemanager.dto;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Data transfer object class for file distribution results.
 * 
 * @author <a href="mailto:cemre.alpsoy@agem.com.tr">Cerme Alpsoy</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandExecutionStatisticDto implements Serializable {

	private static final long serialVersionUID = 7527517152725287797L;

	private Long taskId;

	private Long agentId;

	private String user;

	private String command;

	private String processStartDate;

	private String processTime;

	private String isActive;

	private Date createDate;

	public CommandExecutionStatisticDto() {
		super();
	}

			public CommandExecutionStatisticDto(Long agentId, String command, String user, String processTime,
					String processStartDate, Date createDate, String isActive, Long taskId) {
				super();
				this.agentId = agentId;
				this.command = command;
				this.user = user;
				this.processTime = processTime;
				this.processStartDate = processStartDate;
				this.createDate = createDate;
				this.isActive = isActive;
				this.taskId = taskId;
			}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getProcessStartDate() {
		return processStartDate;
	}

	public void setProcessStartDate(String processStartDate) {
		this.processStartDate = processStartDate;
	}

	public String getProcessTime() {
		return processTime;
	}

	public void setProcessTime(String processTime) {
		this.processTime = processTime;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}

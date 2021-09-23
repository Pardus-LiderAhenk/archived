package tr.org.liderahenk.packagemanager.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity class for Command Execution Statistics.
 * 
 * @author Cemre Alpsoy <cemre.alpsoy@agem.com.tr>
 *
 * 
 */
@Entity
@Table(name = "P_COMMAND_EXECUTION_STATISTICS")
public class CommandExecutionStatistics implements Serializable {

	private static final long serialVersionUID = -4130227601711334080L;

	@Column(name = "AGENT_ID", nullable = false)
	private Long agentId;

	@Column(name = "COMMAND", nullable = false)
	private String command;

	@Column(name = "USER", nullable = false)
	private String user;

	@Column(name = "PROCESS_TIME", precision = 9, scale = 2, nullable = false)
	private Float processTime;

	@Column(name = "PROCESS_START_DATE")
	private Date processStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Column(name = "IS_ACTIVE", nullable = false)
	private String isActive;

	@Column(name = "TASK_ID", nullable = false)
	private Long taskId;

	@Column(name = "COMMAND_EXECUTION_ID", nullable = false)
	private Long commandExecutionId;

	public CommandExecutionStatistics() {
	}

	public CommandExecutionStatistics(Long agentId, String command, String user, Float processTime,
			Date processStartDate, Date createDate, String isActive, Long taskId, Long commandExecutionId) {
		super();
		this.agentId = agentId;
		this.command = command;
		this.user = user;
		this.processTime = processTime;
		this.processStartDate = processStartDate;
		this.createDate = createDate;
		this.isActive = isActive;
		this.taskId = taskId;
		this.commandExecutionId = commandExecutionId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Float getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Float processTime) {
		this.processTime = processTime;
	}

	public Date getProcessStartDate() {
		return processStartDate;
	}

	public void setProcessStartDate(Date processStartDate) {
		this.processStartDate = processStartDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getCommandExecutionId() {
		return commandExecutionId;
	}

	public void setCommandExecutionId(Long commandExecutionId) {
		this.commandExecutionId = commandExecutionId;
	}

}

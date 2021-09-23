package tr.org.liderahenk.packagemanager.entities;


/**
 * Entity class for Executed Command's related packages' names and versions
 * 
 * @author Cemre Alpsoy <cemre.alpsoy@agem.com.tr>
 *
 * 
 */
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "P_EXECUTED_COMMAND_PACKAGE_STATISTICS")
public class CommandPackageVersion implements Serializable {

	private static final long serialVersionUID = -4130227601711334080L;

	@Column(name = "AGENT_ID", nullable = false)
	private Long agentId;

	@Column(name = "COMMAND", nullable = false)
	private String command;

	@Column(name = "PACKAGE_NAME", nullable = false)
	private String packageName;

	@Column(name = "PACKAGE_VERSION", nullable = false)
	private String packageVersion;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	@Column(name = "TASK_ID", nullable = false)
	private Long taskId;

	public CommandPackageVersion() {
	}

	public CommandPackageVersion(Long agentId, String command, String packageName, String packageVersion,
			Date createDate, Long taskId) {
		super();
		this.agentId = agentId;
		this.command = command;
		this.packageName = packageName;
		this.packageVersion = packageVersion;
		this.createDate = createDate;
		this.taskId = taskId;
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

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageVersion() {
		return packageVersion;
	}

	public void setPackageVersion(String packageVersion) {
		this.packageVersion = packageVersion;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}

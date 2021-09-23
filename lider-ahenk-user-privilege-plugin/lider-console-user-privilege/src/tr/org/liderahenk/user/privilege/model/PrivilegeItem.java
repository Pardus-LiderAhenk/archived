package tr.org.liderahenk.user.privilege.model;

import java.io.Serializable;

/**
 * Model class for privilege entries in user privileges profile dialog. It keeps
 * a Linux command and its PolKit choices and resource limit values.
 * 
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 *
 */
public class PrivilegeItem implements Serializable {

	private static final long serialVersionUID = 60063052260147014L;

	private String cmd;

	private String polkitStatus;

	private Boolean limitResourceUsage;

	private Integer cpu;

	private Integer memory;

	public PrivilegeItem() {
		super();
	}

	public PrivilegeItem(String cmd, String polkitStatus, Boolean limitResourceUsage) {
		super();
		this.cmd = cmd;
		this.polkitStatus = polkitStatus;
		this.limitResourceUsage = limitResourceUsage;
	}

	public PrivilegeItem(String cmd, String polkitStatus, Boolean limitResourceUsage, Integer cpu, Integer memory) {
		super();
		this.cmd = cmd;
		this.polkitStatus = polkitStatus;
		this.limitResourceUsage = limitResourceUsage;
		this.cpu = cpu;
		this.memory = memory;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public String getPolkitStatus() {
		return polkitStatus;
	}

	public void setPolkitStatus(String polkitStatus) {
		this.polkitStatus = polkitStatus;
	}

	public Boolean getLimitResourceUsage() {
		return limitResourceUsage;
	}

	public void setLimitResourceUsage(Boolean limitResourceUsage) {
		this.limitResourceUsage = limitResourceUsage;
	}

	public Integer getCpu() {
		return cpu;
	}

	public void setCpu(Integer cpu) {
		this.cpu = cpu;
	}

	public Integer getMemory() {
		return memory;
	}

	public void setMemory(Integer memory) {
		this.memory = memory;
	}

}

package tr.org.liderahenk.backup.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "P_BACKUP_SERVER_CONFIG")
public class BackupServerConfig implements Serializable {

	private static final long serialVersionUID = -4130227601711334080L;

	@Id
	@GeneratedValue
	@Column(name = "BACKUP_SERVER_CONFIG_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "PASSWORD", nullable = false)
	private String password;

	@Column(name = "DEST_HOST", nullable = false)
	private String destHost;

	@Column(name = "DEST_PORT", nullable = false)
	private Integer destPort;

	@Column(name = "DEST_PATH", nullable = false)
	private String destPath;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public BackupServerConfig() {
	}

	public BackupServerConfig(Long id, String username, String password, String destHost, Integer destPort,
			String destPath, Date createDate) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.destHost = destHost;
		this.destPort = destPort;
		this.destPath = destPath;
		this.createDate = createDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDestHost() {
		return destHost;
	}

	public void setDestHost(String destHost) {
		this.destHost = destHost;
	}

	public Integer getDestPort() {
		return destPort;
	}

	public void setDestPort(Integer destPort) {
		this.destPort = destPort;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

}

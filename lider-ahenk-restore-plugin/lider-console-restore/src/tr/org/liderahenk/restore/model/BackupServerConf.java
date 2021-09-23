package tr.org.liderahenk.restore.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackupServerConf implements Serializable {

	private static final long serialVersionUID = -7609843837037791405L;

	private Long id;
	private String username;
	private String password;
	private String destHost;
	private Integer destPort;
	private String destPath;
	private Date createDate;

	public BackupServerConf() {
	}

	public BackupServerConf(Long id, String username, String password, String destHost, Integer destPort,
			String destPath, Date createDate) {
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

	public String getDestPath() {
		return destPath;
	}

	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}

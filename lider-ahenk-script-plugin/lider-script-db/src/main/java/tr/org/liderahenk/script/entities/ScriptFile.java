package tr.org.liderahenk.script.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.script.enums.ScriptType;

/**
 * Entity class for script files.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@Entity
@Table(name = "P_SCRIPT_FILE")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptFile implements Serializable {

	private static final long serialVersionUID = 5867302652909954893L;

	@Id
	@GeneratedValue
	@Column(name = "SCRIPT_FILE_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "SCRIPT_TYPE", length = 1, nullable = false)
	private Integer scriptType;

	@Column(name = "LABEL", nullable = false, unique = true, length = 255)
	private String label;

	@Lob
	@Column(name = "CONTENTS", nullable = false)
	private String contents;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false, updatable = false)
	private Date createDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public ScriptFile() {
	}

	public ScriptFile(Long id, ScriptType scriptType, String label, String contents, Date createDate, Date modifyDate) {
		this.id = id;
		setScriptType(scriptType);
		this.label = label;
		this.contents = contents;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ScriptType getScriptType() {
		return ScriptType.getType(scriptType);
	}

	public void setScriptType(ScriptType scriptType) {
		if (scriptType == null) {
			this.scriptType = null;
		} else {
			this.scriptType = scriptType.getId();
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

}


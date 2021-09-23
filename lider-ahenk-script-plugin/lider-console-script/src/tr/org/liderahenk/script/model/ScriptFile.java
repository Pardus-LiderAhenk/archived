package tr.org.liderahenk.script.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScriptFile implements Serializable {

	private static final long serialVersionUID = -6418691700781592989L;

	private Long id;

	private ScriptType scriptType;

	private String label;

	private String contents;

	private Date createDate;

	private Date modifyDate;

	private Boolean isTemplate;
	
	public ScriptFile() {
		this.isTemplate = false;
	}

	public ScriptFile(Long id, ScriptType scriptType, String label, String contents, Date createDate, Date modifyDate) {
		this.id = id;
		this.scriptType = scriptType;
		this.label = label;
		this.contents = contents;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.isTemplate = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
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

	public Boolean getIsTemplate() {
		return isTemplate;
	}

	public void setIsTemplate(Boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

}

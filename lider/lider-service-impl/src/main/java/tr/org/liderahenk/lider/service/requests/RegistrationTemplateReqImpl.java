package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;


@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationTemplateReqImpl implements IRegistrationTemplate {
	
	private Long id;

	private String unitId;

	private String authGroup;

	private String parentDn;
	
	private Date createDate;
	

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#getUnitId()
	 */
	@Override
	public String getUnitId() {
		return unitId;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#setUnitId(java.lang.String)
	 */
	@Override
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#getAuthGroup()
	 */
	@Override
	public String getAuthGroup() {
		return authGroup;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#setAuthGroup(java.lang.String)
	 */
	@Override
	public void setAuthGroup(String authGroup) {
		this.authGroup = authGroup;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#getParentDn()
	 */
	@Override
	public String getParentDn() {
		return parentDn;
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.service.requests.IRegistrationTemplateRequest#setParentDn(java.lang.String)
	 */
	@Override
	public void setParentDn(String parentDn) {
		this.parentDn = parentDn;
	}
	@Override
	public Date getCreateDate() {
		return createDate;
	}
	@Override
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	
}

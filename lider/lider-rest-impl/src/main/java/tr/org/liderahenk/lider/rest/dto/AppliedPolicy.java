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
package tr.org.liderahenk.lider.rest.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.entities.ICommand;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecution;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPolicy;

/**
 * This is a specialized class which is used to list executed policies with some
 * additional info.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppliedPolicy implements Serializable {

	private static final long serialVersionUID = -8740486474920176215L;

	private Long id;

	private String label;

	private Date createDate;

	private Integer successResults;

	private Integer warningResults;

	private Integer errorResults;

	// FIXME temporary MSB solution. Need to change JSON structure altogether
	private String policy;
	private Date applyDate;
	private Date activationDate;
	private Date expirationDate;
	private List<String> uidList;

	public AppliedPolicy(IPolicy policy, Integer successResults, Integer warningResults, Integer errorResults,
			ICommand cmd) {
		super();
		this.id = policy.getId();
		this.label = policy.getLabel();
		this.createDate = policy.getCreateDate();
		this.successResults = successResults;
		this.warningResults = warningResults;
		this.errorResults = errorResults;
		try {
			this.policy = new ObjectMapper().writeValueAsString(policy);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.applyDate = cmd.getCreateDate();
		this.activationDate = cmd.getActivationDate();
		this.expirationDate = cmd.getExpirationDate();
		this.uidList = new ArrayList<String>();
		if (cmd.getCommandExecutions() != null) {
			for (ICommandExecution exec : cmd.getCommandExecutions()) {
				if (exec.getUid() == null)
					continue;
				this.uidList.add(exec.getUid());
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getSuccessResults() {
		return successResults;
	}

	public void setSuccessResults(Integer successResults) {
		this.successResults = successResults;
	}

	public Integer getErrorResults() {
		return errorResults;
	}

	public void setErrorResults(Integer errorResults) {
		this.errorResults = errorResults;
	}

	public Integer getWarningResults() {
		return warningResults;
	}

	public void setWarningResults(Integer warningResults) {
		this.warningResults = warningResults;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public List<String> getUidList() {
		return uidList;
	}

	public void setUidList(List<String> uidList) {
		this.uidList = uidList;
	}

}

package tr.org.liderahenk.lider.core.api.rest.requests;

import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailContent;

public interface IMailManagementRequest {

	public IMailContent getMailContent();

	public List<? extends IMailAddress> getMailAddressList();


}

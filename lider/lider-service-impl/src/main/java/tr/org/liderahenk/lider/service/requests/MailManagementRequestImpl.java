package tr.org.liderahenk.lider.service.requests;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.rest.requests.IMailManagementRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MailManagementRequestImpl implements IMailManagementRequest {
	
	private List<MailAddressReqImpl> mailAddressList;
	
	private MailContentReqImpl mailContent;
	
	
	public MailManagementRequestImpl() {
	
	}

	public List<MailAddressReqImpl> getMailAddressList() {
		return mailAddressList;
	}


	public void setMailAddressList(List<MailAddressReqImpl> mailAddressList) {
		this.mailAddressList = mailAddressList;
	}


	public MailContentReqImpl getMailContent() {
		return mailContent;
	}


	public void setMailContent(MailContentReqImpl mailContent) {
		this.mailContent = mailContent;
	}


}

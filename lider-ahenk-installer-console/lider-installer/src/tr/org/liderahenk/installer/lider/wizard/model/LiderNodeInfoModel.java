package tr.org.liderahenk.installer.lider.wizard.model;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class LiderNodeInfoModel {

	private int nodeNumber;
	private String nodeIp;
	private String nodeUsername;
	private String nodeRootPwd;
	private String nodeXmppResource;
	private String nodeXmppPresencePriority;
	
	public LiderNodeInfoModel(int nodeNumber, String nodeIp, String nodeUsername, String nodeRootPwd, String nodeXmppResource, String nodeXmppPresencePriority) {
		super();
		this.nodeNumber = nodeNumber;
		this.nodeIp = nodeIp;
		this.nodeUsername = nodeUsername;
		this.nodeRootPwd = nodeRootPwd;
		this.nodeXmppResource = nodeXmppResource;
		this.nodeXmppPresencePriority = nodeXmppPresencePriority;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	public String getNodeIp() {
		return nodeIp;
	}
	public void setNodeIp(String nodeIp) {
		this.nodeIp = nodeIp;
	}
	public String getNodeRootPwd() {
		return nodeRootPwd;
	}
	public void setNodeRootPwd(String nodeRootPwd) {
		this.nodeRootPwd = nodeRootPwd;
	}
	public String getNodeXmppResource() {
		return nodeXmppResource;
	}
	public void setNodeXmppResource(String nodeXmppResource) {
		this.nodeXmppResource = nodeXmppResource;
	}
	public String getNodeXmppPresencePriority() {
		return nodeXmppPresencePriority;
	}
	public void setNodeXmppPresencePriority(String nodeXmppPresencePriority) {
		this.nodeXmppPresencePriority = nodeXmppPresencePriority;
	}
	public String getNodeUsername() {
		return nodeUsername;
	}
	public void setNodeUsername(String nodeUsername) {
		this.nodeUsername = nodeUsername;
	}
}

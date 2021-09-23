package tr.org.liderahenk.installer.lider.wizard.model;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class XmppNodeInfoModel {

	private int nodeNumber;
	private String nodeIp;
	private String nodeName;
	private String nodeUsername;
	private String nodeRootPwd;
	private boolean nodeNewSetup;
	
	public XmppNodeInfoModel(int nodeNumber, String nodeIp, String nodeName, String nodeUsername, String nodeRootPwd,
			boolean nodeNewSetup) {
		super();
		this.nodeNumber = nodeNumber;
		this.nodeIp = nodeIp;
		this.nodeName = nodeName;
		this.nodeUsername = nodeUsername;
		this.nodeRootPwd = nodeRootPwd;
		this.nodeNewSetup = nodeNewSetup;
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
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeRootPwd() {
		return nodeRootPwd;
	}
	public void setNodeRootPwd(String nodeRootPwd) {
		this.nodeRootPwd = nodeRootPwd;
	}
	public boolean isNodeNewSetup() {
		return nodeNewSetup;
	}
	public void setNodeNewSetup(boolean nodeNewSetup) {
		this.nodeNewSetup = nodeNewSetup;
	}
	public String getNodeUsername() {
		return nodeUsername;
	}
	public void setNodeUsername(String nodeUsername) {
		this.nodeUsername = nodeUsername;
	}
}

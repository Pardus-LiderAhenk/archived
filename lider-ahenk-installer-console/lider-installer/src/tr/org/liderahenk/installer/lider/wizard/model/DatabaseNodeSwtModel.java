package tr.org.liderahenk.installer.lider.wizard.model;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner Feyzullahoglu</a>
 * 
 */
public class DatabaseNodeSwtModel {
	
	private int nodeNumber;
	private Text txtNodeIp;
	private Text txtNodeName;
	private Text txtNodeUsername;
	private Text txtNodeRootPwd;
	private Button btnNodeNewSetup;
	
	public Text getTxtNodeIp() {
		return txtNodeIp;
	}
	public void setTxtNodeIp(Text txtNodeIp) {
		this.txtNodeIp = txtNodeIp;
	}
	public Text getTxtNodeName() {
		return txtNodeName;
	}
	public void setTxtNodeName(Text txtNodeName) {
		this.txtNodeName = txtNodeName;
	}
	public Text getTxtNodeRootPwd() {
		return txtNodeRootPwd;
	}
	public void setTxtNodeRootPwd(Text txtNodeRootPwd) {
		this.txtNodeRootPwd = txtNodeRootPwd;
	}
	public Button getBtnNodeNewSetup() {
		return btnNodeNewSetup;
	}
	public void setBtnNodeNewSetup(Button btnNodeNewSetup) {
		this.btnNodeNewSetup = btnNodeNewSetup;
	}
	public int getNodeNumber() {
		return nodeNumber;
	}
	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}
	public Text getTxtNodeUsername() {
		return txtNodeUsername;
	}
	public void setTxtNodeUsername(Text txtNodeUsername) {
		this.txtNodeUsername = txtNodeUsername;
	}
	
	
}

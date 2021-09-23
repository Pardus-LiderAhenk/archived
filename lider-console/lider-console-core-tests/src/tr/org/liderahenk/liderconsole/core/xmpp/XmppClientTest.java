package tr.org.liderahenk.liderconsole.core.xmpp;

import org.junit.Test;

import tr.org.liderahenk.liderconsole.core.testutils.PropertyLoader;
import tr.org.liderahenk.liderconsole.core.xmpp.XMPPClient;

public class XmppClientTest {

	@Test
	public void connect() {
//		XMPPClient.getInstance().connect(PropertyLoader.getInstance().get("xmpp.user.name"),
//				PropertyLoader.getInstance().get("xmpp.user.pwd"),
//				PropertyLoader.getInstance().get("xmpp.service.name"), PropertyLoader.getInstance().get("xmpp.host"),
//				PropertyLoader.getInstance().getInt("xmpp.user.port"));
	}

	@Test
	public void findPresences() {

	}

	@Test
	public void disconnect() {
		XMPPClient.getInstance().disconnect();
	}

}

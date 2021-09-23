package test.liderahenk.xmpp;

public class VysperServerExecuter extends Thread {
	
	private ServerMain server;
	
	@Override
	public void run() {
		try {
			server = new ServerMain();
		} catch (Exception e) {
//			server.stopServer();
			e.printStackTrace();
		}
	}
	
	
	public void stopServer(){
		server.stopServer();
	}

}

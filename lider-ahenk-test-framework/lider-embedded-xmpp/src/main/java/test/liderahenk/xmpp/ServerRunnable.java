package test.liderahenk.xmpp;

public class ServerRunnable implements Runnable {
	
	private ServerMain serverMain;
	
	//private boolean running = true;
	
	public ServerRunnable() {
		try {
			this.serverMain = new ServerMain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Override
	public void run() {
		try {
			if (this.serverMain != null){
			//this.serverMain.startServer();
			System.out.println("ServerRunnable.run()");
			while( !this.serverMain.exit ){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					this.serverMain.stopServer();
					e.printStackTrace();
				}
				
			}
			System.out.println("stopping server");
			this.serverMain.stopServer();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}

}

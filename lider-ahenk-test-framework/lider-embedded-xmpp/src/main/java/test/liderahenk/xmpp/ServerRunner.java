package test.liderahenk.xmpp;

public class ServerRunner {

	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new ServerRunnable());
		t.start();
		
		t.join();
		
		System.exit(0);
	}

}

package test.liderahenk.db;

public class EmbeddedMariaDBExecuter extends Thread{
	
	
	
	private static EmbeddedMariaDB embeddedDb;

	public void run(){
		
		System.out.println("Starting embedded mariadb database");
		embeddedDb = new EmbeddedMariaDB();
		getEmbeddedDb().init();
		
		getEmbeddedDb().start();
		
		System.out.println("Started embedded mariadb database");
	}

	public void stopServer(){
		System.out.println("Stopping embedded mariadb database");
		if (getEmbeddedDb() != null) {
			getEmbeddedDb().stop();
		}
		System.out.println("Stoped embedded mariadb database");
	}

	public static EmbeddedMariaDB getEmbeddedDb() {
		return embeddedDb;
	}
	
	

	
	public static void main(String...strings){
		EmbeddedMariaDBExecuter d = new EmbeddedMariaDBExecuter();
		d.start();
	}
	
	
}

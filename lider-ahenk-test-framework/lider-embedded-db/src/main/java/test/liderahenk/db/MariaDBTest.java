package test.liderahenk.db;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class MariaDBTest {
	
	public static void main(String...strings){
		DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
		configBuilder.setPort(3306); // OR, default: setPort(0); => autom. detect free port
		configBuilder.setDataDir("/tmp/db1"); // just an example
		
		try {
			DB db = DB.newEmbeddedDB(configBuilder.build());
			db.start();
			db.createDB("liderdb");
			System.out.println("created db");
			//db.stop();
		} catch (ManagedProcessException e) {
			e.printStackTrace();
		}
	}

}

package test.liderahenk.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

public class EmbeddedMariaDB {
	
	private Integer port = 3306;
	private String dataPath = "/tmp/db";
	private String dbName = "liderdb";
	private DBConfigurationBuilder configBuilder;
	private DB db;
	Properties liderProp;
	
	public void init(){
		try {
			
			liderProp = readProperties();
			
			DBConfigurationBuilder configBuilder = DBConfigurationBuilder.newBuilder();
			configBuilder.setPort(Integer.parseInt(liderProp.getProperty("embedded.db.port")));
			configBuilder.setDataDir(liderProp.getProperty("embedded.db.dataPath")); 
			db = DB.newEmbeddedDB(configBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		if(db != null){
			try {
				db.start();
				db.createDB(liderProp.getProperty("embedded.db.name"));
			} catch (ManagedProcessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		if(getDb() != null){
			try {
				getDb().stop();
			} catch (ManagedProcessException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public Properties readProperties() throws IOException{
		Properties prop = new Properties();
		InputStream liderProp = new FileInputStream(this.getClass().getResource("/lider-embedded.properties").getFile());
		prop.load(liderProp);
		return prop;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public DBConfigurationBuilder getConfigBuilder() {
		return configBuilder;
	}

	public void setConfigBuilder(DBConfigurationBuilder configBuilder) {
		this.configBuilder = configBuilder;
	}

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}

}

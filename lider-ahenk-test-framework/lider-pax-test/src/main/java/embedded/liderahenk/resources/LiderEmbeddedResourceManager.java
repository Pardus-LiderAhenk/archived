package embedded.liderahenk.resources;

import test.liderahenk.db.EmbeddedMariaDBExecuter;
import test.liderahenk.ldap.LDAPExecutor;
import test.liderahenk.ldap.server.EmbeddedLdapServer;
import test.liderahenk.xmpp.VysperServerExecuter;

public class LiderEmbeddedResourceManager {

	static EmbeddedMariaDBExecuter dbExecuter;
	private static EmbeddedLdapServer server;
	private static LDAPExecutor ldapExecuter;
	private static VysperServerExecuter vysperServerExecuter;
	
	
	
	public static void start(){
		
		dbExecuter = new EmbeddedMariaDBExecuter();
		dbExecuter.start();
		
		
		ldapExecuter = new LDAPExecutor();
		ldapExecuter.start();
		
		
		vysperServerExecuter = new VysperServerExecuter();
		vysperServerExecuter.start();
		
		
	}
	
	
	public static void stop(){
		dbExecuter.stopServer();
	}
	


	
	
	public static void main(String...strings){
		LiderEmbeddedResourceManager.start();
	}
	
}

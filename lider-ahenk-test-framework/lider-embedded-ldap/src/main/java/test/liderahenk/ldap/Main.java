package test.liderahenk.ldap;
//package test.apacheds;
//
//import java.io.File;
//
//import test.apacheds.server.EmbeddedLdapServer;
//
//public class Main {
//
//	
//	/***
//	 * Default admin username uid=admin,ou=system
//	 * Default admin password secret
//	 */
//	
//	
//	private static final String HOST = "localhost";
//	private static final Integer PORT = 10389;
//	public static final String EXAMPLE_DATA = "/example-data.ldif";
//	public static final String LIDERAHENK_LDAP_SCHEMA = "/liderahenk_apacheds.ldif";
//	
//	public static void main(final String[] args) {
//		Main main = new Main();
//		main.execute();
//		System.exit(0);
//	}
//	
//	
//
//	private void execute() {
//		// start server
//		System.out.println("Initializing embedded LDAP server");
//		EmbeddedLdapServer embeddedLdapServer = new EmbeddedLdapServer(HOST,
//				PORT);
//		try {
//			embeddedLdapServer.start();
//
//			// Load the directory as a resource
//			
//			File dir = new File(this.getClass().getResource(LIDERAHENK_LDAP_SCHEMA).getFile());
//			embeddedLdapServer.applyLdif(dir);
//			
//			embeddedLdapServer.getDirectoryService().getSchemaManager().loadAllEnabled();
//
//			dir = new File(this.getClass().getResource(EXAMPLE_DATA).getFile());
//			embeddedLdapServer.applyLdif(dir);
//
//			System.out.println("Embedded LDAP server started");
//			
//			
//			System.out.println(embeddedLdapServer.getDirectoryService().getAdminSession().exists("ou=People,dc=liderahenk,dc=org,dc=tr"));
////			System.out.println(embeddedLdapServer.getDirectoryService().getAdminSession().exists("uid=pardus,ou=People,dc=mys,dc=pardus,dc=org"));
////			Entry lookup = embeddedLdapServer.getDirectoryService().getAdminSession().lookup(new Dn("cn=liderAhenkConfig,dc=liderahenk,dc=org,dc=tr"));
//			
////			System.out.println(lookup.get("objectClass"));
////			embeddedLdapServer.getDirectoryService().getSchemaManager().load(arg0)
//			
//			
////			Dn dnApache = new Dn( "uid=pardus,ou=People,dc=liderahenk,dc=org,dc=tr" );
////	        Entry newEntry = embeddedLdapServer.getDirectoryService().newEntry( dnApache );
////	        newEntry.add( "objectClass", "pardusAccount", "top", "person", "organizationalPerson", "inetOrgPerson" );
////	        newEntry.add( "cn", "pardus" );
////	        newEntry.add( "sn", "pardus" );
////	        newEntry.add( "uid", "pardus" );
////	        newEntry.add( "userPassword", "pardus" );
////	        embeddedLdapServer.getDirectoryService().getAdminSession().add( newEntry );
//	        System.out.println(embeddedLdapServer.getDirectoryService().getAdminSession().exists("uid=test,dc=liderahenk,dc=org,dc=tr"));
//	        
//	        
//	        while(true){
//	        	Thread.sleep(1000);
//	        }
//	        
//	        
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				//stop server
//				embeddedLdapServer.stop();
//				System.out.println("Embedded LDAP server stopped");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//}

package test.liderahenk.ldap;

public class Test {

	public static void main(String[] args) {
		LDAPExecutor ex = new LDAPExecutor();
		ex.start();
		
		
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("durduruyorum");
		ex.stopLdap();
		
	}

}

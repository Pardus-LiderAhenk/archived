package test.liderahenk.xmpp;

public class Main {
	
	
	private static VysperServerExecuter vysperServerExecuter;

	public static void main(String...strings ){
//		try {
//			ServerMain m = new ServerMain();
//		
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		vysperServerExecuter = new VysperServerExecuter();
		vysperServerExecuter.start();
		
		
	}

}

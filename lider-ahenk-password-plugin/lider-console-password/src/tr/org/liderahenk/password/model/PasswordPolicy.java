package tr.org.liderahenk.password.model;

public class PasswordPolicy {
	
	private String dn;
	private String shortName;
	
	public PasswordPolicy(String dn, String shortName) {
		super();
		this.dn = dn;
		this.shortName = shortName;
	}

	public PasswordPolicy() {
		
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getShortName() {
		if(dn!=null){
		
			String[] nameArr= dn.split(",");
			
			String name="";
			if( nameArr.length>1){
				name=nameArr[0];
			}
		this.shortName=name;
		}			
		
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	
	

}

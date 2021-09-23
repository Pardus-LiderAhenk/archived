package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;

public interface IMailAddress  extends IEntity{

	String getMailAddress();
	
	Date getModifyDate();
	
	IPlugin getPlugin();
	
	boolean isDeleted();
	
	
}

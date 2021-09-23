package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;

public interface IMailParameter  extends IEntity{

	String getMailParameter();
	
	Date getModifyDate();
	
	IPlugin getPlugin();
	
	boolean isDeleted();
	
	
}

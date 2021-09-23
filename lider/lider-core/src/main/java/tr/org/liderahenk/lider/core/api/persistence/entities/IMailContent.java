package tr.org.liderahenk.lider.core.api.persistence.entities;

import java.util.Date;

public interface IMailContent extends IEntity {

	String getMailContent();

	Date getModifyDate();

	IPlugin getPlugin();

	int getMailSendStartegy();

	String getMailSchedulerCronString();
	
	String getMailSchdTimePeriod() ;
	
	int getMailSchdTimePeriodType() ;

	boolean isDeleted();

}

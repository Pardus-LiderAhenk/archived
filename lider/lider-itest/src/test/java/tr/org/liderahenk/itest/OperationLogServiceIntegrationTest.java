package tr.org.liderahenk.itest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.liderahenk.test.LiderKarafTestContainer;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public class OperationLogServiceIntegrationTest extends LiderKarafTestContainer{
	
	IOperationLogService operationLogService;
	String userid = "testuser";
	
	@Before
	public void setup(){
		operationLogService = getOsgiService(IOperationLogService.class);
	}
	
	
	@Test
	public void checkService(){
		Assert.assertNotNull(operationLogService);
	}
	
	@Test
	public void saveLog() throws Exception{
		operationLogService.saveLog(userid, CrudType.CREATE, "Integration Test", null, "127.0.0.1");
	}
	
	@Test
	public void saveTaskLog() throws Exception{
		operationLogService.saveTaskLog(userid, CrudType.CREATE,1L ,"Integration Test", null, "127.0.0.1");
	}
	
	@Test
	public void savePolicyLog() throws Exception{
		operationLogService.saveTaskLog(userid, CrudType.CREATE,1L ,"Integration Test", null, "127.0.0.1");
	}

	@Test
	public void saveProfileLog() throws Exception{
		operationLogService.saveTaskLog(userid, CrudType.CREATE,1L ,"Integration Test", null, "127.0.0.1");
	}
	
}

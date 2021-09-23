package tr.org.liderahenk.itest;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.liderahenk.test.LiderKarafTestContainer;
import org.mockito.Mockito;

import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandContextFactory;

public class CommandContextFactoryIntegrationTest extends LiderKarafTestContainer{
	
	ICommandContextFactory commandContextService;
	
	@Before
	public void setup(){
		commandContextService = getOsgiService(ICommandContextFactory.class);
	}
	
	@Test
	public void chechService(){
		assertNotNull(commandContextService);
	}
	
	
	@Test
	public void create(){
		Date now = new Date();
		ITaskRequest iTaskRequest = Mockito.mock(ITaskRequest.class);
		
		Mockito.when(iTaskRequest.getPluginName()).thenReturn("plugin");
		Mockito.when(iTaskRequest.getCommandId()).thenReturn("command1");
		Mockito.when(iTaskRequest.getPluginVersion()).thenReturn("1.0.0");
		Mockito.when(iTaskRequest.getTimestamp()).thenReturn(now);
		
		ICommandContext commandContext = commandContextService.create(iTaskRequest);
		
		assertNotNull(commandContext);
		Assert.assertTrue(commandContext.getRequest().getPluginName() == "plugin1");
		Assert.assertTrue(commandContext.getRequest().getCommandId() == "command1");
		Assert.assertTrue(commandContext.getRequest().getPluginVersion() == "1.0.0");
		Assert.assertTrue(commandContext.getRequest().getTimestamp().equals(now));
		
	}
	

}

package tr.org.liderahenk.itest;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liderahenk.test.LiderKarafTestContainer;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import tr.org.liderahenk.lider.core.api.ldap.ILDAPService;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class LdapServiceIntegrationTest extends LiderKarafTestContainer {

	@Test
	public void checkService() {
		ILDAPService ldapservice = getOsgiService(ILDAPService.class);
		assertNotNull(ldapservice);
	}

}

package tr.org.liderahenk.itest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({LdapServiceIntegrationTest.class,FeatureListTest.class,MessagingServiceIntegrationTest.class})
public class LiderTestSuite {

}

//package tr.org.liderahenk.test;
//
//import static org.junit.Assert.assertFalse;
//import static org.ops4j.pax.exam.CoreOptions.maven;
//import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.liderahenk.test.LiderKarafTestContainer;
//import org.ops4j.pax.exam.Option;
//import org.ops4j.pax.exam.junit.PaxExam;
//import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
//import org.ops4j.pax.exam.spi.reactors.PerClass;
//
//
//
//
//
//@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerClass.class)
//public class LiderFeatureListSampleTest extends LiderKarafTestContainer {
//	
//
//	@Before
//	public void initialize(){
//		System.out.println("####################");
//		System.out.println("Initializing test class");
//		System.out.println("####################");
//	}
//	
//    @Test
//    public void listCommand() throws Exception {
//        String featureListOutput = executeCommand("list");
//        System.out.println(featureListOutput);
//        assertFalse(featureListOutput.isEmpty());
//    }
//    
//    
//	@Override
//	public Option[] getCustomOptions() {
//		Option[] options = new Option[1];
//		
//		options[0] = mavenBundle().groupId("org.freemarker").artifactId("freemarker").version("2.3.20");
//		
//		return options;
//	}
//	
//	
//	@After
//	public void destroy(){
//		System.out.println("####################");
//		System.out.println("Destroying test class");
//		System.out.println("####################");
//	}
//	
//}

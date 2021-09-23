package tr.org.liderahenk.test.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

//@RunWith(PaxExam.class)
//@ExamReactorStrategy(PerSuite.class)
@RunWith(Suite.class)
//@ExamReactorStrategy(PerSuite.class)
@SuiteClasses({PerSuiteSample1.class,PerSuiteSample2.class})
public class SampleSuite {

}

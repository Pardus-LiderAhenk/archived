package tr.org.liderahenk.itest;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liderahenk.test.LiderKarafTestContainer;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class FeatureListTest extends LiderKarafTestContainer{

	@Test
	public void listCommand() throws Exception {
		String featureListOutput = executeCommand("list");
		System.out.println(featureListOutput);
		assertFalse(featureListOutput.isEmpty());
	}
}

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class MainTestParallel {

	@Test
	public void test() {

		// Parallel among methods in a class
		Result result = JUnitCore.runClasses(ParallelComputer.methods(), MainTest.class);
		Util.printResult(result);
	}

	public static class Util {
		static void printResult(Result result) {
			System.out.printf("Test ran: %s, Failed: %s%n", result.getRunCount(), result.getFailureCount());
			result.getFailures().forEach(System.out::println);
		}
	}
}

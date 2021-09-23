package tr.org.pardus.mys.liderahenksetup.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class StringUtils {

	@SafeVarargs
	public static <T> String join(String separator, T... objects) {
		if (objects != null) {
			StringBuilder sb = new StringBuilder();
			String sep = "";
			for (Object o : objects) {
				sb.append(sep).append(o.toString());
				sep = separator;
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * Converts the given InputStream to a String. This is how the streams from
	 * executing NMap are converted and later stored in the ExecutionResults.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String convertStream(InputStream is) {
		String output;
		StringBuffer outputBuffer = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			while ((output = br.readLine()) != null) {
				outputBuffer.append(output);
				outputBuffer.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return outputBuffer.toString();
	}

}

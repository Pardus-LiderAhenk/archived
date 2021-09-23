package tr.org.liderahenk.registration.subscriber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CsvReader {

	public Map<String, String[]> read(String protocol, String path) throws Exception {

		ArrayList<InputStream> iSList = new ArrayList<InputStream>();

		if ("local".equals(protocol)) {

			if (new File(path).exists() == false) {
				throw new Exception("Local path does not exists");
			}

			ArrayList<String> fileList = lookChild(path);

			for (String filePath : fileList) {
				InputStream is = new FileInputStream(filePath);
				iSList.add(is);
			}

			return this.readInputStream(iSList);

		} else if ("http".equals(protocol)) {

			// TODO path check
			InputStream inputStream = new URL(path).openStream();
			iSList.add(inputStream);

			return this.readInputStream(iSList);

		} else if ("inner".equals(protocol)) {
			InputStream is = new FileInputStream(path);
			iSList.add(is);
			return this.readInputStream(iSList);

		} else {
			// TODO unknown protocol
			return null;
		}

	}

	public ArrayList<String> lookChild(String path) {

		ArrayList<String> fileList = new ArrayList<String>();

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return null;

		for (File f : list) {
			if (f.isDirectory()) {
				fileList.addAll(lookChild(f.getAbsolutePath()));
			} else {
				fileList.add(f.getAbsolutePath());
			}
		}

		return fileList;
	}

	public Map<String, String[]> readInputStream(ArrayList<InputStream> inputStreamList) {

		Map<String, String[]> recordsMap = new HashMap<String, String[]>();

		BufferedReader br = null;
		String csvSplitBy = ",";
		String line = "";

		for (InputStream inputStream : inputStreamList) {
			try {
				br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

				while ((line = br.readLine()) != null) {

					line = line.trim();

					if (",".equals(line.charAt(line.length() - 1))) {
						line = line.substring(0, line.length() - 1);
					}

					String[] record = line.split(csvSplitBy);
					if (record.length > 0) {
						String[] ouParameters = Arrays.copyOfRange(record, 1, record.length);
						recordsMap.put(record[0], ouParameters);
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
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
		}

		return recordsMap;
	}

}

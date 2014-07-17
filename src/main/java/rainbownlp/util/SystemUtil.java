package rainbownlp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemUtil {
	/**
	 * Run synchronous shell command and wait till it finishes 
	 * @param command
	 */
	public static void runShellCommand(String command) {
		try {
			System.out.println(command);
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));

			String line = null;

			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}

			int exitVal = pr.waitFor();
			
			System.out.println("Exited with error code " + exitVal);

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}

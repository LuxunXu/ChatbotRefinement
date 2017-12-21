import java.io.*;
import java.util.*;


public class FileGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File f = new File("DALog.txt");
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		BufferedWriter writer = new BufferedWriter(new FileWriter("DALogResponseOnly.txt"));
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split("\\t");
			writer.write(tokens[1].trim());
			writer.newLine();
		}
	}

}

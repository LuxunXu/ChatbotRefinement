package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class GeneratingPairs {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File f = new File("test.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter("testpair.csv"));
		writer.write("Response One, Response Two");
		writer.newLine();
		
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		ArrayList<String> responseList = new ArrayList<>();
		while(sc.hasNextLine()) {
			line = sc.nextLine().trim();
			responseList.add(line);
		}
		sc.close();
		int count = 0;
		ArrayList<String> pairSet = new ArrayList<>();
		for(int i = 0; i < responseList.size() - 1; i++) {
			for(int j = i + 1; j < responseList.size(); j++) {
				pairSet.add("\""+responseList.get(i)+"\"" + "\t" + "\""+responseList.get(j)+"\"");
			}
		}
		Random rnd = new Random();
		rnd.setSeed(10);
		int index;
		while(count < 500) {
			index = rnd.nextInt(pairSet.size());
			String[] tokens = pairSet.get(index).split("\t");
			writer.write(tokens[0] + "," + tokens[1]);
			writer.newLine();
			count++;
		}
		writer.close();
	}

}

package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class GeneratingPairs {

	@SuppressWarnings({ "unused", "unchecked" })
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Map<String, LinkedList<Response>> qAndAMap = (Map<String, LinkedList<Response>>) read("DAResponsesMap");
		BufferedWriter writer = new BufferedWriter(new FileWriter("DOB_test_Mar.csv"));
		writer.write("Responses");
		writer.newLine();
		for(Response r : qAndAMap.get("Thanks. XXX, please tell me your date of birth.")) {
			writer.write("\"" + r.toString() + "\"");
			writer.newLine();
		}
		writer.close();
		File f = new File("Thanks. XXX, please tell me your date of birth-ResponseList");
		writer = new BufferedWriter(new FileWriter("DOB_testpair_Mar.csv"));
		writer.write("Response One,Response Two");
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
				pairSet.add(responseList.get(i) + "\t" +responseList.get(j));
			}
		}
		Random rnd = new Random();
		rnd.setSeed(100);
		int index;
		while(count < 500) {
			index = rnd.nextInt(pairSet.size());
			String[] tokens = pairSet.get(index).split("\t");
			writer.write("\"" + tokens[0] + "\",\"" + tokens[1] + "\"");
			writer.newLine();
			count++;
		}
		writer.close();
	}
	
	public static Object read(String fileName) {
		try {
			Object obj = null;
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(fileName));
			obj = is.readObject();
			is.close();
			return obj;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}

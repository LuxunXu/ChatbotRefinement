package test;
import java.io.*;
import java.util.*;
import weka.core.Utils;


public class FileGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		/*
		File f = new File("DALog.txt");
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		BufferedWriter writer = new BufferedWriter(new FileWriter("DALogResponseOnly.txt"));
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split("\\t");
			writer.write(tokens[1].trim());
			writer.newLine();
		}*/
		
		for (int k = 0; k < 4; k++) {
			int fileNo = k+1;
			BufferedWriter writer = new BufferedWriter(new FileWriter("transformed_March" + fileNo + ".csv"));
			File f = new File("Thanks. XXX, please tell me your date of birth-ResponseList");
			Scanner sc = new Scanner(f);
			String line = "";
			ArrayList<String> res = new ArrayList<>();
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				res.add(line.trim());
			}
			f = new File("DOB_final.csv");
			sc = new Scanner(f);
			Map<String, String> validity = new HashMap<>();
			line = sc.nextLine();
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				String[] tokens = line.split("\\t");
				validity.put(tokens[0].substring(1, tokens[0].length()-1), tokens[1]);
			}
			f = new File("transformed" + fileNo + "");
			sc = new Scanner(f);
			writer.write("Response,Validity,Digit,Letter,Token,Length,Time,NodeCount,Sentiment,yesno,datetime,department,phone_number,");
			for(int i = 1; i <= 174; i++) {
				writer.write(i + ",");
			}
			writer.newLine();
			int j = 0;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				/*
				String[] tokens = line.split("\\s");
				//System.out.println(res.get(j) + " " + tokens.length);
				writer.write("\"" + res.get(j) + "\",");
				for(int i = 0; i < tokens.length; i++) {
					//System.out.print(tokens[i]);
					writer.write(tokens[i] + ",");
				}*/
				if (validity.containsKey(res.get(j))) {
					if (validity.get(res.get(j)).equals("valid")) {
						writer.write("\"" + res.get(j) + "\",1," + line);
					} else {
						writer.write("\"" + res.get(j) + "\",0," + line);
					}
				} else {
					System.out.println(res.get(j));
					writer.write("\"" + res.get(j) + "\",0," + line);
				}
				writer.newLine();
				j++;
			}
			writer.close();
		}
		
		
		// Build constraint matrix
		// Index 1, Index 2, Similarity(1, -1)
		/*
		BufferedWriter writer = new BufferedWriter(new FileWriter("constraints"));
		File f = new File("Thanks. XXX, please tell me your date of birth-ResponseList");
		Scanner sc = new Scanner(f);
		String line = "";
		ArrayList<String> res = new ArrayList<>();
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			res.add(line.trim());
		}
		f = new File("DOB_pair_final.csv");
		sc = new Scanner(f);
		line = sc.nextLine();
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			String one = tokens[0];
			String two = tokens[1];
			if (one.startsWith("\"")) {
				one = one.substring(1, one.length() - 1);
			}
			if (two.startsWith("\"")) {
				two = two.substring(1, two.length() - 1);
			}
			int index1 = res.indexOf(one);
			int index2 = res.indexOf(two);
			if (tokens[2].equals("similar")) {
				writer.write(index1 + " " + index2 + " " + 1);
			} else {
				writer.write(index1 + " " + index2 + " " + -1);
			}
			System.out.println(index1 + " " + index2);
			writer.newLine();
		}
		writer.close();*/
		
		// Clean stupid excel modified file
		/*
		BufferedWriter writer = new BufferedWriter(new FileWriter("DOB_pair_final.csv"));
		File f = new File("DOB_testpair_Mar.csv");
		Scanner sc = new Scanner(f);
		String line = "";
		ArrayList<String> res = new ArrayList<>();
		sc.nextLine();
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			res.add(line.trim());
		}
		f = new File("DOB_testpair_Mar.txt");
		sc = new Scanner(f);
		line = sc.nextLine();
		int count = 0;
		writer.write("Response one,Response two,Similarity");
		writer.newLine();
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split("\\t");
			writer.write(res.get(count) + "," + tokens[2]);
			writer.newLine();
			count++;
		}
		writer.close();*/
		/*
		for (int i = 0; i < 4; i++) {
			int num = i+1;
			BufferedWriter writer1 = new BufferedWriter(new FileWriter("constraints" + num));
			BufferedWriter writer2 = new BufferedWriter(new FileWriter("testset" + num));
			File f = new File("constraints");
			Scanner sc = new Scanner(f);
			String line = "";
			int cnt = 0;
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				if (!(i*125 <= cnt && cnt < (i+1)*125)) {
					writer1.write(line);
					writer1.newLine();
				}
				cnt++;
			}
			cnt = 0;
			f = new File("DOB_pair_final.csv");
			sc = new Scanner(f);
			sc.nextLine();
			while (sc.hasNextLine()) {
				line = sc.nextLine();
				if (i*125 <= cnt && cnt < (i+1)*125) {
					writer2.write(line);
					writer2.newLine();
				}
				cnt++;
			}
			writer1.close();
			writer2.close();
		}*/
	}
}

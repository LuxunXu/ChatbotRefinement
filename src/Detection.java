import java.io.*;
import java.util.*;

public class Detection {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		Map<Integer, Message> idMap = null;
		Map<String, LinkedList<Response>> qAndAMap = null;
		//idMap = processLog("DALog.txt");
		//store("DA", idMap);
		//idMap = (Map<Integer, Message>) read("DA");
		//tallyQAndA(idMap, "DAResponsesMap");
		qAndAMap = (Map<String, LinkedList<Response>>) read("DAResponsesMap");
		findOutliers(qAndAMap, 10);
		//Message test = idMap.get(1231);
		//System.out.println(test.toString());
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
	
	public static void store(String fileName, Object obj) {
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fileName));
			os.writeObject(obj);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Message> processLog(String fileName) throws FileNotFoundException {
		int countLine = 1;
		File f = new File(fileName);
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		int currentID = 0;
		Map<Integer, Message> idMap= new HashMap<Integer, Message>();
		LinkedList<String> qaPair = new LinkedList<String>();
		while(sc.hasNextLine()) {
			countLine++;
			//System.out.println(countLine);
			line = sc.nextLine();
			String[] tokens = line.split("\\t");
			int id = Integer.parseInt(tokens[0]);
			if (currentID != id) {
				currentID = id;
				idMap.put(id, new Message());
				qaPair.clear();
			}
			if (qaPair.size() < 2) {
				if (tokens[2].equals("0")) {
					qaPair.clear();
					qaPair.add(tokens[1].trim());
				} else if (tokens[2].equals("1") && !qaPair.isEmpty()) {
					qaPair.add(tokens[1].trim());
				}
			}
			if (qaPair.size() == 2) {
				if (!idMap.get(id).getMap().containsKey(qaPair.get(0))) {
					idMap.get(id).getMap().put(qaPair.get(0), new LinkedList<Response>());
					Response r = new Response(qaPair.get(1));
					idMap.get(id).getMap().get(qaPair.get(0)).add(r);
				} else {
					Response r = new Response(qaPair.get(1));
					idMap.get(id).getMap().get(qaPair.get(0)).add(r);
				}
				qaPair = new LinkedList<String>();
			}
		}
		return idMap;
	}
	
	public static void tallyQAndA(Map<Integer, Message> idMap, String fileName) {
		Map<String, LinkedList<Response>> overallMap = new HashMap<String, LinkedList<Response>>();
		for (int i : idMap.keySet()) {
			Message m = idMap.get(i);
			Map<String, LinkedList<Response>> qaMap = m.getMap();
			for (String q : qaMap.keySet()) {
				String temp = q;
				if (q.startsWith("Q1:") || q.startsWith("Q2:") || q.startsWith("Q3:")) {
					continue;
				}
				if (q.contains("Congrats")) {
					temp = "Congrats, you are done! Your code is XXX.";
				} else if (q.contains("Please select a time slot")) {
					temp = "Please select a time slot for XXXX-XX-XX.";
				} else if (q.contains("which department would you like to visit?")) {
					temp = "XXX, which department would you like to visit?";
				} else if (q.contains("please tell me your date of birth")) {
					temp = "Thanks. XXX, please tell me your date of birth.";
				} else if (q.contains("May I know your age")) {
					temp = "Good to see you, XXX. May I know your age?";
				}
				if (!overallMap.keySet().contains(temp)) {
					overallMap.put(temp, new LinkedList<Response>());
				}
				for (Response r : qaMap.get(q)) {
					overallMap.get(temp).add(r);
				}
			}
		}
		
		store(fileName, overallMap);
		
		String toPrint = "";
		for (String key : overallMap.keySet()) {
			toPrint += key + "\t" + overallMap.get(key).toString() + "\n";
		}
		System.out.println(toPrint);
	}
	
	public static void findOutliers(Map<String, LinkedList<Response>> qaMap, int cutOffPercentage) {
		/*
		String toPrint = "";
		for (String key : qaMap.keySet()) {
			toPrint += key + "\t" + qaMap.get(key).toString() + "\n";
		}
		System.out.println(toPrint);
		*/
		
		// For outliers with abnormal length
		System.out.println("Outliers by length:");
		for (String q : qaMap.keySet()) {
			//System.out.println(q);
			Set<Response> lengthOutliers = new HashSet<Response>();
			Set<Integer> badLength = new HashSet<Integer>();
			Map<Integer, Integer> countLength = new HashMap<Integer, Integer>();
			for (Response ans : qaMap.get(q)) {
				int length = ans.getTotalLength();
				if (!countLength.containsKey(length)) {
					countLength.put(length, 1);
				} else {
					countLength.put(length, countLength.get(length) + 1);
				}
			}
			//System.out.println(countLength);
			Set<Integer> counts = new TreeSet<Integer>();
			for (int i : countLength.keySet()) {
				counts.add(countLength.get(i));
			}
			//System.out.println(counts);
			int cnt = 0;
			//System.out.println("---------------" + counts.size()*cutOffPercentage/100);
			for (int i : counts) {
				if (cnt > counts.size()*cutOffPercentage/100) {
					break;
				}
				cnt++;
				badLength.add(i);
			}
			//System.out.println(badLength);
			for (Response ans : qaMap.get(q)) {
				if (badLength.contains(countLength.get(ans.getTotalLength()))) {
					lengthOutliers.add(ans);
				}
			}
			System.out.println(q + "\t" + lengthOutliers.toString());
		}
		
		// For outliers with abnormal number of tokens
		System.out.println();
		System.out.println("----------------------------------------------------------");
		System.out.println();
		System.out.println("Outliers by number of words:");
		for (String q : qaMap.keySet()) {
			//System.out.println(q);
			Set<Response> lengthOutliers = new HashSet<Response>();
			Set<Integer> badLength = new HashSet<Integer>();
			Map<Integer, Integer> countLength = new HashMap<Integer, Integer>();
			for (Response ans : qaMap.get(q)) {
				int length = ans.getNumberOfTokens();
				if (!countLength.containsKey(length)) {
					countLength.put(length, 1);
				} else {
					countLength.put(length, countLength.get(length) + 1);
				}
			}
			//System.out.println(countLength);
			Set<Integer> counts = new TreeSet<Integer>();
			for (int i : countLength.keySet()) {
				counts.add(countLength.get(i));
			}
			//System.out.println(counts);
			int cnt = 0;
			//System.out.println("---------------" + counts.size()*cutOffPercentage/100);
			for (int i : counts) {
				if (cnt > counts.size()*cutOffPercentage/100) {
					break;
				}
				cnt++;
				badLength.add(i);
			}
			//System.out.println(badLength);
			for (Response ans : qaMap.get(q)) {
				if (badLength.contains(countLength.get(ans.getNumberOfTokens()))) {
					lengthOutliers.add(ans);
				}
			}
			System.out.println(q + "\t" + lengthOutliers.toString());
		}
	}
}

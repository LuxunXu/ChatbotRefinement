import java.io.*;
import java.util.*;

public class Detection {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Map<Integer, Message> idMap = null;
		Map<String, LinkedList<Response>> qAndAMap = null;
		//idMap = processLog("DALog.txt");
		//store("DA", idMap);
		//idMap = (Map<Integer, Message>) read("DA");
		//tallyQAndA(idMap, "DAResponsesMap");
		qAndAMap = (Map<String, LinkedList<Response>>) read("DAResponsesMap");
		/*for (String q : qAndAMap.keySet()) {
			kmeans(q, qAndAMap.get(q), 5, 5000);
			break; // For testing only the first question
		}*/
		findOutliers(qAndAMap);
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
	
	// Return an SDFUserID Message map
	public static Map<Integer, Message> processLog(String fileName) throws IOException {
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
	
	// Combine all answer to one question, get rid of SDFUserID
	public static void tallyQAndA(Map<Integer, Message> idMap, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("DAResponsesMap.txt"));
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
					continue;
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
					//writer.write(r.toString());
					//writer.newLine();
				}
			}
		}
		//writer.close();
		store(fileName, overallMap);
		
		String toPrint = "";
		for (String key : overallMap.keySet()) {
			toPrint += key + "\t" + overallMap.get(key).toString() + "\n";
		}
		System.out.println(toPrint);
		writer.write(toPrint);
		writer.close();
	}
	
	// This method is out-dated and should not be used.
	public static void clusterByLength(Map<String, LinkedList<Response>> qaMap, int cutOffPercentage) {
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
		System.out.println("Clusters by number of words:");
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
	
	public static void kmeans(String q, LinkedList<Response> list, int k, int iteration) {
		assert k > 1;
		int randomInt;
		Random r = new Random();
		LinkedList<Response> l;
	
		for (int kCount = 2; kCount <= k; kCount++) {
			try {
				double minVariation = Double.MAX_VALUE;
				LinkedList<LinkedList<Response>> finalCluster = new LinkedList<>();
				for (int itr = 0; itr < iteration; itr++) {
					LinkedList<LinkedList<Response>> cluster = new LinkedList<>();
					double totalVariation = 0;
					l = new LinkedList<>(list);
					// Choose initial cluster
					for (int i = 0; i < kCount; i++) {
						//System.out.println(l.size());
						randomInt = r.nextInt(l.size());
						Response ini = l.get(randomInt);
						l.remove(ini);
						cluster.add(new LinkedList<>());
						cluster.get(i).add(new Response(i+"", ini.getTotalLength(), ini.getNumberOfTokens(), ini.getNumberOfDigits(), ini.getNumberOfLetters()));
						cluster.get(i).add(ini);
					}
					// Assign Cluster
					for (Response res : l) {
						int pos = 0;
						double minDist = Double.MAX_VALUE;
						for (int i = 0; i < kCount; i++) {
							double dist = res.eDistance(cluster.get(i).getFirst());
							if (dist < minDist) {
								pos = i;
								minDist = dist;
							}
						}
						cluster.get(pos).add(res);
						String s = pos+"";
						int a = (res.getTotalLength() + cluster.get(pos).getFirst().getTotalLength())/2;
						int b = (res.getNumberOfTokens() + cluster.get(pos).getFirst().getNumberOfTokens())/2;
						int c = (res.getNumberOfDigits() + cluster.get(pos).getFirst().getNumberOfDigits())/2;
						int d = (res.getNumberOfLetters() + cluster.get(pos).getFirst().getNumberOfLetters())/2;
						cluster.get(pos).set(0, new Response(s, a, b, c, d));
					}
					
					for (LinkedList<Response> cl : cluster) {
						Response mean = cl.getFirst();
						for (Response member : cl) {
							totalVariation += mean.eDistance(member);
						}
					}
					if (totalVariation < minVariation) {
						minVariation = totalVariation;
						finalCluster = cluster;
					}
				}
				System.out.println(q + "\t" + kCount + "\t" + minVariation);
		        for (LinkedList<Response> sets : finalCluster) {
		            System.out.println(sets.toString());
		        }
		        System.out.println();
		        
		        // Print inter-cluster distance
		        /*
		        double[][] interDistance = new double[kCount][kCount];
		        for (int i = 0; i < kCount; ++i) {
		        	for (int j = 0; j < kCount; ++j) {
			        	double dist = finalCluster.get(i).getFirst().eDistance(finalCluster.get(j).getFirst());
			        	interDistance[i][j] = dist;
			        }
		        }
		        
		        for (int row = 0; row < interDistance.length; row++) {
		        	String head = "";
	            	String data = row+"";
	            	double total = 0.0;
		            for (int col = 0; col < interDistance[row].length; col++) {
	            		head += "\t" + col;
		            	data += "\t" + interDistance[row][col];
		            	total += interDistance[row][col];
		            }
		            if (row == 0) {
		            	System.out.println(head + "\tTotal");
		            }
		            System.out.println(data + "\t" + total);
		        }*/
		        System.out.println();
			} catch (IllegalArgumentException e) {
				break;
			}
		}
		System.out.println();
		
	}
	
	public static void findOutliers(Map<String, LinkedList<Response>> qAndAMap) {
		Map<String, LinkedList<Response>> qaMap = new HashMap<>(qAndAMap);
		LinkedList<Response> l;
		for (String q : qaMap.keySet()) {
			Map<String, LinkedList<Response>> entityMap = new HashMap<>();
			l = new LinkedList<>(qaMap.get(q));
			for (Response r : l) {
				Set<String> entity;
				String entityName = "No Entity";
				if (r.getJSON().getJSONObject("entities").length() > 0) {
					entity = r.getJSON().getJSONObject("entities").keySet();
					for (String e : entity) {
						if (r.getJSON().getJSONObject("entities").getJSONArray(e).getJSONObject(0).getDouble("confidence") > 0.9) {
							entityName = e;
							break;
						}
					}
				}
				if (!entityMap.containsKey(entityName)) {
					entityMap.put(entityName, new LinkedList<Response>());
				}
				entityMap.get(entityName).add(r);
			}
			
			System.out.println(q);
			for (String s : entityMap.keySet()) {
				System.out.println(s + "\t" + entityMap.get(s));
			}
			System.out.println();
			
			LinkedList<Response> outliersList = new LinkedList<Response>();
			int maxSize = Integer.MIN_VALUE;
			String longest = "";
			for (String s : entityMap.keySet()) {
				if (entityMap.get(s).size() > maxSize) {
					maxSize = entityMap.get(s).size();
					longest = s;
				}
			}
			for (String s : entityMap.keySet()) {
				if (q.contains("name")) {
					outliersList.addAll(entityMap.get(s));
					continue;
				}
				if (!s.equals(longest)) {
					outliersList.addAll(entityMap.get(s));
				}
			}
			System.out.println("Outlier List:\t" + outliersList + "\n");
			
			System.out.println("Possible patterns:\t");
			findPatterns(outliersList);
			System.out.println();
			System.out.println();
			//kmeans(q, outliersList, 5, 5000);
		}
	}
	
	public static void findPatterns(LinkedList<Response> response) {
		ResponseVector rv = new ResponseVector(response);
		Set<String> seen = new HashSet<>();
		LinkedList<Set<String>> group = new LinkedList<>();
		boolean found = false;
		for (String r1 : rv.getVectorMap().keySet()) {
			found = false;
			for (Set<String> g : group) {
				for (String s : g) {
					if (!r1.equals(s) && rv.biCosineSimilarity(r1, s) != 0) {
						g.add(r1);
						found = true;
						break;
					}
				}
			}
			if (found == true) {
				seen.add(r1);
				continue;
			} else {
				for (String r2 : rv.getVectorMap().keySet()) {
					if (!r1.equals(r2) && rv.biCosineSimilarity(r1, r2) != 0 && !seen.contains(r2) && !seen.contains(r1)) {
						group.add(new HashSet<>());
						group.getLast().add(r1);
						group.getLast().add(r2);
						seen.add(r1);
						seen.add(r2);
						//System.out.println(r1.toString() + " --- " + r2.toString() + "\t" + rv.biCosineSimilarity(r1, r2));
					}
				}
			}
		}
		for (Set<String> g : group) {
			System.out.println(g);
		}
	}
}

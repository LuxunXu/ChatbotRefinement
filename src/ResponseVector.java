import java.io.Serializable;
import java.util.*;

public class ResponseVector implements Serializable{
	
	private Map<Response, ArrayList<Integer>> vectorMap;
	private ArrayList<String> vocabList;
	
	private Map<Response, ArrayList<Integer>> bigramVectorMap;
	private ArrayList<String> bigramVocabList;
	
	public ResponseVector(LinkedList<Response> responseList) {
		Set<String> vocab = new HashSet<>();
		for (Response r : responseList) {
			String[] tokens = r.getResponse().split("\\s");
			for (String token : tokens) {
				vocab.add(token);
			}
		}
		this.vocabList = new ArrayList<>(vocab);
		for (Response r : responseList) {
			if (!vectorMap.containsKey(r)) {
				vectorMap.put(r, new ArrayList<>());
				for (int i = 0; i < vocabList.size(); ++i) {
					vectorMap.get(r).add(0);
				}
				String[] tokens = r.getResponse().split("\\s");
				for (String token : tokens) {
					int index = vocabList.indexOf(token);
					int count = vectorMap.get(r).get(index);
					vectorMap.get(r).set(index, ++count);
				}
			}
		}
	}
	
	public ArrayList<Integer> getVector(Response response) {
		return vectorMap.get(response);
	}
	
	public double cosineSimilarity(Response response1, Response response2) {
		if (vectorMap.containsKey(response1) && vectorMap.containsKey(response2)) {
			if (response1.equals(response2)) {
				return 1.0;
			}
			double dotProduct = 0.0;
		    double normA = 0.0;
		    double normB = 0.0;
		    ArrayList<Integer> vector1 = vectorMap.get(response1);
		    ArrayList<Integer> vector2 = vectorMap.get(response2);
		    for (int i = 0; i < vector1.size(); i++) {
		        dotProduct += vector1.get(i) * vector2.get(i);
		        normA += Math.pow(vector1.get(i), 2);
		        normB += Math.pow(vector2.get(i), 2);
		    }   
		    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
		} else {
			return 0.0;
		}
	}
}

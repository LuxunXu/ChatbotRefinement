package test;
import java.io.Serializable;
import java.util.*;

public class ResponseVector implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5320523563109571338L;
	private Map<String, ArrayList<Integer>> vectorMap;
	private ArrayList<String> vocabList;
	
	private Map<String, ArrayList<Integer>> bigramVectorMap;
	private ArrayList<String> bigramVocabList;
	
	public ResponseVector(LinkedList<Response> responseList) {
		this.vectorMap = new HashMap<String, ArrayList<Integer>>();
		Set<String> vocab = new HashSet<>();
		for (Response r : responseList) {
			String[] tokens = r.toString().split("\\W");
			for (String token : tokens) {
				vocab.add(token);
			}
		}
		this.vocabList = new ArrayList<>(vocab);
		for (Response r : responseList) {
			if (!this.vectorMap.containsKey(r.toString())) {
				this.vectorMap.put(r.toString(), new ArrayList<>());
				for (int i = 0; i < vocabList.size(); ++i) {
					this.vectorMap.get(r.toString()).add(0);
				}
				String[] tokens = r.toString().split("\\W");
				for (String token : tokens) {
					int index = this.vocabList.indexOf(token);
					int count = this.vectorMap.get(r.toString()).get(index);
					this.vectorMap.get(r.toString()).set(index, ++count);
				}
			}
		}
		
		this.bigramVectorMap = new HashMap<String, ArrayList<Integer>>();
		Set<String> bigramVocab = new HashSet<>();
		for (Response r : responseList) {
			String[] tokens = r.toString().split("\\W");
			for (int i = 0; i < tokens.length - 1; i++) {
				bigramVocab.add(tokens[i] + " " + tokens[i+1]);
			}
		}
		this.bigramVocabList = new ArrayList<>(bigramVocab);
		for (Response r : responseList) {
			String[] tokens = r.toString().split("\\W");
			if (!this.bigramVectorMap.containsKey(r.toString()) && tokens.length > 1) {
				this.bigramVectorMap.put(r.toString(), new ArrayList<>());
				for (int i = 0; i < bigramVocabList.size(); ++i) {
					this.bigramVectorMap.get(r.toString()).add(0);
				}
				for (int i = 0; i < tokens.length - 1; i++) {
					int index = this.bigramVocabList.indexOf(tokens[i] + " " + tokens[i+1]);
					int count = this.bigramVectorMap.get(r.toString()).get(index);
					this.bigramVectorMap.get(r.toString()).set(index, ++count);
				}
			}
		}
	}
	
	public ArrayList<Integer> getVector(String response) {
		return this.vectorMap.get(response);
	}
	
	public Map<String, ArrayList<Integer>> getVectorMap() {
		return this.vectorMap;
	}
	
	public double cosineSimilarity(String response1, String response2) {
		if (this.vectorMap.containsKey(response1) && this.vectorMap.containsKey(response2)) {
			if (response1.equals(response2)) {
				return 1.0;
			}
			double dotProduct = 0.0;
		    double normA = 0.0;
		    double normB = 0.0;
		    ArrayList<Integer> vector1 = this.vectorMap.get(response1);
		    ArrayList<Integer> vector2 = this.vectorMap.get(response2);
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
	
	public double biCosineSimilarity(String response1, String response2) {
		if (this.bigramVectorMap.containsKey(response1) && this.bigramVectorMap.containsKey(response2)) {
			if (response1.equals(response2)) {
				return 1.0;
			}
			double dotProduct = 0.0;
		    double normA = 0.0;
		    double normB = 0.0;
		    ArrayList<Integer> vector1 = this.bigramVectorMap.get(response1);
		    ArrayList<Integer> vector2 = this.bigramVectorMap.get(response2);
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

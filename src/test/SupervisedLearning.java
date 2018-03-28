package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import weka.classifiers.bayes.*;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.clusterers.SimpleKMeans;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.unsupervised.attribute.NumericToNominal;

import org.json.JSONObject;

public class SupervisedLearning {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//createData();
		
//		naiveBayesClassify();
//		System.out.println();
//		rfClassify();
		//kMeansCluster("beforeDistanceLearnedData.csv", "first, last");
		//kMeansCluster("distanceLearnedData.csv", "first, last");
		
		//Response,Validity,Digit,Letter,Token,Length,Time,NodeCount,Sentiment,yesno,datetime,department,phone_number
		String option = "first, 3-13";
		ArrayList<Double> error = new ArrayList<>();
		int offset = 4;
		for (int k = offset; k <= 10; k++) {
			String dataSource = "transformed_March.csv";
			SimpleKMeans clusterer = kMeansCluster(dataSource, option, k+"");
			error.add(clusterer.getSquaredError());
		}
		double x1 = offset; double x2 = offset + error.size() - 1; double y1 = error.get(0); double y2 = error.get(error.size()-1);
		double maxDist = Double.MIN_VALUE;
		int nCluster = -1;
		for (int i = 0; i < error.size(); i++) {
			double x0 = i + offset;
			double y0 = error.get(i);
			double dist = Math.abs((y2-y1)*x0-(x2-x1)*y0+x2*y1-y2*x1)/Math.sqrt(Math.pow((y2-y1), 2)+Math.pow((x2-x1), 2));
			//System.out.println(dist);
			if (dist > maxDist) {
				maxDist = dist;
				nCluster = i + offset;
			}
		}
		
		System.out.println(nCluster);
		
		if(nCluster < 0) {
			System.out.println("Number of cluster is wrong.");
			return;
		}
		int correctD = 0;
		int correctS = 0;
		int wrong = 0;
		for (int i = 0; i < 4; i++) {
			int fileNo = i+1;
			String dataSource = "transformed_March" + fileNo + ".csv";
			String testFile = "testset" + fileNo +"";
			int[] acc = cv(getCluster(dataSource, kMeansCluster(dataSource, option, nCluster+"")), testFile);
			correctD += acc[0];
			correctS += acc[1];
			wrong += acc[2];
		}
		System.out.println("Number of Cluster: " + nCluster);
//		System.out.println("Correct: " + correct);
//		System.out.println("Wrong: " + wrong);
//		System.out.println("Accuracy: " + 1.0*correct/(correct+wrong));
		System.out.println("Weighted accuracy(2): " + 1.0*(correctD + 2*correctS)/(correctD + 2*correctS + wrong));
		System.out.println();
	}
	
	public static void naiveBayesClassify() throws Exception {
		NaiveBayes nbClassifier = new NaiveBayes();
		DataSource source = new DataSource("transformed_March.csv");
		Instances data = source.getDataSet();
		RemoveByName remove = new RemoveByName();
		String[] options = new String[2];
		options[0] = "-E";
		options[1] = "Response";
		remove.setOptions(options);
		remove.setInputFormat(data); 
		Instances newData1 = Filter.useFilter(data, remove);
		NumericToNominal convert = new NumericToNominal();
		options[0] = "-R";
		options[1] = "first";
		convert.setOptions(options);
		convert.setInputFormat(newData1);
		Instances newData = Filter.useFilter(newData1, convert);
		//System.out.println(newData.toSummaryString());
		newData.setClassIndex(0);
		int folds = 10;
		Random rand = new Random();
		Instances randData = new Instances(newData);
		randData.randomize(rand);
		Evaluation eval = new Evaluation(randData);
		for (int n = 0; n < folds; n++) {
		   Instances train = randData.trainCV(folds, n);
		   Instances test = randData.testCV(folds, n);
		   nbClassifier.buildClassifier(train);
		   eval.evaluateModel(nbClassifier, test);
		}
		//System.out.println(nbClassifier.toString());
		System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
	}
	
	public static void rfClassify() throws Exception {
		RandomForest rfClassifier = new RandomForest();
		DataSource source = new DataSource("transformed_March.csv");
		Instances data = source.getDataSet();
		RemoveByName remove = new RemoveByName();
		String[] options = new String[2];
		options[0] = "-E";
		options[1] = "Response";
		remove.setOptions(options);
		remove.setInputFormat(data); 
		Instances newData1 = Filter.useFilter(data, remove);
		NumericToNominal convert = new NumericToNominal();
		options[0] = "-R";
		options[1] = "first";
		convert.setOptions(options);
		convert.setInputFormat(newData1);
		Instances newData = Filter.useFilter(newData1, convert);
		//System.out.println(newData.toSummaryString());
		newData.setClassIndex(0);
		int folds = 10;
		Random rand = new Random();
		Instances randData = new Instances(newData);
		randData.randomize(rand);
		Evaluation eval = new Evaluation(randData);
		for (int n = 0; n < folds; n++) {
		   Instances train = randData.trainCV(folds, n);
		   Instances test = randData.testCV(folds, n);
		   rfClassifier.buildClassifier(train);
		   eval.evaluateModel(rfClassifier, test);
		}
		//System.out.println(nbClassifier.toString());
		System.out.println(eval.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
	}
	
	public static String getVector(String line) throws IOException {
		String toPrint = "";
		String[] tokens = line.split(",");
		Response r = new Response(tokens[1], 5, 1);
		JSONObject j = r.getJSON();
		toPrint += "\"" + tokens[1] + "\"," + r.getTotalLength() + "," + r.getNumberOfTokens() + "," + r.getNumberOfLetters() + "," + r.getNumberOfDigits() + ",";
		if (j.has("entities") && j.getJSONObject("entities").has("datetime")) {
			toPrint += j.getJSONObject("entities").getJSONArray("datetime").getJSONObject(0).getDouble("confidence") + ",";
		} else {
			toPrint += 0 + ",";
		}
		if (j.has("entities") && j.getJSONObject("entities").has("phone_number")) {
			toPrint += j.getJSONObject("entities").getJSONArray("phone_number").getJSONObject(0).getDouble("confidence") + ",";
		} else {
			toPrint += 0 + ",";
		}
		toPrint +="\"" + tokens[8] + "\"";
		return toPrint;
	}
	
	public static Instance newInstance(String response) throws IOException {
		Instance test = new DenseInstance(6);
		Response r = new Response(response, 5, 1);
		JSONObject j = r.getJSON();
		test.setValue(0, r.getTotalLength());
		test.setValue(1, r.getNumberOfTokens());
		test.setValue(2, r.getNumberOfLetters());
		test.setValue(3, r.getNumberOfDigits());
		if (j.has("entities") && j.getJSONObject("entities").has("datetime")) {
			test.setValue(4, j.getJSONObject("entities").getJSONArray("datetime").getJSONObject(0).getDouble("confidence"));
		} else {
			test.setValue(4, 0);
		}
		if (j.has("entities") && j.getJSONObject("entities").has("phone_number")) {
			test.setValue(4, j.getJSONObject("entities").getJSONArray("phone_number").getJSONObject(0).getDouble("confidence"));
		} else {
			test.setValue(4, 0);
		}
		return test;
	}
	
	public static SimpleKMeans kMeansCluster(String dataSource, String option, String nCluster) throws Exception {
		DataSource source = new DataSource(dataSource);
		Instances data = source.getDataSet();
		Remove remove = new Remove();
		String[] options = new String[2];
		options[0] = "-R";
		options[1] = option;
		remove.setOptions(options);
		remove.setInputFormat(data);
		Instances newData = Filter.useFilter(data, remove);
		//System.out.println(newData.numAttributes() + "\n");
		options = new String[5];
		options[0] = "-N";                 // no. of clusters
		options[1] = nCluster;
		options[2] = "-I";				  // max no. of iterations
		options[3] = "10000";
		options[4] = "-O";
		SimpleKMeans clusterer = new SimpleKMeans();   // new instance of clusterer
		clusterer.setOptions(options);     // set the options
		clusterer.buildClusterer(newData);    // build the clusterer
//		Instance test = newInstance("asfasd");
//		test.setDataset(newData);
		//System.out.println(clusterer.toString());
		return clusterer;
	}
	
	public static void clusterLabel(Map<Integer, ArrayList<String>> cluster, String fileName) throws FileNotFoundException {
		Set<String> validSet = new HashSet<>();
		Set<String> invalidSet = new HashSet<>();
		File f = new File(fileName);
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		Map<String, ArrayList<String>> label = new HashMap<>();
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split(",");
			String response = Utils.quote(tokens[1]);
			if(tokens[8].equals("Valid")) {
				validSet.add(response);
			} else {
				invalidSet.add(response);
			}
		}
		int validID = 0;
		int invalidID = 0;
		for(int a : cluster.keySet()) {
			int countValid = 0;
			int countInvalid = 0;
			String responses = "";
			for(String r : cluster.get(a)) {
				responses += r + ", ";
				if(validSet.contains(r)) {
					countValid++;
				} else if(invalidSet.contains(r)){
					countInvalid++;
				}
			}
			if (countValid > countInvalid) {
				validID++;
				System.out.println("Valid cluster " + validID + ": " + responses);
			} else {
				invalidID++;
				System.out.println("Invalid cluster " + invalidID + ": " + responses);
			}
		}
	}
	
	public static void evalCluster(Map<Integer, ArrayList<String>> cluster, String fileName) throws FileNotFoundException {
		File g = new File(fileName);
		Scanner sc = new Scanner(g);
		String line = sc.nextLine();
		int correctSimilar = 0;
		int wrongSimilar = 0;
		int correctDissimilar = 0;
		int wrongDissimilar = 0;
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split(",");
			String one = Utils.quote(tokens[1]);
			String two = Utils.quote(tokens[2]);
			String isSimilar = tokens[9];
			int oneCluster = -1;
			int twoCluster = -1;
			for(int a : cluster.keySet()) {
				if(cluster.get(a).contains(one)) {
					oneCluster = a;
				}
				if(cluster.get(a).contains(two)) {
					twoCluster = a;
				}
			}
			if(oneCluster != -1 && twoCluster != -1) {
				if(isSimilar.equals("Similar") && oneCluster == twoCluster) {
					correctSimilar++;
				} else if(isSimilar.equals("Dissimilar") && oneCluster != twoCluster) {
					correctDissimilar++;
				} else if (isSimilar.equals("Similar") && oneCluster != twoCluster){
					//System.out.println(one + " " + two + " " + isSimilar);
					wrongSimilar++;
				} else {
					wrongDissimilar++;
				}
			}
		}
		sc.close();
		System.out.println();
		System.out.println("Correct Similar: " + correctSimilar);
		System.out.println("Wrong Similar: " + wrongSimilar);
		System.out.println("Correct Dissimilar: " + correctDissimilar);
		System.out.println("Wrong Dissimilar: " + wrongDissimilar);
	}
	
	//331 dissimilar 169 similar
	public static int[] cv(Map<Integer, ArrayList<String>> cluster, String fileName) throws FileNotFoundException {
		File g = new File(fileName);
		int[] acc = new int[3];
		Scanner sc = new Scanner(g);
		String line = "";
		int rightS = 0;
		int rightD = 0;
		int wrong = 0;
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			String one = Utils.quote(tokens[0].substring(1, tokens[0].length()-1));
			String two = Utils.quote(tokens[1].substring(1, tokens[1].length()-1));
			int first = -1;
			int second = -1;
			for (int i : cluster.keySet()) {
				if (cluster.get(i).contains(one)) {
					first = i;
				}
				if (cluster.get(i).contains(two)) {
					second = i;
				}
			}
			if (first == -1 || second == -1) {
				System.out.println(one + " " + two);
			} else {
				if (first == second && tokens[2].equals("similar")) {
					rightS++;
				} else if (first != second && tokens[2].equals("dissimilar")) {
					rightD++;
				} else {
					//System.out.println(line);
					wrong++;
				}
			}
		}
		acc[0] = rightD;
		acc[1] = rightS;
		acc[2] = wrong;
		return acc;
	}
	
	public static Map<Integer, ArrayList<String>> getCluster (String dataSource, SimpleKMeans clusterer) throws Exception {
		DataSource source = new DataSource(dataSource);
		Instances data = source.getDataSet();
		int[] assignments = clusterer.getAssignments();
		Map<Integer, ArrayList<String>> cluster = new HashMap<>();
		for(int i = 0; i < assignments.length; i++) {
			int clusterNum = assignments[i];
			if(!cluster.containsKey(clusterNum)) {
				cluster.put(clusterNum, new ArrayList<>());
			}
			cluster.get(clusterNum).add(data.get(i).toString(0));
		}
		
		for(int a : cluster.keySet()) {
			System.out.println(cluster.get(a).toString());
		}
		System.out.println();
		//clusterLabel(cluster, "DOB_Cleaned.csv");
		//evalCluster(cluster, "DOB_Similarity_Cleaned.csv");
		return cluster;
	}
}

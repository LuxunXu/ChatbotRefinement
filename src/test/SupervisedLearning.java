package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import weka.classifiers.bayes.*;
import weka.clusterers.SimpleKMeans;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;

import org.json.JSONObject;

public class SupervisedLearning {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//createData();
		
		//naiveBayesClassify();
		kMeansCluster();
	}
	
	public static void createData() throws IOException {
		File f = new File("DOB_Cleaned.csv");
		File g = new File("DOB_Similarity_Cleaned.csv");
		BufferedWriter writer = new BufferedWriter(new FileWriter("ResponseVector.csv"));
		writer.write("Response,Length,Number of Tokens,Number of Letter,Number of Digits,datetime,phone_number,isValid");
		writer.newLine();
		
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			writer.write(getVector(line));
			writer.newLine();
		}
		writer.close();
	}
	
	public static void naiveBayesClassify() throws Exception {
		NaiveBayes nbClassifier = new NaiveBayes();
		DataSource source = new DataSource("DOB_test.arff");
		Instances data = source.getDataSet();
		if (data.classIndex() == -1) {
			   data.setClassIndex(data.numAttributes() - 1);
		}
		nbClassifier.buildClassifier(data);
		//System.out.println(nbClassifier.toString());
		
		Instance test = newInstance("214132");
		test.setDataset(data);
		System.out.println(data.classAttribute().value((int) nbClassifier.classifyInstance(test)));
	}
	
	public static String getVector(String line) throws IOException {
		String toPrint = "";
		String[] tokens = line.split(",");
		Response r = new Response(tokens[1]);
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
		Response r = new Response(response);
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
	
	public static void kMeansCluster() throws Exception {
		DataSource source = new DataSource("DOB_test_cluster.arff");
		Instances data = source.getDataSet();
		RemoveByName remove = new RemoveByName();
		String[] options = new String[2];
		options[0] = "-E";
		options[1] = "Response";
		remove.setOptions(options);
		remove.setInputFormat(data); 
		Instances newData = Filter.useFilter(data, remove);
		options = new String[5];
		options[0] = "-N";                 // max. iterations
		options[1] = "6";
		options[2] = "-I";
		options[3] = "5000";
		options[4] = "-O";
		SimpleKMeans clusterer = new SimpleKMeans();   // new instance of clusterer
		clusterer.setOptions(options);     // set the options
		clusterer.buildClusterer(newData);    // build the clusterer
		Instance test = newInstance("asfasd");
		test.setDataset(newData);
		//System.out.println(clusterer.toString());
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
	}
}

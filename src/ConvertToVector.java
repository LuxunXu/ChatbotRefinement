import java.io.*;
import java.util.*;

public class ConvertToVector {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		Map<String, LinkedList<Response>> qAndAMap = null;
		qAndAMap = (Map<String, LinkedList<Response>>) read("DAResponsesMap");
		BufferedWriter writer = new BufferedWriter(new FileWriter("testVector"));
		BufferedWriter writer1 = new BufferedWriter(new FileWriter("responseList"));
		LinkedList<Response> l = null;
		ResponseVector rv = null;
		int count = 0;
		for (String s : qAndAMap.keySet()) {
			writer = new BufferedWriter(new FileWriter(s + "Vector"));
			writer1 = new BufferedWriter(new FileWriter(s + "responseList"));
			l = qAndAMap.get(s);
			System.out.println(s);
			count = 0;
			rv = new ResponseVector(l);
			for (Response r : l) {
				//System.out.println(rv.getVector(r.toString()));
				System.out.println(count + "\t" + r.toString());
				writer1.write(r.toString() + "\n");
				count++;
				for (Integer i : rv.getVector(r.toString())) {
					writer.write(i + " ");
				}
				writer.write(r.getNumberOfDigits() + " " + r.getNumberOfLetters() + " " + r.getNumberOfTokens() + " " + r.getTotalLength() + "\n");
			}
			//break;
			writer.close();
			writer1.close();
		}
		//System.out.println(count);
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

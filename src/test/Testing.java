package test;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.*;

public class Testing {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String response = "23-9-1995".replaceAll("\\s", "%20");
		JSONObject test = getJObject(response);
		//System.out.println(test.toString());
		//System.out.println(test.getJSONObject("entities").getJSONArray("yesno").getJSONObject(0).getDouble("confidence"));
		
		/*
		Map<String, LinkedList<Response>> qAndAMap = null;
		qAndAMap = (Map<String, LinkedList<Response>>) read("DAResponsesMap");
		ResponseVector rv = null;
		for (String s : qAndAMap.keySet()) {
			System.out.println(s);
			rv = new ResponseVector(qAndAMap.get(s));
			Set<String> seen = new HashSet<>();
			for (String r1 : rv.getVectorMap().keySet()) {
				for (String r2 : rv.getVectorMap().keySet()) {
					if (!r1.equals(r2) && rv.biCosineSimilarity(r1, r2) != 0 && !seen.contains(r2)) {
						System.out.println(r1.toString() + " --- " + r2.toString() + "\t" + rv.biCosineSimilarity(r1, r2));
					}
				}
				seen.add(r1);
			}
			System.out.println();
		}*/
	}
	
	public static JSONObject getJObject(String response) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		//System.out.println(dateFormat.format(date));
		String link = "https://api.wit.ai/message?v=" + dateFormat.format(date) + "&q=" + response;
		URL thePage = new URL(link);
        URLConnection yc = thePage.openConnection();
        yc.setRequestProperty("Authorization", "Bearer 7ECLPXMO67MDIUGVFETO3GP26WB3NMIH");
        //System.out.println(yc.getContentType());
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream(), "utf-8"));
        String inputLine;
        String output = "";
        while ((inputLine = in.readLine()) != null) {
        	output += inputLine + "\n";
        }
        JSONObject get = new JSONObject(output);
        in.close();
        System.out.println(output);
		return get;
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

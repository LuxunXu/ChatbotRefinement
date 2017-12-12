import java.io.Serializable;
import java.util.*;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 874087594914412994L;
	public Map<String, LinkedList<String>> qAndAMap;
	
	public Message() {
		qAndAMap = new LinkedHashMap<String, LinkedList<String>>();
	}
	
	public Map<String, LinkedList<String>> getMap() {
		return qAndAMap;
	}
	
	public String toString() {
		String toPrint = "";
		for (String key : qAndAMap.keySet()) {
			toPrint += key + "\t" + qAndAMap.get(key).toString() + "\n";
		}
		return toPrint;
	}
}

import java.io.Serializable;
import java.util.*;

public class Message implements Serializable{
	public Map<String, LinkedList<String>> qAndAMap;
	
	public Message() {
		qAndAMap = new HashMap<String, LinkedList<String>>();
	}
	
	public Map<String, LinkedList<String>> getMap() {
		return qAndAMap;
	}
}

import java.io.*;
import java.util.*;

public class Detection {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {
		Map<Integer, Message> idMap = null;
		//idMap = process("DAFailTable.txt");
		//store("DA", idMap);
		idMap = (Map<Integer, Message>) read("DA");
		Message test = idMap.get(1156);
		System.out.println(test.toString());
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
	
	public static Map<Integer, Message> process(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		int currentID = 0;
		Map<Integer, Message> idMap= new HashMap<Integer, Message>();
		LinkedList<String> qaPair = new LinkedList<String>();
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split("\\t");
			int id = Integer.parseInt(tokens[0]);
			if (currentID != id) {
				currentID = id;
				idMap.put(id, new Message());
			}
			if (qaPair.size() < 2) {
				if (Integer.parseInt(tokens[2]) == 0) {
					qaPair.clear();
					qaPair.add(tokens[1].trim());
				} else if (Integer.parseInt(tokens[2]) == 1 && !qaPair.isEmpty()) {
					qaPair.add(tokens[1].trim());
				}
			}
			if (qaPair.size() == 2) {
				if (!idMap.get(id).getMap().containsKey(qaPair.get(0))) {
					idMap.get(id).getMap().put(qaPair.get(0), new LinkedList<String>());
					idMap.get(id).getMap().get(qaPair.get(0)).add(qaPair.get(1));
				} else {
					idMap.get(id).getMap().get(qaPair.get(0)).add(qaPair.get(1));
				}
				qaPair = new LinkedList<String>();
			}
		}
		return idMap;
	}
}

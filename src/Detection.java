import java.io.*;
import java.util.*;

public class Detection {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File f = new File("DAFailTable.txt");
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
			if (qaPair.size() == 2) {
				if (!idMap.get(id).getMap().containsKey(qaPair.get(0))) {
					idMap.get(id).getMap().put(qaPair.get(0), new LinkedList<String>());
					idMap.get(id).getMap().get(qaPair.get(0)).add(qaPair.get(1));
				} else {
					idMap.get(id).getMap().get(qaPair.get(0)).add(qaPair.get(1));
				}
				qaPair = new LinkedList<String>();
			} else {
				if (Integer.parseInt(tokens[2]) == 0) {
					qaPair.add(0, tokens[1].trim());
				} else if (Integer.parseInt(tokens[2]) == 1 && !qaPair.isEmpty()) {
					qaPair.add(1, tokens[1].trim());
				}
			}
		}
		
		//store("DA", idMap);
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
}

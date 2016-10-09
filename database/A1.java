import java.util.*;
import java.io.*;

public class A1 {

	public static void main(String []args) {
		File file = new File("Stockmarket-1990-2015.txt");
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e){
			System.out.println("Error: Unable to open file");
			System.exit(1);
		}

		HashMap<String, Company> compList = setData(scan);
		for (String key : compList.keySet()){
			calcCrazyDay(compList.get(key));


		}


		Company comp = compList.get("AAPL");
		System.out.println(comp.crazyDays.size());
		for (String k : comp.crazyDays.keySet()){
			System.out.print("Date: " + k);
			System.out.print(" Percent: " + comp.crazyDays.get(k) + "\n");
		}


	}

	public static HashMap<String, Company> setData(Scanner scan) {
		HashMap<String, Company> compList = new HashMap<String, Company>();
		while(scan.hasNextLine()) {
			Scanner line = new Scanner(scan.nextLine());
			String name = line.next();

			if (!compList.containsKey(name)) {
				compList.put(name, new Company(name));
			}

			Company comp = compList.get(name);
			comp.setDate(line.next());
			comp.setOP(line.nextDouble());
			comp.setHP(line.nextDouble());
			comp.setLP(line.nextDouble());
			comp.setCP(line.nextDouble());
			comp.setShare(line.nextInt());
			comp.setAP(line.nextDouble());
		}

		return compList;	
	}

	public static void calcCrazyDay(Company comp){
		for (int i = 0; i < comp.highPrice.size(); i++) {
			double hp = comp.getHP(i);
			double lp = comp.getLP(i);
			double p = (hp - lp) / hp; 
			if (p >= 0.15) {
				comp.setCD(comp.getDate(i), p);
			}
		}
	}

} 
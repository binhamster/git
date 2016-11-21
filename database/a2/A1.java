import java.util.*;
import java.io.*;
import java.lang.*;
import java.swl.*;

public class A1 {

	public static void main(String []args) {
		/* Create file scanner */
		File file = new File("Stockmarket-1990-2015.txt");
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e){
			System.out.println("Error: Unable to open file");
			System.exit(1);
		}

		/* Create mapping of companies */
		HashMap<String, Company> compList = setData(scan);
		for (String key : compList.keySet()){
			calcCrazyDay(compList.get(key));
			calcStockSplit(compList.get(key));
		}
		printStuff(compList);

	}

	public static HashMap<String, Company> setData(Scanner scan) {
		HashMap<String, Company> compList = new HashMap<String, Company>();
		while(scan.hasNextLine()) {
			Scanner line = new Scanner(scan.nextLine());
			String name = line.next();

			if (!compList.containsKey(name)) {
				compList.put(name, new Company(name));
			}
			/* Set data */
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
		/* Set crazy days */
		for (int i = 0; i < comp.highPrice.size(); i++) {
			double hp = comp.getHP(i);
			double lp = comp.getLP(i);
			double p = (hp - lp) / hp; 
			if (p >= 0.15) {
				comp.setCD(comp.getDate(i), p);
			}
		}

		/* Find craziest day */
		double n = 0;
		String d = "";
		HashMap<String, Double> crazyDays = comp.crazyDays;
		for (String key : crazyDays.keySet()){
			double temp = crazyDays.get(key);
			if (temp > n){
				n = temp;
				d = key;
			}
		}
		comp.setCraziest(d, n);
	}

	public static void calcStockSplit(Company comp){
		for (int i = 0; i < comp.getNumDays(); i++){
			if ((i + 1) != comp.getNumDays()){
				/* Walk through data */
				double op = comp.getOP(i);
				double cp = comp.getCP(i + 1);
				double a = Math.abs((cp / op) - 2.0);
				double b = Math.abs((cp / op) - 3.0);
				double c = Math.abs((cp / op) - 1.5);

				if (a < 0.05)
					comp.setSplit("2-1", comp.getDate(i + 1));
				if (b < 0.05)
					comp.setSplit("3-1", comp.getDate(i + 1));
				if (c < 0.05)
					comp.setSplit("3-2", comp.getDate(i + 1));
			}
		}
	}

	public static void printStuff(HashMap<String, Company> compList){
		for (String key : compList.keySet()){

			Company comp = compList.get(key);
			System.out.println("\nCompany: " + key + "\n====================");

			for (String day : comp.crazyDays.keySet()){
				double n = Math.round(comp.crazyDays.get(day) * 10000.0) / 100.0;
				System.out.print("Crazy Day: " + day);
				System.out.print(" " + n + "%\n");
			}
			if (comp.crazyDays.size() > 0) {
				double n = Math.round(comp.highestPerc * 10000.0) / 100.0;
				System.out.println("Total Crazy Days: " + comp.crazyDays.size());
				System.out.println("The craziest day: " + comp.craziestDay + " " + n + "%");
			}

			if (comp.split21.size() > 0){
				for (String day : comp.split21) {
					int i = comp.date.indexOf(day);
					double cp = comp.getCP(i);
					double op = comp.getOP(i - 1);

					System.out.print("2:1 split on: " + day + " ");
					System.out.print(cp + " --> " + op + "\n");
				}
			}

			if (comp.split31.size() > 0){
				for (String day : comp.split31) {
					int i = comp.date.indexOf(day);
					double cp = comp.getCP(i);
					double op = comp.getOP(i - 1);

					System.out.print("3:1 split on: " + day + " ");
					System.out.print(cp + " --> " + op + "\n");
				}
			}

			if (comp.split32.size() > 0){
				for (String day : comp.split32) {
					int i = comp.date.indexOf(day);
					double cp = comp.getCP(i);
					double op = comp.getOP(i - 1);

					System.out.print("3:2 split on: " + day + " ");
					System.out.print(cp + " --> " + op + "\n");
				}
			}

			int splitCount = comp.split21.size() + comp.split31.size() + comp.split32.size();
			if (splitCount > 0)
				System.out.println("Total number of splits: " + splitCount);
		}
	}
} 
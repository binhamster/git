import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class Assignment2 {
	static Connection conn = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");;

	public static void main (String[] args) throws Exception {
		connectSQL();
		Scanner user = new Scanner(System.in);
		while(true) {
			// Prompt
			System.out.print("Enter a ticker symbol [start/end dates]: ");

			// Exit program and close connection
			String line = user.nextLine();
			if (line.isEmpty() || line.trim().isEmpty()) {
				conn.close();
				System.out.println("Database connection closed");
				break;
			}

			Scanner lscan = new Scanner(line);
			String ticker = lscan.next();
			
			boolean b = printName(ticker);
			if (b)
				continue;

			PriceVolume pv = getPriVol(ticker);
			PriceVolume adjPV = new PriceVolume("adj");
			
			java.util.Date sdate;
			java.util.Date edate;
			if (!lscan.hasNext()) {
				// Execute with no date boundaries
				calcStockSplit(pv, adjPV, null, null);
				invest(adjPV, null, null);
			} else {
				// Get dates
				String date1 = lscan.next();
				String date2;
				if (lscan.hasNext()) {
					date2 = lscan.next();

					try {
						sdate = sdf.parse(date1);
						edate = sdf.parse(date2);
					} catch (ParseException ex){
						System.out.println("Incorrect date format");
						continue;
					}
					if (sdate.after(edate)){
						System.out.println("Start date is after end date");
						continue;
					}

					// Execute with date boundaries
					calcStockSplit(pv, adjPV, sdate, edate);
					invest(adjPV, sdate, edate);
				} else {
					System.out.println("Second date required");
				}
			}

			try{
			    PrintWriter writer = new PrintWriter("a2splits", "UTF-8");

			    for (int i = 0; i < adjPV.getNumDays(); i++) {
			    	writer.printf("%s Open: %.2f Close: %.2f\n", 
			    		adjPV.getDate(i),
			    		adjPV.getOP(i),
			    		adjPV.getCP(i));
			    }
			    writer.close();
			    System.out.printf("Last Day: %s Open: %.2f Close: %.2f\n",
		    	adjPV.getDate(adjPV.getNumDays()-1),
		    	adjPV.getOP(adjPV.getNumDays()-1),
				adjPV.getCP(adjPV.getNumDays()-1));
			} catch (Exception e) {}

			try{
			    PrintWriter writer = new PrintWriter("a2normal", "UTF-8");

			    for (int i = 0; i < pv.getNumDays(); i++) {
			    	writer.printf("%s Open: %.2f Close: %.2f\n", 
			    		pv.getDate(i),
			    		pv.getOP(i),
			    		pv.getCP(i));
			    }
			    writer.close();
			} catch (Exception e) {}
		}
	}

	static void connectSQL () throws Exception{
		// Basic Conenction
		String paramsFile = "ConnectionParameters.txt";
		Properties connectprops = new Properties();
		connectprops.load(new FileInputStream(paramsFile));

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dburl = connectprops.getProperty("dburl");
			String username = connectprops.getProperty("user");
			conn = DriverManager.getConnection(dburl, connectprops);
			System.out.printf("Database connection %s %s established. %n", dburl, username);
		} catch (SQLException se) {
			System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n", se.getMessage(), se.getSQLState(), se.getErrorCode());
		}
	}

	static boolean printName(String ticker) throws SQLException{
		// Print company name
		PreparedStatement pstmt = conn.prepareStatement("select Name from Company where Ticker = ?");
		pstmt.setString(1, ticker);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			System.out.printf("%s\n", rs.getString(1));
			return false;
		} else {
			System.out.printf("%s is not in the database\n", ticker);
			return true;
		}
	}

	static PriceVolume getPriVol(String ticker) throws SQLException{
		// Retrieve important information from database and store it in object PriceVolume
		PreparedStatement pstmt = conn.prepareStatement("select * from PriceVolume where Ticker = ? order by TransDate DESC");
		pstmt.setString(1, ticker);
		ResultSet rs = pstmt.executeQuery();

		PriceVolume pv = new PriceVolume(ticker);

		while(rs.next()) {
			pv.setDate(rs.getString("TransDate"));
			pv.setOP(Double.parseDouble(rs.getString("OpenPrice")));
			pv.setHP(Double.parseDouble(rs.getString("HighPrice")));
			pv.setLP(Double.parseDouble(rs.getString("LowPrice")));
			pv.setCP(Double.parseDouble(rs.getString("ClosePrice")));
		}

		return pv;
	}

	static void calcStockSplit(PriceVolume pv, PriceVolume adjPV, java.util.Date sdate, java.util.Date edate) throws SQLException{
		// mode = true : date boundaries
		// mode = false : no date boundaries
		boolean mode = true;
		if ((sdate == null) && (edate == null))
			mode = false;

		int splits = 0; 	// number of splits
		int days = 1;		// trading days	
		double d = 1.0;		// split divide factor
		java.util.Date thisDate = null;
		for (int i = 0; i < pv.getNumDays(); i++){
			if ((i + 1) != pv.getNumDays()){
				try {
					thisDate = sdf.parse(pv.getDate(i));
				} catch (ParseException ex) {};

				boolean bool;
				if (mode)
					bool = sdate.before(thisDate) && edate.after(thisDate) || edate.equals(thisDate) || sdate.equals(thisDate);
				else
					bool = true;

				if (bool){
					days++;
					double op = pv.getOP(i);
					double cp = pv.getCP(i + 1);
					String date = pv.getDate(i + 1);

					// Add to adjusted data
					adjPV.setDate(pv.getDate(i));
					adjPV.setOP(pv.getOP(i) / d);
					adjPV.setHP(pv.getHP(i) / d);
					adjPV.setLP(pv.getLP(i) / d);
					adjPV.setCP(pv.getCP(i) / d);

					double a = Math.abs((cp / op) - 2.0);
					double b = Math.abs((cp / op) - 3.0);
					double c = Math.abs((cp / op) - 1.5);

					if (a < 0.2) {
						System.out.printf("2:1 split on %s %.2f --> %.2f\n", date, cp, op);
						d = d * 2.0;
						splits++;
					} if (b < 0.3) {
						System.out.printf("3:1 split on %s %.2f --> %.2f\n", date, cp, op);
						d = d * 3.0;
						splits++;
					} if (c < 0.15) {
						System.out.printf("3:2 split on %s %.2f --> %.2f\n", date, cp, op);
						d = d * 1.5;
						splits++;
					}
				}
			}
		}

		System.out.printf("%d splits in %d trading days\n", splits, days);
	}

	static void invest(PriceVolume adjPV, java.util.Date sdate, java.util.Date edate) {
		boolean mode = true;
		if ((sdate == null) && (edate == null))
			mode = false;

		double sum = 0.0;
		double avg;
		double cash = 0.0;
		int shares = 0;
		int count = 1;
		int nTrans = 0;
		java.util.Date thisDate = null;
		// Scan forward in time
		for (int i = (adjPV.getNumDays() - 1); i > 0; i--) {
			try {
				thisDate = sdf.parse(adjPV.getDate(i));
			} catch (ParseException ex) {};

			boolean bool;
			if (mode)
				bool = sdate.before(thisDate) && edate.after(thisDate) || edate.equals(thisDate) || sdate.equals(thisDate);
			else
				bool = true;

			if (bool){
				if (count >= 51) {
					avg = sum / 50; // Compute average
					double cp = adjPV.getCP(i);
					double op = adjPV.getOP(i);
					boolean buyCri= (cp < avg) && (cp / op < 0.97000001);
					boolean sellCri = (shares >= 100) && (op > avg) && ((op / adjPV.getCP(i+1)) > 1.00999999); 

					if (buyCri) {
						cash = cash - 100 * adjPV.getOP(i-1);
						cash = cash - 8.0;
						shares = shares + 100;
						nTrans++;
					} else if (sellCri) {
						cash = cash + 100 * ((op + cp) / 2);
						cash = cash - 8.0;
						shares = shares - 100;
						nTrans++;
					} else if ((i == 1) && (shares > 0)) {
						cash = cash + shares * op;
						nTrans++;
					}

					// remove trailing data point, to compute next 50 day average
					sum = sum - adjPV.getCP(i + 50);
				}
				sum = sum + adjPV.getCP(i);
				count++;
			}
		}

		System.out.printf("Transactions executed: %d\n", nTrans);
		System.out.printf("Net cash: %.2f\n", cash);
	}
}
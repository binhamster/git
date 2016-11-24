import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class a3 {
	static Connection conRead = null;
	static Connection conWrite = null;

	public static void main (String[] args) throws Exception{ 
		// Variables: 
		String anIndustry;
		ResultSet dateRange;
		String sDate;
		String eDate;
		ResultSet tickers;
		String aTicker;
		PriceVolume adjpv;
		LinkedHashMap<String, PriceVolume> basket = new LinkedHashMap<String, PriceVolume>();

		connectSQL("readerparams.txt");
		connectSQL("writerparams.txt");
		makeTable();

		Statement stmt = conRead.createStatement();
		ResultSet industries = stmt.executeQuery(
			"select Industry " +
			"from Company natural join PriceVolume " +
			"group by Industry " +
			"order by Industry ASC");
		PreparedStatement getDates = conRead.prepareStatement(
			"select max(TD1), min(TD2) from " +
				"(select Ticker, min(TransDate) as TD1, max(TransDate) as TD2, " +
 				"count(distinct TransDate) as TradingDays " +
 				"from Company natural join PriceVolume " +
 				"where Industry = ? " +
 				"group by Ticker " +
 				"having TradingDays >= 150 " +
 				"order by Ticker) AS X");
		PreparedStatement getTickers = conRead.prepareStatement(
			"select Ticker " +
			"from Company natural join PriceVolume " +
			"where Industry = ? " +
			"group by Ticker " +
			"order by Ticker ASC");

		// while (industries.next()) {
		// 	anIndustry = industries.getString("Industry");
		// 	System.out.printf("%s: \n", anIndustry);

		// 	getDates.setString(1, anIndustry);
		// 	dateRange = getDates.executeQuery();
		// 	dateRange.next();
		// 	sDate = dateRange.getString(1);
		// 	eDate = dateRange.getString(2); 
		// 	System.out.printf("%s - %s\n", sDate, eDate);

		// 	getTickers.setString(1, anIndustry);
		// 	tickers = getTickers.executeQuery();
			
		// 	while (tickers.next()) {
		// 		aTicker = tickers.getString("Ticker");
		// 		System.out.printf("%s     \r", aTicker);
		// 		adjpv = getAdjPV(aTicker);
		// 		basket.put(aTicker, adjpv);
		// 	}

		// 	// int sDate = 1000000;
		// 	// String sKey = null;
		// 	// for (String key : basket.keySet()){
		// 	// 	if (basket.get(key).getNumDays() < sDate) {
		// 	// 		sDate = basket.get(key).getNumDays() - 1;
		// 	// 		sKey = key;
		// 	// 	}
		// 	// }
		// 	// System.out.printf("Start Date: %s\n", basket.get(sKey).getDate(sDate));
		// 	// sDate = 1000000;
		// }

		getDates.setString(1, "Telecommunications Services");
		dateRange = getDates.executeQuery();
		dateRange.next();
		sDate = dateRange.getString(1);
		eDate = dateRange.getString(2);

		getTickers.setString(1, "Telecommunications Services");
		tickers = getTickers.executeQuery();

		while (tickers.next()) {
			aTicker = tickers.getString("Ticker");
			adjpv = getAdjPV(aTicker);
			basket.put(aTicker, adjpv);
		}

		PrintWriter writer = null;
		try{
		    writer = new PrintWriter("tickerReturn", "UTF-8");
		} catch (Exception e) {}

		PriceVolume aComp;
		PriceVolume inComp;
		Double tickerReturn;
		Double industryReturn = 0.0;
		for (String key : basket.keySet()) {
			System.out.println(key + ":");
			aComp = basket.get(key);
			int s = aComp.indexOf(sDate);
			int e = aComp.indexOf(eDate);

			for (String inKey : basket.keySet()) {
				if (!key.equals(inKey)) {
					inComp = basket.get(inKey);
					//industryReturn = industryReturn + inComp.getCP(s - 59) / inComp.getOP(s) - 1;
					industryReturn = industryReturn + inComp.getCP(s - 59 - 60) / inComp.getOP(s - 60) - 1;

				}
			}
			industryReturn = industryReturn * (1.0/6.0);
			System.out.printf("%10.7f\n", industryReturn);
			industryReturn = 0.0;



			// for (int i = s; i > s%60; i = i - 60){
			// 	tickerReturn = aComp.getCP(i-59) / aComp.getOP(i) - 1;
			// 	writer.printf("%s  %s  %s  %10.7f\n",
			// 		key,
			// 		aComp.getDate(i),
			// 		aComp.getDate(i-59),
			// 		tickerReturn);
			// }
		}		
		writer.close();




		// try{
		//     PrintWriter writer = new PrintWriter("a3splits", "UTF-8");

		//     for (int i = 0; i < adjpv.getNumDays(); i++) {
		//     	writer.printf("%s Open: %.2f Close: %.2f\n", 
		//     		adjpv.getDate(i),
		//     		adjpv.getOP(i),
		//     		adjpv.getCP(i));
		//     }
		//     System.out.printf("Last Day: %s Open: %.2f Close: %.2f\n",
		//     	adjpv.getDate(adjpv.getNumDays()-1),
		//     	adjpv.getOP(adjpv.getNumDays()-1),
		// 		adjpv.getCP(adjpv.getNumDays()-1));
		//     writer.close();
		// } catch (Exception e) {}





		conRead.close();
		conWrite.close();
	}

	static void connectSQL(String param) throws Exception{
		Properties connectProps = new Properties();
		connectProps.load(new FileInputStream(param));

		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dburl = connectProps.getProperty("dburl");
			String username = connectProps.getProperty("user");
			if (param.equals("readerparams.txt"))
				conRead = DriverManager.getConnection(dburl, connectProps);
			else
				conWrite = DriverManager.getConnection(dburl, connectProps);
		} catch (SQLException se) {
			System.out.printf("SQLException: %s%nSQLState: %s%nVendorError: %s%n", se.getMessage(), se.getSQLState(), se.getErrorCode());
		}
	}

	static void makeTable() throws SQLException{
		Statement stmt = conWrite.createStatement();

		stmt.executeUpdate("drop table if exists Performance");
		stmt.executeUpdate(
			"CREATE TABLE Performance" +
			"(Industry CHAR(30)," +
			"Ticker CHAR(6)," +
			"StartDate CHAR(10)," +
			"TickerReturn CHAR(12)," +
			"IndustryReturn CHAR(12))");
		stmt.executeUpdate("DELETE FROM Performance");
	}

	static PriceVolume getAdjPV(String ticker) throws SQLException{
		PreparedStatement pstmt = conRead.prepareStatement(
			"select * " + 
			"from PriceVolume " +
			"where Ticker = ? " +
			"order by TransDate DESC");
		pstmt.setString(1, ticker);
		ResultSet rs = pstmt.executeQuery();

		PriceVolume adjpv = new PriceVolume(ticker);
		double div = 1;
		boolean bool = false;
		while(rs.next()) {
			double op = rs.getDouble("OpenPrice");
			if(rs.isLast()) {
				adjpv.setDate(rs.getString("TransDate"));
				adjpv.setOP(rs.getDouble("OpenPrice") / div);
				adjpv.setCP(rs.getDouble("ClosePrice") / div);
				break;
			} else 
				rs.next();
			double cp = rs.getDouble("ClosePrice");
			rs.previous();

			adjpv.setDate(rs.getString("TransDate"));
			adjpv.setOP(rs.getDouble("OpenPrice") / div);
			adjpv.setCP(rs.getDouble("ClosePrice") / div);

			if (Math.abs((cp / op) - 2.0) < 0.2) 
				div = div * 2.0;
			if (Math.abs((cp / op) - 3.0) < 0.3) 
				div = div * 3.0;
			if (Math.abs((cp / op) - 1.5) < 0.15) 
				div = div * 1.5;
		}

		return adjpv;
	}


}

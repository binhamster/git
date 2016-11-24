import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.*;

public class a3 {
	static Connection conRead = null;
	static Connection conWrite = null;

	public static void main (String[] args) throws Exception{ 
		// Variables 
		String anIndustry;
		ResultSet dateRange;
		String sDate;
		String eDate;
		ResultSet tickers;
		String aTicker;
		PriceVolume adjpv = null;
		LinkedHashMap<String, PriceVolume> basket = new LinkedHashMap<String, PriceVolume>();
		PriceVolume outComp;
		PriceVolume inComp;
		Double tickerReturn;
		Double industryReturn = 0.0;
		Double m = 0.0;

		// Set up connections
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

		PrintWriter writer = null;
		try{
		    writer = new PrintWriter("finalOutput", "UTF-8");
		    writer.println("Industry:  Ticker:  StartDate:  EndDate:  TickerReturn:  IndustryReturn:");
		} catch (Exception e) {}

		while (industries.next()) {
			anIndustry = industries.getString("Industry");
			System.out.printf("Processing %s: \n", anIndustry);

			// Get Date Range 
			getDates.setString(1, anIndustry);
			dateRange = getDates.executeQuery();
			dateRange.next();
			sDate = dateRange.getString(1);
			eDate = dateRange.getString(2); 
			System.out.printf("%s - %s\n", sDate, eDate);

			// Set up basket of companies for an industry
			getTickers.setString(1, anIndustry);
			tickers = getTickers.executeQuery();
			while (tickers.next()) {
				aTicker = tickers.getString("Ticker");
				adjpv = getAdjPV(aTicker);
				basket.put(aTicker, adjpv);
				m = m + 1.0;
			}

			for (String outKey : basket.keySet()) {
				System.out.print("Calculating: ");
				System.out.printf("%s     \r", outKey);
				outComp = basket.get(outKey);
				int s = outComp.indexOf(sDate);
				int e = outComp.indexOf(eDate);

				for (int i = s; i < e - (e-s)%60; i = i + 60){
					// Ticker Return Calculation
					tickerReturn = outComp.getCP(i+59) / outComp.getOP(i) - 1;
					
					// Industry Return Calculation
					for (String inKey : basket.keySet()) {
						if (!outKey.equals(inKey)) {
							inComp = basket.get(inKey);
							int a = inComp.getFirstDay(outComp.getDate(i));
							int b = inComp.getLastDay(outComp.getDate(i+59));
							industryReturn = industryReturn + (inComp.getCP(b) / inComp.getOP(a));
						}
					}
					industryReturn = industryReturn * (1.0/(m-1.0))-1.0;
					writer.printf("%s  %s  %s  %s  %10.7f %10.7f\n",
						anIndustry, outKey, sDate, eDate, tickerReturn, industryReturn);
					industryReturn = 0.0;
				}

			}	

		}
		writer.close();

		// getDates.setString(1, "Consumer Staples");
		// dateRange = getDates.executeQuery();
		// dateRange.next();
		// sDate = dateRange.getString(1);
		// eDate = dateRange.getString(2);

		// getTickers.setString(1, "Consumer Staples");
		// tickers = getTickers.executeQuery();
		// while (tickers.next()) {
		// 	aTicker = tickers.getString("Ticker");
		// 	adjpv = getAdjPV(aTicker);
		// 	basket.put(aTicker, adjpv);
		// 	m = m + 1.0;
		// }

		// PrintWriter writer = null;
		// try{
		//     writer = new PrintWriter("testOutput", "UTF-8");
		// } catch (Exception e) {}

		// for (String outKey : basket.keySet()) {
		// 	System.out.print("Calculating: ");
		// 	System.out.printf("%s     \r", outKey);
		// 	outComp = basket.get(outKey);
		// 	int s = outComp.indexOf(sDate);
		// 	int e = outComp.indexOf(eDate);

		// 	for (int i = s; i < e - (e-s)%60; i = i + 60){
		// 		tickerReturn = outComp.getCP(i+59) / outComp.getOP(i) - 1;

		// 		for (String inKey : basket.keySet()) {
		// 			if (!outKey.equals(inKey)) {
		// 				inComp = basket.get(inKey);
		// 				int a = inComp.getFirstDay(sDate);
		// 				int b = inComp.getLastDay(outComp.getDate(i+59));
		// 				industryReturn = industryReturn + (inComp.getCP(b) / inComp.getOP(a));
		// 			}
		// 		}
		// 		industryReturn = industryReturn * (1.0/(m-1.0))-1;
		// 		writer.printf("%s  %s  %s  %10.7f %10.7f\n",
		// 			outKey, sDate, eDate, tickerReturn, industryReturn);
		// 		industryReturn = 0.0;
		// 	}

		// }		
		// writer.close();







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

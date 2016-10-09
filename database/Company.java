import java.util.*;
import java.lang.*;

public class Company{
	String name;
	ArrayList<String> date;
	ArrayList<Double> openPrice;
	ArrayList<Double> highPrice;
	ArrayList<Double> lowPrice;
	ArrayList<Double> closePrice;
	ArrayList<Integer> shares;
	ArrayList<Double> adjustPrice;
	HashMap<String, String> crazyDays;

	public Company(String name){
		this.name = name;
		this.date = new ArrayList<String>();
		this.openPrice = new ArrayList<Double>();
		this.highPrice = new ArrayList<Double>();
		this.lowPrice = new ArrayList<Double>();
		this.closePrice = new ArrayList<Double>();
		this.shares = new ArrayList<Integer>();
		this.adjustPrice = new ArrayList<Double>();
		this.crazyDays = new HashMap<String, String>();
	}

	public void setDate(String date){
		(this.date).add(date);
	}

	public String getDate(int i){
		return (this.date).get(i);
	}

	public void setOP(double p){
		(this.openPrice).add(p);
	}

	public double getOP(int i){
		return (this.openPrice).get(i);
	}

	public void setHP(double p){
		(this.highPrice).add(p);
	}

	public double getHP(int i){
		return (this.highPrice).get(i);
	}

	public void setLP(double p){
		(this.lowPrice).add(p);
	}

	public double getLP(int i){
		return (this.lowPrice).get(i);
	}

	public void setCP(double p){
		(this.closePrice).add(p);
	}

	public double getCP(int i){
		return (this.closePrice).get(i);
	}

	public void setShare(int s){
		(this.shares).add(s);
	}

	public int getShare(int i){
		return (this.shares).get(i);
	}

	public void setAP(double p){
		(this.adjustPrice).add(p);
	}

	public double getAP(int i){
		return (this.adjustPrice).get(i);
	}

	public void setCD(String date, double perc){
		double n = (double) Math.round(perc * 100) / 100;
		n = n * 100;
		(this.crazyDays).put(date, n + "%");
	}

	public String getCD(String date){
		return (this.crazyDays).get(date);
	}
}
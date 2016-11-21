import java.util.*;
import java.lang.*;

public class PriceVolume{
	String ticker;
	LinkedList<String> transDates;
	LinkedList<Double> openPrice;
	LinkedList<Double> highPrice;
	LinkedList<Double> lowPrice;
	LinkedList<Double> closePrice;

	public PriceVolume(String name){
		this.ticker = ticker;
		this.transDates = new LinkedList<String>();
		this.openPrice = new LinkedList<Double>();
		this.highPrice = new LinkedList<Double>();
		this.lowPrice = new LinkedList<Double>();
		this.closePrice = new LinkedList<Double>();
	}

	public void setDate(String date){
		(this.transDates).add(date);
	}

	public String getDate(int i){
		return (this.transDates).get(i);
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

	public Integer getNumDays() {
		return (this.transDates).size();
	}



}
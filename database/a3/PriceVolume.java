import java.util.*;
import java.lang.*;
import java.text.*;

public class PriceVolume{
	String ticker;
	LinkedList<String> transDates;
	LinkedList<Double> openPrice;
	LinkedList<Double> closePrice;

	public PriceVolume(String name){
		this.ticker = ticker;
		this.transDates = new LinkedList<String>();
		this.openPrice = new LinkedList<Double>();
		this.closePrice = new LinkedList<Double>();
	}

	public String getTick() {
		return this.ticker;
	}

	public void setDate(String date){
		(this.transDates).addFirst(date);
	}

	public String getDate(int i){
		return (this.transDates).get(i);
	}

	public void setOP(double p){
		(this.openPrice).addFirst(p);
	}

	public double getOP(int i){
		return (this.openPrice).get(i);
	}

	public void setCP(double p){
		(this.closePrice).addFirst(p);
	}

	public double getCP(int i){
		return (this.closePrice).get(i);
	}

	public Integer getNumDays(){
		return (this.transDates).size();
	}

	public Integer indexOf(String date){
		return (this.transDates).indexOf(date);
	}

	public Integer getFirstDay(String date, Integer s){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mainDate = null;
		Date firstDate = null;
		try {
			mainDate = sdf.parse(date);
			firstDate = sdf.parse(this.transDates.get(s));
		} catch (ParseException ex){};

		if (this.transDates.contains(date)) {
			return transDates.indexOf(date);
		} else {
			if (firstDate.after(mainDate)) {
				s = s - 5;
				try {
					firstDate = sdf.parse(this.transDates.get(s));
				} catch (ParseException ex){};
			}
			while(firstDate.before(mainDate)) {
				s++;
				try {
					firstDate = sdf.parse(this.transDates.get(s));
				} catch (ParseException ex){};
			}
		}
		return s;
	}

	public Integer getLastDay(String date, Integer e){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mainDate = null;
		Date lastDate = null;
		try {
			mainDate = sdf.parse(date);
			lastDate = sdf.parse(this.transDates.get(e));
		} catch (ParseException ex){};

		if (this.transDates.contains(date)) {
			return transDates.indexOf(date);
		} else {
			if (lastDate.before(mainDate)) {
				e = e + 5;
				try {
					lastDate = sdf.parse(this.transDates.get(e));
				} catch (ParseException ex){};
			} 

			while(lastDate.after(mainDate)) {
				e--;
				try {
					lastDate = sdf.parse(this.transDates.get(e));
				} catch (ParseException ex){};
			}

		}
		return e;
	}
}
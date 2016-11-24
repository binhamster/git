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

	public void setCP(double p){
		(this.closePrice).add(p);
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

	public Integer getFirstDay(String mainDate, Integer s){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mDate = null;
		Date firstDate = null;
		try {
			mDate = sdf.parse(mainDate);
			firstDate = sdf.parse(this.transDates.get(s));
		} catch (ParseException ex){};

		if (this.transDates.contains(mainDate)) {
			return transDates.indexOf(mainDate);
		} else {
			if (firstDate.after(mDate)) {
				s = s + 5;
				try {
					firstDate = sdf.parse(this.transDates.get(s));
				} catch (ParseException ex){};
			}
			while(firstDate.before(mDate)) {
				s--;
				try {
					firstDate = sdf.parse(this.transDates.get(s));
				} catch (ParseException ex){};
			}
		}
		return s;
	}

	public Integer getLastDay(String mainDate, Integer e){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mDate = null;
		Date lastDate = null;
		try {
			mDate = sdf.parse(mainDate);
			lastDate = sdf.parse(this.transDates.get(e));
		} catch (ParseException ex){};

		if (this.transDates.contains(mainDate)) {
			return transDates.indexOf(mainDate);
		} else {
			if (lastDate.before(mDate)) {
				e = e - 5;
				try {
					lastDate = sdf.parse(this.transDates.get(e));
				} catch (ParseException ex){};
			} 

			while(lastDate.after(mDate)) {
				e++;
				try {
					lastDate = sdf.parse(this.transDates.get(e));
				} catch (ParseException ex){};
			}

		}
		return e;
	}
}
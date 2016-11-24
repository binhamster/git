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

	public Integer getFirstDay(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mainDate = null;
		Date firstDate = null;
		int i = 0;
		try {
			mainDate = sdf.parse(date);
			firstDate = sdf.parse(this.transDates.get(i));
		} catch (ParseException ex){};

		if (this.transDates.contains(date)) {
			return transDates.indexOf(date);
		} else {
			while(firstDate.before(mainDate)) {
				i++;
				try {
					firstDate = sdf.parse(this.transDates.get(i));
				} catch (ParseException ex){};
			}
		}
		return i;
	}

	public Integer getLastDay(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date mainDate = null;
		Date lastDate = null;
		int i = transDates.size();
		try {
			mainDate = sdf.parse(date);
			lastDate = sdf.parse(this.transDates.get(i));
		} catch (ParseException ex){};

		if (this.transDates.contains(date)) {
			return transDates.indexOf(date);
		} else {
			while(lastDate.after(mainDate)) {
				i--;
				try {
					lastDate = sdf.parse(this.transDates.get(i));
				} catch (ParseException ex){};
			}

		}
		return i;
	}
}
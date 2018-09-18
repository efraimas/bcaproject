package sailpoint.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BcaCalendar {

	List lstTanggal;

	List lstBulan;

	List lstTahun;

	public static String getTanggal() {
		return String.valueOf(Calendar.getInstance().get(Calendar.DATE));
	}

	public static String getTahun() {
		return String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	}

	public static String getBulan() {

		Calendar cal = Calendar.getInstance();

		int month = cal.get(Calendar.MONTH);

		if (month == 0)
			return "Jan";
		else if (month == 1)
			return "Feb";
		else if (month == 2)
			return "Mar";
		else if (month == 3)
			return "Apr";
		else if (month == 4)
			return "Mei";
		else if (month == 5)
			return "Jun";
		else if (month == 6)
			return "Jul";
		else if (month == 7)
			return "Agu";
		else if (month == 8)
			return "Sep";
		else if (month == 9)
			return "Okt";
		else if (month == 10)
			return "Nov";
		else
			return "Des";
	}

	/**
	 * @return the lstBulan
	 */
	public List getLstBulan() {

		lstBulan = new ArrayList<String>();

		lstBulan.add("Jan");
		lstBulan.add("Feb");
		lstBulan.add("Mar");
		lstBulan.add("Apr");
		lstBulan.add("Mei");
		lstBulan.add("Jun");
		lstBulan.add("Jul");
		lstBulan.add("Agu");
		lstBulan.add("Sep");
		lstBulan.add("Okt");
		lstBulan.add("Nov");
		lstBulan.add("Des");

		return lstBulan;
	}

	/**
	 * @return the lstTahun
	 */
	public List getLstTahun() {

		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);

		lstTahun = new ArrayList<String>();

		for (int i = (year - 2); i <= (year + 2); i++) {
			lstTahun.add(String.valueOf(i));
		}

		return lstTahun;
	}

	/**
	 * @return the lstTanggal
	 */
	public List getLstTanggal() {

		lstTanggal = new ArrayList<String>();

		for (int i = 1; i <= 31; i++) {
			lstTanggal.add(String.valueOf(i));
		}

		return lstTanggal;
	}

	public static String get2Digit(String input) {
		String value = input.trim();

		if (Integer.parseInt(input) < 10 && input.length() < 2)
			value = "0" + input;

		return value;
	}
	
	public static String get2DigitMonth(String month) {
		if(month.length()==1) {
			month = "0" + month;
		}
		
		return month;
	}

	public static String getMonthInt(String month) {
		String monthInt = "01";

		if ("Feb".equalsIgnoreCase(month))
			monthInt = "02";
		else if ("Mar".equalsIgnoreCase(month))
			monthInt = "03";
		else if ("Apr".equalsIgnoreCase(month))
			monthInt = "04";
		else if ("Mei".equalsIgnoreCase(month))
			monthInt = "05";
		else if ("Jun".equalsIgnoreCase(month))
			monthInt = "06";
		else if ("Jul".equalsIgnoreCase(month))
			monthInt = "07";
		else if ("Agu".equalsIgnoreCase(month))
			monthInt = "08";
		else if ("Sep".equalsIgnoreCase(month))
			monthInt = "09";
		else if ("Okt".equalsIgnoreCase(month))
			monthInt = "10";
		else if ("Nov".equalsIgnoreCase(month))
			monthInt = "11";
		else if ("Des".equalsIgnoreCase(month))
			monthInt = "12";

		return monthInt;
	}

	public static String getMainframeTodayDate() {
		return getTahun() + getMonthInt(getBulan()) + get2Digit(getTanggal());
	}

	/*
	 * public static void main(String args[]){
	 * 
	 * 
	 * int ResumedTanggal = Integer.parseInt(BcaCalendar.get2Digit("01"));
	 * 
	 * System.out.println(BcaCalendar.get2Digit("30")); }
	 */

}

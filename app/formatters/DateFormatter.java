package formatters;

import play.data.format.Formatters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter extends Formatters.AnnotationFormatter<DateFormat, Date>{
	static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	public Date parse(DateFormat arg0, String text, Locale arg2) throws ParseException {
		return convert(text);
	}

	@Override
	public String print(DateFormat arg0, Date object, Locale arg2) {
		return formatter.format(object);
	}

	public static Date convert(String text) {
		try {
			return formatter.parse(text);
		} catch (Exception e) { return null; }
	}

	public static String format(Date date) {
		try {
			return formatter.format(date);
		} catch (Exception e) { return null; }
	}

}

package formatters;

import play.data.format.Formatters;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter2 extends Formatters.AnnotationFormatter<DateFormat, LocalDateTime>{
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	@Override
	public LocalDateTime parse(DateFormat arg0, String text, Locale arg2) throws ParseException {
		return convert(text);
	}

	@Override
	public String print(DateFormat arg0, LocalDateTime object, Locale arg2) {
		return object.format(formatter);
	}

	public static LocalDateTime convert(String text) {
		LocalDate date = LocalDate.parse(text, formatter);
		return LocalDateTime.of(date, LocalTime.of(0,0));
	}

}

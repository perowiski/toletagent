package formatters;

import play.data.format.Formatters;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class IntFormatter extends Formatters.AnnotationFormatter<IntFormat, Integer>{

	@Override
	public Integer parse(IntFormat arg0, String text, Locale arg2) throws ParseException {
		return Integer.parseInt(text.replaceAll(",", ""));
	}

	@Override
	public String print(IntFormat arg0, Integer object, Locale arg2) {
		return format(object);
	}

	public static String format(Integer object) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(object);
	}

}

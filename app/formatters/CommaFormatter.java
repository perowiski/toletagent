package formatters;

import controllers.Utility;
import play.data.format.Formatters;

import java.util.*;

public class CommaFormatter extends Formatters.AnnotationFormatter<CommaFormat, List> {
	@Override
	public List parse(CommaFormat arg0, String text, Locale arg2)  {
		List list = new ArrayList();
		Arrays.asList(text.split(",")).forEach(entry -> {
		    if(Utility.isNotEmpty(entry.trim())) {
		        list.add(entry.trim());
            }
		});
		return list;
	}

	@Override
	public String print(CommaFormat arg0, List object, Locale arg2) {
		StringJoiner joiner = new StringJoiner(",");
		object.forEach(input -> joiner.add((String)input));
		return joiner.toString();
	}
}

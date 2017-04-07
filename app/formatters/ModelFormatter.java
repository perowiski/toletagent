package formatters;

import org.apache.commons.lang3.reflect.FieldUtils;
import play.data.format.Formatters;
import services.DBService;

import java.text.ParseException;
import java.util.Locale;

public class ModelFormatter extends Formatters.AnnotationFormatter<ModelFormat, Object> {

	private DBService db = DBService.Q;

    @Override
	public Object parse(ModelFormat clazz, String id, Locale arg2) throws ParseException {
		return this.db.findOne(clazz.value(), id);
	}

	@Override
	public String print(ModelFormat clazz, Object model, Locale arg2) {
		try {
			return FieldUtils.readField(model, "id", true).toString();
		} catch (IllegalAccessException e) {
		}
		throw new IllegalArgumentException("Cannot convert " + model + " into String id!");
	}
}

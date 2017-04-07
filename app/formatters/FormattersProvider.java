package formatters;

import org.joda.time.LocalTime;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Singleton
public class FormattersProvider implements Provider<Formatters> {

    private final MessagesApi messagesApi;


    @Inject
    public FormattersProvider(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    @Override
    public Formatters get() {
        Formatters formatters = new Formatters(messagesApi);

        formatters.register(Double.class, new AmtFormatter());
        formatters.register(Integer.class, new IntFormatter());
        formatters.register(Date.class, new DateFormatter());
        formatters.register(Object.class, new ModelFormatter());
        formatters.register(List.class, new CommaFormatter());

        formatters.register(LocalTime.class, new SimpleFormatter<LocalTime>() {

            private Pattern timePattern = Pattern.compile(
                    "([012]?\\d)(?:[\\s:\\._\\-]+([0-5]\\d))?"
            );

            @Override
            public LocalTime parse(String input, Locale l) throws ParseException {
                Matcher m = timePattern.matcher(input);
                if (!m.find()) throw new ParseException("No valid Input", 0);
                int hour = Integer.valueOf(m.group(1));
                int min = m.group(2) == null ? 0 : Integer.valueOf(m.group(2));
                return new LocalTime(hour, min);
            }

            @Override
            public String print(LocalTime localTime, Locale l) {
                return localTime.toString("HH:mm");
            }

        });

        return formatters;
    }
}
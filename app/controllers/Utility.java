package controllers;

import formatters.DateFormatter;
import models.Subscription;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Http;
import pojos.Param;
import services.DBFilter;
import utility.DateUtil;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by seyi on 1/26/15.
 */
public class Utility extends Controller {

    public static boolean isNotEmpty(String text) {
        return StringUtils.isNotEmpty(text);
    }
    public static boolean isEmpty(String text) {
        return StringUtils.isEmpty(text);
    }
    public static boolean isNumeric(String text) {
        return NumberUtils.isNumber(text);
    }

    public static boolean isEmailValid(String email) {
        Constraints.EmailValidator validator = new Constraints.EmailValidator();
        return validator.isValid(email);
    }

    public static Param getParam() {
        return getParam(50);
    }

    public static Param getParam(int lim) {
        int page = 0;
        try {
            String p = getQuery("page");
            if(StringUtils.isNumeric(p))
                page = Integer.valueOf(p);
        } catch(Exception e)  {}

        int limit = lim;
        try {
            String l = getQuery("limit");
            if(StringUtils.isNumeric(l))
                limit = Integer.valueOf(l);
        } catch(Exception e)  {}

        Param param = new Param(page, limit);
        String sort = getQuery("sort");
        if(Utility.isNotEmpty(sort)) {
            param.setSort(sort);
        }

        String order = getQuery("order");
        if(Utility.isNotEmpty(order)) {
            param.setOrder(order);
        }

        return param;
    }

    public static String getQuery(String query) { return Http.Context.current().request().getQueryString(query); }

    public static String getUri() { return Http.Context.current().request().uri(); }
    public static String getUriQuery() {
        String uri = getUri();
        String[] arr = uri.split("\\?");
        if(arr.length>1) return arr[1];
        return "";
    }

    public static Map<String, String> getParams(String path) {
        Map<String, String> map = new HashMap<>();
        String url = path;
        try {
            url = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {}
        String[] queries = url.split("/");
        for(String query: queries) {
            String[] array = query.split(":");
            if(array.length==2) {
                String key =  array[0];
                String value = array[1];
                map.put(key, value);
            }
        }
        return map;
    }

    public static boolean isImage(String uri) {
        String path = uri.toLowerCase();
        return path.contains("jpg")
                || path.contains("png")
                || path.contains("gif")
                || path.contains("jpeg")
                || path.contains("svg");
    }

    public static long remainingTime(Date date, int duration) {
        return DateUtil.remainingTime(date, duration);
    }

    public static String formatDate(LocalDateTime dt, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dt.format(formatter);
    }

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String makeSlug(String text) {
        String input = text.trim().replace("-", " ").replaceAll(" +", " ");
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String summarize(String text, int size) {
        List<String> list = Arrays.asList(text.split(" "));
        if(list.size() > size) {
            list = list.subList(0, size);
            StringBuilder b = new StringBuilder();
            list.forEach(e -> {
                b.append(e).append(" ");
            });
            return b.toString();
        } else {
            return text;
        }
    }

    public static String summarize1(String text) {
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        boundary.setText(text);
        int start = boundary.first();
        int end = boundary.next();

        if(start < 0) start = 0;
        if(end > text.length()) end = text.length();

        return text.substring(start,end);
    }

    private static final int ID_LENGTH = 30;

    public static String generateUniqueId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }

    public static String generateUniqueId(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String capitalize(String input) {
        if(input == null) return "";
        input = input.trim().replaceAll("_+", " ").replaceAll("-+", " ").replaceAll(" +", " ");
        return WordUtils.capitalizeFully(input);
    }

    public static Object field(Object obj, String name) {
        try {
            Field field = obj.getClass().getField(name);
            return field.get(obj);
        }catch (Exception e) {
            return null;
        }
    }

    public static String makeUrl(String text) {
        return text.replace("-", "_").replace(" ", "-").toLowerCase();
    }

    public static String unMakeUrl(String text) {
        return text.replace("-", " ").replace("_", "-").toLowerCase();
    }

    public static void dateRange(String field, DBFilter filter) {
        try {
            String daterange = request().getQueryString("daterange");
            if(Utility.isNotEmpty(daterange)) {
                String[] dates = daterange.split(" - ");
                if(dates.length > 1) {
                    filter.field(field)
                            .between(DateFormatter.convert(dates[0].trim()),
                                    DateUtils.addDays(DateFormatter.convert(dates[1].trim()), 1)
                            );
                }
            }
        } catch(Exception e) {}
    }


    public static double getTotalCost(List<Subscription.Product> products){
        double totalCost = 0.0;
        for(Subscription.Product product : products){
            totalCost = totalCost + (product.getUnitPrice() * product.getMaximum());
        }
        return totalCost;
    }

    public static String formatDate(Date date, String pattern){
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String getBasicPlanName(){
        return "Basic";
    }

    public static Date addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

}

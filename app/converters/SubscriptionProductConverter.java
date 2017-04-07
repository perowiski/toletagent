package converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Subscription;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tolet on 3/7/17.
 */
@Converter
public class SubscriptionProductConverter implements AttributeConverter<List, String> {

    private final static Gson GSON = new Gson();
    private Type listType = new TypeToken<List<Subscription.Product>>() {}.getType();
    @Override
    public String convertToDatabaseColumn(List meta) {
        try {
            if(meta == null) return "";
            return GSON.toJson(meta, listType);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public List<Subscription.Product> convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null) return new ArrayList<Subscription.Product>();
            List<Subscription.Product> subscriptionProducts = GSON.fromJson(dbData, listType);
            return subscriptionProducts;

        } catch (Exception ex) {
            return null;
        }
    }
}

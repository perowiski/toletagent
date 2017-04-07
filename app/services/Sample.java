package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import play.libs.ws.WSResponse;
import search.PropertyIndex;
import search.Searcher;

import java.util.concurrent.TimeUnit;

/**
 * Created by toletdeveloper2 on 1/30/17.
 */
public class Sample {
    public static PropertyIndex[] getPropertyIndexes() {
        try {
            WSResponse response = ApiPull.ws.url(ApiPull.WEB_URL+"/sampleIndex")
                    .setAuth(ApiPull.USERNAME, ApiPull.PASSWORD)
                    .setRequestTimeout(1000000000).get().toCompletableFuture().get(1, TimeUnit.HOURS);
            Gson gson = new Gson();
            JsonNode jsonNode = response.asJson();
            String jsonInput = jsonNode.toString();
            System.out.println(jsonInput);
            PropertyIndex[] indexes = gson.fromJson(jsonInput, PropertyIndex[].class);
            for (PropertyIndex propertyIndex : indexes) {
                indexProperty(propertyIndex);
            }
            return indexes;
        } catch(Exception e)   {
            e.printStackTrace();
            return null;
        }
    }

    public static void indexProperty(PropertyIndex index) {
        Gson gson = new Gson();
        String json = gson.toJson(index);
        System.out.println(json);
        Searcher.getClient().prepareIndex("property_index", "property_type", index.getId().toString()).setSource(json).get();
    }
}

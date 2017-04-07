package controllers;

import pojos.Property;
import services.ApiPull;
import services.CacheService;

/**
 * Created by seyi on 1/20/17.
 */
public class Misc {

    public static void cacheProperty(Property property) {
        if(property != null) {
            String key = getPropertyCacheKey(property.pid);
            CacheService.C.set(key, property);
        }
    }

    public static Property findProperty(String pid) {
        String key = getPropertyCacheKey(pid);
        Property property = (Property) CacheService.C.get(key);
        if (property == null) {
            property = ApiPull.getProperty(pid); //fetch from api
            cacheProperty(property);
        }
        return property;
    }

    public static String getPropertyCacheKey(String pid) {
        return "property-" + pid.trim();
    }
}

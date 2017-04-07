package services;

import play.cache.CacheApi;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by seyi on 7/5/16.
 */
@Singleton
public class CacheService {
    private CacheApi cache;

    public static CacheService C;

    @Inject
    public CacheService(CacheApi cache) {
        this.cache = cache;
        C = this;
    }

    public void set(String key, Object value) {
        cache.set(key, value);
    }

    public Object get(String key) {
        return cache.get(key);
    }

    public void remove(String key) {
        cache.remove(key);
    }
}

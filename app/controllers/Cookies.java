package controllers;

import play.mvc.Http;

import static play.mvc.Http.Context.current;

/**
 * Created by seyi on 2/13/15.
 */
public class Cookies {
    public static void set(String name, String value) {
        Http.Cookie cookie = Http.Cookie.builder(name, value).build();
        current().response().setCookie(cookie);
    }

    public static void set(String name, String value, int interval) {
        Http.Cookie cookie = Http.Cookie.builder(name, value).withMaxAge(interval).build();
        current().response().setCookie(cookie);
    }

    public static String get(String name) {
        Http.Cookie cookie = current().request().cookie(name);
        return (cookie == null) ? "" : cookie.value();
    }

    public static void discard(String name) {
        current().response().discardCookie(name);
    }
}

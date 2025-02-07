package com.shopme.utils;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    private String name;
    private String value;
    private int maxAge = -1;
    private boolean httpOnly = false;
    private boolean secure = false;
    private String path = "/";
    private String domain = null;

    private CookieUtil() {
    }

    public static CookieUtil builder() {
        return new CookieUtil();
    }

    public CookieUtil name(String name) {
        this.name = name;
        return this;
    }

    public CookieUtil value(String value) {
        this.value = value;
        return this;
    }

    public CookieUtil maxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public CookieUtil httpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
        return this;
    }

    public CookieUtil secure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public CookieUtil path(String path) {
        this.path = path;
        return this;
    }

    public CookieUtil domain(String domain) {
        this.domain = domain;
        return this;
    }

    public Cookie build() {
        if (name == null || value == null) {
            throw new IllegalArgumentException("Name and value are required fields for a cookie.");
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);
        if (domain != null) {
            cookie.setDomain(domain);
        }
        return cookie;
    }

    public static Cookie createCookie(String name, String value, long validity) {
        int maxAge = Math.toIntExact(validity / 1000);
        return CookieUtil.builder()
                .name(name)
                .value(value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .build();
    }

    public static Cookie deleteCookie(String tokenName) {
        return CookieUtil.createCookie(tokenName, "", 0);
    }
}

package com.github.albertosh.flitetrakr.util.language;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageUtils {

    private final static String DEFAULT_LANGUAGE = "en";
    private final static String DEFAULT_COUNTRY = "US";

    private static String language = DEFAULT_LANGUAGE;
    private static String country = DEFAULT_COUNTRY;

    private static Locale currentLocale = new Locale(language, country);

    public static void setLanguage(String language, String country) {
        LanguageUtils.language = language;
        LanguageUtils.country = country;
        currentLocale = new Locale(language, country);
    }

    public static String getMessage(Message message) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", currentLocale);
        return messages.getString(message.getKey());
    }
}

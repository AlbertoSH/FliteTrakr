package com.github.albertosh.flitetrakr.util.language

import java.util.*

object LanguageUtils {

    private val DEFAULT_LANGUAGE = "en"
    private val DEFAULT_COUNTRY = "US"

    private var language = DEFAULT_LANGUAGE
    private var country = DEFAULT_COUNTRY

    private var currentLocale = Locale(language, country)

    fun setLanguage(language: String, country: String) {
        LanguageUtils.language = language
        LanguageUtils.country = country
        currentLocale = Locale(language, country)
    }

    fun getMessage(message: Message): String {
        val messages = ResourceBundle.getBundle("messages", currentLocale)
        val key = message.getKey()
        return messages.getString(key)
    }
}

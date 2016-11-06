package com.github.albertosh.flitetrakr.model

import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message

data class Connection(val from: String, val to: String, val price: Int) {

    companion object {
        fun fromString(value: String): Connection {
            try {
                val separatedValues = value.split(delimiters = '-')

                if (separatedValues.size != 3)
                    throw IllegalArgumentException()
                val from = separatedValues[0]
                val to = separatedValues[1]
                val price = separatedValues[2].toInt()

                return Connection(from, to, price)
            } catch (e: IllegalArgumentException) {
                val errorFormat = LanguageUtils.getMessage(Message.INVALID_CONNECTION_FORMAT)
                val errorString = String.format(errorFormat, value)
                throw IllegalArgumentException(errorString)
            }
        }
    }

}

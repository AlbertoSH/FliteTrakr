package com.github.albertosh.flitetrakr.util.language

enum class Message {
    CONNECTIONS_INPUT {
        override fun getKey(): String {
            return "connections.input"
        }
    },
    CONNECTIONS_INPUT_ERROR {
        override fun getKey(): String {
            return "connections.error"
        }
    },


    USECASE_PRICE_OF_CONNECTION {
        override fun getKey(): String {
            return "usecase.priceOfConnection"
        }
    },
    USECASE_CHEAPEST_CONNECTION {
        override fun getKey(): String {
            return "usecase.cheapestConnection"
        }
    },
    USECASE_MAXIMUM_STOPS {
        override fun getKey(): String {
            return "usecase.maximumStops"
        }
    },
    USECASE_EXACT_STOPS {
        override fun getKey(): String {
            return "usecase.exactStops"
        }
    },
    USECASE_FIND_ALL_CONNECTIONS {
        override fun getKey(): String {
            return "usecase.findAllConnections"
        }
    },


    CONNECTION_NOT_FOUND {
        override fun getKey(): String {
            return "error.connectionNotFound"
        }
    },
    INVALID_CONNECTION_FORMAT {
        override fun getKey(): String {
            return "error.invalidConnectionFormat"
        }
    },
    UNKNOWN_ERROR {
        override fun getKey(): String {
            return "error.unknown"
        }
    },
    UNKNOWN_COMMAND {
        override fun getKey(): String {
            return "error.unknownCommand"
        }
    },
    INPUT_FILE_NOT_FOUND_ERROR {
        override fun getKey(): String {
            return "error.inputFileNotFound"
        }
    };


    abstract fun getKey(): String

}
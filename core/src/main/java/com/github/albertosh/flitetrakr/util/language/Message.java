package com.github.albertosh.flitetrakr.util.language;

public enum Message {

    CONNECTIONS_INPUT {
        @Override
        String getKey() {
            return "connections.input";
        }
    },
    CONNECTIONS_INPUT_ERROR {
        @Override
        String getKey() {
            return "connections.error";
        }
    },


    USECASE_PRICE_OF_CONNECTION {
        @Override
        String getKey() {
            return "usecase.priceOfConnection";
        }
    },
    USECASE_CHEAPEST_CONNECTION {
        @Override
        String getKey() {
            return "usecase.cheapestConnection";
        }
    },
    USECASE_MAXIMUM_STOPS {
        @Override
        String getKey() {
            return "usecase.maximumStops";
        }
    },
    USECASE_EXACT_STOPS {
        @Override
        String getKey() {
            return "usecase.exactStops";
        }
    },
    USECASE_FIND_ALL_CONNECTIONS {
        @Override
        String getKey() {
            return "usecase.findAllConnections";
        }
    },


    CONNECTION_NOT_FOUND {
        @Override
        String getKey() {
            return "error.connectionNotFound";
        }
    },
    INVALID_CONNECTION_FORMAT {
        @Override
        String getKey() {
            return "error.invalidConnectionFormat";
        }
    },
    UNKNOWN_ERROR {
        @Override
        String getKey() {
            return "error.unknown";
        }
    },
    UNKNOWN_COMMAND {
        @Override
        String getKey() {
            return "error.unknownCommand";
        }
    },
    INPUT_FILE_NOT_FOUND_ERROR {
        @Override
        String getKey() {
            return "error.inputFileNotFound";
        }
    };


    abstract String getKey();

}

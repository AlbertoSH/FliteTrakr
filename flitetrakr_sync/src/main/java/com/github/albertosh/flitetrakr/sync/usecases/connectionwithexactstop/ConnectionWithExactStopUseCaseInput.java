package com.github.albertosh.flitetrakr.sync.usecases.connectionwithexactstop;

import com.google.common.base.Preconditions;

public class ConnectionWithExactStopUseCaseInput {

    private final String from;
    private final String to;
    private final Integer stops;

    private ConnectionWithExactStopUseCaseInput(Builder builder) {
        this.from = Preconditions.checkNotNull(builder.from);
        this.to = Preconditions.checkNotNull(builder.to);
        this.stops = Preconditions.checkNotNull(builder.stops);
        Preconditions.checkState(this.stops >= 0);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Integer getStops() {
        return stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionWithExactStopUseCaseInput that = (ConnectionWithExactStopUseCaseInput) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        return stops.equals(that.stops);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + stops.hashCode();
        return result;
    }

    public static class Builder {
        private String from;
        private String to;
        private Integer stops;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder stops(Integer stops) {
            this.stops = stops;
            return this;
        }

        public Builder fromPrototype(ConnectionWithExactStopUseCaseInput prototype) {
            from = prototype.from;
            to = prototype.to;
            stops = prototype.stops;
            return this;
        }

        public ConnectionWithExactStopUseCaseInput build() {
            return new ConnectionWithExactStopUseCaseInput(this);
        }
    }
}

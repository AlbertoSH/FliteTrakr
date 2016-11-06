package com.github.albertosh.flitetrakr.sync.usecases.connectionsbelowprice;

import com.google.common.base.Preconditions;

public class ConnectionsBelowPriceUseCaseInput {

    private final String from;
    private final String to;
    private final Integer price;

    private ConnectionsBelowPriceUseCaseInput(Builder builder) {
        this.from = Preconditions.checkNotNull(builder.from);
        this.to = Preconditions.checkNotNull(builder.to);
        this.price = Preconditions.checkNotNull(builder.price);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Integer getPrice() {
        return price;
    }


    public static class Builder {
        private String from;
        private String to;
        private Integer price;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder price(Integer price) {
            this.price = price;
            return this;
        }

        public Builder fromPrototype(ConnectionsBelowPriceUseCaseInput prototype) {
            from = prototype.from;
            to = prototype.to;
            price = prototype.price;
            return this;
        }

        public ConnectionsBelowPriceUseCaseInput build() {
            return new ConnectionsBelowPriceUseCaseInput(this);
        }
    }
}

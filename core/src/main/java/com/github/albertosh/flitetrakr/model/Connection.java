package com.github.albertosh.flitetrakr.model;

import com.google.common.base.Preconditions;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

public class Connection {

    private final String from;
    private final String to;
    private final Integer price;

    private Connection(Builder builder) {
        this.from = Preconditions.checkNotNull(builder.from);
        this.to = Preconditions.checkNotNull(builder.to);
        this.price = Preconditions.checkNotNull(builder.price);
    }

    public Connection(String value) {
        try {
            String[] separatedValues = value.split("-");
            if (separatedValues.length != 3)
                throw new IllegalArgumentException();

            this.from = separatedValues[0];
            this.to = separatedValues[1];
            this.price = Integer.parseInt(separatedValues[2]);

        } catch (IllegalArgumentException e) {
            String errorFormat = LanguageUtils.getMessage(Message.INVALID_CONNECTION_FORMAT);
            String errorString = String.format(errorFormat, value);
            throw new IllegalArgumentException(errorString);
        }
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;
        return price.equals(that.price);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", price=" + price +
                '}';
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

        public Builder fromPrototype(Connection prototype) {
            from = prototype.from;
            to = prototype.to;
            price = prototype.price;
            return this;
        }

        public Connection build() {
            return new Connection(this);
        }
    }
}

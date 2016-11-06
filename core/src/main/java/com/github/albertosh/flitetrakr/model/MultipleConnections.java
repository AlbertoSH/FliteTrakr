package com.github.albertosh.flitetrakr.model;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class MultipleConnections implements Comparable<MultipleConnections> {
    private List<String> cities;
    private Integer price;

    private MultipleConnections(Builder builder) {
        this.cities = Preconditions.checkNotNull(builder.cities);
        this.price = Preconditions.checkNotNull(builder.price);
    }

    public List<String> getCities() {
        return cities;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public int compareTo(MultipleConnections o) {
        return price.compareTo(o.getPrice());
    }


    public static class Builder implements Cloneable {
        private List<String> cities = new ArrayList<>();
        private Integer price = 0;

        public Builder(String firstCity) {
            this.cities.add(firstCity);
        }

        private Builder() {
        }

        @Override
        public Builder clone() {
            Builder builder = new Builder();
            builder.cities = new ArrayList<>(this.cities);
            builder.price = new Integer(this.price);
            return builder;
        }

        public Builder withCity(String city) {
            this.cities.add(city);
            return this;
        }

        public Builder plusPrice(Integer price) {
            this.price += price;
            return this;
        }

        public Builder fromPrototype(MultipleConnections prototype) {
            cities = prototype.cities;
            price = prototype.price;
            return this;
        }

        public MultipleConnections build() {
            return new MultipleConnections(this);
        }
    }
}
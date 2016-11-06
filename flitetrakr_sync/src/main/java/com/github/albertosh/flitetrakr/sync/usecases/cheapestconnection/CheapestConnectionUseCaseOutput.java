package com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection;

import com.google.common.base.Preconditions;

import java.util.List;

public class CheapestConnectionUseCaseOutput {

    private final List<String> cities;
    private final Integer price;

    private CheapestConnectionUseCaseOutput(Builder builder) {
        this.cities = Preconditions.checkNotNull(builder.cities);
        this.price = Preconditions.checkNotNull(builder.price);
    }

    public List<String> getCities() {
        return cities;
    }

    public Integer getPrice() {
        return price;
    }

    public static class Builder {
        private List<String> cities;
        private Integer price;

        public Builder cities(List<String> cities) {
            this.cities = cities;
            return this;
        }

        public Builder price(Integer price) {
            this.price = price;
            return this;
        }

        public Builder fromPrototype(CheapestConnectionUseCaseOutput prototype) {
            cities = prototype.cities;
            price = prototype.price;
            return this;
        }

        public CheapestConnectionUseCaseOutput build() {
            return new CheapestConnectionUseCaseOutput(this);
        }
    }
}

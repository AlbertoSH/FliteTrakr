package com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class PriceOfConnectionUseCaseInput {

    private final List<String> codes;

    private PriceOfConnectionUseCaseInput(Builder builder) {
        this.codes = Preconditions.checkNotNull(builder.codes);
        Preconditions.checkState(this.codes.size() > 1);
    }

    public List<String> getCodes() {
        return new ArrayList<>(codes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceOfConnectionUseCaseInput that = (PriceOfConnectionUseCaseInput) o;

        return codes.equals(that.codes);

    }

    @Override
    public int hashCode() {
        return codes.hashCode();
    }

    public static class Builder {
        private List<String> codes = new ArrayList<>();

        public Builder codes(List<String> codes) {
            this.codes = codes;
            return this;
        }

        public Builder addCode(String code) {
            this.codes.add(code);
            return this;
        }

        public Builder fromPrototype(PriceOfConnectionUseCaseInput prototype) {
            codes = prototype.codes;
            return this;
        }

        public PriceOfConnectionUseCaseInput build() {
            return new PriceOfConnectionUseCaseInput(this);
        }
    }
}

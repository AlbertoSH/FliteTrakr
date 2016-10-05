package com.github.albertosh.flitetrakr.sync.usecases.cheapestconnection;

import com.google.common.base.Preconditions;

public class CheapestConnectionUseCaseInput {

    private final String from;
    private final String to;

    private CheapestConnectionUseCaseInput(Builder builder) {
        this.from = Preconditions.checkNotNull(builder.from);
        this.to = Preconditions.checkNotNull(builder.to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CheapestConnectionUseCaseInput that = (CheapestConnectionUseCaseInput) o;

        if (!from.equals(that.from)) return false;
        return to.equals(that.to);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    public static class Builder {
        private String from;
        private String to;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder fromPrototype(CheapestConnectionUseCaseInput prototype) {
            from = prototype.from;
            to = prototype.to;
            return this;
        }

        public CheapestConnectionUseCaseInput build() {
            return new CheapestConnectionUseCaseInput(this);
        }
    }
}

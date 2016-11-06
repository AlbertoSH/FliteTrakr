package com.github.albertosh.flitetrakr.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConnectionTest {

    @Test
    public void validString() throws Exception {
        Connection actual = new Connection("NUE-FRA-43");

        Connection expected = new Connection.Builder()
                .from("NUE")
                .to("FRA")
                .price(43)
                .build();

        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void invalidPrice() throws Exception {
        IllegalArgumentException exception = null;

        try {
            new Connection("NUE-FRA-invalid");
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertThat(exception, is(not(nullValue())));
    }

    @Test
    public void invalidFormat() throws Exception {
        IllegalArgumentException exception = null;

        try {
            new Connection("NUE-50");
        } catch (IllegalArgumentException e) {
            exception = e;
        }

        assertThat(exception, is(not(nullValue())));
    }

}
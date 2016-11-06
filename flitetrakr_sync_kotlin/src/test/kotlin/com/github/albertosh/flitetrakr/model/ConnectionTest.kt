package com.github.albertosh.flitetrakr.model

import io.kotlintest.specs.ShouldSpec

class ConnectionTest : ShouldSpec() {
    init {
        "connectionFromString" {
            should("create a connection when a valid input is given") {
                Connection.fromString("NUE-FRA-43") shouldBe Connection("NUE", "FRA", 43)
            }

            should("throw an IllegalArgumentException when the price is not an Int") {
                shouldThrow<IllegalArgumentException> {
                    Connection.fromString("NUE-FRA-invalid")
                }
            }

            should("throw an IllegalArgumentException when format is invalid") {
                shouldThrow<IllegalArgumentException> {
                    Connection.fromString("NUE-43")
                }
            }
        }
    }
}

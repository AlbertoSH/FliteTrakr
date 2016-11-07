package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import io.kotlintest.specs.ShouldSpec

class ConnectionsBelowPriceUseCaseTest : ShouldSpec() {

    init {
        "ConnectionBelowPrice" {
            should("throw an error if a connection doesn't exist") {
                val service = buildService()
                val cheapestConnectionUseCase: ICheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                val exception: ConnectionsBelowPriceUseCaseError? =
                        try {
                            useCase.execute(
                                    ConnectionsBelowPriceUseCaseInput(
                                            from = "NUE",
                                            to = "DBX",
                                            price = Int.MAX_VALUE
                                    ))
                            null
                        } catch (e: ConnectionsBelowPriceUseCaseError) {
                            e
                        }

                exception shouldBe ConnectionsBelowPriceUseCaseError.connectionNotFound
            }

            should("look for connections below some price if at least a connection below that price exists") {
                val service = buildService()
                val cheapestConnectionUseCase: ICheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                val output = useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 170))

                output shouldBe ConnectionsBelowPriceUseCaseOutput(listOf(
                        MultipleConnections(
                                cities = listOf("NUE", "FRA", "LHR"),
                                price = 70
                        ),
                        MultipleConnections(
                                cities = listOf("NUE", "FRA", "LHR", "NUE", "FRA", "LHR"),
                                price = 163
                        )
                ))
            }

            should("halt when the cheapest connection is over maximum price") {
                val service = buildService()
                val cheapestConnectionUseCase: ICheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                val output = useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 60))

                output shouldBe ConnectionsBelowPriceUseCaseOutput(emptyList())
            }

            should("return the existing connections ordered below some price even if the destination and origin is the same") {
                val service = buildService()
                val cheapestConnectionUseCase: ICheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                val output = useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "LHR",
                                to = "LHR",
                                price = 100))

                output shouldBe ConnectionsBelowPriceUseCaseOutput(listOf(
                        MultipleConnections(
                                cities = listOf("LHR"),
                                price = 0
                        ),
                        MultipleConnections(
                                cities = listOf("LHR", "NUE", "FRA", "LHR"),
                                price = 93
                        )
                ))
            }

        }
    }
}

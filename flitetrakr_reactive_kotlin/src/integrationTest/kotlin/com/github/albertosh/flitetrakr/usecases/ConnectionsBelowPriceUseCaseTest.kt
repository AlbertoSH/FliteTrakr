package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.specs.ShouldSpec

class ConnectionsBelowPriceUseCaseTest : ShouldSpec() {

    init {
        "ConnectionsBelowPriceUseCase" {
            should("throw an error if a connection doesn't exist") {
                val service: IConnectionService = buildService()
                val cheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "DBX",
                                price = Int.MAX_VALUE
                        ))
                        .test()
                        .assertError(ConnectionsBelowPriceUseCaseError.connectionNotFound)

            }

            should("look for connections below some price if at least a connection below that price exists") {
                val service: IConnectionService = buildService()
                val cheapestConnection = CheapestConnectionUseCase(service)
                val usecase = ConnectionsBelowPriceUseCase(service, cheapestConnection)

                usecase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 170
                        ))
                        .test()
                        .assertValues(
                                ConnectionsBelowPriceUseCaseOutput(
                                        connections = listOf(
                                                MultipleConnections(
                                                        listOf("NUE", "FRA", "LHR"),
                                                        70)
                                        )),
                                ConnectionsBelowPriceUseCaseOutput(
                                        connections = listOf(
                                                MultipleConnections(
                                                        listOf("NUE", "FRA", "LHR"),
                                                        70),
                                                MultipleConnections(
                                                        listOf("NUE", "FRA", "LHR", "NUE", "FRA", "LHR"),
                                                        163)
                                        )
                                ))
                        .assertComplete()

            }

            should("halt when the cheapest connection is over maximum price") {
                val service: IConnectionService = buildService()
                val cheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 60))
                        .test()
                        .assertValues(
                                ConnectionsBelowPriceUseCaseOutput(emptyList<MultipleConnections>())
                        )
                        .assertComplete()
            }

            should("return the existing connections ordered below some price even if the destination and origin is the same") {
                val service: IConnectionService = buildService()
                val cheapestConnectionUseCase = CheapestConnectionUseCase(service)
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "LHR",
                                to = "LHR",
                                price = 100))
                        .test()
                        .assertValues(
                                ConnectionsBelowPriceUseCaseOutput(
                                        connections = listOf(
                                                MultipleConnections(
                                                        listOf("LHR"),
                                                        0)
                                        )),
                                ConnectionsBelowPriceUseCaseOutput(
                                        connections = listOf(
                                                MultipleConnections(
                                                        listOf("LHR"),
                                                        0),
                                                MultipleConnections(
                                                        listOf("LHR", "NUE", "FRA", "LHR"),
                                                        93
                                                )
                                        )
                                ))
                        .assertComplete()

            }

        }
    }
}

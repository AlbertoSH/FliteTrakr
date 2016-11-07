package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.nhaarman.mockito_kotlin.any
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.kotlintest.specs.ShouldSpec
import io.reactivex.Single
import org.mockito.Mockito

class ConnectionsBelowPriceUseCaseTest : ShouldSpec() {

    init {
        "ConnectionsBelowPriceUseCase" {
            should("throw an error if a connection doesn't exist") {
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                // Force that there is no connection
                `when`(cheapestConnectionUseCase.execute(any()))
                        .thenReturn(Single.error(CheapestConnectionUseCaseError.connectionNotFound))

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
                val service: IConnectionService = mockService()
                val cheapestConnection = mock<ICheapestConnectionUseCase>()
                val usecase = ConnectionsBelowPriceUseCase(service, cheapestConnection)

                `when`(cheapestConnection.execute(any()))
                        .thenReturn(Single.just(CheapestConnectionUseCaseOutput(emptyList(), 0)))

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
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                `when`(
                        cheapestConnectionUseCase.execute(any()))
                        .thenReturn(Single.just(CheapestConnectionUseCaseOutput(emptyList(), 200)))

                useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 170))
                        .test()
                        .assertValues(
                                ConnectionsBelowPriceUseCaseOutput(emptyList<MultipleConnections>())
                        )
                        .assertComplete()

                Mockito.verifyZeroInteractions(service)
            }

            should("return the existing connections ordered below some price even if the destination and origin is the same") {
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(service, cheapestConnectionUseCase)

                // Force that at least a cheap connection exists
                `when`(
                        cheapestConnectionUseCase.execute(any()))
                        .thenReturn(Single.just(CheapestConnectionUseCaseOutput(emptyList(), 0)))

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

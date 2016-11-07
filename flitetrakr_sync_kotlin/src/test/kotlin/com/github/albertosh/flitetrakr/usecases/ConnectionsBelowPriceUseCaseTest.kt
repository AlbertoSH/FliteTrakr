package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.mock.`when`
import io.kotlintest.specs.ShouldSpec
import org.mockito.Mockito.verifyZeroInteractions

class ConnectionsBelowPriceUseCaseTest : ShouldSpec() {

    init {
        "ConnectionBelowPrice" {
            should("throw an error if a connection doesn't exist") {
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                // Force that there is no connection
                doThrow(
                        CheapestConnectionUseCaseError.connectionNotFound
                ).`when`(cheapestConnectionUseCase).execute(any())

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
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                // Force that at least a cheap connection exists
                `when`(
                        cheapestConnectionUseCase.execute(any()))
                        .thenReturn(CheapestConnectionUseCaseOutput(listOf(), 0))

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
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                `when`(
                        cheapestConnectionUseCase.execute(any()))
                        .thenReturn(CheapestConnectionUseCaseOutput(listOf(), 200))

                val output = useCase.execute(
                        ConnectionsBelowPriceUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                price = 170))

                output shouldBe ConnectionsBelowPriceUseCaseOutput(emptyList())
                verifyZeroInteractions(service)
            }

            should("return the existing connections ordered below some price even if the destination and origin is the same") {
                val service: IConnectionService = mockService()
                val cheapestConnectionUseCase = mock<ICheapestConnectionUseCase>()
                val useCase = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, service)

                // Force that at least a cheap connection exists
                `when`(
                        cheapestConnectionUseCase.execute(any()))
                        .thenReturn(CheapestConnectionUseCaseOutput(listOf(), 0))

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

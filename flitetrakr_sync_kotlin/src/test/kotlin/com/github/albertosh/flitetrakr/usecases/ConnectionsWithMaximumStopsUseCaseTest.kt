package com.github.albertosh.flitetrakr.usecases

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.kotlintest.mock.`when`
import io.kotlintest.specs.ShouldSpec

class ConnectionsWithMaximumStopsUseCaseTest : ShouldSpec() {

    init {
        "ConnectionsWithMaximumStopsUseCase" {

            should("delegate its job to ConnectionsWithExactStopsUseCase") {
                val exactStopsUseCase = mock<IConnectionsWithExactStopsUseCase>()
                val usecase = ConnectionsWithMaximumStopsUseCase(exactStopsUseCase)

                `when`(
                        exactStopsUseCase.execute(any()))
                        .thenReturn(ConnectionsWithExactStopsUseCaseOutput(0))

                val stops = 5
                val from = "LHR"
                val to = "NUE"

                val output = usecase.execute(
                        ConnectionsWithMaximumStopsUseCaseInput(
                                from,
                                to,
                                stops
                        )
                )
                output shouldBe ConnectionsWithMaximumStopsUseCaseOutput(0)
                for (i in 0..stops) {
                    verify(exactStopsUseCase)
                            .execute(ConnectionsWithExactStopsUseCaseInput(
                                    from,
                                    to,
                                    stops
                            ))
                }
            }

        }
    }
}

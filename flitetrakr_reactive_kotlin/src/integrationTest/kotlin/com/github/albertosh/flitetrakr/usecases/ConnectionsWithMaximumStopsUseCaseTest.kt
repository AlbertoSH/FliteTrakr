package com.github.albertosh.flitetrakr.usecases

import io.kotlintest.specs.ShouldSpec

class ConnectionsWithMaximumStopsUseCaseTest : ShouldSpec() {

    init {
        "ConnectionsWithMaximumStopsUseCase" {

            should("calculate how many connections with at most X stops there are between two cities") {
                val service = buildService()
                val exactStopsUseCase = ConnectionsWithExactStopsUseCase(service)
                val usecase = ConnectionsWithMaximumStopsUseCase(exactStopsUseCase)

                val stops = 3
                val from = "LHR"
                val to = "NUE"


                usecase.execute(
                        ConnectionsWithMaximumStopsUseCaseInput(
                                from,
                                to,
                                stops
                        ))
                        .test()
                        .assertValues(ConnectionsWithMaximumStopsUseCaseOutput(2))
            }

        }
    }
}

package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.specs.ShouldSpec

class ConnectionsWithExactStopsUseCaseTest : ShouldSpec() {

    init {
        "ConnectionsWithExactStopsUseCase" {

            should("return 1 if there is a connection without stops") {
                val service: IConnectionService = buildService()
                val usecase = ConnectionsWithExactStopsUseCase(service)
                usecase.execute(
                        ConnectionsWithExactStopsUseCaseInput(
                                from = "LHR",
                                to = "NUE",
                                stops = 0
                        ))
                        .test()
                        .assertValues(ConnectionsWithExactStopsUseCaseOutput(1))
            }

            should("return 1 if there is a connection with 1 stops") {
                val service: IConnectionService = buildService()
                val usecase = ConnectionsWithExactStopsUseCase(service)
                usecase.execute(
                        ConnectionsWithExactStopsUseCaseInput(
                                from = "LHR",
                                to = "AMS",
                                stops = 1
                        ))
                        .test()
                        .assertValues(ConnectionsWithExactStopsUseCaseOutput(1))
            }

            should("return 0 if there isn't a direct connection without stops") {
                val service: IConnectionService = buildService()
                val usecase = ConnectionsWithExactStopsUseCase(service)
                usecase.execute(
                        ConnectionsWithExactStopsUseCaseInput(
                                from = "NUE",
                                to = "LHR",
                                stops = 0
                        ))
                        .test()
                        .assertValues(ConnectionsWithExactStopsUseCaseOutput(0))
            }

            should("calculate how many connections with exact stops are in a general case") {
                val service: IConnectionService = buildService()
                val usecase = ConnectionsWithExactStopsUseCase(service)
                usecase.execute(
                        ConnectionsWithExactStopsUseCaseInput(
                                from = "NUE",
                                to = "FRA",
                                stops = 2
                        ))
                        .test()
                        .assertValues(ConnectionsWithExactStopsUseCaseOutput(0))
            }
        }
    }
}

package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.specs.ShouldSpec

class CheapestConnectionUseCaseTest : ShouldSpec() {

    init {
        "CheapestConnectionUseCase" {

            should("throw an error if the connection doesn't exist") {
                val service: IConnectionService = buildService()
                val usecase = CheapestConnectionUseCase(service)

                usecase.execute(
                        CheapestConnectionUseCaseInput(
                                from = "NUE",
                                to = "DXB"
                        ))
                        .test()
                        .assertError(CheapestConnectionUseCaseError.connectionNotFound)

            }

            should("return the minimum price of the connection") {
                val service: IConnectionService = buildService()
                val usecase = CheapestConnectionUseCase(service)

                usecase.execute(
                        CheapestConnectionUseCaseInput(
                                from = "NUE",
                                to = "AMS"
                        ))
                        .test()
                        .assertValue(
                                CheapestConnectionUseCaseOutput(
                                        cities = listOf("NUE", "FRA", "AMS"),
                                        price = 60
                                ))
            }

            should("look for a connection around the globe if the origin and destination is the same") {
                val service: IConnectionService = buildService()
                val usecase = CheapestConnectionUseCase(service)

                usecase.execute(
                        CheapestConnectionUseCaseInput(
                                from = "LHR",
                                to = "LHR"
                        ))
                        .test()
                        .assertValue(CheapestConnectionUseCaseOutput(
                                cities = listOf("LHR", "NUE", "FRA", "LHR"),
                                price = 93
                        ))
            }

        }
    }
}

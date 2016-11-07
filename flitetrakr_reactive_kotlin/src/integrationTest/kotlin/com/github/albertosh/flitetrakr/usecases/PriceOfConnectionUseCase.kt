package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.specs.ShouldSpec

class PriceOfConnectionUseCaseTest : ShouldSpec() {

    init {
        "PriceOfConnection" {
            should("throw an error if a connection doesn't exist") {
                val service: IConnectionService = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                useCase.execute(
                        PriceOfConnectionUseCaseInput(
                                listOf("NUE", "DBX")
                        ))
                        .test()
                        .assertError(PriceOfConnectionUseCaseError.connectionNotFound)
            }

            should("return the price of a connection between two cities") {
                val service: IConnectionService = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                useCase.execute(
                        PriceOfConnectionUseCaseInput(
                                listOf("NUE", "FRA")))
                        .test()
                        .assertValue(PriceOfConnectionUseCaseOutput(43))
            }

            should("return the price of a connection between several cities") {
                val service: IConnectionService = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                useCase.execute(
                        PriceOfConnectionUseCaseInput(
                                listOf("NUE", "FRA", "LHR")))
                        .test()
                        .assertValue(PriceOfConnectionUseCaseOutput(70))
            }
        }
    }
}

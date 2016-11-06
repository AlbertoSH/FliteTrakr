package com.github.albertosh.flitetrakr.usecases

import io.kotlintest.specs.ShouldSpec

class PriceOfConnectionUseCaseTest : ShouldSpec() {

    init {
        "PriceOfConnection" {
            should("throw an error if a connection doesn't exist") {
                val service = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                val exception: PriceOfConnectionUseCaseError? =
                        try {
                            useCase.execute(
                                    PriceOfConnectionUseCaseInput(
                                            listOf("NUE", "DBX")
                                    ))
                            null
                        } catch (e: PriceOfConnectionUseCaseError) {
                            e
                        }

                exception shouldBe PriceOfConnectionUseCaseError.connectionNotFound
            }

            should("return the price of a connection between two cities") {
                val service = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                val price = useCase.execute(
                        PriceOfConnectionUseCaseInput(
                                listOf("NUE", "FRA")))

                price shouldBe PriceOfConnectionUseCaseOutput(43)
            }

            should("return the price of a connection between several cities") {
                val service = buildService()
                val useCase = PriceOfConnectionUseCase(service)

                val price = useCase.execute(
                        PriceOfConnectionUseCaseInput(
                                listOf("NUE", "FRA", "LHR")))

                price shouldBe PriceOfConnectionUseCaseOutput(70)
            }
        }
    }
}

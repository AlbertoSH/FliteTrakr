package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.specs.ShouldSpec

class CheapestConnectionUseCaseTest : ShouldSpec() {

    init {
        "CheapestConnectionUseCase" {

            should("throw an error if the connection doesn't exist") {
                val service: IConnectionService = mockService()
                val usecase = CheapestConnectionUseCase(service)

                val exception: CheapestConnectionUseCaseError? =
                        try {
                            usecase.execute(
                                    CheapestConnectionUseCaseInput(
                                            from = "NUE",
                                            to = "DXB"
                                    ))
                            null
                        } catch (e: CheapestConnectionUseCaseError) {
                            e
                        }

                exception shouldBe CheapestConnectionUseCaseError.connectionNotFound
            }

            should("return the minimum price of the connection") {
                val service: IConnectionService = mockService()
                val usecase = CheapestConnectionUseCase(service)

                val output = usecase.execute(
                        CheapestConnectionUseCaseInput(
                                from = "NUE",
                                to = "AMS"
                        ))

                output shouldBe CheapestConnectionUseCaseOutput(
                        cities = listOf("NUE", "FRA", "AMS"),
                        price = 60
                )
            }

            should("look for a connection around the globe if the origin and destination is the same") {
                val service: IConnectionService = mockService()
                val usecase = CheapestConnectionUseCase(service)

                val output = usecase.execute(
                        CheapestConnectionUseCaseInput(
                                from = "LHR",
                                to = "LHR"
                        ))

                output shouldBe CheapestConnectionUseCaseOutput(
                        cities = listOf("LHR", "NUE", "FRA", "LHR"),
                        price = 93
                )
            }

        }
    }
}

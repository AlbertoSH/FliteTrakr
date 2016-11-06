package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService

interface IConnectionsWithExactStopsUseCase {

    fun execute(input: ConnectionsWithExactStopsUseCaseInput): ConnectionsWithExactStopsUseCaseOutput

}

data class ConnectionsWithExactStopsUseCaseInput(val from: String, val to: String, val stops: Int)

data class ConnectionsWithExactStopsUseCaseOutput(val connections: Int)

class ConnectionsWithExactStopsUseCase(
        private val service: IConnectionService)
: IConnectionsWithExactStopsUseCase {

    override fun execute(input: ConnectionsWithExactStopsUseCaseInput): ConnectionsWithExactStopsUseCaseOutput {
        val stops = applyRecursion(input)
        return ConnectionsWithExactStopsUseCaseOutput(stops)
    }

    private fun applyRecursion(input: ConnectionsWithExactStopsUseCaseInput): Int =
            if (input.stops === 0) {
                if (service.getDestiniesFromCity(input.from).contains(input.to))
                    1
                else
                    0
            } else {
                service.getDestiniesFromCity(input.from)
                        .map {
                            ConnectionsWithExactStopsUseCaseInput(
                                    from = it,
                                    to = input.to,
                                    stops = input.stops - 1
                            )
                        }
                        .map { applyRecursion(it) }
                        .filter { it > 0 }
                        .fold(0) { acc, value -> acc + value }
            }
}

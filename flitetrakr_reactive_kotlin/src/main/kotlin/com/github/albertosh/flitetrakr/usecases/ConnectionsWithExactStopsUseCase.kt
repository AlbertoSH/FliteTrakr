package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import io.reactivex.Single

interface IConnectionsWithExactStopsUseCase {

    fun execute(input: ConnectionsWithExactStopsUseCaseInput): Single<ConnectionsWithExactStopsUseCaseOutput>

}

data class ConnectionsWithExactStopsUseCaseInput(val from: String, val to: String, val stops: Int)

data class ConnectionsWithExactStopsUseCaseOutput(val connections: Int)

class ConnectionsWithExactStopsUseCase(
        private val service: IConnectionService)
: IConnectionsWithExactStopsUseCase {

    override fun execute(input: ConnectionsWithExactStopsUseCaseInput): Single<ConnectionsWithExactStopsUseCaseOutput> {
        return applyRecursion(input)
                .map { ConnectionsWithExactStopsUseCaseOutput(it) }
    }

    private fun applyRecursion(input: ConnectionsWithExactStopsUseCaseInput): Single<Int> =
            if (input.stops === 0) {
                service.getDestiniesFromCity(input.from)
                        .filter { it == input.to }
                        .count()
                        .map { it.toInt() }
            } else {
                service.getDestiniesFromCity(input.from)
                        .map {
                            ConnectionsWithExactStopsUseCaseInput(
                                    from = it,
                                    to = input.to,
                                    stops = input.stops - 1
                            )
                        }
                        .flatMap { applyRecursion(it).toFlowable() }
                        .filter { it > 0 }
                        .reduce(0) { acc, value -> acc + value }
            }
}

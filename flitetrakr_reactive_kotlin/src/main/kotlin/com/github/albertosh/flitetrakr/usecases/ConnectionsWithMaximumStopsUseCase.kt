package com.github.albertosh.flitetrakr.usecases

import io.reactivex.Flowable
import io.reactivex.Single

interface IConnectionsWithMaximumStopsUseCase {

    fun execute(input: ConnectionsWithMaximumStopsUseCaseInput): Single<ConnectionsWithMaximumStopsUseCaseOutput>

}

data class ConnectionsWithMaximumStopsUseCaseInput(val from: String, val to: String, val stops: Int)

data class ConnectionsWithMaximumStopsUseCaseOutput(val connections: Int)

class ConnectionsWithMaximumStopsUseCase(
        private val connectionsWithExactStopsUseCase: IConnectionsWithExactStopsUseCase)
: IConnectionsWithMaximumStopsUseCase {

    override fun execute(input: ConnectionsWithMaximumStopsUseCaseInput)
            : Single<ConnectionsWithMaximumStopsUseCaseOutput> =

            Flowable.range(0, input.stops + 1)
                    .map {
                        ConnectionsWithExactStopsUseCaseInput(
                                from = input.from,
                                to = input.to,
                                stops = it
                        )
                    }
                    .flatMap { connectionsWithExactStopsUseCase.execute(it).toFlowable() }
                    .map { it.connections }
                    .reduce { acc, stops -> acc + stops }
                    .map { ConnectionsWithMaximumStopsUseCaseOutput(it) }
                    .toSingle()

}

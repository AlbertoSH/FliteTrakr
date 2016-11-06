package com.github.albertosh.flitetrakr.usecases

interface IConnectionsWithMaximumStopsUseCase {

    fun execute(input: ConnectionsWithMaximumStopsUseCaseInput): ConnectionsWithMaximumStopsUseCaseOutput

}

data class ConnectionsWithMaximumStopsUseCaseInput(val from: String, val to: String, val stops: Int)

data class ConnectionsWithMaximumStopsUseCaseOutput(val connections: Int)

class ConnectionsWithMaximumStopsUseCase(
        private val connectionsWithExactStopsUseCase: IConnectionsWithExactStopsUseCase)
: IConnectionsWithMaximumStopsUseCase {

    override fun execute(input: ConnectionsWithMaximumStopsUseCaseInput)
            : ConnectionsWithMaximumStopsUseCaseOutput {

        return ConnectionsWithMaximumStopsUseCaseOutput(
                IntProgression.fromClosedRange(0, input.stops, 1)
                        .map {
                            ConnectionsWithExactStopsUseCaseInput(
                                    from = input.from,
                                    to = input.to,
                                    stops = it
                            )
                        }
                        .map { connectionsWithExactStopsUseCase.execute(it) }
                        .map { it.connections }
                        .reduce { acc, stops -> acc + stops }
        )
    }

}

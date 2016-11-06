package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import java.util.*

interface IConnectionsBelowPriceUseCase {

    fun execute(input: ConnectionsBelowPriceUseCaseInput): ConnectionsBelowPriceUseCaseOutput

}

data class ConnectionsBelowPriceUseCaseInput(val from: String, val to: String, val price: Int)

data class ConnectionsBelowPriceUseCaseOutput(val connections: List<MultipleConnections>)

sealed class ConnectionsBelowPriceUseCaseError(message : String) : RuntimeException(message){
    object connectionNotFound : ConnectionsBelowPriceUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class ConnectionsBelowPriceUseCase(
        private val cheapestConnectionUseCase: ICheapestConnectionUseCase,
        private val service: IConnectionService)
: IConnectionsBelowPriceUseCase {

    override fun execute(input: ConnectionsBelowPriceUseCaseInput): ConnectionsBelowPriceUseCaseOutput {

        val cheapestPrice = calculateCheapestConnection(input)
        if (cheapestPrice > input.price) {
            // The cheapest connection is more expensive than our output
            // We're done...
            return ConnectionsBelowPriceUseCaseOutput(emptyList())
        } else {
            val connections = performDPSSearch(input)

            return ConnectionsBelowPriceUseCaseOutput(
                    connections.sortedBy { it.price }
            )
        }
    }

    private fun calculateCheapestConnection(input: ConnectionsBelowPriceUseCaseInput): Int =
            try {
                cheapestConnectionUseCase.execute(
                        CheapestConnectionUseCaseInput(
                                from = input.from,
                                to = input.to
                        )
                ).price
            } catch (error: CheapestConnectionUseCaseError) {
                when (error) {
                    is CheapestConnectionUseCaseError.connectionNotFound ->
                        throw ConnectionsBelowPriceUseCaseError.connectionNotFound
                }
            }

    private fun performDPSSearch(input: ConnectionsBelowPriceUseCaseInput): List<MultipleConnections> {
        val result = mutableListOf<MultipleConnections>()
        val visitedCities = mutableListOf(input.from)

        // Concrete case of starting and ending at the same place
        if (input.from == input.to) {
            result.add(MultipleConnections(listOf(input.from), 0))
        }

        performDPSSearch(input.from, input.to, input.price, result, 0, visitedCities)

        return result
    }

    private fun performDPSSearch(from: String, to: String, availableMoney: Int,
                                 result: MutableList<MultipleConnections>,
                                 alreadyPaidMoney: Int,
                                 visitedCities: MutableList<String>) {

        service.getDestiniesFromCity(from)
                .map { service.recoverConnection(from, it)!! }
                .filter { it.price <= availableMoney }
                .forEach {
                    val city = it.to
                    val newVisitedCities = ArrayList(visitedCities)
                    newVisitedCities.add(city)
                    val newAlreadyPaidMoney = alreadyPaidMoney + it.price
                    if (city == to) {
                        result.add(MultipleConnections(newVisitedCities, newAlreadyPaidMoney))
                    }

                    performDPSSearch(
                            from = city,
                            to = to,
                            availableMoney = availableMoney - it.price,
                            result = result,
                            alreadyPaidMoney = newAlreadyPaidMoney,
                            visitedCities = newVisitedCities)
                }

    }

}



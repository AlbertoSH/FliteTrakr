package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.MultipleConnections
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import io.reactivex.Flowable
import io.reactivex.SingleSource
import org.reactivestreams.Publisher

interface IConnectionsBelowPriceUseCase {

    fun execute(input: ConnectionsBelowPriceUseCaseInput): Flowable<ConnectionsBelowPriceUseCaseOutput>

}

data class ConnectionsBelowPriceUseCaseInput(val from: String, val to: String, val price: Int)

data class ConnectionsBelowPriceUseCaseOutput(val connections: List<MultipleConnections>)

sealed class ConnectionsBelowPriceUseCaseError(message: String) : RuntimeException(message) {
    object connectionNotFound : ConnectionsBelowPriceUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class ConnectionsBelowPriceUseCase(
        private val service: IConnectionService,
        private val cheapestConnectionUseCase: ICheapestConnectionUseCase)
: IConnectionsBelowPriceUseCase {

    override fun execute(input: ConnectionsBelowPriceUseCaseInput): Flowable<ConnectionsBelowPriceUseCaseOutput> =
            calculateCheapestConnection(input)
                    .onErrorResumeNext { error ->
                        when (error) {
                            is CheapestConnectionUseCaseError.connectionNotFound -> SingleSource {
                                it.onError(ConnectionsBelowPriceUseCaseError.connectionNotFound)
                            }
                            else -> SingleSource {
                                it.onError(error)
                            }
                        }
                    }
                    .toFlowable()
                    .flatMap {
                        if (it > input.price) {
                            Publisher<ConnectionsBelowPriceUseCaseOutput> {
                                it.onNext(ConnectionsBelowPriceUseCaseOutput(emptyList()))
                                it.onComplete()
                            }
                        } else {
                            performDPSSearch(input)
                                    .scan(emptyList<MultipleConnections>(), { accum, newValue ->
                                        accum.plus(newValue).sorted()
                                    })
                                    .skip(1) // Ignore first empty list
                                    .map { ConnectionsBelowPriceUseCaseOutput(it) }
                        }
                    }


    private fun calculateCheapestConnection(input: ConnectionsBelowPriceUseCaseInput) =
            cheapestConnectionUseCase.execute(
                    CheapestConnectionUseCaseInput(
                            from = input.from,
                            to = input.to
                    ))
                    .map { it.price }


    private fun performDPSSearch(input: ConnectionsBelowPriceUseCaseInput): Flowable<MultipleConnections> {
        val connections = MultipleConnections(mutableListOf(input.from), 0)
        val firstItem = if (input.from == input.to)
            Flowable.just(connections)
        else
            Flowable.empty()
        return Flowable.concat(firstItem, performDPSSearch(input.from, input.to, input.price, connections))
    }


    private fun performDPSSearch(from: String, to: String, price: Int,
                                 connections: MultipleConnections): Flowable<MultipleConnections> =
            service.getDestiniesFromCity(from)
                    .flatMap { city ->
                        service.recoverConnection(from, city)
                                .filter { it.price <= price }
                                .toFlowable()
                                .flatMap {
                                    val newConnections = connections.copy(
                                            cities = connections.cities.plus(city),
                                            price = connections.price + it.price
                                    )

                                    if (city == to)
                                        Flowable.concat(
                                                Flowable.just(newConnections),
                                                performDPSSearch(city, to,
                                                        price - it.price, newConnections)
                                        )
                                    else
                                        performDPSSearch(city, to,
                                                price - it.price, newConnections)

                                }

                    }

}

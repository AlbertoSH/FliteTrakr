package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.ConnectionServiceError
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleSource

interface IPriceOfConnectionUseCase {

    fun execute(input: PriceOfConnectionUseCaseInput): Single<PriceOfConnectionUseCaseOutput>

}

data class PriceOfConnectionUseCaseInput(val codes: List<String>)

data class PriceOfConnectionUseCaseOutput(val price: Int)

sealed class PriceOfConnectionUseCaseError(message: String) : RuntimeException(message) {
    object connectionNotFound : PriceOfConnectionUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class PriceOfConnectionUseCase(
        private val service: IConnectionService)
: IPriceOfConnectionUseCase {

    override fun execute(input: PriceOfConnectionUseCaseInput): Single<PriceOfConnectionUseCaseOutput> {

        val codes = input.codes

        val pairedCities = codes
                .subList(1, codes.size)
                .mapIndexed { i, city -> Pair(codes[i], city) }

        return Flowable.fromIterable(pairedCities)
                .flatMap {
                    service.recoverConnection(it.first, it.second)
                            .toFlowable()
                }
                .map { it.price }
                .reduce { acc, price -> acc + price }
                .map { PriceOfConnectionUseCaseOutput(it) }
                .toSingle()
                .onErrorResumeNext { error ->
                    when (error) {
                        is ConnectionServiceError.connectionNotFound ->
                            SingleSource {
                                it.onError(PriceOfConnectionUseCaseError.connectionNotFound)
                            }
                        else ->
                            SingleSource {
                                it.onError(error)
                            }
                    }
                }

    }

}
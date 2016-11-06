package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message

interface IPriceOfConnectionUseCase {

    fun execute(input: PriceOfConnectionUseCaseInput): PriceOfConnectionUseCaseOutput

}

data class PriceOfConnectionUseCaseInput(val codes: List<String>)

data class PriceOfConnectionUseCaseOutput(val price: Int)

sealed class PriceOfConnectionUseCaseError(message : String) : RuntimeException(message){
    object connectionNotFound : PriceOfConnectionUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class PriceOfConnectionUseCase(
        private val service: IConnectionService)
: IPriceOfConnectionUseCase {

    override fun execute(input: PriceOfConnectionUseCaseInput): PriceOfConnectionUseCaseOutput {

        val codes = input.codes

        val price = codes
                .subList(1, codes.size)
                .mapIndexed { i, city -> Pair(codes[i], city) }
                .map {
                    service.recoverConnection(it.first, it.second)
                            ?: throw PriceOfConnectionUseCaseError.connectionNotFound
                }
                .map { it.price }
                .reduce { acc, price -> acc + price }

        return PriceOfConnectionUseCaseOutput(price)
    }

}
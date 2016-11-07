package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import java.util.*

interface ICheapestConnectionUseCase {

    fun execute(input: CheapestConnectionUseCaseInput): CheapestConnectionUseCaseOutput

}

data class CheapestConnectionUseCaseInput(val from: String, val to: String)

data class CheapestConnectionUseCaseOutput(val cities: List<String>, val price: Int)

sealed class CheapestConnectionUseCaseError(message: String) : RuntimeException(message) {
    object connectionNotFound : CheapestConnectionUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class CheapestConnectionUseCase(
        private val service: IConnectionService)
: ICheapestConnectionUseCase {

    override fun execute(input: CheapestConnectionUseCaseInput): CheapestConnectionUseCaseOutput {
        val distance = mutableMapOf<String, Int>()
        val father = mutableMapOf<String, String?>()
        val seen = mutableMapOf<String, Boolean>()
        service.getDestiniesFromCity(input.from)
                .forEach { distance[it] = Int.MAX_VALUE }

        val queue = PriorityQueue<Pair<String, Int>> { p1, p2 -> p1.second.compareTo(p2.second) }

        // Modification so the same start and end matches the specification
        if (input.from == input.to) {
            service.getDestiniesFromCity(input.from)
                    .forEach {
                        val c = service.recoverConnection(input.from, it)!!
                        queue.add(Pair(c.to, c.price))
                        distance[c.to] = c.price
                        father[it] = input.from
                    }
        } else {
            distance[input.from] = 0
            queue.add(Pair(input.from, 0))
        }

        while (queue.isNotEmpty()) {
            val minDistance = queue.poll()
            seen[minDistance.first] = true
            val connectedCities = service.getDestiniesFromCity(minDistance.first)
            connectedCities
                    .filter { !(seen[it] ?: false) }
                    .filter {
                        val connectionPrice = service.recoverConnection(minDistance.first, it)!!.price
                        (distance[it] ?: Int.MAX_VALUE > (distance[minDistance.first]!! + connectionPrice))
                    }
                    .forEach {
                        val connection = service.recoverConnection(minDistance.first, it)!!
                        distance[it] = distance[minDistance.first]!! + connection.price
                        father.put(it, minDistance.first)
                        queue.add(Pair(it, distance[it]!!))
                    }
        }

        if (father[input.to] == null) {
            throw CheapestConnectionUseCaseError.connectionNotFound
        } else {
            val citiesVisited = mutableListOf(input.to)
            var cityVisited = input.to
            do {
                cityVisited = father[cityVisited]!!
                citiesVisited.add(0, cityVisited)
            } while (cityVisited != input.from)

            return CheapestConnectionUseCaseOutput(citiesVisited, distance[input.to]!!)
        }
    }

}

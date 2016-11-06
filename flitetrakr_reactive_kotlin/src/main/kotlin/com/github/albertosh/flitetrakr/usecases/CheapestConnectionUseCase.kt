package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

interface ICheapestConnectionUseCase {

    fun execute(input: CheapestConnectionUseCaseInput): Single<CheapestConnectionUseCaseOutput>

}

data class CheapestConnectionUseCaseInput(val from : String, val to : String)

data class CheapestConnectionUseCaseOutput(val cities : List<String>, val price : Int)

sealed class CheapestConnectionUseCaseError(message : String) : RuntimeException(message){
    object connectionNotFound : CheapestConnectionUseCaseError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}


class CheapestConnectionUseCase(
        private val service: IConnectionService)
: ICheapestConnectionUseCase {

    override fun execute(input: CheapestConnectionUseCaseInput): Single<CheapestConnectionUseCaseOutput> {

        val distance = mutableMapOf<String, Int>()
        val father = mutableMapOf<String, String>()
        val seen = mutableMapOf<String, Boolean>()
        val queue = PriorityQueue<Pair<String, Int>> { o1, o2 -> o1.second.compareTo(o2.second) }

        return service.getCities()
                .doOnNext {
                    distance.put(it, Integer.MAX_VALUE)
                    seen.put(it, false)
                }
                .toList()
                .flatMap { list ->
                    if (input.from == input.to) {
                        service.getDestiniesFromCity(input.from)
                                .flatMap {
                                    service.recoverConnection(input.from, it)
                                            .toFlowable()
                                }
                                .doOnNext {
                                    queue.add(Pair(it.to, it.price))
                                    distance[it.to] = it.price
                                    father[it.to] = input.from
                                }
                                .toList()
                                .map { list }

                    } else {
                        distance[input.from] = 0
                        queue.add(Pair(input.from, 0))

                        Single.just(list)
                    }
                }
                .flatMapCompletable { round(distance, father, seen, queue) }
                .toSingle {
                    if (father[input.to] == null) {
                        throw CheapestConnectionUseCaseError.connectionNotFound
                    } else {
                        val citiesVisited = mutableListOf<String>()
                        var cityVisited : String? = input.to
                        citiesVisited.add(cityVisited!!)
                        do {
                            cityVisited = father[cityVisited]
                            cityVisited?.let {
                                citiesVisited.add(0, it)
                            }
                        } while (cityVisited != null && cityVisited != input.from)
                        CheapestConnectionUseCaseOutput(
                                citiesVisited, distance[input.to]!!
                        )
                    }

                }

    }

    private fun round(distance: MutableMap<String, Int>,
                      father: MutableMap<String, String>,
                      seen: MutableMap<String, Boolean>,
                      queue: PriorityQueue<Pair<String, Int>>): Completable {

        val minDistance = queue.poll()
        seen[minDistance.first] = true

        return service.getDestiniesFromCity(minDistance.first)
                .filter { !seen[it]!! }
                .flatMap { service.recoverConnection(minDistance.first, it).toFlowable() }
                .filter { distance[it.to]!! > (distance[minDistance.first]!! + it.price) }
                .doOnNext {
                    val city = it.to
                    distance[city] = distance[minDistance.first]!! + it.price
                    father[city] = minDistance.first
                    queue.add(Pair(city, distance[city]!!))
                }
                .toList()
                .flatMapCompletable {
                    if (queue.isEmpty())
                        Completable.complete()
                    else
                        round(distance, father, seen, queue)
                }

    }
}
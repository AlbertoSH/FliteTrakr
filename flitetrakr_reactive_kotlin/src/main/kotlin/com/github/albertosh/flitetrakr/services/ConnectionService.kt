package com.github.albertosh.flitetrakr.services

import com.github.albertosh.flitetrakr.model.Connection
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface IConnectionService {

    fun addConnection(connection: Connection): Completable

    fun recoverConnection(from: String, to: String): Single<Connection>

    fun getCities(): Flowable<String>

    fun getDestiniesFromCity(city: String): Flowable<String>

}

sealed class ConnectionServiceError(message: String) : RuntimeException(message) {
    object connectionNotFound : ConnectionServiceError(
            LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
}

/**
 * Basic in memory storage
 */
class ConnectionService : IConnectionService {

    private val storedCities = mutableSetOf<String>()
    private val fromConnections = mutableMapOf<String, MutableList<Connection>>()

    override fun getCities(): Flowable<String> {
        return Flowable.fromIterable(storedCities)
    }

    override fun addConnection(connection: Connection): Completable {
        return Completable.create {
            var cityConnections = fromConnections.get(connection.from)
            if (cityConnections == null) {
                cityConnections = mutableListOf<Connection>()
                fromConnections.put(connection.from, cityConnections)
            }
            cityConnections.add(connection)
            storedCities.add(connection.from)
            storedCities.add(connection.to)
        }
    }

    override fun recoverConnection(from: String, to: String): Single<Connection> {
        return Single.create { source ->
            val connection = fromConnections.get(from)
                    ?.firstOrNull { it.to == to }
            if (connection != null)
                source.onSuccess(connection)
            else
                source.onError(ConnectionServiceError.connectionNotFound)
        }
    }

    override fun getDestiniesFromCity(city: String): Flowable<String> {
        return Flowable.fromIterable(
                fromConnections.get(city)
                        ?.map { it.to }
                        ?.toSet()
                        ?: emptySet()
        )
    }
}
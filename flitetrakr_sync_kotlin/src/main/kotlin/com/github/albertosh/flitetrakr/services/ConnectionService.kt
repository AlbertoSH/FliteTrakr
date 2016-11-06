package com.github.albertosh.flitetrakr.services

import com.github.albertosh.flitetrakr.model.Connection

interface IConnectionService {

    fun addConnection(connection: Connection)

    fun recoverConnection(from: String, to: String): Connection?

    fun getCities(): Set<String>

    fun getDestiniesFromCity(city: String): Set<String>

}

/**
 * Basic in memory storage
 */
class ConnectionService : IConnectionService {

    private val storedCities = mutableSetOf<String>()
    private val fromConnections = mutableMapOf<String, MutableList<Connection>>()

    override fun getCities(): Set<String> {
        return storedCities
    }

    override fun addConnection(connection: Connection) {
        var cityConnections = fromConnections.get(connection.from)
        if (cityConnections == null) {
            cityConnections = mutableListOf<Connection>()
            fromConnections.put(connection.from, cityConnections)
        }
        cityConnections.add(connection)
        storedCities.add(connection.from)
        storedCities.add(connection.to)
    }

    override fun recoverConnection(from: String, to: String): Connection? {
        return fromConnections.get(from)
                ?.firstOrNull { it.to == to }
    }

    override fun getDestiniesFromCity(city: String): Set<String> {
        return fromConnections.get(city)
                ?.map { it.to }
                ?.toSet()
                ?: emptySet()
    }
}
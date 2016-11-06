package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.Connection
import com.github.albertosh.flitetrakr.services.ConnectionService
import com.github.albertosh.flitetrakr.services.IConnectionService

fun buildService(): IConnectionService {
    val service = ConnectionService()
    service.addConnection(Connection.fromString("NUE-FRA-43"))
    service.addConnection(Connection.fromString("NUE-AMS-67"))
    service.addConnection(Connection.fromString("FRA-AMS-17"))
    service.addConnection(Connection.fromString("FRA-LHR-27"))
    service.addConnection(Connection.fromString("LHR-NUE-23"))
    return service
}
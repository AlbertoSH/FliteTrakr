package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.Connection
import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

fun mockService(): IConnectionService {
    val service = mock<IConnectionService>()

    `when`(
            service.recoverConnection(anyString(), anyString()))
            .thenReturn(null)

    `when`(
            service.getCities())
            .thenReturn(setOf("NUE", "FRA", "AMS", "LHR", "DXB"))

    `when`(
            service.getDestiniesFromCity("NUE"))
            .thenReturn(setOf("FRA", "AMS"))
    `when`(
            service.recoverConnection("NUE", "FRA"))
            .thenReturn(Connection("NUE", "FRA", 43))
    `when`(
            service.recoverConnection("NUE", "AMS"))
            .thenReturn(Connection("NUE", "AMS", 67))

    `when`(service.getDestiniesFromCity("FRA"))
            .thenReturn(setOf("AMS", "LHR"))
    `when`(
            service.recoverConnection("FRA", "AMS"))
            .thenReturn(Connection("FRA", "AMS", 17))
    `when`(
            service.recoverConnection("FRA", "LHR"))
            .thenReturn(Connection("FRA", "LHR", 27))

    `when`(
            service.getDestiniesFromCity("AMS"))
            .thenReturn(setOf())

    `when`(
            service.getDestiniesFromCity("LHR"))
            .thenReturn(setOf("NUE"))
    `when`(
            service.recoverConnection("LHR", "NUE"))
            .thenReturn(Connection("LHR", "NUE", 23))

    return service
}
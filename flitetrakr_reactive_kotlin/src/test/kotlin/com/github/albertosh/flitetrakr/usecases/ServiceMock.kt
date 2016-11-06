package com.github.albertosh.flitetrakr.usecases

import com.github.albertosh.flitetrakr.model.Connection
import com.github.albertosh.flitetrakr.services.ConnectionServiceError
import com.github.albertosh.flitetrakr.services.IConnectionService
import io.kotlintest.mock.`when`
import io.kotlintest.mock.mock
import io.reactivex.Flowable
import io.reactivex.Single
import org.mockito.ArgumentMatchers.anyString

fun mockService(): IConnectionService {
    val service = mock<IConnectionService>()

    `when`(
            service.recoverConnection(anyString(), anyString()))
            .thenReturn(Single.error(ConnectionServiceError.connectionNotFound))

    `when`(
            service.getCities())
            .thenReturn(Flowable.fromIterable(setOf("NUE", "FRA", "AMS", "LHR", "DXB")))

    `when`(
            service.getDestiniesFromCity("NUE"))
            .thenReturn(Flowable.fromIterable(setOf("FRA", "AMS")))
    `when`(
            service.recoverConnection("NUE", "FRA"))
            .thenReturn(Single.just(Connection("NUE", "FRA", 43)))
    `when`(
            service.recoverConnection("NUE", "AMS"))
            .thenReturn(Single.just(Connection("NUE", "AMS", 67)))

    `when`(service.getDestiniesFromCity("FRA"))
            .thenReturn(Flowable.fromIterable(setOf("AMS", "LHR")))
    `when`(
            service.recoverConnection("FRA", "AMS"))
            .thenReturn(Single.just(Connection("FRA", "AMS", 17)))
    `when`(
            service.recoverConnection("FRA", "LHR"))
            .thenReturn(Single.just(Connection("FRA", "LHR", 27)))

    `when`(
            service.getDestiniesFromCity("AMS"))
            .thenReturn(Flowable.fromIterable(setOf()))

    `when`(
            service.getDestiniesFromCity("LHR"))
            .thenReturn(Flowable.fromIterable(setOf("NUE")))
    `when`(
            service.recoverConnection("LHR", "NUE"))
            .thenReturn(Single.just(Connection("LHR", "NUE", 23)))

    return service
}
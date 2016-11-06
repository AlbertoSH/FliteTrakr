package com.github.albertosh.flitetrakr.reactive.usecases;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionServiceError;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;

import rx.Observable;
import rx.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockService {

    public static IConnectionService doMock() {
        IConnectionService service = mock(IConnectionService.class);

        when(service.recoverConnection(any(), any()))
                .thenReturn(Single.error(ConnectionServiceError.connectionNotFound()));

        when(service.getCities())
                .thenReturn(Observable.just("NUE", "FRA", "AMS", "LHR", "DXB"));

        when(service.getDestiniesFromCity("NUE"))
                .thenReturn(Observable.just("FRA", "AMS"));
        when(service.recoverConnection("NUE", "FRA"))
                .thenReturn(Single.just(new Connection("NUE-FRA-43")));
        when(service.recoverConnection("NUE", "AMS"))
                .thenReturn(Single.just(new Connection("NUE-AMS-67")));

        when(service.getDestiniesFromCity("FRA"))
                .thenReturn(Observable.just("AMS", "LHR"));
        when(service.recoverConnection("FRA", "AMS"))
                .thenReturn(Single.just(new Connection("FRA-AMS-17")));
        when(service.recoverConnection("FRA", "LHR"))
                .thenReturn(Single.just(new Connection("FRA-LHR-27")));

        when(service.getDestiniesFromCity("AMS"))
                .thenReturn(Observable.empty());

        when(service.getDestiniesFromCity("LHR"))
                .thenReturn(Observable.just("NUE"));
        when(service.recoverConnection("LHR", "NUE"))
                .thenReturn(Single.just(new Connection("LHR-NUE-23")));

        return service;
    }
}

package com.github.albertosh.flitetrakr.sync.usecases;

import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.sync.services.IConnectionService;

import org.mockito.internal.util.collections.Sets;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockService {

    public static IConnectionService doMock() {
        IConnectionService service = mock(IConnectionService.class);

        when(service.recoverConnection(any(), any()))
                .thenReturn(Optional.empty());

        when(service.getCities())
                .thenReturn(Sets.newSet("NUE", "FRA", "AMS", "LHR", "DXB"));

        when(service.getDestiniesFromCity("NUE"))
                .thenReturn(Sets.newSet("FRA", "AMS"));
        when(service.recoverConnection("NUE", "FRA"))
                .thenReturn(Optional.of(Connection.fromString("NUE-FRA-43")));
        when(service.recoverConnection("NUE", "AMS"))
                .thenReturn(Optional.of(Connection.fromString("NUE-AMS-67")));

        when(service.getDestiniesFromCity("FRA"))
                .thenReturn(Sets.newSet("AMS", "LHR"));
        when(service.recoverConnection("FRA", "AMS"))
                .thenReturn(Optional.of(Connection.fromString("FRA-AMS-17")));
        when(service.recoverConnection("FRA", "LHR"))
                .thenReturn(Optional.of(Connection.fromString("FRA-LHR-27")));

        when(service.getDestiniesFromCity("AMS"))
                .thenReturn(Sets.newSet());

        when(service.getDestiniesFromCity("LHR"))
                .thenReturn(Sets.newSet("NUE"));
        when(service.recoverConnection("LHR", "NUE"))
                .thenReturn(Optional.of(Connection.fromString("LHR-NUE-23")));

        return service;
    }
}

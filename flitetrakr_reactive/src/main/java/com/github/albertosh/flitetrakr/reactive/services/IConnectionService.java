package com.github.albertosh.flitetrakr.reactive.services;

import com.github.albertosh.flitetrakr.model.Connection;

import rx.Observable;
import rx.Single;

public interface IConnectionService {

    Observable<Void> addConnection(Connection connection);

    Single<Connection> recoverConnection(String from, String to);

    Observable<String> getCities();

    Observable<String> getDestiniesFromCity(String city);

}

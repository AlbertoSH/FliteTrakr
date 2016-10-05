package com.github.albertosh.flitetrakr.sync.services;

import com.github.albertosh.flitetrakr.model.Connection;

import java.util.Optional;
import java.util.Set;

public interface IConnectionService {

    void addConnection(Connection connection);

    Optional<Connection> recoverConnection(String from, String to);

    Set<String> getCities();

    Set<String> getDestiniesFromCity(String city);

}

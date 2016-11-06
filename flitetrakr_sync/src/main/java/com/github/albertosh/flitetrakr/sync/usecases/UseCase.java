package com.github.albertosh.flitetrakr.sync.usecases;


public interface UseCase<I, O, E extends Throwable> {

    O execute(I input) throws E;

}

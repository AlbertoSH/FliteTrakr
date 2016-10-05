package com.github.albertosh.flitetrakr.input;

import java.util.Scanner;

class AppInput implements IAppInput {

    private Scanner scanner;

    public AppInput(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }


}

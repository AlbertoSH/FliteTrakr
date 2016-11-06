package com.github.albertosh.flitetrakr.input;

import java.util.Scanner;

public class StandardInput extends AppInput {

    public StandardInput() {
        super(new Scanner(System.in));
    }

}

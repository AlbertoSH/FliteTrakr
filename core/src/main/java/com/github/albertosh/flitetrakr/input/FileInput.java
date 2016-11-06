package com.github.albertosh.flitetrakr.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileInput extends AppInput {

    public FileInput(String fileName) throws FileNotFoundException {
        super(new Scanner(new File(fileName)));
    }
}

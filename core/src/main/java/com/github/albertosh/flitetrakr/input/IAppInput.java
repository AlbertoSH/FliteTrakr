package com.github.albertosh.flitetrakr.input;

import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

import java.io.FileNotFoundException;

public interface IAppInput {

    public static IAppInput setupInput(String[] args) {
        if (args.length > 0)
            try {
                return new FileInput(args[0]);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(LanguageUtils.getMessage(Message.INPUT_FILE_NOT_FOUND_ERROR));
            }
        else
            return new StandardInput();
    }


    boolean hasNext();

    String nextLine();

    void close();
}

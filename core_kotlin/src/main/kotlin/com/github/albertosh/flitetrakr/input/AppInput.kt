package com.github.albertosh.flitetrakr.input

import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import java.io.Closeable
import java.io.File
import java.io.FileNotFoundException
import java.util.*

interface IAppInput : Closeable {

    fun hasNext(): Boolean

    fun nextLine(): String

}

open class AppInput(private val scanner: Scanner) : IAppInput {

    companion object {
        fun setupInput(args: Array<String>): IAppInput {
            if (args.size > 0)
                try {
                    return FileInput(args[0])
                } catch (e: FileNotFoundException) {
                    throw IllegalStateException(LanguageUtils.getMessage(Message.INPUT_FILE_NOT_FOUND_ERROR))
                }
            else
                return StandardInput()
        }

    }

    override fun hasNext(): Boolean {
        return scanner.hasNext()
    }

    override fun nextLine(): String {
        return scanner.nextLine()
    }

    override fun close() {
        scanner.close()
    }

}

class FileInput(file: String) : AppInput(Scanner(File(file)))

class StandardInput() : AppInput(Scanner(System.`in`))

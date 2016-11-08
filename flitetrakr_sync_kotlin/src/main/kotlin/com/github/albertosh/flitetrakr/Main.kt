package com.github.albertosh.flitetrakr

import com.github.albertosh.flitetrakr.input.AppInput
import com.github.albertosh.flitetrakr.input.IAppInput
import com.github.albertosh.flitetrakr.model.Connection
import com.github.albertosh.flitetrakr.services.ConnectionService
import com.github.albertosh.flitetrakr.services.IConnectionService
import com.github.albertosh.flitetrakr.usecases.*
import com.github.albertosh.flitetrakr.util.language.LanguageUtils
import com.github.albertosh.flitetrakr.util.language.Message
import java.util.regex.Pattern

// We have an easy setup here. No need for Dependency Injection frameworks
private val connectionService: IConnectionService
        = ConnectionService()
private val cheapestConnectionUseCase: ICheapestConnectionUseCase
        = CheapestConnectionUseCase(connectionService)
private val connectionsBelowPriceUseCase: IConnectionsBelowPriceUseCase
        = ConnectionsBelowPriceUseCase(cheapestConnectionUseCase, connectionService)
private val connectionWithExactStopUseCase: IConnectionsWithExactStopsUseCase
        = ConnectionsWithExactStopsUseCase(connectionService)
private val connectionWithMaximumStopsUseCase: IConnectionsWithMaximumStopsUseCase
        = ConnectionsWithMaximumStopsUseCase(connectionWithExactStopUseCase)
private val priceOfConnectionUseCase: IPriceOfConnectionUseCase
        = PriceOfConnectionUseCase(connectionService)

private val cheapestPattern: Pattern by lazy {
    Pattern.compile(LanguageUtils.getMessage(Message.USECASE_CHEAPEST_CONNECTION))
}
private val belowPricePattern: Pattern by lazy {
    Pattern.compile(LanguageUtils.getMessage(Message.USECASE_FIND_ALL_CONNECTIONS))
}
private val exactStopsPattern: Pattern by lazy {
    Pattern.compile(LanguageUtils.getMessage(Message.USECASE_EXACT_STOPS))
}
private val maximumStopstPattern: Pattern by lazy {
    Pattern.compile(LanguageUtils.getMessage(Message.USECASE_MAXIMUM_STOPS))
}
private val connectionPricePattern: Pattern by lazy {
    Pattern.compile(LanguageUtils.getMessage(Message.USECASE_PRICE_OF_CONNECTION))
}

public fun main(args: Array<String>) {
    LanguageUtils.setLanguage("en", "US")

    val appInput = AppInput.setupInput(args)
    appInput.use {
        initConnections(appInput)
        while (appInput.hasNext()) {
            handleInput(appInput.nextLine())
        }
    }

}

private fun initConnections(appInput: IAppInput) {
    val fullInput = appInput.nextLine()
    val validStart = LanguageUtils.getMessage(Message.CONNECTIONS_INPUT)
    if (!fullInput.startsWith(validStart))
        throw IllegalArgumentException(LanguageUtils.getMessage(Message.CONNECTIONS_INPUT_ERROR))

    fullInput
            .replace(validStart.toRegex(), "")
            .split(delimiters = ",")
            .map(String::trim)
            .map { Connection.fromString(it) }
            .forEach { connectionService.addConnection(it) }

}


private fun handleInput(input: String) {
    var matcher = cheapestPattern.matcher(input)
    if (matcher.matches()) {
        executeCheapestConnectionUseCase(input)
        return
    }
    matcher = belowPricePattern.matcher(input)
    if (matcher.matches()) {
        executeConnectionsBelowPrice(input)
        return
    }
    matcher = exactStopsPattern.matcher(input)
    if (matcher.matches()) {
        executeExactStopsConnectionUseCase(input)
        return
    }
    matcher = maximumStopstPattern.matcher(input)
    if (matcher.matches()) {
        executeMaximumStopsConnectionUseCase(input)
        return
    }
    matcher = connectionPricePattern.matcher(input)
    if (matcher.matches()) {
        executePriceOfConnectionUseCase(input)
        return
    }

    System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + input)
}

private fun executePriceOfConnectionUseCase(inputString: String) {
    val regex = ".* ([A-Z]+(-[A-Z]+)+).*"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(inputString)
    if (!matcher.matches()) {
        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString)
    } else {
        val input = PriceOfConnectionUseCaseInput(
                codes = matcher.group(1)
                        .split("-")
        )

        try {
            val output = priceOfConnectionUseCase.execute(input)
            println(output.price)
        } catch (priceOfConnectionUseCaseError: PriceOfConnectionUseCaseError) {
            println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
        }

    }
}

private fun executeCheapestConnectionUseCase(inputString: String) {
    val regex = ".* ([A-Z]+) .* ([A-Z]+).*"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(inputString)
    if (!matcher.matches()) {
        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString)
    } else {
        val input = CheapestConnectionUseCaseInput(
                from = matcher.group(1),
                to = matcher.group(2)
        )

        try {
            val output = cheapestConnectionUseCase.execute(input)
            println(output.cities.joinToString(separator = "-")
                    + "-"
                    + output.price)
        } catch (cheapestConnectionUseCaseError: CheapestConnectionUseCaseError) {
            println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
        }

    }
}

private fun executeMaximumStopsConnectionUseCase(inputString: String) {
    val regex = ".* ([0-9]+) .* ([A-Z]+) .* ([A-Z]+).*"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(inputString)
    if (!matcher.matches()) {
        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString)
    } else {
        val input = ConnectionsWithMaximumStopsUseCaseInput(
                from = matcher.group(2),
                to = matcher.group(3),
                stops = matcher.group(1).toInt()
        )
        val output = connectionWithMaximumStopsUseCase.execute(input)
        println(output.connections)
    }
}

private fun executeExactStopsConnectionUseCase(inputString: String) {
    val regex = ".* ([0-9]+) .* ([A-Z]+) .* ([A-Z]+).*"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(inputString)
    if (!matcher.matches()) {
        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString)
    } else {
        val input = ConnectionsWithExactStopsUseCaseInput(
                from = matcher.group(2),
                to = matcher.group(3),
                stops = matcher.group(1).toInt()
        )
        val output = connectionWithExactStopUseCase.execute(input)
        println(output.connections)
    }
}

private fun executeConnectionsBelowPrice(inputString: String) {
    val regex = ".* ([A-Z]+) .* ([A-Z]+) .* ([0-9]+) ?[Ee]uro.*"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(inputString)
    if (!matcher.matches()) {
        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString)
    } else {
        val input = ConnectionsBelowPriceUseCaseInput(
                from = matcher.group(1),
                to = matcher.group(2),
                price = matcher.group(3).toInt()
        )
        try {
            val output = connectionsBelowPriceUseCase.execute(input)

            println(output.connections
                    .map { it.cities.joinToString(separator = "-") + "-" + it.price }
                    .joinToString()
            )
        } catch (connectionsBelowPriceUseCaseError: ConnectionsBelowPriceUseCaseError) {
            println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
        }

    }
}

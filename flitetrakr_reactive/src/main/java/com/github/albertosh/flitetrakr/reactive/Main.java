package com.github.albertosh.flitetrakr.reactive;

import com.github.albertosh.flitetrakr.input.IAppInput;
import com.github.albertosh.flitetrakr.model.Connection;
import com.github.albertosh.flitetrakr.model.MultipleConnections;
import com.github.albertosh.flitetrakr.reactive.services.ConnectionService;
import com.github.albertosh.flitetrakr.reactive.services.IConnectionService;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.CheapestConnectionUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.cheapestconnection.ICheapestConnectionUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice.ConnectionsBelowPriceUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice.ConnectionsBelowPriceUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionsbelowprice.IConnectionsBelowPriceUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.ConnectionWithExactStopUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithexactstop.IConnectionWithExactStopUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops.ConnectionWithMaximumStopUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops.ConnectionWithMaximumStopUseCaseInput;
import com.github.albertosh.flitetrakr.reactive.usecases.connectionwithmaximumstops.IConnectionWithMaximumStopsUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection.IPriceOfConnectionUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection.PriceOfConnectionUseCase;
import com.github.albertosh.flitetrakr.reactive.usecases.priceofconnection.PriceOfConnectionUseCaseInput;
import com.github.albertosh.flitetrakr.util.language.LanguageUtils;
import com.github.albertosh.flitetrakr.util.language.Message;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import rx.schedulers.Schedulers;

public class Main {

    private static IAppInput appInput;
    // We have an easy setup here. No need for Dependency Injection frameworks
    private static IConnectionService connectionService;
    private static ICheapestConnectionUseCase cheapestConnectionUseCase;
    private static IConnectionsBelowPriceUseCase connectionsBelowPriceUseCase;
    private static IConnectionWithExactStopUseCase connectionWithExactStopUseCase;
    private static IConnectionWithMaximumStopsUseCase connectionWithMaximumStopsUseCase;
    private static IPriceOfConnectionUseCase priceOfConnectionUseCase;

    private static Pattern cheapestPattern;
    private static Pattern belowPricePattern;
    private static Pattern exactStopsPattern;
    private static Pattern maximumStopstPattern;
    private static Pattern connectionPricePattern;

    public static void main(String[] args) {
        LanguageUtils.setLanguage("en", "US");

        initUseCases();
        appInput = IAppInput.setupInput(args);
        initConnections();
        initPatterns();
        while (appInput.hasNext()) {
            handleInput(appInput.nextLine());
        }
        appInput.close();

    }

    private static void initUseCases() {
        connectionService = new ConnectionService();
        cheapestConnectionUseCase = new CheapestConnectionUseCase(connectionService);
        connectionsBelowPriceUseCase = new ConnectionsBelowPriceUseCase(cheapestConnectionUseCase,
                connectionService);
        connectionWithExactStopUseCase = new ConnectionWithExactStopUseCase(connectionService);
        connectionWithMaximumStopsUseCase = new ConnectionWithMaximumStopUseCase(connectionWithExactStopUseCase);
        priceOfConnectionUseCase = new PriceOfConnectionUseCase(connectionService);
    }

    private static void initConnections() {
        String fullInput = appInput.nextLine();
        String validStart = LanguageUtils.getMessage(Message.CONNECTIONS_INPUT);
        if (!fullInput.startsWith(validStart))
            throw new IllegalArgumentException(LanguageUtils.getMessage(Message.CONNECTIONS_INPUT_ERROR));

        String[] connections = fullInput.replaceAll(validStart, "")
                .split(",");

        for (String c : connections) {
            connectionService.addConnection(new Connection(c.trim())).subscribe();
        }
    }

    private static void initPatterns() {
        cheapestPattern = Pattern.compile(LanguageUtils.getMessage(Message.USECASE_CHEAPEST_CONNECTION));
        belowPricePattern = Pattern.compile(LanguageUtils.getMessage(Message.USECASE_FIND_ALL_CONNECTIONS));
        exactStopsPattern = Pattern.compile(LanguageUtils.getMessage(Message.USECASE_EXACT_STOPS));
        maximumStopstPattern = Pattern.compile(LanguageUtils.getMessage(Message.USECASE_MAXIMUM_STOPS));
        connectionPricePattern = Pattern.compile(LanguageUtils.getMessage(Message.USECASE_PRICE_OF_CONNECTION));
    }

    private static void handleInput(String input) {
        Matcher matcher = cheapestPattern.matcher(input);
        if (matcher.matches()) {
            executeCheapestConnectionUseCase(input);
            return;
        }
        matcher = belowPricePattern.matcher(input);
        if (matcher.matches()) {
            executeConnectionsBelowPrice(input);
            return;
        }
        matcher = exactStopsPattern.matcher(input);
        if (matcher.matches()) {
            executeExactStopsConnectionUseCase(input);
            return;
        }
        matcher = maximumStopstPattern.matcher(input);
        if (matcher.matches()) {
            executeMaximumStopsConnectionUseCase(input);
            return;
        }
        matcher = connectionPricePattern.matcher(input);
        if (matcher.matches()) {
            executePriceOfConnectionUseCase(input);
            return;
        }

        System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + input);
    }

    private static void executePriceOfConnectionUseCase(String inputString) {
        String regex = ".* ([A-Z]+(-[A-Z]+)+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString);
        } else {
            PriceOfConnectionUseCaseInput.Builder inputBuilder = new PriceOfConnectionUseCaseInput.Builder();
            String[] codes = matcher.group(1).split("-");
            for (String code : codes)
                inputBuilder.addCode(code);
            PriceOfConnectionUseCaseInput input = inputBuilder.build();

            priceOfConnectionUseCase.execute(input)
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(Schedulers.immediate())
                    .subscribe(
                            System.out::println,
                            error ->
                                    System.out.println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND)));

        }
    }

    private static void executeCheapestConnectionUseCase(String inputString) {
        String regex = ".* ([A-Z]+) .* ([A-Z]+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString);
        } else {
            CheapestConnectionUseCaseInput input = new CheapestConnectionUseCaseInput.Builder()
                    .from(matcher.group(1))
                    .to(matcher.group(2))
                    .build();

            cheapestConnectionUseCase.execute(input)
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(Schedulers.immediate())
                    .subscribe(next ->
                                    System.out.println(
                                            String.join("-", next.getCities())
                                                    + "-"
                                                    + next.getPrice()),
                            error ->
                                    System.out.println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND))
                    );
        }
    }

    private static void executeMaximumStopsConnectionUseCase(String inputString) {
        String regex = ".* ([0-9]+) .* ([A-Z]+) .* ([A-Z]+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString);
        } else {
            ConnectionWithMaximumStopUseCaseInput input = new ConnectionWithMaximumStopUseCaseInput.Builder()
                    .from(matcher.group(2))
                    .to(matcher.group(3))
                    .stops(Integer.parseInt(matcher.group(1)))
                    .build();

            connectionWithMaximumStopsUseCase.execute(input)
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(Schedulers.immediate())
                    .subscribe(System.out::println);
        }
    }

    private static void executeExactStopsConnectionUseCase(String inputString) {
        String regex = ".* ([0-9]+) .* ([A-Z]+) .* ([A-Z]+).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString);
        } else {
            ConnectionWithExactStopUseCaseInput input = new ConnectionWithExactStopUseCaseInput.Builder()
                    .from(matcher.group(2))
                    .to(matcher.group(3))
                    .stops(Integer.parseInt(matcher.group(1)))
                    .build();

            connectionWithExactStopUseCase.execute(input)
                    .subscribeOn(Schedulers.immediate())
                    .observeOn(Schedulers.immediate())
                    .subscribe(System.out::println);
        }
    }

    private static void executeConnectionsBelowPrice(String inputString) {
        String regex = ".* ([A-Z]+) .* ([A-Z]+) .* ([0-9]+)[Ee]uro.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        if (!matcher.matches()) {
            System.err.println(LanguageUtils.getMessage(Message.UNKNOWN_COMMAND) + ": " + inputString);
        } else {
            ConnectionsBelowPriceUseCaseInput input = new ConnectionsBelowPriceUseCaseInput.Builder()
                    .from(matcher.group(1))
                    .to(matcher.group(2))
                    .price(Integer.parseInt(matcher.group(3)))
                    .build();

            try {
                // In this case the execution is parallel
                // We have to convert wait until it finishes with .toBlocking()
                List<MultipleConnections> output = connectionsBelowPriceUseCase.execute(input)
                        .toBlocking()
                        .last(); // We only care about last result
                System.out.println(
                        String.join(", ",
                                output.stream()
                                        .map(multipleConnections ->
                                                String.join("-", multipleConnections.getCities())
                                                        + "-"
                                                        + multipleConnections.getPrice())
                                        .collect(Collectors.toList())));
            } catch (Exception connectionsBelowPriceUseCaseError) {
                System.out.println(LanguageUtils.getMessage(Message.CONNECTION_NOT_FOUND));
            }

        }
    }
}
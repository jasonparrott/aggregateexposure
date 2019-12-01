package com.jasonparrott.aggregateexposure;

import java.time.LocalDate;

public class StaticBusinessDateService implements BusinessDateService {
    public static final LocalDate today = LocalDate.of(2019, 10, 26);
    public static final LocalDate previous = LocalDate.of(2019, 10, 25);

    @Override
    public LocalDate getToday() {
        return today;
    }

    @Override
    public LocalDate getPrevious() {
        return previous;
    }
}

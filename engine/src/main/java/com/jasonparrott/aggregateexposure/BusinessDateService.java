package com.jasonparrott.aggregateexposure;

import java.time.LocalDate;

public interface BusinessDateService {
    LocalDate getToday();

    LocalDate getPrevious();
}

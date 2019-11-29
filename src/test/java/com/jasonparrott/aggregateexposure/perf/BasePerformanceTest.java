package com.jasonparrott.aggregateexposure.perf;

import org.junit.Test;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public abstract class BasePerformanceTest {
    @Test
    public void executeJmhRunner() throws RunnerException {
        Options jmhRunnerOptions = new OptionsBuilder()
                .include(this.getClass().getName() + "\\.*")
                .warmupIterations(1)
                .measurementIterations(3)
                .forks(0)
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .resultFormat(ResultFormatType.JSON)
                .shouldFailOnError(true)
                .build();

        new Runner(jmhRunnerOptions).run();
    }
}

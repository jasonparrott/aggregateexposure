package com.jasonparrott.aggregateexposure;

import org.apache.commons.lang3.Validate;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;

public class ContainedWithinMatcher<T> extends TypeSafeMatcher<Collection<T>> {
    private final Collection<T> items;

    public ContainedWithinMatcher(Collection<T> items) {
        Validate.notNull(items);
        this.items = items;
    }

    public static Matcher<Collection> containedWithin(Collection items) {
        return new ContainedWithinMatcher(items);
    }

    @Override
    protected boolean matchesSafely(Collection<T> ts) {
        if (ts == null)
            return false;

        for (T t : ts) {
            if (!items.contains(t))
                return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("all items to be within ").appendValue(items);
    }
}

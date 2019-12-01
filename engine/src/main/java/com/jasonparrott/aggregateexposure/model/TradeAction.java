package com.jasonparrott.aggregateexposure.model;

public enum TradeAction {
    New, // new position as of T
    LateBooked, // Amended on T but affects T-1 (open)
    EarlyBooked, // Amended but affects sometime after T
    Cancel, // trade cancelled
    Amend, // trade amended for T
    Reset, // trade reset
}

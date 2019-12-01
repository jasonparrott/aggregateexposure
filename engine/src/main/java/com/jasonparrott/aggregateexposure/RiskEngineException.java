package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.position.Position;

public class RiskEngineException extends Exception {
    private final Position position;

    public RiskEngineException(Position position) {
        this.position = position;
    }

    public RiskEngineException(String message, Position position) {
        super(message);
        this.position = position;
    }

    public RiskEngineException(String message, Throwable cause, Position position) {
        super(message, cause);
        this.position = position;
    }

    public RiskEngineException(Throwable cause, Position position) {
        super(cause);
        this.position = position;
    }

    public RiskEngineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Position position) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}

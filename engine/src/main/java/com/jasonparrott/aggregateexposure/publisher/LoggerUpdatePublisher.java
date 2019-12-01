package com.jasonparrott.aggregateexposure.publisher;

import com.jasonparrott.aggregateexposure.UpdatePublisher;
import com.jasonparrott.aggregateexposure.model.SecurityGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUpdatePublisher implements UpdatePublisher {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void accept(SecurityGroup securityGroup) {
        logger.info("Update received for SecurityGroup %s, with metrics %s",
                securityGroup.getId(),
                securityGroup.getMetrics());
    }
}

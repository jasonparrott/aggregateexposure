package com.jasonparrott.aggregateexposure;

import com.jasonparrott.aggregateexposure.model.SecurityGroup;

import java.util.concurrent.ExecutorService;

public class DelegatingExecutorSecurityGroupUpdateManager implements SecurityGroupUpdateManager {
    private final ExecutorService executorService;

    public DelegatingExecutorSecurityGroupUpdateManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void updateSecurityGroup(SecurityGroup securityGroup) {
        executorService.submit(securityGroup.updateMetrics());
    }
}

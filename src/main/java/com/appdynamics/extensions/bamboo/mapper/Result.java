/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.bamboo.mapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author Satish Muddam
 */
@XStreamAlias("result")
public class Result {

    @XStreamAlias("projectName")
    private String projectName;
    @XStreamAlias("planName")
    private String planName;
    @XStreamAlias("buildDurationInSeconds")
    private int buildDurationInSeconds;
    @XStreamAlias("successfulTestCount")
    private int successfulTestCount;
    @XStreamAlias("failedTestCount")
    private int failedTestCount;
    @XStreamAlias("quarantinedTestCount")
    private int quarantinedTestCount;
    @XStreamAlias("skippedTestCount")
    private int skippedTestCount;
    @XStreamAlias("buildNumber")
    private int buildNumber;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getBuildDurationInSeconds() {
        return buildDurationInSeconds;
    }

    public void setBuildDurationInSeconds(int buildDurationInSeconds) {
        this.buildDurationInSeconds = buildDurationInSeconds;
    }

    public int getSuccessfulTestCount() {
        return successfulTestCount;
    }

    public void setSuccessfulTestCount(int successfulTestCount) {
        this.successfulTestCount = successfulTestCount;
    }

    public int getFailedTestCount() {
        return failedTestCount;
    }

    public void setFailedTestCount(int failedTestCount) {
        this.failedTestCount = failedTestCount;
    }

    public int getQuarantinedTestCount() {
        return quarantinedTestCount;
    }

    public void setQuarantinedTestCount(int quarantinedTestCount) {
        this.quarantinedTestCount = quarantinedTestCount;
    }

    public int getSkippedTestCount() {
        return skippedTestCount;
    }

    public void setSkippedTestCount(int skippedTestCount) {
        this.skippedTestCount = skippedTestCount;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
}

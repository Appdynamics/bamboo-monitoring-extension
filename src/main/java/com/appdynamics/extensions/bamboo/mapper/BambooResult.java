package com.appdynamics.extensions.bamboo.mapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Satish Muddam
 */
@XStreamAlias("results")
public class BambooResult {

    @XStreamImplicit(itemFieldName = "result")
    private List<Result> results = new ArrayList<Result>();

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}


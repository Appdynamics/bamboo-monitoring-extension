package com.appdynamics.extensions.bamboo;

import com.appdynamics.extensions.bamboo.mapper.BambooResult;
import com.appdynamics.extensions.bamboo.mapper.Result;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.http.UrlBuilder;
import com.thoughtworks.xstream.XStream;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Satish Muddam
 */
public class BambooMetricFetcher implements Runnable {

    private static final Logger logger = Logger.getLogger(BambooMetricFetcher.class);
    private Map server;
    private MonitorConfiguration configuration;
    private XStream xstream;

    public BambooMetricFetcher(Map server, MonitorConfiguration configuration, XStream xstream) {
        this.server = server;
        this.configuration = configuration;
        this.xstream = xstream;
    }

    public void run() {
        BambooResult response = getResponse();
        if (response != null) {
            List<Result> results = response.getResults();
            String serverDisplayName = (String) server.get("displayName");
            printMetrics(results, serverDisplayName);
        }
    }

    private void printMetrics(List<Result> results, String serverDisplayName) {

        for (Result result : results) {

            String projectName = result.getProjectName();
            String planName = result.getPlanName();

            String metricPath = configuration.getMetricPrefix() + "|" + serverDisplayName + "|" + projectName + "|" + planName;

            int buildDurationInSeconds = result.getBuildDurationInSeconds();
            int failedTestCount = result.getFailedTestCount();
            int quarantinedTestCount = result.getQuarantinedTestCount();
            int skippedTestCount = result.getSkippedTestCount();
            int successfulTestCount = result.getSuccessfulTestCount();
            int buildNumber = result.getBuildNumber();

            configuration.getMetricWriter().printMetric(metricPath + "|Build Duration in Sec", new BigDecimal(buildDurationInSeconds), "OBS.CUR.COL");
            configuration.getMetricWriter().printMetric(metricPath + "|Failed Tests", new BigDecimal(failedTestCount), "OBS.CUR.COL");
            configuration.getMetricWriter().printMetric(metricPath + "|Quarantined Tests", new BigDecimal(quarantinedTestCount), "OBS.CUR.COL");
            configuration.getMetricWriter().printMetric(metricPath + "|Skipped Tests", new BigDecimal(skippedTestCount), "OBS.CUR.COL");
            configuration.getMetricWriter().printMetric(metricPath + "|Successful Tests", new BigDecimal(successfulTestCount), "OBS.CUR.COL");
            configuration.getMetricWriter().printMetric(metricPath + "|Build Number", new BigDecimal(buildNumber), "OBS.CUR.COL");
        }
    }

    private BambooResult getResponse() {
        UrlBuilder urlBuilder = UrlBuilder.fromYmlServerConfig(server).path("rest/api/latest/result.json?os_authType=basic&expand=results.result");
        String url = urlBuilder.build();
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpClient = configuration.getHttpClient();
            logger.debug("Sending request to [ " + url + " ]");
            HttpGet get = new HttpGet(url);
            response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                return (BambooResult) xstream.fromXML(response.getEntity().getContent());
            } else {
                logger.error(String.format("Error while fetching the data from absolute url={%s}", url));
                logger.error(String.format("The response is {%s}", EntityUtils.toString(response.getEntity())));
            }
        } catch (Exception e) {
            String msg = String.format("Error while fetching the data from url=[%s]", url);
            logger.error(msg, e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("Exception while closing the response", e);
                }
            }
        }
        return null;
    }
}

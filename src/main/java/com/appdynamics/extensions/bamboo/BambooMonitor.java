/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.bamboo;

import com.appdynamics.extensions.bamboo.mapper.BambooResult;
import com.appdynamics.extensions.bamboo.mapper.Result;
import com.appdynamics.extensions.conf.MonitorConfiguration;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.appdynamics.extensions.util.MetricWriteHelperFactory;
import com.singularity.ee.agent.systemagent.api.AManagedMonitor;
import com.singularity.ee.agent.systemagent.api.TaskExecutionContext;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author Satish Muddam
 */
public class BambooMonitor extends AManagedMonitor {

    private static final String METRIC_PREFIX = "Custom Metrics|Bamboo|";

    private static final Logger logger = Logger.getLogger(BambooMonitor.class);

    private static final String CONFIG_ARG = "config-file";

    private boolean initialized;
    private MonitorConfiguration configuration;
    private XStream xstream;


    public BambooMonitor() {
        String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
        logger.info(msg);
        System.out.println(msg);
    }


    private static String getImplementationVersion() {
        return BambooMonitor.class.getPackage().getImplementationTitle();
    }

    public TaskOutput execute(Map<String, String> args, TaskExecutionContext taskExecutionContext) throws TaskExecutionException {
        String msg = "Using Monitor Version [" + getImplementationVersion() + "]";
        logger.info(msg);
        logger.info("Starting the Bamboo Monitoring task.");

        Thread thread = Thread.currentThread();
        ClassLoader originalCl = thread.getContextClassLoader();
        thread.setContextClassLoader(AManagedMonitor.class.getClassLoader());

        try {
            if (!initialized) {
                initialize(args);
            }
            configuration.executeTask();

            logger.info("Finished Bamboo monitor execution");
            return new TaskOutput("Finished Bamboo monitor execution");
        } catch (Exception e) {
            logger.error("Failed to execute the Bamboo monitoring task", e);
            throw new TaskExecutionException("Failed to execute the Bamboo monitoring task" + e);
        } finally {
            thread.setContextClassLoader(originalCl);
        }
    }

    private void initialize(Map<String, String> argsMap) {
        if (!initialized) {
            final String configFilePath = argsMap.get(CONFIG_ARG);
            MetricWriteHelper metricWriteHelper = MetricWriteHelperFactory.create(this);
            MonitorConfiguration conf = new MonitorConfiguration(METRIC_PREFIX, new TaskRunnable(), metricWriteHelper);
            conf.setConfigYml(configFilePath);
            conf.checkIfInitialized(MonitorConfiguration.ConfItem.CONFIG_YML, MonitorConfiguration.ConfItem.METRIC_PREFIX,
                    MonitorConfiguration.ConfItem.HTTP_CLIENT, MonitorConfiguration.ConfItem.METRIC_WRITE_HELPER, MonitorConfiguration.ConfItem.EXECUTOR_SERVICE);
            this.configuration = conf;
            createXStream();
            initialized = true;
        }
    }

    private void createXStream() {
        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.ignoreUnknownElements();
        xstream.processAnnotations(BambooResult.class);
        xstream.processAnnotations(Result.class);
        this.xstream = xstream;
    }

    private class TaskRunnable implements Runnable {

        public void run() {

            if (!initialized) {
                logger.info("Bamboo Monitor is still initializing");
                return;
            }

            Map<String, ?> config = configuration.getConfigYml();

            List<Map> servers = (List) config.get("servers");
            if (servers != null && !servers.isEmpty()) {
                for (Map server : servers) {
                    BambooMetricFetcher task = new BambooMetricFetcher(server, configuration, xstream);
                    configuration.getExecutorService().execute(task);
                }
            } else {
                logger.error("There are no servers configured");
            }
        }
    }
}

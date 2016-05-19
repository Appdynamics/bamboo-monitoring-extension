Atlassian Bamboo Monitoring Extension
====================================

## Introduction ##

This extension monitors the Bamboo continuous integration server. This extension should be used with standalone Java Machine Agents.


## Installation ##

1. To build from the source, run "mvn clean install" and find the BambooMonitor.zip file in the "target" folder.
   You can also download the BambooMonitor.zip from [AppDynamics Exchange][].
2. Unzip as "BambooMonitor" and copy the "BambooMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`.

## Configuration ##

###Note
Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a yaml validator http://yamllint.com/

1. Configure the Bamboo servers by editing the config.yaml file in `<MACHINE_AGENT_HOME>/monitors/BambooMonitor/conf`.

     ```
      servers:
        # Fires a REST request to https://localhost:8443/rest/api/latest/result.json?os_authType=basic&expand=results.result to get latest test results
          - displayName: Local Bamboo
            host: localhost
            port: 8443
            useSSL: true
            username:
            #Provide password or passwordEncrypted and encryptionKey
            password:
            passwordEncrypted:


        encryptionKey:

        # number of concurrent tasks
        numberOfThreads: 5

        connection:
          socketTimeout: 2000
          connectTimeout: 2000
          sslCertCheckEnabled: false
          sslVerifyHostname: false
          sslProtocols: ["TLSv1"]

        taskSchedule:
          numberOfThreads: 1
          # Fires REST api every taskDelaySeconds seconds and caches the results to display every minute
          taskDelaySeconds: 120


        #prefix used to show up metrics in AppDynamics
        metricPrefix: "Custom Metrics|Bamboo|"  
    ```


2. Configure the path to the config.yaml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/BambooMonitor/` directory.
You can also change the frequency at which the MachineAgent calls the extension by changing the <execution-frequency-in-seconds> in monitor.xml. Below is the sample

    ```

         <task-arguments>
            <!-- config file-->
            <argument name="config-file" is-required="true" default-value="monitors/BambooMonitor/conf/config.yaml" />
         </task-arguments>

    ```

    On Windows, please specify the absolute path to the config.yml.
    
##Password Encryption Support
To avoid setting the clear text password in the config.yaml, please follow the process below to encrypt the password

1. Download the util jar to encrypt the password from [https://github.com/Appdynamics/maven-repo/blob/master/releases/com/appdynamics/appd-exts-commons/1.1.2/appd-exts-commons-1.1.2.jar](https://github.com/Appdynamics/maven-repo/blob/master/releases/com/appdynamics/appd-exts-commons/1.1.2/appd-exts-commons-1.1.2.jar) and navigate to the downloaded directory
2. Encrypt password from the commandline
`java -cp appd-exts-commons-1.1.2.jar com.appdynamics.extensions.crypto.Encryptor encryptionKey myPassword`
3. Specify the passwordEncrypted and encryptionKey in config.yaml    

## Metrics

In metric browser metrics will be displayed in [Custom Metrics|Bamboo|{displayName}|{projectName}|{planName}|{metricName}

|Metric Name            	|
|------------------------------	|
|Build Duration in Sec				|	
|Failed Tests					|
|Quarantined Tests					|
|Skipped Tests    | 
|Successful Tests    | 
|Build Number    | 

## Custom Dashboard ##
![]()

## Troubleshooting ##

1. Verify Machine Agent Data: Please start the Machine Agent without the extension and make sure that it reports data.
   Verify that the machine agent status is UP and it is reporting Hardware Metrics.

2. config.yaml:Validate the file here. http://www.yamllint.com/

3. The config cannot be null :
   This usually happens when on a windows machine in monitor.xml you give config.yaml file path with linux file path separator `/`.
   Use Windows file path separator `\` e.g. `monitors\BambooMonitor\config.yaml`. On Windows, please specify absolute file path.

4. Metric Limit: Please start the machine agent with the argument -Dappdynamics.agent.maxMetrics=5000 if there is a metric limit reached
   error in the logs. If you don't see the expected metrics, this could be the cause.

5. Debug Logs:Edit the file, /conf/logging/log4j.xml and update the level of the appender com.appdynamics to debug .
   Let it run for 5-10 minutes and attach the logs to a support ticket

## Contributing ##

Always feel free to fork and contribute any changes directly via [GitHub][].

## Community ##

Find out more in the [AppDynamics Exchange][].

## Support ##

For any questions or feature request, please contact [AppDynamics Center of Excellence][].

**Version:** 1.0.0
**Controller Compatibility:** 3.7+


[Github]: https://github.com/Appdynamics/bamboo-monitoring-extension
[AppDynamics Exchange]: http://community.appdynamics.com/t5/AppDynamics-eXchange/idb-p/extensions
[AppDynamics Center of Excellence]: mailto:help@appdynamics.com


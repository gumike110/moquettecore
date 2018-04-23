package person.catcircle.core;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.Intercept.ServerInterceptHandler;
import person.catcircle.auth.Authenticator;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by catcircle on 2018/4/23.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
@Slf4j
public class MqttStartUp {
    @Autowired
    private Authenticator authenticator;

    @Autowired
    private Server mqttBroker;

    @Autowired
    private ServerInterceptHandler interceptHandler;

    private int port = 1884;

    @PostConstruct
    public void start() throws Exception {
        startBroker();
    }

    @PreDestroy
    public void stop() throws Exception {
        mqttBroker.stopServer();
        log.info("MQTT Broker stopped");
    }

    private void startBroker() throws IOException{
        Properties props = new Properties();
        props.put(BrokerConstants.PORT_PROPERTY_NAME, "" + port);
        props.put(BrokerConstants.HOST_PROPERTY_NAME, BrokerConstants.HOST);
        props.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, BrokerConstants.DISABLED_PORT_BIND);
        props.put(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, Boolean.FALSE.toString());
        //props.put(BrokerConstants.INTERCEPT_HANDLER_PROPERTY_NAME,"io.moquette.interception.HazelcastInterceptHandler");

        mqttBroker.startServer(new MemoryConfig(props), null, null, null, null);
        mqttBroker.addInterceptHandler(interceptHandler);
        log.info("MQTT Broker started on port: " + port + " (tcp)");
    }
}

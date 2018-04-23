package person.catcircle.config;

import io.moquette.server.Server;
import org.msgpack.MessagePack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by catcircle on 2018/4/23.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Configuration
public class MqttConfig {
    @Bean
    public Server mqttServer(){
        return new Server();
    }

    /**
     * msgpack序列化
     * @return
     */
    @Bean
    public MessagePack msgpack() {
        return new MessagePack();
    }
}

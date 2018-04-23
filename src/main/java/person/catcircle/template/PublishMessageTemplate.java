package person.catcircle.template;

import io.moquette.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.entity.common.Packet;
import person.catcircle.utils.PacketUtils;

/**
 * 发布消息template
 * Created by catcircle on 2018/4/23.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
@Slf4j
public class PublishMessageTemplate {
    @Autowired
    private Server mqttBroker;

    @Autowired
    private PacketUtils packetUtils;

    public void send(String topic, Packet<?> packet){
        byte[] bytes = packetUtils.serizable(packet);
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer().writeBytes(bytes);
        MqttPublishMessage pm = MqttMessageBuilders.publish().topicName(topic).qos(MqttQoS.AT_MOST_ONCE).payload(buf).build();
        mqttBroker.internalPublish(pm,"SELF");
        log.info("send message to topic[{}], message:{}", topic, packet.toString());
    }
}

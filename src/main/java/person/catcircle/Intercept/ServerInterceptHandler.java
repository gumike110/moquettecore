package person.catcircle.Intercept;

import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import person.catcircle.core.TopicDeliver;
import person.catcircle.entity.Device;
import person.catcircle.service.DeviceService;

/**
 * 核心拦截器
 * Created by Administrator on 2018/4/23.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
@Slf4j
public class ServerInterceptHandler implements InterceptHandler {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TopicDeliver deliver;

    @Override
    public String getID() {
        return this.getClass().getName();
    }

    @Override
    public Class<?>[] getInterceptedMessageTypes() {
        return null;
    }

    @Override
    public void onConnect(InterceptConnectMessage interceptConnectMessage) {
        String id = interceptConnectMessage.getClientID();
        log.info("device[" + id + "] connected ...");
    }

    @Override
    public void onDisconnect(InterceptDisconnectMessage interceptDisconnectMessage) {
        String id = interceptDisconnectMessage.getClientID();
        log.info("device[" + id + "] disconnected ...");
        Device device = deviceService.findBySn(id);
        if(device == null){
            throw new IllegalArgumentException("device["+id+"] not exists.");
        }
        deviceService.offLine(id);

        // TODO: 2018/4/23　发送广播消息到控制台
    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage interceptConnectionLostMessage) {
        String id = interceptConnectionLostMessage.getClientID();
        log.info("device[" + id + "] lost ...");
        Device device = deviceService.findBySn(id);
        if(device == null){
            throw new IllegalArgumentException("device["+id+"] not exists.");
        }
        deviceService.offLine(id);

        // TODO: 2018/4/23　发送广播消息到控制台
    }

    @Override
    public void onPublish(InterceptPublishMessage interceptPublishMessage) {
        deliver.deliver(interceptPublishMessage);
    }

    @Override
    public void onSubscribe(InterceptSubscribeMessage interceptSubscribeMessage) {
        log.info(interceptSubscribeMessage.getClientID());
    }

    @Override
    public void onUnsubscribe(InterceptUnsubscribeMessage interceptUnsubscribeMessage) {

    }

    @Override
    public void onMessageAcknowledged(InterceptAcknowledgedMessage interceptAcknowledgedMessage) {

    }
}


package person.catcircle.core;

import com.fasterxml.jackson.core.JsonParser;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import person.catcircle.annotation.Message;
import person.catcircle.entity.common.Packet;
import person.catcircle.utils.PacketUtils;

import java.io.IOException;

/**
 * topic 处理器
 * Created by Administrator on 2018/4/23.
 * Code is far away from bug with the maomaoquan rotecting
 * 猫猫圈保佑,代码无bug
 */
@Message
@Slf4j
public class TopicHandler {
    @Autowired
    private PacketUtils packetUtils;

    @Autowired
    private MessageDeliver messageDeliver;

    @Message.Topic("/s/{model}")
    public void process(@Message.MessageParam(Message.MessageParam.ParamType.clientId) String clientId, @Message.MessageParam(Message.MessageParam.ParamType.username) String username, ByteBuf payload,@PathVariable("model") String model) throws IOException{
        try {
            byte[] payloadBytes = new byte[payload.readableBytes()];
            payload.getBytes(0,payloadBytes);
            JsonParser parser = packetUtils.createParser(payloadBytes);
            Packet.Head head = packetUtils.parseHead(parser);
            log.info("get a packet,head:{}",head);
            int code = head.getCode();
            int type = head.getType();
            //如果是收到的act,则type >= 2，直接返回
            // TODO: 2018/4/23 device 发送来的act如何处理
            if(Packet.Head.Type.isResponse(type)){
                return;
            }
            messageDeliver.deliver(code,type,model,clientId,username,head,parser);
        }catch (IOException e){
            log.error("unserialize payload error",e.getMessage());
        }finally {
            payload.release();
        }

    }
}

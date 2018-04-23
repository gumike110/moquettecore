
package person.catcircle.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;
import person.catcircle.annotation.DeviceCommand;
import person.catcircle.entity.common.Packet;
import person.catcircle.template.PublishMessageTemplate;
import person.catcircle.utils.PacketUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息分发器，根据报文头的指令代码找到对应的消息handle
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
@Slf4j
public class MessageDeliver {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PublishMessageTemplate template;

    @Autowired
    private PacketUtils packetUtils;

    public static final Map<Integer,Map<Integer,MessageHandleMethodContext>> commandHandlers = new ConcurrentHashMap<>();

    @PostConstruct
    private void init(){
        Map<String,Object> beans = context.getBeansWithAnnotation(DeviceCommand.class);

        for(Object obj : beans.values()){
            StandardAnnotationMetadata s = new StandardAnnotationMetadata(obj.getClass());
            Map<String,Object> map = s.getAnnotationAttributes(DeviceCommand.class.getName());
            Set<MethodMetadata> methods = s.getAnnotatedMethods(DeviceCommand.Command.class.getName());
            for(MethodMetadata method : methods){
                StandardMethodMetadata m = (StandardMethodMetadata)method;
                Map<String,Object> methodData = m.getAnnotationAttributes(DeviceCommand.Command.class.getName());
                int[] codes = (int[])methodData.get("code");
                for(int code : codes){
                    commandHandlers.putIfAbsent(code,new ConcurrentHashMap<>());
                    Map<Integer,MessageHandleMethodContext> codeMap = commandHandlers.get(code);
                    int[] types = (int[]) methodData.get("type");
                    Method h = m.getIntrospectedMethod();
                    h.setAccessible(true);
                    MessageHandleMethodContext msgContext = new MessageHandleMethodContext(obj,h);
                    if(types.length == 0){
                        codeMap.putIfAbsent(null,msgContext);
                    }else {
                        for(int type : types){
                            codeMap.putIfAbsent(type,msgContext);
                        }
                    }
                }
            }
        }
    }

    /**
     * cmd deliver
     * @param code
     * @param type
     * @param model
     * @param clientId
     * @param username
     * @param mapper
     */
    public void deliver(int code, int type, String model, String clientId, String username, Packet.Head head, Object mapper){
        MessageHandleMethodContext methodContext = getProcessMethodContext(code,type);
        if(methodContext == null){
            return;
        }
        try {
            Packet<?> result = methodContext.process(clientId,username,head,mapper);
            //如果是notify的消息，只需要本平台处理，不需要返回给device,如果是request的消息，则需要将处理结果返回给device,MqttMessageBuilders.publish()
            // TODO: 2018/4/23 处理MqttMessageBuilders.publish()
            if(type == Packet.Head.Type.REQUEST){
                template.send(packetUtils.getAddr(model,clientId),result);
            }
        }catch (Exception e){
            log.error("message process error", e);
        }
    }

    private MessageHandleMethodContext getProcessMethodContext(int code,int type){
        Map<Integer,MessageHandleMethodContext> codeMap = commandHandlers.get(code);
        if(codeMap == null){
            return null;
        }
        MessageHandleMethodContext methodContext = codeMap.get(type);
        return methodContext == null ? codeMap.get(null) : methodContext;
    }
}


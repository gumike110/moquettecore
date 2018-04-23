
package person.catcircle.core;

import lombok.AllArgsConstructor;
import person.catcircle.annotation.Message;
import person.catcircle.entity.common.Packet;
import person.catcircle.utils.PacketUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息处理器上下文
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@AllArgsConstructor
public class MessageHandleMethodContext {

    private Object obj;

    private Method method;

    public Packet<?> process(String clientId, String username, Packet.Head head, Object wapper) throws Exception{
        Parameter[] ps = method.getParameters();
        List<Object> args = new ArrayList<>();
        for(Parameter p : ps){
            //构建参数
            Object arg = doArg(p,clientId,username,head,wapper);
            args.add(arg);
        }
        Object o = method.invoke(obj,args.toArray());
        if(o == null){
            o = Packet.Head.Type.RESPONSE_OK;
        }
        Packet<?> result;
        if(o instanceof Integer){//response code
            result = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head,(int)o));
        }else if(o instanceof Packet.Head){//head
            result = new Packet<Object>(head);
        }else if(o instanceof Packet){//packet
            result = (Packet<?>) o;
        }else {//body
            result = new Packet<Object>(PacketUtils.getInstance().buildResponseHead(head, Packet.Head.Type.RESPONSE_OK),o);
        }

        return result;
    }

    private Object doArg(Parameter p, String clientId, String username, Packet.Head head, Object wapper) {
        if(p.getType().equals(Packet.Head.class)){
            return head;
        }
        /**
         * 如果arg 是　字符串
         */
        if(p.getType().equals(String.class)){
            Message.MessageParam annotation = p.getDeclaredAnnotation(Message.MessageParam.class);
            switch (annotation.value()){
                case clientId:
                    return clientId;
                case username:
                    return username;
                default:
                    return clientId;
            }
        }

        /**
         * 构造body
         */
        try {
            return PacketUtils.getInstance().parseBody(wapper,p.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

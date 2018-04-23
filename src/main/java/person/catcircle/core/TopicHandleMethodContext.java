
package person.catcircle.core;

import io.moquette.interception.messages.InterceptPublishMessage;
import io.netty.buffer.ByteBuf;
import org.springframework.web.bind.annotation.PathVariable;
import person.catcircle.annotation.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * topic 上下文
 * Created by Administrator on 2018/4/23.
 * Code is far away from bug with the maomaoquan rotecting
 * 猫猫圈保佑,代码无bug
 */

public class TopicHandleMethodContext {
    private Object obj;

    private Method method;

    public TopicHandleMethodContext(Object obj, Method method) {
        this.obj = obj;
        this.method = method;
    }

    public Object process (InterceptPublishMessage message,Map<String,String> pathVariables){
        Parameter[] ps = method.getParameters();
        List<Object> args = new ArrayList();
        for(Parameter pa : ps){
            Object arg = doArg(pa,message,pathVariables);
            args.add(arg);
        }
        try {
            Object object = method.invoke(obj,args.toArray());
            return object;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object doArg(Parameter parameter,InterceptPublishMessage message,Map<String,String> pathVariables){
        if(parameter.getType().equals(InterceptPublishMessage.class)){
            return message;
        }
        if(parameter.getType().equals(ByteBuf.class)){
            return message.getPayload();
        }
        if(parameter.getType().equals(String.class)){
            Message.MessageParam paramAnnotation = parameter.getDeclaredAnnotation(Message.MessageParam.class);
            if(paramAnnotation != null){
                switch (paramAnnotation.value()){
                    case username:
                        return message.getUsername();
                    case clientId:
                        return message.getClientID();
                }
            }
            PathVariable pathVariable = parameter.getDeclaredAnnotation(PathVariable.class);
            if(pathVariable != null && pathVariables != null && pathVariables.size() > 0){
                String pathVariableString = pathVariable.value();
                if(pathVariableString == null){
                    pathVariableString = parameter.getName();
                }
                return pathVariables.get(pathVariableString);
            }
        }
        return null;
    }
}

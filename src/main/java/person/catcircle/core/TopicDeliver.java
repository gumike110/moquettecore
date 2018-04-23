
package person.catcircle.core;

import io.moquette.interception.messages.InterceptPublishMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import person.catcircle.annotation.Message;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * topic 分发器
 * Created by Administrator on 2018/4/23.
 * Code is far away from bug with the maomaoquan rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
public class TopicDeliver {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AntPathMatcher pathMatcher;


    private ExecutorService executor;

    private Map<String,TopicHandleMethodContext> topicHandlers = new HashMap<>();

    @PostConstruct
    private void init(){
        executor = Executors.newCachedThreadPool();
        Map<String,Object> beans = context.getBeansWithAnnotation(Message.class);
        for(Object obj : beans.values()){
            StandardAnnotationMetadata s = new StandardAnnotationMetadata(obj.getClass());
            Set<MethodMetadata> methods = s.getAnnotatedMethods(Message.Topic.class.getName());
            for(MethodMetadata mt : methods){
                StandardMethodMetadata m = (StandardMethodMetadata) mt;
                Map<String,Object> methodData = m.getAnnotationAttributes(Message.Topic.class.getName());
                String topic = (String)methodData.get("value");
                Method h = m.getIntrospectedMethod();
                h.setAccessible(true);
                TopicHandleMethodContext methodContext = new TopicHandleMethodContext(obj,h);
                topicHandlers.putIfAbsent(topic,methodContext);
            }
        }
    }

    public void deliver(InterceptPublishMessage message){
        String topicName = message.getTopicName();
        for(String topic : topicHandlers.keySet()){
            if(pathMatcher.match(topic,topicName)){
                Map<String,String> map = pathMatcher.extractUriTemplateVariables(topic,topicName);
                TopicHandleMethodContext methodContext = topicHandlers.get(topic);
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        methodContext.process(message,map);
                    }
                });
                return;
            }
        }
    }
}

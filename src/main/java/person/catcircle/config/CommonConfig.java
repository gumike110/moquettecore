package person.catcircle.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.AntPathMatcher;
import person.catcircle.auth.Authenticator;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018/4/23.
 * Code is far away from bug with the maomaoquan rotecting
 * 猫猫圈保佑,代码无bug
 */
@Configuration
public class CommonConfig {
    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return om;
    }

    @Bean
    public AntPathMatcher pathMatcher(){
        return new AntPathMatcher();
    }

    @Bean
    public StandardPasswordEncoder passwordEncoder(){
        return new StandardPasswordEncoder("catcircle");
    }

    @Bean("mqttSenderService")
    public ExecutorService mqttSenderService(){
        return new ThreadPoolExecutor(10,100,10L, TimeUnit.MINUTES,new SynchronousQueue<>(),new MyThreadFactory("mqttSenderService"));
    }

    @Bean
    public SecureRandom random(){
        return new SecureRandom("catcircle".getBytes());
    }


    static class MyThreadFactory implements ThreadFactory {
        private final ThreadGroup group;

        private final String prefix;

        private final AtomicInteger tNumber = new AtomicInteger();

        public MyThreadFactory(String prefix) {
            group = Thread.currentThread().getThreadGroup();
            this.prefix = prefix + "-thread";
        }

        /**
         * Constructs a new {@code Thread}.  Implementations may also initialize
         * priority, name, daemon status, {@code ThreadGroup}, etc.
         *
         * @param r a runnable to be executed by new thread instance
         * @return constructed thread, or {@code null} if the request to
         * create a thread is rejected
         */
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group,r,prefix+tNumber.getAndIncrement(),0);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}

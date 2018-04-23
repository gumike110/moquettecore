
package person.catcircle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2018/4/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Message {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Topic {
        String value();
    }

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface MessageParam {
        enum ParamType{
            clientId, username
        }
        ParamType value() default ParamType.clientId;
    }
}


package person.catcircle.annotation;

import org.springframework.core.annotation.AliasFor;
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
public @interface DeviceCommand {

    @AliasFor("model")
    String[] value() default {};
    @AliasFor("value")
    String[] model() default {};

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Command{
        @AliasFor("code")
        int[] value() default {};
        @AliasFor("value")
        int[] code() default {};

        int[] type() default {};
    }
}

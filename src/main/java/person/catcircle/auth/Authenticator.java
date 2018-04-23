
package person.catcircle.auth;

import io.moquette.spi.security.IAuthenticator;
import org.springframework.stereotype.Component;

/**
 * Created by catcircle on 2018/04/20.
 * Code is far away from bug with the catcircle rotecting
 * 猫猫圈保佑,代码无bug
 */
@Component
public class Authenticator implements IAuthenticator{
    @Override
    public boolean checkValid(String s, String s1, byte[] bytes) {
        return true;
    }
}

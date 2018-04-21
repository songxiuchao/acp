package pers.acp.springcloud.oauth.component;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author zhangbin by 11/04/2018 17:14
 * @since JDK1.8
 */
@Component
public class UserPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equalsIgnoreCase(encodedPassword);
    }

}

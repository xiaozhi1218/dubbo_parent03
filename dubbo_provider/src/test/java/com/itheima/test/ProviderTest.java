package com.itheima.test;

import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2020/11/19
 */
public class ProviderTest {

    @Test
    public void tt(){
        ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath:spring-service.xml");
        UserService userService = app.getBean(UserService.class);
        User user = userService.findById(1);
        System.out.println(user);
    }
}

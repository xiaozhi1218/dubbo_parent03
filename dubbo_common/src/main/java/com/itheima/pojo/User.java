package com.itheima.pojo;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2020/11/19
 */
public class User implements Serializable {

    private Integer id;
    private Integer age;
    private String username;

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", age=" + age +
            ", username='" + username + '\'' +
            '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

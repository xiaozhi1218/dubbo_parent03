package com.itheima.dao;

import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2020/11/19
 */
public interface UserDao {

    @Select("select * from t_user where id=#{id}")
    User findById(int id);
}

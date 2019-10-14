package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;

import java.util.List;
import java.util.Map;

public interface AuthorityService {
    PageResult<User> findByAllUser(String username, QueryPageBean queryPageBean);

    List<Role> findAllRole(String name);

    void checkUsername(String username) throws MyException;

    void addUser(User user, List<Integer> checkitemIds, String name);

    Map<String,Object> showOne(Integer id);

    void updateUser(User user, List<Integer> checkitemIds);

    void deleteUser(Integer id);
}

package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface RoleAuthorityService {
    PageResult<Role> findAllRole(String name, QueryPageBean queryPageBean);

    List<Permission> findAllPermission(String name);

    List<Menu> findAllMenu(String name);

    void checkKeyword(String keyword) throws MyException;

    void addRole(String name, Map<String,Object> map) throws Exception;

    Map<String,Object> findRole(Integer id);

    void updateRole(Map<String,Object> map);

    void deleteRole(Integer id) throws MyException;
}

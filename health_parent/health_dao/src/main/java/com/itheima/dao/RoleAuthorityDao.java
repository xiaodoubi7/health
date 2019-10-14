package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.exception.MyException;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleAuthorityDao {
    Page<Role> findAllRole(String queryString);

    Page<Role> findRoleByName(@Param("name") String name,@Param("queryString") String queryString);

    List<Permission> findAllPermission();

    List<Permission> findPermissionByName(String name);

    List<Menu> findAllMenu();

    List<Menu> findMenuByName();

    Role findRoleByKeyword(String keyword);

    void addRole(Role role);

    void addRoleMenu(@Param("id") Integer id,@Param("integer") Integer integer);

    void addRolePermission(@Param("id") Integer id,@Param("integer") Integer integer);

    Role findRole(Integer id);

    List<Integer> findPermissionsById(Integer id);

    List<Integer> findMenuIdsById(Integer id);

    void updateRole(Map<String,Object> roleData);

    void deleteRolePermission(Integer id);

    void deleteRoleMenu(Integer id);

    int findRoleUserById(Integer id);

    void deleteRoleById(Integer id);
}

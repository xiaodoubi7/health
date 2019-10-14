package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorityDao {
    Page<User> findAllUser(String queryString);

    Page<User> findAllUserByUsername(@Param("username") String username,@Param("queryString") String queryString);

    List<Role> findAllRole();

    List<Role> findAllRoleByUsername(String name);

    List<String> findIdRolenameByUsername(String username);

    User checkUsername(String username);

    void addUser(User user);

    void addUserRole(@Param("id") Integer id,@Param("checkitemId") Integer checkitemId);

    User findIdByUser(Integer id);

    List<Integer> findUserRoleById(Integer id);

    void updateUser(User user);

    void deleteUserRole(Integer id);

    void deleteUser(Integer id);
}

package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.AuthorityDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = AuthorityService.class)
public class AuthorityServiceImpl implements AuthorityService {
    @Autowired
    private AuthorityDao authorityDao;

    /*
     * 查询当前用户创建的所有用户信息
     * admin管理员可查询所有用户信息
     * username 用户名
     * */
    @Override
    public PageResult<User> findByAllUser(String username, QueryPageBean queryPageBean) {
        String queryString = queryPageBean.getQueryString();
        if (queryString == null || "null".equals(queryString) || "".equals(queryString)) {
            queryString ="%%";
        }else {
            queryString = "%" + queryString + "%";
        }
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<User> list = null;
        //判断用户是否是超级管理员
        if (checkAdmin(username)) {
            list = authorityDao.findAllUser(queryString);
        }else {
            list = authorityDao.findAllUserByUsername(username,queryString);
        }
        PageResult<User> pageResult = new PageResult<>(list.getTotal(), list.getResult());
        return pageResult;
    }

    /*
    * 查询当前用户设置所有的角色信息
    * */
    @Override
    public List<Role> findAllRole(String name) {
        List<Role> list = null;
        if (checkAdmin(name)) {
            list = authorityDao.findAllRole();
        }else {
           list = authorityDao.findAllRoleByUsername(name);
        }
        return list;
    }

    /*
    * 校验用户名是否可用
    * */
    @Override
    public void checkUsername(String username) throws MyException{
        if (username == null || "".equals(username)) {
            throw new MyException(MessageConstant.CHECK_USERNAME_NULL);
        }
        User user = authorityDao.checkUsername(username);
        if (user != null) {
            throw new MyException(MessageConstant.CHECK_USERNAME_FAILURE);
        }
    }

    /*
    * 添加用户
    * */
    @Override
    @Transactional
    public void addUser(User user, List<Integer> checkitemIds, String name) {
        User user1 = authorityDao.checkUsername(name);
        user.setAdd_user_id(user1.getId());
        String password = user.getPassword();
        BCryptPasswordEncoder bcry = new BCryptPasswordEncoder();
        password = bcry.encode(password);
        user.setPassword(password);
        //这个需要返回id
        authorityDao.addUser(user);
        if (checkitemIds != null) {
            for (Integer checkitemId : checkitemIds) {
                authorityDao.addUserRole(user.getId(),checkitemId);
            }
        }
    }

    /*
     * 编辑数据回显
     * */
    @Override
    public Map<String, Object> showOne(Integer id) {
        User user = authorityDao.findIdByUser(id);
        List<Integer> list = authorityDao.findUserRoleById(id);
        HashMap<String, Object> map = new HashMap<>();
        map.put("formData",user);
        map.put("checkitemIds",list);
        return map;
    }

    /*
     * 修改用户信息
     * */
    @Override
    @Transactional
    public void updateUser(User user, List<Integer> checkitemIds) {
        //修改t_user表用户信息
        authorityDao.updateUser(user);
        //删除原先t_user_role表用户与角色的绑定
        authorityDao.deleteUserRole(user.getId());
        //新增用户和角色的绑定
        Integer id = user.getId();
        if (checkitemIds != null) {
            for (Integer checkitemId : checkitemIds) {
                authorityDao.addUserRole(id,checkitemId);
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        authorityDao.deleteUserRole(id);
        authorityDao.deleteUser(id);
    }

    /*
    * 校验用户是否是ROLE_ADMIN超级管理员
    * username 用户名
    * 返回用户id 和 角色名 name
    * */
    public boolean checkAdmin(String username){
        List<String> list = null;
        try {
            list = authorityDao.findIdRolenameByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null) {
            for (String s : list) {
                if ("ROLE_ADMIN".equals(s)) {
                    return true;
                }
            }
        }
        return false;
    }
}

package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.AuthorityDao;
import com.itheima.dao.RoleAuthorityDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.exception.MyException;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.RoleAuthorityService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = RoleAuthorityService.class)
public class RoleAuthorityServiceImpl implements RoleAuthorityService {
    @Autowired
    private RoleAuthorityDao roleAuthorityDao;
    @Autowired
    private AuthorityDao authorityDao;
    @Autowired
    private AuthorityServiceImpl authorityService;

    /*
    * 查询该用户创建的所有角色,并分页
    * */
    @Override
    public PageResult<Role> findAllRole(String name, QueryPageBean queryPageBean) {
        String queryString = queryPageBean.getQueryString();
        if (queryString == null || "null".equals(queryString) || "".equals(queryString)) {
            queryString = "%%";
        }else {
            queryString = "%" + queryString + "%";
        }
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<Role> page = null;
        //校验用户是否是超级管理员
        if (authorityService.checkAdmin(name)) {
            //是超级管理员
            page = roleAuthorityDao.findAllRole(queryString);
        }else {
            //非超级管理员
            page = roleAuthorityDao.findRoleByName(name,queryString);
        }
        PageResult<Role> rolePageResult = new PageResult<>(page.getTotal(), page.getResult());
        return rolePageResult;
    }

    @Override
    public List<Permission> findAllPermission(String name) {
        List<Permission> list = null;
        if (authorityService.checkAdmin(name)) {
            list = roleAuthorityDao.findAllPermission();
        }else{
            list = roleAuthorityDao.findPermissionByName(name);
        }
        return list;
    }

    @Override
    public List<Menu> findAllMenu(String name) {
        List<Menu> list = null;
        if (authorityService.checkAdmin(name)) {
            list = roleAuthorityDao.findAllMenu();
        }else {
            list = roleAuthorityDao.findMenuByName();
        }
        return list;
    }

    /*
    * 校验角色关键字
    * */
    @Override
    public void checkKeyword(String keyword) throws MyException {
        if (keyword ==null || "".equals(keyword)) {
            throw new MyException(MessageConstant.CHECK_KEYWORD_NULL);
        }
        Role role = roleAuthorityDao.findRoleByKeyword(keyword);
        if (role != null) {
            throw new MyException(MessageConstant.CHECK_KEYWORD_FAILURE);
        }
    }

    @Override
    @Transactional
    public void addRole(String name, Map<String, Object> map) throws Exception {
        List<Integer> menuId = (List<Integer>) map.get("menuId");
        List<Integer> permissionId = (List<Integer>) map.get("permissionId");
        Map<String,Object> roleData = (Map<String, Object>) map.get("roleData");
        Role role = new Role();
        BeanUtils.populate(role,roleData);
        User user = authorityDao.checkUsername(name);
        //创建角色,需要返回id
        role.setAdd_user_id(user.getId());
        roleAuthorityDao.addRole(role);
        //添加角色和菜单的关联表数据
        if (menuId != null && menuId.size() > 0) {
            for (Integer integer : menuId) {
                roleAuthorityDao.addRoleMenu(role.getId(),integer);
            }
        }
        //添加角色和权限的关联表数据
        if (permissionId != null && permissionId.size() > 0) {
            for (Integer integer : permissionId) {
                roleAuthorityDao.addRolePermission(role.getId(),integer);
            }
        }
    }

    @Override
    public Map<String, Object> findRole(Integer id) {
        //查询角色信息
        Role role = roleAuthorityDao.findRole(id);
        //查询角色和权限关系
        List<Integer> permissionIds = roleAuthorityDao.findPermissionsById(id);
        //查询角色和菜单关系
        List<Integer> menuIds = roleAuthorityDao.findMenuIdsById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("formData",role);
        map.put("permissionIds",permissionIds);
        map.put("menuIds",menuIds);
        return map;
    }

    @Override
    @Transactional
    public void updateRole(Map<String, Object> map) {
        List<Integer> menuId = (List<Integer>) map.get("menuId");
        List<Integer> permissionId = (List<Integer>) map.get("permissionId");
        Map<String,Object> roleData = (Map<String, Object>) map.get("roleData");
        //先修改角色表
        roleAuthorityDao.updateRole(roleData);
        //删除角色-权限绑定
        Integer id = (Integer) roleData.get("id");
        roleAuthorityDao.deleteRolePermission(id);
        //删除角色-菜单绑定
        roleAuthorityDao.deleteRoleMenu(id);
        //添加角色和菜单的关联表数据
        if (menuId != null && menuId.size() > 0) {
            for (Integer integer : menuId) {
                roleAuthorityDao.addRoleMenu(id,integer);
            }
        }
        //添加角色和权限的关联表数据
        if (permissionId != null && permissionId.size() > 0) {
            for (Integer integer : permissionId) {
                roleAuthorityDao.addRolePermission(id,integer);
            }
        }
    }

    @Override
    @Transactional
    public void deleteRole(Integer id) throws MyException{
        //1.先判断角色是否被用户引用
        int count = roleAuthorityDao.findRoleUserById(id);
        //2. 引用则报错
        if (count > 0) {
            throw new MyException(MessageConstant.CHECK_ROLE_NULL);
        }
        //3. 没引用则, 先删除角色和 权限,菜单的绑定
        roleAuthorityDao.deleteRoleMenu(id);
        roleAuthorityDao.deleteRolePermission(id);
        //4. 删除角色
        roleAuthorityDao.deleteRoleById(id);
    }
}

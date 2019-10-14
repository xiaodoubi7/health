package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import com.itheima.pojo.Menu;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.service.RoleAuthorityService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roleAuthority")
public class RoleAuthorityController {
    @Reference
    private RoleAuthorityService roleAuthorityService;

    @RequestMapping("/allRole")
    public Result allRole(@RequestBody QueryPageBean queryPageBean){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        PageResult<Role> pageResult = roleAuthorityService.findAllRole(name,queryPageBean);
        return new Result(true,MessageConstant.GET_ALLROLE_SUCCESS,pageResult);
    }

    @RequestMapping("/findAllPermission")
    public Result findAllPermission(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Permission> list = roleAuthorityService.findAllPermission(name);
        return new Result(true,MessageConstant.GET_PERMISSION_SUCCESS,list);
    }

    @RequestMapping("/findAllMenu")
    public Result findAllMenu(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Menu> list = roleAuthorityService.findAllMenu(name);
        return new Result(true,MessageConstant.GET_MENU_SUCCESS,list);
    }

    @RequestMapping("/checkKeyword")
    public Result checkKeyword(String keyword) throws MyException {
        roleAuthorityService.checkKeyword(keyword);
        return new Result(true,MessageConstant.CHECK_KEYWORD_SUCCESS);
    }


    /*
    * 1. 校验上面方法,不是用@RequestParam 是否也可以成功
    * 2. 校验下面方法,传参是否能成功, 先试list 后map
    * 3. 校验两个同为string类型的数据,给dao, 一个需要判断校验时, 能否使用需要校验那个参数的名称
    * */
    @RequestMapping("/addRole")
    public Result addRole(@RequestBody Map<String,Object> map) throws Exception {
        // 1. 获取参数, 2. 获取当前用户名, 便于后续给此角色设置add_user_id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        roleAuthorityService.addRole(name,map);
        return new Result(true,MessageConstant.ADD_MEMBER_SUCCESS);
    }

    @RequestMapping("/showOne")
    public Result showOne(Integer id){
        Map<String,Object> map = roleAuthorityService.findRole(id);
        return new Result(true,MessageConstant.GET_ALLROLE_SUCCESS,map);
    }

    @RequestMapping("/updateRole")
    public Result updateRole(@RequestBody Map<String,Object> map){
        roleAuthorityService.updateRole(map);
        return new Result(true,MessageConstant.UPDATE_ROLE_SUCCESS);
    }

    @RequestMapping("/deleteRole")
    public Result deleteRole(Integer id) throws MyException{
        roleAuthorityService.deleteRole(id);
        return new Result(true,MessageConstant.DELETE_ROLE_SUCCESS);
    }
}

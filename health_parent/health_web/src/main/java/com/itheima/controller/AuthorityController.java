package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.exception.MyException;
import com.itheima.pojo.Role;
import com.itheima.service.AuthorityService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/authority")
public class AuthorityController {
    @Reference
    private AuthorityService authorityService;
    /*
    * 查询当前用户创建的所有用户信息
    * admin管理员可查询所有用户信息
    * */
    @RequestMapping("/allUser")
    public Result allUser(@RequestBody QueryPageBean queryPageBean){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageResult<com.itheima.pojo.User> pageResult = authorityService.findByAllUser(user.getUsername(),queryPageBean);
        return new Result(true,MessageConstant.GET_ALLUSER_SUCCESS,pageResult);
    }

    /*
    * 查询用户添加的角色
    * */
    @RequestMapping("/findAllRole")
    public Result findAllRole(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Role> list = authorityService.findAllRole(name);
        return new Result(true,MessageConstant.ADD_USER_SUCCESS,list);
    }

    /*
     * 添加用户
     * */
    @RequestMapping("/addUser")
    public Result addUser(@RequestBody com.itheima.pojo.User user, @RequestParam("checkitemIds") List<Integer> checkitemIds){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        authorityService.addUser(user,checkitemIds,name);
        return new Result(true,MessageConstant.ADD_USER_SUCCESS);
    }

    /*
     * 校验用户名是否可用
     * */
    @RequestMapping("/checkUsername")
    public Result checkUsername(String username) throws MyException{
        authorityService.checkUsername(username);
        return new Result(true,MessageConstant.CHECK_USERNAME_SECCESS);
    }

    /*
    * 编辑数据回显
    * */
    @RequestMapping("/showOne")
    public Result showOne(@RequestParam("id") Integer id){
        Map<String,Object> map = authorityService.showOne(id);
        return new Result(true,MessageConstant.GET_ALLUSER_SUCCESS,map);
    }

    /*
    * 修改用户信息
    * */
    @RequestMapping("/updateUser")
    public Result updateUser(@RequestBody com.itheima.pojo.User user,@RequestParam List<Integer> checkitemIds){
        authorityService.updateUser(user,checkitemIds);
        return new Result(true,MessageConstant.UPDATE_USER_SUCCESS);
    }

    /*
    * 删除用户
    * */
    @RequestMapping("/deleteUser")
    public Result deleteUser(Integer id){
        authorityService.deleteUser(id);
        return new Result(true,MessageConstant.DELETE_USER_SUCCESS);
    }
}

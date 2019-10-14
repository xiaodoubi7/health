package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.google.gson.Gson;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.PackageDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Package;
import com.itheima.service.PackageService;
import com.itheima.util.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
@Service(interfaceClass = PackageService.class)
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageDao packageDao;
    @Autowired
    private JedisPool jedisPool;

    @Override
    public List<CheckGroup> findAllGroups() {
        return packageDao.findAllGroups();
    }

    @Transactional
    @Override
    public void addPackage(Package mypackage, int[] ids) {
        packageDao.addPackage(mypackage);
        for (int id : ids) {
            packageDao.addGroupRelation(mypackage.getId(),id);
        }

    }

    @Override
    public PageResult<Package> findPage(QueryPageBean queryPageBean) {
        if (!StringUtil.isEmpty(queryPageBean.getQueryString())) {
            queryPageBean.setQueryString("%"+queryPageBean.getQueryString()+"%");
        }
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        Page<Package> page=packageDao.findPackage(queryPageBean.getQueryString());
        return new PageResult<Package>(page.getTotal(),page.getResult());

    }

    @Override
    public String getPackage() {
        Jedis jedis = jedisPool.getResource();
        String jsonPackages = jedis.get(MessageConstant.HEALTH_MOBILE_PACKAGES);
        if (jsonPackages == null) {
            List<Package> packages = packageDao.getPackage();
            if (packages != null && packages.size() > 0) {
                for (Package aPackage : packages) {
                    String img = aPackage.getImg();
                    aPackage.setImg("http://"+ QiNiuUtil.DOMAIN+"/"+img);
                }
            }
            Gson gson = new Gson();
            jsonPackages = gson.toJson(packages);
            jedis.set(MessageConstant.HEALTH_MOBILE_PACKAGES,jsonPackages);
        }

        /*if (packages != null && packages.size() > 0) {
            for (Package aPackage : packages) {
                jedis.sadd("health_package_ids",aPackage.getId()+"");

                jedis.hset(aPackage.getId()+"","img",aPackage.getImg());
                jedis.hset(aPackage.getId()+"","id",aPackage.getId()+"");
                jedis.hset(aPackage.getId()+"","name",aPackage.getName());
                jedis.hset(aPackage.getId()+"","code",aPackage.getCode());
                jedis.hset(aPackage.getId()+"","helpCode",aPackage.getHelpCode());
                jedis.hset(aPackage.getId()+"","sex",aPackage.getSex());
                jedis.hset(aPackage.getId()+"","age",aPackage.getAge());
                jedis.hset(aPackage.getId()+"","price",aPackage.getPrice()+"");
                jedis.hset(aPackage.getId()+"","remark",aPackage.getRemark());
                jedis.hset(aPackage.getId()+"","attention",aPackage.getAttention());
            }
        }*/
        return jsonPackages;
    }

    @Override
    public Package findById(int id) {
        return packageDao.findById(id);
    }
}

package com.mulan.user_center.service;

import com.mulan.user_center.model.domain.CenterUser;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wwwwind
* @description 针对表【center_user(用户中心表)】的数据库操作Service
* @createDate 2022-12-01 11:20:07
*/
public interface CenterUserService extends IService<CenterUser> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 较验密码
     * @return 新用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登陆
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request 要向request域中放session
     * @return 脱敏后的用户信息
     */
    CenterUser userLogin(String userAccount, String userPassword, HttpServletRequest request);

    int userLogout(HttpServletRequest request);
    CenterUser getSafetyUser(CenterUser user);
}

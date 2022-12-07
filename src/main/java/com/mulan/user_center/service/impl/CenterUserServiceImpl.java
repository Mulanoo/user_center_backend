package com.mulan.user_center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mulan.user_center.mapper.CenterUserMapper;
import com.mulan.user_center.model.domain.CenterUser;
import com.mulan.user_center.service.CenterUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mulan.user_center.constant.UserConstant.*;

/**
 * @author wwwwind
 * @description 针对表【center_user(用户中心表)】的数据库操作Service实现
 * @createDate 2022-12-01 11:20:07
 */
@Service
@Slf4j
public class CenterUserServiceImpl extends ServiceImpl<CenterUserMapper, CenterUser>
        implements CenterUserService {

    /**
     * 用户登陆状态键
     * 用在session中
     */
    @Resource
    CenterUserMapper centerUserMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.较验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;//暂时实现逻辑，后期完善异常
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        //账户不能包含特殊字符：用到正则表达式，具体百度
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        //密码和较验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        //账户不能重复：此处会查询一次数据库，应放到较验逻辑的最后，节省资源
        QueryWrapper<CenterUser> centerUserQueryWrapper = new QueryWrapper<>();
        centerUserQueryWrapper.eq("userAccount", userAccount);
        Long count = centerUserMapper.selectCount(centerUserQueryWrapper);
        if (count > 0) {
            return -1;
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.通过全部较验后插入新用户数据
        CenterUser user = new CenterUser();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        //此处调用service继承的IService<T>接口的保存方法，就是插入一条数据
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public CenterUser userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        // todo 修改为自定义异常
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8){
            return null;
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<CenterUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        CenterUser user = centerUserMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount Cannot match userPassword");
            return null;
        }
        // 3.用户脱敏
        CenterUser safetyUser = getSafetyUser(user);
        // 4.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public CenterUser getSafetyUser(CenterUser user){
        CenterUser safetyUser = new CenterUser();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl("");
        safetyUser.setGender(0);
        safetyUser.setPhone("");
        safetyUser.setEmail("");
        safetyUser.setUserStatus(0);
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }
}





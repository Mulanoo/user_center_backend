package com.mulan.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mulan.user_center.common.BaseResponse;
import com.mulan.user_center.common.ErrorCode;
import com.mulan.user_center.common.ResultUtils;
import com.mulan.user_center.exceptions.BusinessException;
import com.mulan.user_center.model.domain.CenterUser;
import com.mulan.user_center.model.domain.request.UserLoginRequest;
import com.mulan.user_center.model.domain.request.UserRegisterRequest;
import com.mulan.user_center.service.CenterUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.mulan.user_center.constant.UserConstant.ADMIN_ROLE;
import static com.mulan.user_center.constant.UserConstant.USER_LOGIN_STATE;


@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private CenterUserService centerUserService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw  new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long res = centerUserService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(res);
    }

    @PostMapping("/login")
    public BaseResponse<CenterUser> userLogin(@RequestBody UserLoginRequest userLoginRequest,
                                              HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        CenterUser res = centerUserService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(res);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int res = centerUserService.userLogout(request);
        return ResultUtils.success(res);
    }


    @GetMapping("search")
    public BaseResponse<List<CenterUser>> searchUsers(String userName, HttpServletRequest request) {
        //仅管理员可查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<CenterUser> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            qw.like("username", userName);
        }
        //这里返回的用户列表也要脱敏
        List<CenterUser> userList = centerUserService.list(qw);
        //stream操作
        List<CenterUser> res = userList.stream().map(centerUser -> centerUserService.getSafetyUser(centerUser)).collect(Collectors.toList());
        return ResultUtils.success(res);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"id小于0");
        }
        Boolean res =  centerUserService.removeById(id);
        return ResultUtils.success(res);
    }

    //判断用户是否是管理员
    private boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        CenterUser user = (CenterUser) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}

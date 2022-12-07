package com.mulan.user_center.model.domain.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 5797352008098970221L;
    private String userAccount;
    private String userPassword;
}

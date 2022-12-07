package com.mulan.user_center.model.domain.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -2381185512811479174L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}

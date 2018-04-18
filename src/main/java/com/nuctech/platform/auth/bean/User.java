package com.nuctech.platform.auth.bean;

import lombok.Data;

/**
 * Created by @author wangzunhui on 2017/10/10.
 */
@Data
public class User {
    private String name;
    private String id;
    private String orgId;
    private String createTime;
    private int status;
    private int timeout;
}

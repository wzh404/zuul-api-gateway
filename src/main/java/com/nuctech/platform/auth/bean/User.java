package com.nuctech.platform.auth.bean;

import lombok.Data;

/**
 * User Bean.
 *
 * Created by @author wangzunhui on 2017/10/10.
 */
@Data
public class User {
    private String name;
    private String id;
    private String orgId;
    private String createTime;
    private int status;
    /** token timeout */
    private int timeout;
}

package com.nuctech.platform.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by wangzunhui on 2017/10/10.
 */
@Data
public class User {
    private String name;
    private String free;
    private String total;
    private String time;
    private String type;
}

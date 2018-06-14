package com.nuctech.platform.auth.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * User authority returned from the urpm.
 *
 * <p>
 * Created by @author wangzunhui on 2017/10/17.
 */
@Data
public class AuthorizeResponse {
    @JsonProperty("flag")
    private Boolean flag;

    @JsonProperty("result")
    private List<String> result;

    public List<String> getUserAuthority() {
        if (!getFlag()) {
            return new ArrayList<>();
        }

        return result;
    }
}

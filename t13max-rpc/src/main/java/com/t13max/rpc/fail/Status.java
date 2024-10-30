package com.t13max.rpc.fail;

import lombok.Data;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: zl1030
 * @Date: 2019/4/24
 */
@Data
public class Status {

    @Setter
    private Map<String, FailSafeStatus> failSafeStatusMap;
}

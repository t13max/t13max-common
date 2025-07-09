package com.t13max.rpc.fail;

import lombok.Data;
import lombok.Setter;

import java.util.Map;

/** 
 * 
 *        
 * @Author t13max
 * @Date 13:56 2025/4/21
 */
@Data
public class Status {

    @Setter
    private Map<String, FailSafeStatus> failSafeStatusMap;
}

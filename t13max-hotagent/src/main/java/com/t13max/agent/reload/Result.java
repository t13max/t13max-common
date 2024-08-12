package com.t13max.agent.reload;

import com.t13max.agent.util.Log;
import lombok.Getter;
import lombok.Setter;

/**
 * 执行结果
 *
 * @author: t13max
 * @since: 15:43 2024/8/12
 */
@Setter
@Getter
public class Result {

    //是否成功
    private boolean success = true;

    //执行信息
    private final StringBuilder msgBuilder = new StringBuilder();

    public Result() {
    }

    public String getMsgBuilder() {
        return this.msgBuilder.toString();
    }

    public void appendMsg(String msg) {
        Log.agent.error(msg);
        this.msgBuilder.append(msg).append("\r\n");
    }
}

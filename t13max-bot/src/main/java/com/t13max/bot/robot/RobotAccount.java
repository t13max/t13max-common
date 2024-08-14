package com.t13max.bot.robot;

import com.t13max.bot.exception.RobotException;
import com.t13max.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 机器人账号信息
 *
 * @author: t13max
 * @since: 18:12 2024/4/7
 */
@Getter
public class RobotAccount {
    //id
    private final int id;
    //账号
    private final String username;
    //密码
    private final String password;
    //服务器ID
    @Setter
    private int serverId;
    //是否已注册
    private final boolean register;

    public RobotAccount(int id, String prefix, String password, int serverId) {
        this.id = id;
        this.username = prefix + id;
        this.password = password;
        this.serverId = serverId;
        this.register = false;
        //这样创建的是没注册的
    }

    private RobotAccount(String readLine) {
        String[] split = readLine.split(StringUtil.COMMA);
        if (split.length < 3) {
            throw new RobotException("账号信息错误, readLine=" + readLine);
        }
        this.id = Integer.parseInt(split[0]);
        this.username = split[1];
        this.password = split[2];
        this.serverId = Integer.parseInt(split[3]);
        //读文件的就是已注册的
        this.register = true;
    }

    /**
     * 根据字符串解析
     *
     * @Author t13max
     * @Date 18:18 2024/4/7
     */
    public static RobotAccount parse(String readLine) {
        RobotAccount robotAccount = null;
        try {
            robotAccount = new RobotAccount(readLine);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return robotAccount;
    }

    public String toString() {
        String separation = StringUtil.COMMA;
        return this.id + separation + this.username + separation + this.password + separation + this.serverId;
    }

}

package com.t13max.common.msg;


/**
 * 消息常量
 *
 * @author: t13max
 * @since: 20:08 2024/5/28
 */
public interface MessageConst {
    int LEN_LENGTH = 4;
    int MSG_ID_LENGTH = 4;
    int MSG_RESULT_CODE = 4;
    int HEADER_LENGTH = LEN_LENGTH + MSG_ID_LENGTH;
    int CLIENT_HEADER_LENGTH = LEN_LENGTH + MSG_ID_LENGTH + MSG_RESULT_CODE;
}

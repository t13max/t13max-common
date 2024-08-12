package com.t13max.util;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 解析转换工具类
 *
 * @author: t13max
 * @since: 15:19 2024/4/23
 */
@UtilityClass
public class ParseUtil {

    public static byte[] toBytes(InputStream var0) throws IOException {
        ByteArrayOutputStream var1 = new ByteArrayOutputStream();
        byte[] var2 = new byte[1000];

        int var3;
        while ((var3 = var0.read(var2)) != -1) {
            var1.write(var2, 0, var3);
        }

        var0.close();
        var1.close();
        return var1.toByteArray();
    }
}

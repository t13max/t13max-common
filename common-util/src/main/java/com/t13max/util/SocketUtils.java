package com.t13max.util;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author t13max
 * @since 17:22 2024/10/30
 */
@UtilityClass
public class SocketUtils {

    public int randomPort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            return 0;
        }
    }
}

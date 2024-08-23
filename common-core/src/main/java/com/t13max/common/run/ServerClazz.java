package com.t13max.common.run;


import com.t13max.common.net.INettyServer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerClazz {

    Class<? extends INettyServer> serverClazz();
}

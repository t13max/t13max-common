package com.t13max.template.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: t13max
 * @since: 16:56 2024/8/5
 */
@Getter
public enum DataTypeEnum {

    INT("int", "Integer") {
        @Override
        public Object getDefaultValue() {
            return 0;
        }
    },
    INT_ARRAY("int[]", "ArrayList") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }
    },
    FLOAT("float","Float") {
        @Override
        public Object getDefaultValue() {
            return 0f;
        }
    },
    FLOAT_ARR("float[]", "ArrayList") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }
    },

    STRING("String","String") {
        @Override
        public Object getDefaultValue() {
            return "";
        }
    },
    STRING_ARR("String[]","ArrayList") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }
    },

    ;


    private final String dataType;

    private final String wrapperType;

    public abstract Object getDefaultValue();

    private final static Map<String, DataTypeEnum> DATA_MAP = new HashMap<>();

    static {
        for (DataTypeEnum dataTypeEnum : values()) {
            DATA_MAP.put(dataTypeEnum.getDataType(), dataTypeEnum);
        }
    }

    DataTypeEnum(String dataType, String wrapperType) {
        this.dataType = dataType;
        this.wrapperType = wrapperType;
    }

    public static DataTypeEnum of(String type) {
        return DATA_MAP.get(type);
    }
}

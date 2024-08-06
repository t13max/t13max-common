package com.t13max.template.enums;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: t13max
 * @since: 16:56 2024/8/5
 */
@Getter
public enum DataTypeEnum {

    INT("int", "int") {
        @Override
        public Object getDefaultValue() {
            return 0;
        }
    },
    INT_ARRAY("int[]", "List<Integer>") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }

        @Override
        public String getConvertor() {
            return "ToIntListConverter.class";
        }
    },
    FLOAT("float", "float") {
        @Override
        public Object getDefaultValue() {
            return 0f;
        }
    },
    FLOAT_ARR("float[]", "List<Float>") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }
    },

    STRING("String", "String") {
        @Override
        public Object getDefaultValue() {
            return "";
        }
    },
    STRING_ARR("String[]", "List<String>") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyList();
        }
    },

    MAP("map", "Map<Integer,Integer>") {
        @Override
        public Object getDefaultValue() {
            return Collections.emptyMap();
        }

        @Override
        public String getConvertor() {
            return "ToIntMapConverter.class";
        }
    };


    private final String dataType;

    private final String javaType;

    public abstract Object getDefaultValue();

    public String getConvertor() {
        return "";
    }

    private final static Map<String, DataTypeEnum> DATA_MAP = new HashMap<>();

    static {
        for (DataTypeEnum dataTypeEnum : values()) {
            DATA_MAP.put(dataTypeEnum.getDataType(), dataTypeEnum);
        }
    }

    DataTypeEnum(String dataType, String javaType) {
        this.dataType = dataType;
        this.javaType = javaType;
    }

    public static DataTypeEnum of(String type) {
        return DATA_MAP.get(type);
    }


}

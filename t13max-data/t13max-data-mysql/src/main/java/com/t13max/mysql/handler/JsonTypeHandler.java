package com.t13max.mysql.handler;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Json类型处理器
 *
 * @Author t13max
 * @Date 10:00 2024/9/30
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json != null) {
            try {
                return JSONObject.parseObject(json, type);
            } catch (Exception e) {
                throw new SQLException("Error converting JSON to object", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (json != null) {
            try {
                return JSONObject.parseObject(json, type);
            } catch (Exception e) {
                throw new SQLException("Error converting JSON to object", e);
            }
        }
        return null;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json != null) {
            try {
                return JSONObject.parseObject(json, type);
            } catch (Exception e) {
                throw new SQLException("Error converting JSON to object", e);
            }
        }
        return null;
    }
}
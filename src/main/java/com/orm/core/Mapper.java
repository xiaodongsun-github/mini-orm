package com.orm.core;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>封装映射信息</p>
 *
 * @author xiaodongsun
 * @date 2018/12/17
 */
public class Mapper {

    private String className;
    private String table;
    private Map<String, String> idMapper = new HashMap<>();
    private Map<String, String> propMapper = new HashMap<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getIdMapper() {
        return idMapper;
    }

    public void setIdMapper(Map<String, String> idMapper) {
        this.idMapper = idMapper;
    }

    public Map<String, String> getPropMapper() {
        return propMapper;
    }

    public void setPropMapper(Map<String, String> propMapper) {
        this.propMapper = propMapper;
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "className='" + className + '\'' +
                ", table='" + table + '\'' +
                ", idMapper=" + idMapper +
                ", propMapper=" + propMapper +
                '}';
    }
}

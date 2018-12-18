package com.orm.core;

import com.orm.utils.AnnotationUtil;
import com.orm.utils.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>解析并封装框架的核心配置文件中的数据</p>
 *
 * @author xiaodongsun
 * @date 2018/12/17
 */
public class ORMConfig {
    /** classpath路径 */
    private static String classpath;
    /** 核心配置文件 */
    private static File cfgFile;
    /** <property></property>属性信息 */
    private static Map<String, String> propConfig;
    /** 映射配置文件路径 */
    private static Set<String> mappingSet;
    /** 实体类 */
    private static Set<String> entitySet;
    /** 映射信息 */
    public static List<Mapper> mapperList;

    static {
        //得到的classpath路径
        classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        try {
            //防止中文路径转码
            classpath = URLDecoder.decode(classpath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //得到核心配置文件
        cfgFile = new File(classpath + "miniORM.cfg.xml");
        if (cfgFile.exists()){
            //解析核心配置文件中的数据
            Document document = Dom4jUtil.getXMLByFilePath(cfgFile.getPath());
            propConfig = Dom4jUtil.Elements2Map(document, "property", "name");
            mappingSet = Dom4jUtil.Elements2Set(document, "mapping", "resource");
            entitySet = Dom4jUtil.Elements2Set(document, "entity", "package");
        }else{
            cfgFile = null;
            System.out.println("未找到核心配置文件miniORM.cfg.xml");
        }
    }

    /**
     * 从propConfig集合中获取数据，并连接数据库
     * @return Connection
     */
    private Connection getConnection(){
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");
        Connection connection = null;
        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 封装Mapper对象
     */
    private void getMapper(){
        mapperList = new ArrayList<>();
        //1.解析xxx.mapper.xml文件拿到映射数据
        for(String xmlPath:mappingSet){
            Document document = Dom4jUtil.getXMLByFilePath(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> id_id = Dom4jUtil.ElementsID2Map(document);
            Map<String, String> mapping = Dom4jUtil.Elements2Map(document);

            Mapper mapper = new Mapper();
            mapper.setTable(tableName);
            mapper.setClassName(className);
            mapper.setIdMapper(id_id);
            mapper.setPropMapper(mapping);

            mapperList.add(mapper);
        }
        //2.解析实体类中的注解拿到映射数据
        for (String packagePath:entitySet){
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(packagePath);
            for (String name:nameSet){
                try {
                    Class<?> clz = Class.forName(name);
                    String className = AnnotationUtil.getClassName(clz);
                    String tableName = AnnotationUtil.getTableName(clz);
                    Map<String, String> id_id = AnnotationUtil.getIdMapper(clz);
                    Map<String, String> mapping = AnnotationUtil.getPropMapping(clz);

                    Mapper mapper = new Mapper();
                    mapper.setTable(tableName);
                    mapper.setClassName(className);
                    mapper.setIdMapper(id_id);
                    mapper.setPropMapper(mapping);

                    mapperList.add(mapper);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建ORMSession对象
     * @return ORMSession
     */
    public ORMSession buildORMSession(){
        //连接数据库
        Connection connection = this.getConnection();
        //得到映射数据
        this.getMapper();
        //创建ORMSession对象
        return new ORMSession(connection);
    }

}

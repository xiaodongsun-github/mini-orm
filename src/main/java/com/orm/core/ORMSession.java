package com.orm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>ORMSession操作增删改查对象</p>
 *
 * @author xiaodongsun
 * @date 2018/12/18
 */
public class ORMSession {

    /** 数据库连接 */
    private Connection connection;

    public ORMSession(Connection connection){
        this.connection = connection;
    }

    /**
     * 保存数据
     * @param entity 实体
     */
    public void save(Object entity) throws IllegalAccessException, SQLException {

        String insertSQL = "";
        //1.从ORMConfig中获取保存有映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;
        //2.遍历集合查找符合的对象
        for (Mapper mapper:mapperList){
            if (mapper.getClassName().equals(entity.getClass().getName())){
                String tableName = mapper.getTable();
                String insertSQL1 = "INSERT INTO" + tableName + "(";
                String insertSQL2 = ") VALUES(";

                //3.获取当前对象所属类中的所以属性
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field:fields){
                    field.setAccessible(true);
                    //4.遍历过程中根据属性名得到字段名
                    String cloumeName = mapper.getPropMapper().get(field.getName());
                    //5.遍历过程中根据属性名得到值
                    String columnValue = field.get(entity).toString();
                    //6.拼接SQL语句
                    insertSQL1 += cloumeName + ",";
                    insertSQL2 += "'" + columnValue + "',";
                }
                insertSQL = insertSQL1.substring(0, insertSQL1.length() - 1)
                        + insertSQL2.substring(0, insertSQL2.length() - 1) + ")";
                break;
            }
        }
        //打印SQL语句到控制台
        System.out.println("MiniORM-save： " + insertSQL);
        PreparedStatement statement = connection.prepareStatement(insertSQL);
        statement.executeUpdate();
    }

    /**
     * 根据主键进行数据删除
     * @param entity
     */
    public void delete(Object entity) throws NoSuchFieldException, IllegalAccessException, SQLException {
        String delSQL = "DELETE FROM ";
        List<Mapper> mapperList = ORMConfig.mapperList;
        for (Mapper mapper:mapperList){
            if (mapper.getClassName().equals(entity.getClass().getName())){
                String tableName = mapper.getTable();
                delSQL += tableName + "WHERE ";

                //获取主键字段名
                Object[] idProp = mapper.getIdMapper().keySet().toArray();
                //获取主键属性名
                Object[] idCloumn = mapper.getIdMapper().values().toArray();
                //获取主键值
                Field field = entity.getClass().getDeclaredField(idProp[0].toString());
                field.setAccessible(true);
                String idVal = field.get(entity).toString();

                delSQL += idCloumn[0].toString() + " = " + idVal;
                break;
            }
        }
        //打印SQL语句到控制台
        System.out.println("MiniORM-delete： " + delSQL);
        PreparedStatement statement = connection.prepareStatement(delSQL);
        statement.executeUpdate();
    }

    /**
     * 根据主键查询
     * @param clz 实体类
     * @param id 主键
     * @return 更新对象
     */
    public Object findOne(Class clz, Object id) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        String querySQL = "SELECT * FROM ";

        //1.从ORMConfig中得到存有映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;
        //2.遍历集合获取需要的mapper对象
        for (Mapper mapper:mapperList){
            if (mapper.getClassName().equals(clz.getName())){
                //3.获取表名
                String tableName = mapper.getTable();
                //4.主键字段名
                Object[] idCloumn = mapper.getIdMapper().values().toArray();
                querySQL += tableName + " WHERE " + idCloumn[0].toString() + " = " + id;

                break;
            }
        }
        //打印SQL语句到控制台
        System.out.println("MiniORM-findOne： " + querySQL);
        PreparedStatement statement = connection.prepareStatement(querySQL);
        ResultSet resultSet = statement.executeQuery();

        //封装结果集，返回对象
        Object obj = null;
        if (resultSet.next()){
            obj = clz.newInstance();
            for(Mapper mapper:mapperList){
                if (mapper.getClassName().equals(clz.getName())){
                    Map<String, String> propMap = mapper.getPropMapper();
                    Set<String> keySet = propMap.keySet();
                    for (String prop:keySet){
                        //prop属性名
                        //column就是和属性对应的字段名
                        String column = propMap.get(prop);
                        Field field = clz.getDeclaredField(prop);
                        field.setAccessible(true);
                        field.set(obj, resultSet.getObject(column));
                    }
                    break;
                }
            }
        }
        statement.close();
        resultSet.close();
        return obj;
    }

    public void close() throws SQLException {
        if (connection != null){
            connection.close();
            connection = null;
        }
    }
}

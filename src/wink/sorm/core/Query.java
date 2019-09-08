package wink.sorm.core;


import javafx.scene.control.Tab;
import wink.sorm.bean.ColumnInfo;
import wink.sorm.bean.TableInfo;
import wink.sorm.utils.JDBCUtils;
import wink.sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责查询（对外提供的核心类）
 * @author wink
 */
//@SuppressWarnings("all")
public abstract class Query implements Cloneable {

    /**
     * 采用模板模式方式发将JDBC操作封装成模板，便于重用。　　　　
     * @param sql sql语句
     * @param params sql的参数
     * @param clazz 记录要封装到的java类
     * @param back CallBack的实现类，实现回调
     * @return
     */
    public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back) {
        Connection conn = DBManager.getConnection();
//        List list = null;//存放查询结果的容器
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            //给sql设参数
            JDBCUtils.handleParrms(ps,params);
//            System.out.println(ps);
            rs = ps.executeQuery();

            return back.doExecute(conn, ps, rs);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DBManager.close(ps,conn);
        }

    }

    /**
     * 直接执行一个DML语句
     * @param sql sql语句
     * @param params 参数
     * @return 执行sql语句之后影像记录的行数
     */
    public int executeDML(String sql, Object[] params){
        Connection conn = DBManager.getConnection();
        int count = 0;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);

            //给sql设参数
            JDBCUtils.handleParrms(ps,params);
            System.out.println(ps);
            count = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DBManager.close(ps,conn);
        }
        return count;
    }

    /**
     * 将一个对象存储到数据库中
     * 把对象中不为null的属性往数据库中存储！如果数字为null则放0
     * @param obj 要存储的对象
     */
    public void insert(Object obj) {
        //obj-->表中 insert into 表名(id,username,pwd) values (?,?,?)
        //
        Class c = obj.getClass();
        List<Object> params = new ArrayList<>();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getName()+" (");
        int countNotNullField = 0;//计算不为null的属性值
        Field[] fs = c.getDeclaredFields();
        for (Field f:fs) {
            String fieldName = f.getName();
            Object fieldValue = ReflectUtils.invokeGet(fieldName,obj);

            if (fieldValue != null) {
                countNotNullField++;
                sql.append(fieldName+",");
                params.add(fieldValue);
            }
        }
        sql.setCharAt(sql.length()-1,')');
        sql.append("values (");
        for (int i = 0; i < countNotNullField; i++) {
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');

        executeDML(sql.toString(), params.toArray());
    }

    /**
     * 删除clazz表示类对应的表中的记录（指定主键id的记录）
     * @param clazz 跟表对印的类的Class对象
     * @param id 主键的值
     * @return
     */
    public void delete(Class clazz, Object id) {
        //Emp.class,2-->delete from emp where id=2

        //通过Class对象找TableInfo
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        //获得主键
        ColumnInfo onlyPrikey = tableInfo.getOnlyPrikey();

        String sql = "delete from "+tableInfo.getName()+" where "+onlyPrikey.getName()+"=?";

        executeDML(sql,new Object[]{id});


    }


    /**
     * 删除对象在数据库中对应的记录（对象所在的类对应到表，对象的主键的值对应到记录）
     * @param obj
     */
    public void delete(Object obj) {
        Class  c = obj.getClass();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo onlyPrikey = tableInfo.getOnlyPrikey();//主键

        //通过反射机制，调用属性对应的get或set方法
        Object priKeyValue = ReflectUtils.invokeGet(onlyPrikey.getName(), obj);

        delete(c,priKeyValue);


    }


    /**
     * 更新对象对应的记录，并且只更新指定的字段的值
     * @param obj 所更新的对象
     * @param fieldNames 更新的属性列表
     * @return 执行sql语句后影像记录的行数
     */
    public int update(Object obj, String[] fieldNames) {
        //obj("uname","pwd")-->update 表名 set uname=?,pwd=? where id=?
        Class c = obj.getClass();
        List<Object> params = new ArrayList <>();
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        ColumnInfo priKey = tableInfo.getOnlyPrikey();//获得唯一的主键
        StringBuilder sql = new StringBuilder("update "+tableInfo.getName()+" set ");

        for (String fname : fieldNames) {
            Object fvalue = ReflectUtils.invokeGet(fname,obj);
            params.add(fvalue);
            sql.append(fname+"=?,");
        }
        sql.setCharAt(sql.length()-1,' ');
        sql.append(" where ");
        sql.append(priKey.getName()+"=? ");

        params.add(ReflectUtils.invokeGet(priKey.getName(),obj));//主键的值

        return executeDML(sql.toString(),params.toArray());
    }


    /**
     * 查询多行记录，并将每行记录封装到clazz指定的类的对象中
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public List queryRows( String sql, Class clazz,  Object[] params) {
//        final List list = new ArrayList();;//存放查询结果的容器

        return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                List list = null;
                try {
                    ResultSetMetaData metaData = rs.getMetaData();
                    //多行
                    while (rs.next()) {
                        if (null == list) {
                            list = new ArrayList();
                        }

                        Object rowObj = clazz.newInstance();//调用javabean的无参构造器

                        //多列 select username,pwd,age from user where id>? and age>18
                        for (int i=0;i<metaData.getColumnCount();i++){
                            String columnName = metaData.getColumnLabel(i+1);
                            Object columnValue = rs.getObject(i+1);

                            //调用rowObject对象的setUsername(String uname)方法，将columnValue设置进去
                            if (rowObj != null) {
                                ReflectUtils.invokeSet(rowObj,columnName,columnValue);
                            }
                        }
                        list.add(rowObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return list;
            }
        });
    }




    /**
     * 查询返回一行记录，并将该行记录封装到clazz指定的类的对象中
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Object queryUniqueRow(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql, clazz, params);
        return (list!=null&&list.size()>0)?list.get(0):null;
    }


    /**
     * 根据主键的值查找对应的对象
     * @param clazz
     * @param id
     * @return
     */
    public Object queryById(Class clazz, Object id) {
        //select * from emp where id=?
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        //
        ColumnInfo onlyPrikey = tableInfo.getOnlyPrikey();

        String sql = "select * from "+tableInfo.getName()+" where "+onlyPrikey.getName()+"=?";
        return queryUniqueRow(sql, clazz, new Object[]{id});
    }


    /**
     * 查询返回一个值（一行一列），并将该值返回
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Object queryValue(String sql, Object[] params) {

        return executeQueryTemplate(sql, params, null, new CallBack() {
            @Override
            public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
                Object value = null;
                try {
                    while (rs.next()) {
                        value = rs.getObject(1);
                        //select count(*) from user
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return value;
            }
        });
    }


    /**
     * 查询返回一个值（一行一列），并将该值返回
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询到的结果
     */
    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql,params);
    }

    /**
     * 分页查询
     * @param pageNum 第几页数据
     * @param size 每页显示多少记录
     * @return
     */
    public abstract Object queryPagenate(int pageNum,int size);


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

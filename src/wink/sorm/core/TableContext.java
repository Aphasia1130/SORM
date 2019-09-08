package wink.sorm.core;

import wink.sorm.bean.ColumnInfo;
import wink.sorm.bean.TableInfo;
import wink.sorm.utils.JavaFileUtils;
import wink.sorm.utils.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责管理数据库中所有表结构和类结构的关系，并可以根据表结构生成类结构。
 */
public class TableContext {
    /**
     * 标名为key，表信息对象为value
     */
    public static Map<String, TableInfo> tables = new HashMap <>();

    /**
     * 将po的class对象和表信息对象关联起来，便于重用！
     */
    public static Map<Class,TableInfo> poClassTableMap = new HashMap <>();

    public TableContext() {}

    static {
        //初始化获得表的信息
        Connection con = DBManager.getConnection();
        try {
            DatabaseMetaData dbmd = con.getMetaData();

            ResultSet tableRet = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});

            while (tableRet.next()) {
                String tableName = (String) tableRet.getObject("TABLE_NAME");

                TableInfo ti = new TableInfo(tableName,new ArrayList<ColumnInfo>(),new HashMap<String,ColumnInfo>());

                tables.put(tableName, ti);

                ResultSet set = dbmd.getColumns(null,"%",tableName,"%");//查询表中的所有字段
                while (set.next()) {

                    ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"),
                            set.getString("TYPE_NAME"),0);
                    ti.getColumns().put(set.getString("COLUMN_NAME"),ci);
                }

                ResultSet set2 = dbmd.getPrimaryKeys(null,"%",tableName);//查询表中的主键
                while (set2.next()) {
                    ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
                    ci2.setKeyType(1);//设为主键类型
                    ti.getPrikeys().add(ci2);
                }
                if (ti.getPrikeys().size() > 0) {//取唯一主键，方便使用。如果是来联合主键，则为空
                    ti.setOnlyPrikey(ti.getPrikeys().get(0));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //更新类结构
        updateJavaPOFile();

        //加载po包下面的类，便于重用，提高效率
        loadPOTables();
    }

    /**
     * 根据表结构，更新配置下的po包下的java类
     * 实现了从表结构转化到类结构
     */
    public static void updateJavaPOFile(){
        Map <String, TableInfo> map = TableContext.tables;
        for (TableInfo t : map.values()) {
            JavaFileUtils.createJavaPOFile(t, new MySqlTypeConvertor());
        }
    }


    /**
     * 加载po包下面的类
     */
    public static void loadPOTables(){

        for (TableInfo tableInfo : tables.values()) {

            Class c = null;
            try {
                c = Class.forName(DBManager.getConf().getPoPackage()+
                        "."+ StringUtils.firstChar2UpperCase(tableInfo.getName()));
                poClassTableMap.put(c,tableInfo);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

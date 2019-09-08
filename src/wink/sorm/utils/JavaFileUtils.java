package wink.sorm.utils;

import wink.sorm.bean.ColumnInfo;
import wink.sorm.bean.JavaFiledGetSet;
import wink.sorm.bean.TableInfo;
import wink.sorm.core.DBManager;
import wink.sorm.core.MySqlTypeConvertor;
import wink.sorm.core.TableContext;
import wink.sorm.core.TypeConvertor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 封装了生成Java文件（源文件）的操作
 *
 * @author wink
 */
public class JavaFileUtils {

    /**
     * 根据字段信息生成Java属性信息。如 varchar username --> private String username;
     * 以及相应的set和get方法源码
     *
     * @param columnInfo
     * @param convertor
     * @return
     */
    public static JavaFiledGetSet createFieldGetSetSRC(ColumnInfo columnInfo, TypeConvertor convertor) {

        JavaFiledGetSet jfgs = new JavaFiledGetSet();

        String javaFieldType = convertor.databaseType2JavaType(columnInfo.getDataType());

        jfgs.setFieldInfo("\tprivate " + javaFieldType + " " + columnInfo.getName() + ";\n");

        //public String getUsername(){return username;}
        //生成get方法的源代码
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic " + javaFieldType + " get" + StringUtils.firstChar2UpperCase(columnInfo.getName()) + "(){\n");
        getSrc.append("\t\treturn " + columnInfo.getName() + ";\n");
        getSrc.append("\t}\n");
        jfgs.setGetInfo(getSrc.toString());

        //public void getUsername(String username){this.username =  username;}
        //生成set方法的源代码
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void set" + StringUtils.firstChar2UpperCase(columnInfo.getName()) + "(");
        setSrc.append(javaFieldType + " " + columnInfo.getName() + "){\n");
        setSrc.append("\t\tthis." + columnInfo.getName() + " = " + columnInfo.getName() + ";\n");
        setSrc.append("\t}\n");
        jfgs.setSetInfo(setSrc.toString());
        return jfgs;
    }

    /**
     * 根据表信息生成java类的源代码
     *
     * @param tableInfo tableInfo 表信息
     * @param convertor convertor 数据类型转换器
     * @return java类的源代码
     */
    public static String createJavaSrc(TableInfo tableInfo, TypeConvertor convertor) {

        Map <String, ColumnInfo> columns = tableInfo.getColumns();
        List <JavaFiledGetSet> javaFileds = new ArrayList <>();

        for (ColumnInfo c : columns.values()) {
            javaFileds.add(createFieldGetSetSRC(c, convertor));
        }

        StringBuilder src = new StringBuilder();

        //生成package语句
        src.append("package " + DBManager.getConf().getPoPackage() + ";\n\n");

        //生成import语句
        src.append("import java.sql.*;\n");
        src.append("import java.util.*;\n\n");

        //生成类声明语句
        src.append("public class " + StringUtils.firstChar2UpperCase(tableInfo.getName()) + " {\n\n");

        //生成属性列表
        for (JavaFiledGetSet f : javaFileds) {
            src.append(f.getFieldInfo());
        }
        src.append("\n\n");
        //生成get方法列表
        for (JavaFiledGetSet f : javaFileds) {
            src.append(f.getGetInfo());
        }

        //生成set方法列表
        for (JavaFiledGetSet f : javaFileds) {
            src.append(f.getSetInfo());
        }

        //生成类结束语句
        src.append("}\n");
//        System.out.println(src);
        return src.toString();
    }

    public static void createJavaPOFile(TableInfo tableInfo, TypeConvertor convertor) {
        String src = createJavaSrc(tableInfo, convertor);

        String srcPath = DBManager.getConf().getSrcPath()+"\\";
        String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.","\\\\");

        File f = new File(srcPath+packagePath);
//        System.out.println(f.getAbsolutePath()+"******************");

        if (!f.exists()) {//如果目录不存在，则帮助用户建立
            f.mkdirs();
        }

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstChar2UpperCase(tableInfo.getName())+".java"));
            bw.write(src);
            System.out.println("建立表"+tableInfo.getName()+"对应的java类："+StringUtils.firstChar2UpperCase(tableInfo.getName()+".java"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        ColumnInfo ci = new ColumnInfo("username", "varchar", 0);
//        JavaFiledGetSet f = createFieldGetSetSRC(ci, new MySqlTypeConvertor());
//        System.out.println(f);

        Map <String, TableInfo> map = TableContext.tables;
        for (TableInfo t : map.values()) {
            createJavaPOFile(t, new MySqlTypeConvertor());
        }

    }
}

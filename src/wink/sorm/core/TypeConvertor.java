package wink.sorm.core;

/**
 * 负责java数据类型和数据库数据类型进行转换
 * @author wink
 */
public interface TypeConvertor {

    /**
     * 将数据库数据类型转换成java的数据类型
     * @param columnType
     * @return
     */
    public String databaseType2JavaType(String columnType);

    /**
     *将java的数据类型转化成数据库数据类型
     * @param javaDataType
     * @return
     */
    public String JavaType2databaseType(String javaDataType);
}

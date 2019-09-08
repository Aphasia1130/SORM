package wink.sorm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储表结构的信息
 * @author wink
 */
public class TableInfo {
    /**
     * 表名
     */
    private String name;
    /**
     * 所有字段的信息
     */
    private Map<String,ColumnInfo> columns;

    /**
     * 唯一主键（目前只能处理表中有且只有一个主键的情况）
     */
    private ColumnInfo onlyPrikey;

    /**
     * 如果联合主键，则在这里存储
     */
    private List<ColumnInfo> prikeys;

    public TableInfo(String name, Map <String, ColumnInfo> columns, ColumnInfo onlyPrikey) {
        this.name = name;
        this.columns = columns;
        this.onlyPrikey = onlyPrikey;
    }

    public TableInfo() {
    }

    public TableInfo(String name, List <ColumnInfo> prikeys, Map <String, ColumnInfo> columns) {
        this.name = name;
        this.prikeys = prikeys;
        this.columns = columns;
    }

    //    public TableInfo(TableInfo(String tname,new ArrayList<ColumnInfo>(),new HashMap<String,ColumnInfo>()))

    public void setName(String name) {
        this.name = name;
    }

    public void setColumns(Map <String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public void setOnlyPrikey(ColumnInfo onlyPrikey) {
        this.onlyPrikey = onlyPrikey;
    }

    public void setPrikeys(List <ColumnInfo> prikeys) {
        this.prikeys = prikeys;
    }

    public String getName() {
        return name;
    }

    public Map <String, ColumnInfo> getColumns() {
        return columns;
    }

    public ColumnInfo getOnlyPrikey() {
        return onlyPrikey;
    }

    public List <ColumnInfo> getPrikeys() {
        return prikeys;
    }
}

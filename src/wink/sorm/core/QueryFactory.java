package wink.sorm.core;

/**
 * 创建Query对象的工厂类
 * @author wink
 */
public class QueryFactory {

    private static Query prototypeobj;  //原型对象

    static {
        try {
            Class  c = Class.forName(DBManager.getConf().getQueryClass());
            prototypeobj = (Query) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QueryFactory() {//私用构造器
    }

    /**
     * 生成查询对象
     * @return
     */
    public static Query createQuery() {
        try {
            return (Query) prototypeobj.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}




    /*
     private static QueryFactory factory = new QueryFactory();
    private static Class c;

    static{
        try {
            c = Class.forName(DBManager.getConf().getQueryClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private QueryFactory(){//私用构造器
    }

    public Query createQuery() {
        try {
            return (Query) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
     */


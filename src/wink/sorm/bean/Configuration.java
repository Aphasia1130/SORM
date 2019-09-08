package wink.sorm.bean;

/**
 * 管理配置信息
 * @author wink
 */
public class Configuration {
    /**
     * 驱动类
     */
    private String driver;
    /**
     * jdbc的url
     */
    private String url;
    /**
     * 数据库的用户名
     */
    private String user;
    /**
     * 数据库的密码
     */
    private String pwd;
    /**
     * 正在使用的数据库的名字
     */
    private String usingDB;
    /**
     * 项目的源码路径
     */
    private String srcPath;
    /**
     * 扫描生成Java类的包 （po的意思 ：Persistence object 持久化对象）
     */
    private String poPackage;

    /**
     * 项目使用的查询类是哪一个类
     */
    private String queryClass;

    /**
     * 连接池中最小的连接数
     */
    private int poolMinSize;

    /**
     * 连接池中最大的连接数
     */
    private int poolMaxSize;




    public Configuration(String driver, String url, String user, String pwd,
                         String usingDb, String srcPaht, String poPackage) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
        this.usingDB = usingDb;
        this.srcPath = srcPaht;
        this.poPackage = poPackage;
    }

    public Configuration() {
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getUsingDB() {
        return usingDB;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getPoPackage() {
        return poPackage;
    }

    public String getQueryClass() {
        return queryClass;
    }

    public int getPoolMinSize() {
        return poolMinSize;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setUsingDB(String usingDB) {
        this.usingDB = usingDB;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setPoPackage(String poPackage) {
        this.poPackage = poPackage;
    }

    public void setQueryClass(String queryClass) {
        this.queryClass = queryClass;
    }

    public void setPoolMinSize(int poolMinSize) {
        this.poolMinSize = poolMinSize;
    }

    public void setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }
}

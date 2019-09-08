package wink.sorm.pool;

import wink.sorm.core.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池的类
 * @author wink
 */
public class DBConnPool {
    /**
     * 连接池对象
     */
    public  List<Connection> pool;

    /**
     * 最大连接数
     */
    private static final int POOL_MAX_SIZE  = DBManager.getConf().getPoolMaxSize();

    /**
     * 最小连接数
     */
    private static final int POOL_MIN_SIZE  = DBManager.getConf().getPoolMinSize();

    static {

    }

    /**
     * 初始化连接池，使池中的连接数到达最小值
     */
    public void initPool() {
        if (pool == null) {
            pool = new ArrayList <>();
        }
        while (pool.size() < DBConnPool.POOL_MIN_SIZE) {
            pool.add(DBManager.createConnection());
            System.out.println("初始化池，池中连接数"+pool.size());
        }
    }

    /**
     * 从连接池中取出一个连接
     * @return
     */
    public synchronized Connection getConnection() {
        int last_intdex = pool.size()-1;
        Connection conn = pool.get(last_intdex);
        pool.remove(last_intdex);
        return conn;
    }

    /**
     * 将连接放回池中
     * @param conn
     */
    public synchronized void close(Connection conn) {
        if (pool.size() >= POOL_MAX_SIZE) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            pool.add(conn);
        }
    }

    public DBConnPool() {
        initPool();
    }

}

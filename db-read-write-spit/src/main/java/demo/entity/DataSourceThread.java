package demo.entity;

/**
 * 定义线程类，用于从库的设置，清空和获取
 * create by hanshin on 2021/4/19
 */
public class DataSourceThread {
    private static final ThreadLocal<String> DATASOURCE_THREAD = new ThreadLocal<>();

    public static void set() {
        DATASOURCE_THREAD.set("slave");
    }

    public static String get() {
        return DATASOURCE_THREAD.get();
    }

    public static void clear() {
        DATASOURCE_THREAD.remove();
    }
}

package com.itheima.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Properties;

/**
 * <p>
 *
 * </p>
 *
 * @author: Eric
 * @since: 2020/11/19
 */
public class SettingCenterUtil extends PropertyPlaceholderConfigurer implements ApplicationContextAware {

    private AbstractApplicationContext applicationContext;

    private static boolean flag = true;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        // 读取zookeeper上的数据库配置信息
        loadZk(props);
        if(flag) {
            addWatch();
            flag = false; // 不需要再次监听
        }
        super.processProperties(beanFactoryToProcess, props);
    }

    /**
     * 监听数据库的配置信息
     */
    private void addWatch(){
        String connectString = "127.0.0.1:2181";
        int sessionTimeoutMs = 3000; //3 s
        int connectionTimeoutMs = 3000;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1,3,1);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        client.start();

        TreeCache treeCache = new TreeCache(client,"/config");
        try {
            treeCache.start();
            treeCache.getListenable().addListener((cli,event)->{
                // 配置被修改了
                if(event.getType() == TreeCacheEvent.Type.NODE_UPDATED){
                    String path = event.getData().getPath();
                    String data = new String(event.getData().getData());
                    System.out.println(path + "节点修改: " + data);
                    // 刷新容器
                    this.applicationContext.refresh();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取zookeeper上的数据库配置信息
     * @param props
     */
    private void loadZk( Properties props){
        String connectString = "127.0.0.1:2181";
        int sessionTimeoutMs = 3000; //3 s
        int connectionTimeoutMs = 3000;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1,3,1);
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
        client.start();
        try {

            String driver = new String(client.getData().forPath("/config/jdbc.driver"));
            String url = new String(client.getData().forPath("/config/jdbc.url"));
            String user = new String(client.getData().forPath("/config/jdbc.user"));
            String password = new String(client.getData().forPath("/config/jdbc.password"));

            // 设置进spring配置信息里，将来用到datasource替换表达式,key必须与表达式一致
            props.setProperty("jdbc.driver",driver);
            props.setProperty("jdbc.url",url);
            props.setProperty("jdbc.user",user);
            props.setProperty("jdbc.password",password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (AbstractApplicationContext) applicationContext;
    }
}

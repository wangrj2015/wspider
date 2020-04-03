package com.rjay.wspider.service.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ZookeeperClient {

    private static final String ZK_NAMESPACE = "wspider";

    private static final int SESSION_TIMEOUT_MS = 30000;

    private static final String CHARSET = "utf-8";

    @Value("${zookeeper.hostList}")
    private String zkHostList;

    private CuratorFramework client;

    private boolean init = false;

    public void init(){
        //初始化zk client
        client = CuratorFrameworkFactory.builder().retryPolicy(new RetryUntilElapsed(20000, 1000))
                                        .connectString(zkHostList)
                                        .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                                        .namespace(ZK_NAMESPACE)
                                        .build();
        client.start();

        this.init = true;
    }


    public String createEphemeralPath(String path, String data) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,data.getBytes(CHARSET));
    }

    public String createEphemeralPath(String path) throws Exception {
        return client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
    }

    public void delete(String path) throws Exception{
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public Stat setData(String path, byte[] data) throws Exception{
        return client.setData().forPath(path, data);
    }

    public Stat checkExist(String path) throws Exception{
        return client.checkExists().forPath(path);
    }

    public PathChildrenCache pathChildrenCache(String path){
        return new PathChildrenCache(client, path,true);
    }

    public boolean isInit() {
        return init;
    }
}

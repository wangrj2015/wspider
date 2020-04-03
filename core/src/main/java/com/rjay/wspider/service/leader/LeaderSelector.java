package com.rjay.wspider.service.leader;

import com.rjay.wspider.service.worker.Worker;
import com.rjay.wspider.service.worker.WorkerHolder;
import com.rjay.wspider.util.NetWorkUtil;
import com.google.common.collect.Lists;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.utils.ZKPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 选举任务分发者
 */
@Service("leaderSelector")
public class LeaderSelector {

    private Logger logger = LoggerFactory.getLogger(LeaderSelector.class);

    private static final String LEADER_PATH = "/leader";

    private static final String WORKER_PATH = "/worker";

    @Autowired
    private LeaderHolder leaderHolder;

    @Autowired
    private WorkerHolder workerHolder;

    @Autowired
    private ZookeeperClient zookeeperClient;

    private List<LeaderSelectorListener> listeners = Lists.newArrayList();

    private PathChildrenCache leaderCache;

    private PathChildrenCache workerCache;

    /**
     * 注册监听器
     * @param listener
     */
    public void registerListener(LeaderSelectorListener listener){
        this.listeners.add(listener);
    }

    /**
     * 选举
     */
    public void select(){
        init();
        //获取本机ip
        String ip = NetWorkUtil.getLocalHostAddress();
        if(null == ip){
            logger.error("获取本机ip失败");
            return;
        }
        logger.info("本机ip:{}",ip);
        try{
            //创建leader临时节点，竞选leader
            String result = zookeeperClient.createEphemeralPath(ZKPaths.makePath(LEADER_PATH,"lock"), ip);
            logger.info("当选为leader:{}",result);
            leaderHolder.setActive(true);
            leaderHolder.setHost(ip);
            //如果存在本机的worker节点，则删除
            String workerChildPath = ZKPaths.makePath(WORKER_PATH,ip);
            if(null != zookeeperClient.checkExist(workerChildPath)){
                zookeeperClient.delete(workerChildPath);
            }
            //添加worker子节点监听
            addWorkerListener();
            //触发当选监听器
            listeners.stream().forEach(listener -> listener.isLeader());
        }catch (Exception e){
            logger.warn("竞选leader失败",e);
            try{
                //创建worker临时子节点(路径如：/worker/192.168.0.1)
                String workerChildPath = ZKPaths.makePath(WORKER_PATH,ip);
                if(null == zookeeperClient.checkExist(workerChildPath)){
                    String result = zookeeperClient.createEphemeralPath(workerChildPath);
                    logger.info("创建worker临时子节点完成:{}",result);
                }
                workerHolder.setHost(ip);
                workerHolder.setActive(true);
                //添加leader子节点监听
                addLeaderListener();
            }catch (Exception ee){
                logger.error("创建worker临时子节点失败",ee);
            }
        }
    }

    private void init(){
        zookeeperClient.init();
        //reset leaderHolder/workerHolder
        leaderHolder.reset();
        workerHolder.reset();
        //clear cache listener
        if(null != leaderCache){
            leaderCache.getListenable().clear();
        }
        if(null != workerCache){
            workerCache.getListenable().clear();
        }
    }

    private void addWorkerListener(){
        workerCache = zookeeperClient.pathChildrenCache(WORKER_PATH);
        workerCache.getListenable().addListener((client,event)->{
            if(null == event.getData()){
                return;
            }
            if(!this.leaderHolder.isActive()){
                return;
            }
            logger.info("catch worker event,type:{},path:{}",event.getType(), event.getData().getPath());
            String childPath = event.getData().getPath();
            byte[] childData = event.getData().getData();
            String host = ZKPaths.getNodeFromPath(childPath);
            Worker worker = new Worker(host, childData);
            switch (event.getType()){
                case CHILD_ADDED:
                case CHILD_UPDATED:
                    leaderHolder.addWorker(worker);
                    break;
                case CHILD_REMOVED:
                    leaderHolder.rmWorker(worker);
            }
        });
        try{
            workerCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        }catch (Exception e){
            logger.error("worker cache start error",e);
        }
        List<ChildData> childDataList = workerCache.getCurrentData();
        childDataList.stream().forEach(childData -> {
            String childPath = childData.getPath();
            byte[] data = childData.getData();
            String host = ZKPaths.getNodeFromPath(childPath);
            Worker worker = new Worker(host,data);
            leaderHolder.addWorker(worker);
        });
    }

    private void addLeaderListener(){
        leaderCache = zookeeperClient.pathChildrenCache(LEADER_PATH);
        leaderCache.getListenable().addListener((client,event)->{
            if(this.leaderHolder.isActive()){
                return;
            }
            logger.info("catch leader event,type:{},data:{}",event.getType(),event.getData());
            switch (event.getType()){
                case CHILD_REMOVED:
                    //leader节点消失，重新选举
                    logger.info("leader节点消失，重新选举");
                    select();
            }
        });
        try{
            leaderCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        }catch (Exception e){
            logger.error("leader cache start error",e);
        }
    }
}

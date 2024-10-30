package com.t13max.data.mongo;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.connection.ConnectionPoolSettings;
import com.t13max.common.config.DataConfig;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.run.Application;
import dev.morphia.Datastore;
import dev.morphia.DeleteOptions;
import dev.morphia.InsertOneOptions;
import dev.morphia.Morphia;
import dev.morphia.query.filters.Filters;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Mongodb操作工具类
 * 待完善
 *
 * @author: t13max
 * @since: 13:28 2024/5/29
 */
public class MongoManager extends ManagerBase {

    //mongodb客户端
    private MongoClient mongoClient;
    //java类映射
    private Datastore datastore;

    public static MongoManager inst() {
        return inst(MongoManager.class);
    }

    @Override
    protected void onShutdown() {
        mongoClient.close();
    }

    @Override
    public void init() {

        DataConfig dataConfig = Application.config().getDataConfig();

        // 配置连接池设置
        ConnectionPoolSettings connectionPoolSettings = ConnectionPoolSettings.builder()
                .maxSize(dataConfig.getMaxSize()) // 最大连接数
                .minSize(dataConfig.getMinSize()) // 最小连接数
                .maxWaitTime(dataConfig.getWaitTime(), TimeUnit.MILLISECONDS) // 获取连接的最大等待时间
                .maxConnectionIdleTime(dataConfig.getMaxIdle(), TimeUnit.SECONDS) // 连接的最大空闲时间
                .build();

        // 配置MongoClient设置
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(dataConfig.getUrl()))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(connectionPoolSettings))
                .build();

        // 创建MongoClient
        mongoClient = MongoClients.create(settings);

        // 创建Datastore并连接到数据库
        datastore = Morphia.createDatastore(mongoClient, dataConfig.getDatabase());

    }

    public <T extends IData> List<T> findList(Class<T> clazz) {
        return datastore.find(clazz, new Document()).stream().toList();
    }

    public <T extends IData> List<T> findList(long id, Class<T> clazz) {
        return datastore.find(clazz, new Document("_id", id)).stream().toList();
    }

    /**
     * 根据id查找数据
     *
     * @Author t13max
     * @Date 15:51 2024/8/2
     */
    public <T extends IData> T findById(Class<T> clazz, long id) {
        return datastore.find(clazz, new Document("_id", id)).first();
    }

    /**
     * 根据id查找数据 id是int  给区块用的
     *
     * @Author t13max
     * @Date 15:25 2024/8/14
     */
    public <T extends IData> T findById(String collectionName, int id) {
        return (T) datastore.find(collectionName).filter(Filters.eq("_id", id)).first();
    }

    /**
     * 条件查询
     *
     * @Author t13max
     * @Date 15:51 2024/8/2
     */
    public <T extends IData> T findById(Class<T> clazz, Document filter) {
        return datastore.find(clazz, filter).first();
    }

    /**
     * 删除指定数据
     *
     * @Author t13max
     * @Date 15:51 2024/8/2
     */
    public <T extends IData> boolean delete(T data) {
        DeleteResult deleteResult = datastore.delete(data);
        return deleteResult.getDeletedCount() > 0;
    }

    public <T extends IData> boolean delete(String collectionName, T data) {
        DeleteResult deleteResult = datastore.delete(data, new DeleteOptions().collection(collectionName));
        return deleteResult.getDeletedCount() > 0;
    }

    /**
     * 批量删除
     *
     * @Author t13max
     * @Date 16:43 2024/8/7
     */
    public <T extends IData> boolean deleteList(List<T> dataList) {
        for (T data : dataList) {
            this.delete(data);
        }
        return true;
    }

    /**
     * 保存
     *
     * @Author t13max
     * @Date 15:53 2024/8/2
     */
    public <T extends IData> T save(T data) {
        return datastore.save(data);
    }

    /**
     * 保存到指定表
     *
     * @Author t13max
     * @Date 16:19 2024/8/2
     */
    public <T extends IData> T save(T data, String collectionName) {
        return datastore.save(data, new InsertOneOptions().collection(collectionName));
    }

    /**
     * 批量保存
     *
     * @Author t13max
     * @Date 16:35 2024/8/7
     */
    public <T extends IData> boolean saveList(List<T> dataList) {
        for (T data : dataList) {
            save(data);
        }
        return true;
    }

}

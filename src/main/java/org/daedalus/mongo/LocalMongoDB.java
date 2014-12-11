package org.daedalus.mongo;

import java.net.UnknownHostException;
import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class LocalMongoDB {
    private MongoClient mongoClient;
    private DB db;
    private final String dbName;
    private final int port;


    public LocalMongoDB(String dbName, int port) {
        this.dbName = dbName;
        this.port = port;

        createLocalConnection();
    }

    private void createLocalConnection() {
        try {
            MongoCredential credential = MongoCredential.createMongoCRCredential("mongodevuser", "thermostat", "mongodevpassword".toCharArray());
            ServerAddress address = new ServerAddress("127.0.0.1", port);
            mongoClient = new MongoClient(address, Arrays.asList(credential));
            db = mongoClient.getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public DB getDB() {
        return this.db;
    }

    public String getDbName() {
        return this.dbName;
    }

    public int getPort() {
        return this.port;
    }

    public String getHost() {
        return "127.0.0.1";
    }
}

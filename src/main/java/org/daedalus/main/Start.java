package org.daedalus.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.daedalus.mongo.LocalMongoDB;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class Start {

    private static final int NUMBER_OF_DOCS = 500000;
    private static final ExecutorService executor = Executors.newFixedThreadPool(50);
    private static final CountDownLatch latch = new CountDownLatch(NUMBER_OF_DOCS);

    private static FileOutputStream fout ;

    public static void main(String[] args) {
        if (args.length > 0) {
            File f = new File(args[0]);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (f.canWrite()) {
                try {
                    fout = new FileOutputStream(f);

                    System.out.println("Outputting to file: " + f.getAbsolutePath());
                    System.out.println("Starting");

                    LocalMongoDB localMongoDB = new LocalMongoDB("thermostat", 27518);

                    DB db = localMongoDB.getDB();

//                    testLoop(db);

                    testThread(db);

                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void testThread(DB db) throws IOException {
        if (db.collectionExists("collection")) {
            db.getCollection("collection").drop();
        }
        db.createCollection("collection", new BasicDBObject());

        final DBCollection collection = db.getCollection("collection");

        long s = System.nanoTime();
        for (int i = 0; i < NUMBER_OF_DOCS; i++) {
            final int finalI = i;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                            .append("type", "database")
                            .append("count", 1)
                            .append("info", new BasicDBObject("x", finalI).append("y", finalI * 10))
                            .append("other", new BasicDBObject("z", finalI * 2).append("xyz", finalI * 3));
                    collection.insert(doc);
                    latch.countDown();
                }
            };
            executor.execute(run);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String elapsed = "Thread: Elapsed: " + (System.nanoTime() - s) / 1E9 + "\n";
        System.out.println(elapsed);
        fout.write(elapsed.getBytes());
    }

    private static void testLoop(DB db) throws IOException {
        if (db.collectionExists("collection")) {
            db.getCollection("collection").drop();
        }

        if (!db.collectionExists("collection")) {
            db.createCollection("collection", new BasicDBObject());
        }

        final DBCollection collection = db.getCollection("collection");

        long s = System.nanoTime();
        for (int i = 0; i < NUMBER_OF_DOCS; i++) {
            BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                    .append("type", "database")
                    .append("count", 1)
                    .append("info", new BasicDBObject("x", i).append("y", i * 10));
            collection.insert(doc);
        }

        String elapsed = "Thread: Elapsed: " + (System.nanoTime() - s) / 1E9 + "\n";
        System.out.println(elapsed);
        fout.write(elapsed.getBytes());
    }
}
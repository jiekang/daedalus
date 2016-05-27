package org.daedalus.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.daedalus.mongo.LocalMongoDB;

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

                    MongoDatabase db = localMongoDB.getDB();

                    testRead(db);

                    fout.close();
                    localMongoDB.closeConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void testInsert(MongoDatabase db) {
        MongoCollection<Document> vmCollection = db.getCollection("vm-info");
        Document doc = new Document("name", "user");
        vmCollection.insertOne(doc);
    }

    private static void testRead(MongoDatabase db) throws IOException {
        MongoCollection<Document> vmCollection = db.getCollection("vm-info");

        long s = System.nanoTime();
        FindIterable<Document> it = vmCollection.find();

        String elapsed = "Thread: Elapsed: " + (System.nanoTime() - s) / 1E9 + "\n";
        for (Document d : it) {
            System.out.println(d.toString());
            fout.write(d.toString().getBytes());
        }

        System.out.println(elapsed);
        fout.write(elapsed.getBytes());
    }

    private static void testThread(MongoDatabase db) throws IOException {
        db.getCollection("collection").drop();
        db.createCollection("collection");

        final MongoCollection<Document> collection = db.getCollection("collection");

        long s = System.nanoTime();
        for (int i = 0; i < NUMBER_OF_DOCS; i++) {
            final int finalI = i;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    Document doc = new Document("name", "MongoDB")
                            .append("type", "database")
                            .append("count", 1)
                            .append("info", new Document("x", finalI).append("y", finalI * 10))
                            .append("other", new Document("z", finalI * 2).append("xyz", finalI * 3));
                    collection.insertOne(doc);
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
}

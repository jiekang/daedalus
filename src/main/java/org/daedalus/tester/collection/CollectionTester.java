package org.daedalus.tester.collection;

import com.mongodb.client.MongoCollection;
import org.daedalus.tester.TestRunnable;

import java.io.PrintStream;
import java.util.List;

public interface CollectionTester {
    List<TestRunnable> getTests(PrintStream stream, MongoCollection collection);
    String getCollectionName();
}

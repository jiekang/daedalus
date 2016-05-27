package org.daedalus.tester;

import com.mongodb.client.MongoDatabase;
import org.daedalus.tester.collection.vm.info.VmInfoCollectionTester;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Tester {

    private static final List<TestRunnable> tests = new ArrayList<>();

    public static void buildTests(PrintStream stream, MongoDatabase db) {
        VmInfoCollectionTester vmInfoCollectionTester = new VmInfoCollectionTester();
        tests.addAll(vmInfoCollectionTester.getTests(stream, db.getCollection(vmInfoCollectionTester.getCollectionName())));
    }

    public static void runTests() {
        for (TestRunnable t : tests) {
            timeTest(t);
        }
    }

    public static void printResuls(PrintStream stream) {
        for (TestRunnable t : tests) {
            stream.print("Test: " + t.getTestName() + " " + "Elapsed: " + t.elapsedTime);
        }
    }

    public static void timeTest(TestRunnable test) {
        long s = System.nanoTime();

        test.run();

        long e = System.nanoTime();

        test.elapsedTime = e - s;
    }
}

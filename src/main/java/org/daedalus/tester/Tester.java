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

    public static void runTests(int iterations) {
        for (TestRunnable t : tests) {
            timeTest(iterations, t);
        }
    }

    public static void printResults(PrintStream ... streams) {
        for (PrintStream stream : streams) {
            stream.println();
        }
        for (TestRunnable t : tests) {
            for (PrintStream stream : streams) {
                stream.println("Test: " + t.getTestName() + " " + "Elapsed: " + t.elapsedTime + " nanoseconds");
            }
        }
        for (PrintStream stream : streams) {
            stream.println();
        }
    }

    public static void timeTest(int iterations, TestRunnable test) {
        long s = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            test.run();
        }

        long e = System.nanoTime();

        test.elapsedTime = (e - s) / iterations;
    }
}

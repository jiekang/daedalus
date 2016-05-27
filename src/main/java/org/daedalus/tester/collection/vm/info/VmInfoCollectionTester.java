package org.daedalus.tester.collection.vm.info;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.daedalus.tester.TestRunnable;
import org.daedalus.tester.collection.CollectionTester;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class VmInfoCollectionTester implements CollectionTester {

    private MongoCollection collection;

    private void testGetAllVmInfo() {
        collection.find();
    }
    private void testGetAllVmInfoForAgent() {
        collection.find();
    }
    private void testGetVmInfo() {
        collection.find();
    }
    private void testPutVmInfo() {
        Document d = new Document();
        collection.insertOne(d);
    }

    @Override
    public List<TestRunnable> getTests(PrintStream stream, MongoCollection collection) {
        this.collection = collection;

        List<TestRunnable> tests = new ArrayList<>();
        tests.add(new TestRunnable() {
            @Override
            public void run() {
                testGetAllVmInfo();
            }

            @Override
            public String getTestName() {
                return "vm-info-get-all";
            }
        });
        tests.add(new TestRunnable() {
            @Override
            public void run() {
                testGetAllVmInfoForAgent();
            }

            @Override
            public String getTestName() {
                return "vm-info-get-all-agent";
            }
        });
        tests.add(new TestRunnable() {
            @Override
            public void run() {
                testGetVmInfo();
            }

            @Override
            public String getTestName() {
                return "vm-info-get";
            }
        });
        tests.add(new TestRunnable() {
            @Override
            public void run() {
                testPutVmInfo();
            }

            @Override
            public String getTestName() {
                return "vm-info-put";
            }
        });

        return tests;
    }

    @Override
    public String getCollectionName() {
        return "vm-info";
    }
}

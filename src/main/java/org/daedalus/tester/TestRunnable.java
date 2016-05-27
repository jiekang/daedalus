package org.daedalus.tester;

public abstract class TestRunnable {
    public long elapsedTime;

    public abstract void run();
    public abstract String getTestName();

}

package com.diamondfire.helpbot.sys.task.timeprovider;

public interface TimeProvider {

    long getInitialTime();

    long getRepeatingTime();

}

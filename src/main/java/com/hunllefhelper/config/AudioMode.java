package com.hunllefhelper.config;

public enum AudioMode
{
    Default("default"),
    Disabled("disabled"),
    Custom("custom"),
    Soft_ASMR("asmr");

    private final String dirName;

    AudioMode(String dirName)
    {
        this.dirName = dirName;
    }

    public String getDirName()
    {
        return this.dirName;
    }
}
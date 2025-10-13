package com.hunllefhelper.config;

import lombok.Getter;


public enum AudioMode
{
    Default("default"),
    Disabled("disabled"),
    Soft_ASMR("asmr"),
    Custom("custom");

    @Getter
    private final String dirName;

    AudioMode(String dirName)
    {
        this.dirName = dirName;
    }
}
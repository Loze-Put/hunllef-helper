package com.hunllefhelper.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PanelVisibility
{
    Always("Always"),
    InsideGauntlet("Inside Gauntlet"),
    AtHunllef("At Hunllef");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
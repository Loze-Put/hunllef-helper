package com.hunllefhelper.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VisibilityMode
{
    AlwaysVisible("Always Visible"),
    InsideGauntlet("Only Inside Gauntlet"),
    OnlyInHunllefRoom("Only In Hunllef Room");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
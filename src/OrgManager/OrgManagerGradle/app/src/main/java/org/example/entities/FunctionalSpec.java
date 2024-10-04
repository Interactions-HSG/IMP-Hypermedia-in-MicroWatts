package org.example.entities;

import com.google.gson.annotations.SerializedName;

public class FunctionalSpec {
    @SerializedName(value = "qk")
    public int hasQuantityKind;
    @SerializedName(value = "mi")
    public int measurementInterval;
    @SerializedName(value = "ui")
    public int updateInterval;
    @SerializedName(value = "md")
    public int measurementDuration;
}

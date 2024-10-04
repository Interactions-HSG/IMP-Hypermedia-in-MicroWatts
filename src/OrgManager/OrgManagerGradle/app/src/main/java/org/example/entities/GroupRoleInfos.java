package org.example.entities;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GroupRoleInfos{
    @SerializedName(value = "e")
    public List<GroupRoleInfo> elements;
    @SerializedName(value = "n")
    public int num_elements;
}
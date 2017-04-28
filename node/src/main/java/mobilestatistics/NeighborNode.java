package mobilestatistics;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NeighborNode {
    @SerializedName("neighborId") @Expose private long id;
    @SerializedName("distance") @Expose private int distance;
    @SerializedName("x") @Expose private int x;
    @SerializedName("y") @Expose private int y;
    @SerializedName("totalPacketsSent") @Expose private int totalPacketsSent;
    @SerializedName("totalPacketsForwarded") @Expose private int totalPacketsForwarded;
    @SerializedName("meanConnectivityRatio") @Expose private double meanConnectivityRatio;

    public void setId(long id) {
        this.id = id;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setTotalPacketsSent(int totalPacketsSent) {
        this.totalPacketsSent = totalPacketsSent;
    }

    public void setTotalPacketsForwarded(int totalPacketsForwarded) {
        this.totalPacketsForwarded = totalPacketsForwarded;
    }

    public void setMeanConnectivityRatio(double meanConnectivityRatio) {
        this.meanConnectivityRatio = meanConnectivityRatio;
    }
}

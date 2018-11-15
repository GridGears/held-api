package at.gridgears.held;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class AmlData implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double latitude;
    private final double longitude;
    private final Double radius;
    private final Instant timestamp;
    private final int confidenceLevel;
    private final PositioningMethod positioningMethod;
    private final String imsi;
    private final String imei;
    private final String mcc;
    private final String mnc;

    public AmlData(double latitude, double longitude, Double radius, Instant timestamp, int confidenceLevel, PositioningMethod positioningMethod, String imsi, String imei, String mcc, String mnc) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.timestamp = timestamp;
        this.confidenceLevel = confidenceLevel;
        this.positioningMethod = positioningMethod;
        this.imsi = imsi;
        this.imei = imei;
        this.mcc = mcc;
        this.mnc = mnc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getRadius() {
        return radius;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getConfidenceLevel() {
        return confidenceLevel;
    }

    public PositioningMethod getPositioningMethod() {
        return positioningMethod;
    }

    public String getImsi() {
        return imsi;
    }

    public String getImei() {
        return imei;
    }

    public String getMcc() {
        return mcc;
    }

    public String getMnc() {
        return mnc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AmlData amlData = (AmlData) o;
        return Double.compare(amlData.latitude, latitude) == 0 &&
                Double.compare(amlData.longitude, longitude) == 0 &&
                ((radius == null && amlData.radius == null) || (radius != null && amlData.radius != null && Double.compare(amlData.radius, radius) == 0)) &&
                confidenceLevel == amlData.confidenceLevel &&
                Objects.equals(timestamp, amlData.timestamp) &&
                positioningMethod == amlData.positioningMethod &&
                Objects.equals(imsi, amlData.imsi) &&
                Objects.equals(imei, amlData.imei) &&
                Objects.equals(mcc, amlData.mcc) &&
                Objects.equals(mnc, amlData.mnc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, radius, timestamp, confidenceLevel, positioningMethod, imsi, imei, mcc, mnc);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .append("radius", radius)
                .append("timestamp", timestamp)
                .append("confidenceLevel", confidenceLevel)
                .append("positioningMethod", positioningMethod)
                .append("imsi", imsi)
                .append("imei", imei)
                .append("mcc", mcc)
                .append("mnc", mnc)
                .toString();
    }
}

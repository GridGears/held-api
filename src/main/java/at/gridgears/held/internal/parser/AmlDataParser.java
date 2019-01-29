package at.gridgears.held.internal.parser;

import at.gridgears.held.AmlData;
import at.gridgears.held.PositioningMethod;
import at.gridgears.schemas.held.AmlType;
import at.gridgears.schemas.held.PositioningMethodType;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

import static at.gridgears.held.internal.parser.ParseUtils.toInstant;

class AmlDataParser {
    private static final Logger LOG = LoggerFactory.getLogger(AmlDataParser.class);

    AmlData parseAmlData(AmlType amlType) {
        double latitude = amlType.getLatitude();
        double longitude = amlType.getLongitude();
        Double radius = NumberUtils.isCreatable(amlType.getRadius()) ? Double.valueOf(amlType.getRadius()) : null;
        Instant timestamp = toInstant(amlType.getTimestamp());
        int confidenceLevel = amlType.getConfidenceLevel();
        PositioningMethod positioningMethod;

        PositioningMethodType positioningMethodType = amlType.getPositioningMethod();
        if (positioningMethodType != null) {
            switch (positioningMethodType) {
                case GNSS:
                    positioningMethod = PositioningMethod.GNSS;
                    break;
                case WIFI:
                    positioningMethod = PositioningMethod.WIFI;
                    break;
                case CELL:
                    positioningMethod = PositioningMethod.CELL;
                    break;
                case NO_LOCATION:
                    positioningMethod = PositioningMethod.NO_LOCATION;
                    break;
                case UNKNOWN:
                    positioningMethod = PositioningMethod.UNKNOWN;
                    break;
                default:
                    LOG.warn("Could not parse positioningMethod: " + positioningMethodType);
                    positioningMethod = PositioningMethod.UNKNOWN;
                    break;
            }
        } else {
            LOG.warn("positioningMethod was null");
            positioningMethod = PositioningMethod.UNKNOWN;
        }
        String imsi = amlType.getImsi();
        String imei = amlType.getImei();
        String mcc = amlType.getMcc();
        String mnc = amlType.getMnc();
        return new AmlData(latitude, longitude, radius, timestamp, confidenceLevel, positioningMethod, imsi, imei, mcc, mnc);
    }
}

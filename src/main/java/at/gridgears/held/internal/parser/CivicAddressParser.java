package at.gridgears.held.internal.parser;

import at.gridgears.held.CivicAddress;
import at.gridgears.schemas.held.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static at.gridgears.held.internal.parser.ParseUtils.getStringValue;

final class CivicAddressParser {
    List<CivicAddress> parse(LocationResponseType locationResponseType) {
        List<CivicAddress> result = new LinkedList<>();

        ParseUtils.getValue(locationResponseType.getAny(), Presence.class)
                .map(Presence::getTuple)
                .map(ParseUtils::first)
                .map(Tuple::getStatus)
                .map(Status::getAny)
                .map(ParseUtils::first)
                .map(CivicAddressParser::getGeopriv)
                .ifPresent(geopriv -> result.addAll(parseCivicAddresses(geopriv)));
        return result;
    }

    private List<CivicAddress> parseCivicAddresses(Geopriv geopriv) {
        List<CivicAddress> result = new LinkedList<>();

        LocInfoType locationInfo = geopriv.getLocationInfo();
        if (locationInfo != null) {
            result.addAll(parseCivicAddresses(locationInfo.getAny()));
        }

        return result;
    }

    private List<CivicAddress> parseCivicAddresses(List<Object> locations) {
        List<CivicAddress> result = new LinkedList<>();
        locations.forEach(location -> {
            Optional<at.gridgears.schemas.held.CivicAddress> civicAddressOptional = ParseUtils.getValue(location, at.gridgears.schemas.held.CivicAddress.class);
            if (civicAddressOptional.isPresent()) {
                civicAddressOptional.ifPresent(civic -> result.add(getCivicAddress(civic)));
            }
        });
        return result;
    }

    private CivicAddress getCivicAddress(at.gridgears.schemas.held.CivicAddress civic) {
        CivicAddress.CivicAddressBuilder builder = CivicAddress.CivicAddressBuilder.builder();

        builder.withCountry(civic.getCountry());
        getStringValue(civic.getA1()).ifPresent(builder::withA1);
        getStringValue(civic.getA2()).ifPresent(builder::withA2);
        getStringValue(civic.getA3()).ifPresent(builder::withA3);
        getStringValue(civic.getA4()).ifPresent(builder::withA4);
        getStringValue(civic.getA5()).ifPresent(builder::withA5);
        getStringValue(civic.getA6()).ifPresent(builder::withA6);
        getStringValue(civic.getPRM()).ifPresent(builder::withPrm);
        getStringValue(civic.getPRD()).ifPresent(builder::withPrd);
        getStringValue(civic.getRD()).ifPresent(builder::withRd);
        getStringValue(civic.getSTS()).ifPresent(builder::withSts);
        getStringValue(civic.getPOD()).ifPresent(builder::withPod);
        getStringValue(civic.getPOM()).ifPresent(builder::withPom);
        getStringValue(civic.getRDSEC()).ifPresent(builder::withRdsec);
        getStringValue(civic.getRDBR()).ifPresent(builder::withRdbr);
        getStringValue(civic.getRDSUBBR()).ifPresent(builder::withRdsubbr);
        getStringValue(civic.getHNO()).ifPresent(builder::withHno);
        getStringValue(civic.getHNS()).ifPresent(builder::withHns);
        getStringValue(civic.getLMK()).ifPresent(builder::withLmk);
        getStringValue(civic.getLOC()).ifPresent(builder::withLoc);
        getStringValue(civic.getFLR()).ifPresent(builder::withFlr);
        getStringValue(civic.getNAM()).ifPresent(builder::withNam);
        getStringValue(civic.getPC()).ifPresent(builder::withPc);
        getStringValue(civic.getBLD()).ifPresent(builder::withBld);
        getStringValue(civic.getUNIT()).ifPresent(builder::withUnit);
        getStringValue(civic.getROOM()).ifPresent(builder::withRoom);
        getStringValue(civic.getSEAT()).ifPresent(builder::withSeat);
        builder.withPlc(civic.getPLC());
        getStringValue(civic.getPCN()).ifPresent(builder::withPcn);
        getStringValue(civic.getPOBOX()).ifPresent(builder::withPobox);
        getStringValue(civic.getADDCODE()).ifPresent(builder::withAddcode);

        return builder.build();
    }

    private static Geopriv getGeopriv(Object object) {
        return ParseUtils.getValue(object, Geopriv.class).get();
    }
}

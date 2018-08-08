package at.gridgears.held;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Objects;

public class CivicAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    private String country;
    private String a1;
    private String a2;
    private String a3;
    private String a4;
    private String a5;
    private String a6;
    private String prm;
    private String prd;
    private String rd;
    private String sts;
    private String pod;
    private String pom;
    private String rdsec;
    private String rdbr;
    private String rdsubbr;
    private String hno;
    private String hns;
    private String lmk;
    private String loc;
    private String flr;
    private String nam;
    private String pc;
    private String bld;
    private String unit;
    private String room;
    private String seat;
    private String plc;
    private String pcn;
    private String pobox;
    private String addcode;


    CivicAddress() {
        //nothing to do
    }

    public String getCountry() {
        return country;
    }

    public String getA1() {
        return a1;
    }

    public String getA2() {
        return a2;
    }

    public String getA3() {
        return a3;
    }

    public String getA4() {
        return a4;
    }

    public String getA5() {
        return a5;
    }

    public String getA6() {
        return a6;
    }

    public String getPrm() {
        return prm;
    }

    public String getPrd() {
        return prd;
    }

    public String getRd() {
        return rd;
    }

    public String getSts() {
        return sts;
    }

    public String getPod() {
        return pod;
    }

    public String getPom() {
        return pom;
    }

    public String getRdsec() {
        return rdsec;
    }

    public String getRdbr() {
        return rdbr;
    }

    public String getRdsubbr() {
        return rdsubbr;
    }

    public String getHno() {
        return hno;
    }

    public String getHns() {
        return hns;
    }

    public String getLmk() {
        return lmk;
    }

    public String getLoc() {
        return loc;
    }

    public String getFlr() {
        return flr;
    }

    public String getNam() {
        return nam;
    }

    public String getPc() {
        return pc;
    }

    public String getBld() {
        return bld;
    }

    public String getUnit() {
        return unit;
    }

    public String getRoom() {
        return room;
    }

    public String getSeat() {
        return seat;
    }

    public String getPlc() {
        return plc;
    }

    public String getPcn() {
        return pcn;
    }

    public String getPobox() {
        return pobox;
    }

    public String getAddcode() {
        return addcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CivicAddress that = (CivicAddress) o;
        return Objects.equals(country, that.country) &&
                Objects.equals(a1, that.a1) &&
                Objects.equals(a2, that.a2) &&
                Objects.equals(a3, that.a3) &&
                Objects.equals(a4, that.a4) &&
                Objects.equals(a5, that.a5) &&
                Objects.equals(a6, that.a6) &&
                Objects.equals(prm, that.prm) &&
                Objects.equals(prd, that.prd) &&
                Objects.equals(rd, that.rd) &&
                Objects.equals(sts, that.sts) &&
                Objects.equals(pod, that.pod) &&
                Objects.equals(pom, that.pom) &&
                Objects.equals(rdsec, that.rdsec) &&
                Objects.equals(rdbr, that.rdbr) &&
                Objects.equals(rdsubbr, that.rdsubbr) &&
                Objects.equals(hno, that.hno) &&
                Objects.equals(hns, that.hns) &&
                Objects.equals(lmk, that.lmk) &&
                Objects.equals(loc, that.loc) &&
                Objects.equals(flr, that.flr) &&
                Objects.equals(nam, that.nam) &&
                Objects.equals(pc, that.pc) &&
                Objects.equals(bld, that.bld) &&
                Objects.equals(unit, that.unit) &&
                Objects.equals(room, that.room) &&
                Objects.equals(seat, that.seat) &&
                Objects.equals(plc, that.plc) &&
                Objects.equals(pcn, that.pcn) &&
                Objects.equals(pobox, that.pobox) &&
                Objects.equals(addcode, that.addcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, a1, a2, a3, a4, a5, a6, prm, prd, rd, sts, pod, pom, rdsec, rdbr, rdsubbr, hno, hns, lmk, loc, flr, nam, pc, bld, unit, room, seat, plc, pcn, pobox, addcode);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("country", country)
                .append("a1", a1)
                .append("a2", a2)
                .append("a3", a3)
                .append("a4", a4)
                .append("a5", a5)
                .append("a6", a6)
                .append("prm", prm)
                .append("prd", prd)
                .append("rd", rd)
                .append("sts", sts)
                .append("pod", pod)
                .append("pom", pom)
                .append("rdsec", rdsec)
                .append("rdbr", rdbr)
                .append("rdsubbr", rdsubbr)
                .append("hno", hno)
                .append("hns", hns)
                .append("lmk", lmk)
                .append("loc", loc)
                .append("flr", flr)
                .append("nam", nam)
                .append("pc", pc)
                .append("bld", bld)
                .append("unit", unit)
                .append("room", room)
                .append("seat", seat)
                .append("plc", plc)
                .append("pcn", pcn)
                .append("pobox", pobox)
                .append("addcode", addcode)
                .toString();
    }

    public static final class CivicAddressBuilder {
        private String country;
        private String a1;
        private String a2;
        private String a3;
        private String a4;
        private String a5;
        private String a6;
        private String prm;
        private String prd;
        private String rd;
        private String sts;
        private String pod;
        private String pom;
        private String rdsec;
        private String rdbr;
        private String rdsubbr;
        private String hno;
        private String hns;
        private String lmk;
        private String loc;
        private String flr;
        private String nam;
        private String pc;
        private String bld;
        private String unit;
        private String room;
        private String seat;
        private String plc;
        private String pcn;
        private String pobox;
        private String addcode;

        private CivicAddressBuilder() {
        }

        public static CivicAddressBuilder builder() {
            return new CivicAddressBuilder();
        }

        public CivicAddressBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        public CivicAddressBuilder withA1(String a1) {
            this.a1 = a1;
            return this;
        }

        public CivicAddressBuilder withA2(String a2) {
            this.a2 = a2;
            return this;
        }

        public CivicAddressBuilder withA3(String a3) {
            this.a3 = a3;
            return this;
        }

        public CivicAddressBuilder withA4(String a4) {
            this.a4 = a4;
            return this;
        }

        public CivicAddressBuilder withA5(String a5) {
            this.a5 = a5;
            return this;
        }

        public CivicAddressBuilder withA6(String a6) {
            this.a6 = a6;
            return this;
        }

        public CivicAddressBuilder withPrm(String prm) {
            this.prm = prm;
            return this;
        }

        public CivicAddressBuilder withPrd(String prd) {
            this.prd = prd;
            return this;
        }

        public CivicAddressBuilder withRd(String rd) {
            this.rd = rd;
            return this;
        }

        public CivicAddressBuilder withSts(String sts) {
            this.sts = sts;
            return this;
        }

        public CivicAddressBuilder withPod(String pod) {
            this.pod = pod;
            return this;
        }

        public CivicAddressBuilder withPom(String pom) {
            this.pom = pom;
            return this;
        }

        public CivicAddressBuilder withRdsec(String rdsec) {
            this.rdsec = rdsec;
            return this;
        }

        public CivicAddressBuilder withRdbr(String rdbr) {
            this.rdbr = rdbr;
            return this;
        }

        public CivicAddressBuilder withRdsubbr(String rdsubbr) {
            this.rdsubbr = rdsubbr;
            return this;
        }

        public CivicAddressBuilder withHno(String hno) {
            this.hno = hno;
            return this;
        }

        public CivicAddressBuilder withHns(String hns) {
            this.hns = hns;
            return this;
        }

        public CivicAddressBuilder withLmk(String lmk) {
            this.lmk = lmk;
            return this;
        }

        public CivicAddressBuilder withLoc(String loc) {
            this.loc = loc;
            return this;
        }

        public CivicAddressBuilder withFlr(String flr) {
            this.flr = flr;
            return this;
        }

        public CivicAddressBuilder withNam(String nam) {
            this.nam = nam;
            return this;
        }

        public CivicAddressBuilder withPc(String pc) {
            this.pc = pc;
            return this;
        }

        public CivicAddressBuilder withBld(String bld) {
            this.bld = bld;
            return this;
        }

        public CivicAddressBuilder withUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public CivicAddressBuilder withRoom(String room) {
            this.room = room;
            return this;
        }

        public CivicAddressBuilder withSeat(String seat) {
            this.seat = seat;
            return this;
        }

        public CivicAddressBuilder withPlc(String plc) {
            this.plc = plc;
            return this;
        }

        public CivicAddressBuilder withPcn(String pcn) {
            this.pcn = pcn;
            return this;
        }

        public CivicAddressBuilder withPobox(String pobox) {
            this.pobox = pobox;
            return this;
        }

        public CivicAddressBuilder withAddcode(String addcode) {
            this.addcode = addcode;
            return this;
        }

        public CivicAddress build() {
            CivicAddress civicAddress = new CivicAddress();
            civicAddress.country = country;
            civicAddress.a1 = a1;
            civicAddress.a2 = a2;
            civicAddress.a3 = a3;
            civicAddress.a4 = a4;
            civicAddress.a5 = a5;
            civicAddress.a6 = a6;
            civicAddress.prm = prm;
            civicAddress.prd = prd;
            civicAddress.rd = rd;
            civicAddress.sts = sts;
            civicAddress.pod = pod;
            civicAddress.pom = pom;
            civicAddress.rdsec = rdsec;
            civicAddress.rdbr = rdbr;
            civicAddress.rdsubbr = rdsubbr;
            civicAddress.hno = hno;
            civicAddress.hns = hns;
            civicAddress.lmk = lmk;
            civicAddress.loc = loc;
            civicAddress.flr = flr;
            civicAddress.nam = nam;
            civicAddress.pc = pc;
            civicAddress.bld = bld;
            civicAddress.unit = unit;
            civicAddress.room = room;
            civicAddress.seat = seat;
            civicAddress.plc = plc;
            civicAddress.pcn = pcn;
            civicAddress.pobox = pobox;
            civicAddress.addcode = addcode;
            return civicAddress;
        }
    }
}

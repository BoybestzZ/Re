/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class ColumnSectionInfo implements SectionInfo {

    private final String name;
    private final StrainGaugeInfo no;
    private final StrainGaugeInfo ea;
    private final StrainGaugeInfo we;
    private final StrainGaugeInfo so;
    private final double A;
    private final double Zns;
    private final double Zew;
    private final double E;
    private final double height;

    public ColumnSectionInfo(String name, StrainGaugeInfo north, StrainGaugeInfo east, StrainGaugeInfo west, StrainGaugeInfo south, 
            double E, double A, double Zns, double Zew, double h) {
        this.name = name;
        this.no = north;
        this.ea = east;
        this.we = west;
        this.so = south;
        this.E = E;
        this.A = A;
        this.Zns = Zns;
        this.Zew = Zew;
        this.height = h;
    }

    public double getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public double getA() {
        return A;
    }

    public double getZns() {
        return Zns;
    }

    public double getZew() {
        return Zew;
    }

    public double getE() {
        return E;
    }

    public String getEastName() {
        return ea.getShortName();
    }

    public String getNorthName() {
        return no.getShortName();
    }

    public String getSouthName() {
        return so.getShortName();
    }

    public String getWestName() {
        return we.getShortName();
    }

    @Override
    public boolean isColumn() {
        return true;
    }

}

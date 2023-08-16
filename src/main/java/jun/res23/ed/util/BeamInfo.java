/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class BeamInfo implements ElementInfo {

    public static String[] getSection() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private final BeamSectionInfo[] sections;
    private final double[] locations;
    private final String name;

    BeamInfo(String name, double totalLength, double distance1,
            BeamSectionInfo LA3S1, double distance2, BeamSectionInfo LA3S2, double distance3, BeamSectionInfo LA3S3, double distance4,
            BeamSectionInfo LA3S4, double distance5, BeamSectionInfo LA3S5, double distance6) {
        this.sections = new BeamSectionInfo[5];
        this.name = name;
        sections[0] = LA3S1;
        sections[1] = LA3S2;
        sections[2] = LA3S3;
        sections[3] = LA3S4;
        sections[4] = LA3S5;
        locations = new double[sections.length + 1];
        locations[0] = distance1;
        locations[1] = distance2 + locations[0];
        locations[2] = distance3 + locations[1];
        locations[3] = distance4 + locations[2];
        locations[4] = distance5 + locations[3];
        locations[5] = distance6 + locations[4];
        if (Math.abs(totalLength - locations[5]) > 0.01) {
            throw new RuntimeException("Length is different " + totalLength + " : " + locations[5]);
        }

    }

    public double getLength() {
        return locations[5];
    }

    public String getName() {
        return name;
    }

    public double getLocation(int i) {
        return locations[i];
    }

    @Override
    public SectionInfo[] getSections() {
        return sections;
    }

    public BeamSectionInfo[] getBeamSections() {
        return sections;
    }

    @Override
    public boolean isColumn() {
        return false;
    }

}

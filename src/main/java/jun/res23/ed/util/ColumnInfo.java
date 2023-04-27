/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class ColumnInfo implements ElementInfo {

    private final ColumnSectionInfo[] sections;
    private final double[] locations;
    private final String name;

    public ColumnInfo(String name, double totalLength, double distance1,
            ColumnSectionInfo LA3S1, double distance2, ColumnSectionInfo LA3S2, double distance3,
            ColumnSectionInfo LA3S3, double distance4) {
        this.sections = new ColumnSectionInfo[3];
        this.name = name;
        sections[0] = LA3S1;
        sections[1] = LA3S2;
        sections[2] = LA3S3;
        locations = new double[sections.length + 1];
        locations[0] = distance1;
        locations[1] = distance2 + locations[0];
        locations[2] = distance3 + locations[1];
        locations[3] = distance4 + locations[2];
        if (Math.abs(totalLength - locations[3]) > 0.01) {
            throw new RuntimeException("Length is different " + totalLength + " : " + locations[5]);
        }

    }

    public double getLength() {
        return locations[3];
    }

    public String getName() {
        return name;
    }

    public double getLocation(int i) {
        return locations[i];
    }

    public SectionInfo[] getSections() {
        return sections;
    }

    public ColumnSectionInfo[] getColumnSections() {
        return sections;
    }

    @Override
    public boolean isColumn() {
        return true;
    }

}

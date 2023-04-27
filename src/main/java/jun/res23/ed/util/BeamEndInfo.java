/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class BeamEndInfo {

    private String name;
    private GaugeGroup gg; // 1I 2I 3I 4I 1O 2O 3O 4O

    public BeamEndInfo(String name, GaugeGroup gg) {
        this.name = name;
        this.gg = gg;
    }

    public String getName() {
        return this.name;
    }

    public String getGaugeShortName(int i) {
        return gg.getUnitName(i);//x + "/0" + gg.getUnitChannel(i);

    }

    public String getGauge1I() {
        // return gauge[0].getShortName();
        return getGaugeShortName(0);
    }

    public String getGauge2I() {
        return getGaugeShortName(1);
//        return gauge[1].getShortName();
    }

    public String getGauge3I() {
        return getGaugeShortName(2);

    }

    public String getGauge4I() {
        return getGaugeShortName(3);

    }

    public String getGauge1O() {
        return getGaugeShortName(4);

    }

    public String getGauge2O() {
        return getGaugeShortName(5);

    }

    public String getGauge3O() {
        return getGaugeShortName(6);

    }

    public String getGauge4O() {
        return getGaugeShortName(7);

    }

}

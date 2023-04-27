/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.util;

import java.util.ArrayList;
import jun.raspi.alive.StrainGaugeInfo;
import jun.raspi.alive.UnitInfo;

/**
 *
 * @author jun
 */
public class GaugeGroup {

    private String name;

    ArrayList<StrainGaugeInfo> list;

    GaugeGroup(String name, UnitInfo unit, int startch, int endch) {
        this.name = name;
        list = new ArrayList();
        for (int i = startch; i <= endch; i++) {
            list.add(new StrainGaugeInfo(unit, i));
        }
    }

    GaugeGroup(String name, UnitInfo unit1, int startch1, int endch1, UnitInfo unit2, int startch2, int endch2) {
        this.name = name;
        list = new ArrayList();
        for (int i = startch1; i <= endch1; i++) {
            list.add(new StrainGaugeInfo(unit1, i));
        }
        for (int i = startch2; i <= endch2; i++) {
            list.add(new StrainGaugeInfo(unit2, i));
        }
    }

    GaugeGroup(String name, UnitInfo unit, int startch, int endch,
            UnitInfo unit2, int startch2, int endch2,
            UnitInfo unit3, int startch3, int endch3) {
        this.name = name;
        list = new ArrayList();
        for (int i = startch; i <= endch; i++) {
            list.add(new StrainGaugeInfo(unit, i));
        }
        for (int i = startch2; i <= endch2; i++) {
            list.add(new StrainGaugeInfo(unit2, i));
        }
        for (int i = startch3; i <= endch3; i++) {
            list.add(new StrainGaugeInfo(unit3, i));
        }
    }

    GaugeGroup(String name, UnitInfo unit, int startch, int endch,
            UnitInfo unit2, int startch2, int endch2,
            UnitInfo unit3, int startch3, int endch3,
            UnitInfo unit4, int startch4, int endch4) {
        this.name = name;
        list = new ArrayList();
        for (int i = startch; i <= endch; i++) {
            list.add(new StrainGaugeInfo(unit, i));
        }

        for (int i = startch2; i <= endch2; i++) {
            list.add(new StrainGaugeInfo(unit2, i));
        }
        for (int i = startch3; i <= endch3; i++) {
            list.add(new StrainGaugeInfo(unit3, i));
        }
        for (int i = startch4; i <= endch4; i++) {
            list.add(new StrainGaugeInfo(unit4, i));
        }
    }
public
    int getNumgerOfChannels() {
        return list.size();
    }
public
    String getMacAddress(int ch) {
        return list.get(ch).getUnitInfo().getHardwareAddress();
    }
public
    int getUnitChannel(int ch) {
        return list.get(ch).getChNo();
    }
public
    String getName() {
        return name;
    }
public
    String getUnitName(int ch) {
        return list.get(ch).getName();

    }

}

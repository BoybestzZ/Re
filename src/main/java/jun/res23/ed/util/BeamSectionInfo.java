/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class BeamSectionInfo implements SectionInfo {

    private final String name;
    private final StrainGaugeInfo ul;
    private final StrainGaugeInfo ur;
    private final StrainGaugeInfo ll;
    private final StrainGaugeInfo lr;
    private final double A;
    private final double ZxInner;
    private final double Zy;
    private final double E;
    private final double height;
    private final double ZxOuter;

    public BeamSectionInfo(String name, StrainGaugeInfo U_L, StrainGaugeInfo U_R, StrainGaugeInfo L_L, StrainGaugeInfo L_R, double E, double A, double ZxInner, double ZxOuter, double Zy, double h) {
        this.name = name;
        this.ul = U_L;
        this.ur = U_R;
        this.ll = L_L;
        this.lr = L_R;
        this.E = E;
        this.A = A;
        this.ZxInner = ZxInner;
        this.ZxOuter=ZxOuter;
        this.Zy = Zy;
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

    public double getInnerZx() {
        return ZxInner;
    }

    public double getOuterZx() {
        return ZxOuter;
    }

    public double getZy() {
        return Zy;
    }

    public double getE() {
        return E;
    }

    public String getURname() {
        return ur.getShortName();
    }

    public String getULname() {
        return ul.getShortName();
    }

    public String getLRname() {
        return lr.getShortName();
    }

    public String getLLname() {
        return ll.getShortName();
    }

    @Override
    public boolean isColumn() {
        return false;
    }

}

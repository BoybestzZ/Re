/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.alive.UnitInfo;

/**
 * str01前提
 *
 * @author jun
 */
public class StrainGaugeInfo {

    private static final Logger logger = Logger.getLogger(StrainGaugeInfo.class.getName());
    private final UnitInfo ui;
    private final int chno; // 1 〜 4
    private final double gaugeFactor;
    private final double gain;
    private final String location;

    public StrainGaugeInfo(UnitInfo ui, int chno) {
        this(ui, chno, 0, 0);
    }

    public StrainGaugeInfo(UnitInfo ui, int chno, double gaugeFactor, double gain) {
        this(null, ui, chno, gaugeFactor, gain);
    }

    public StrainGaugeInfo(String location, UnitInfo ui, int chno, double gaugeFactor, double gain) {
        this.location = location;
        this.ui = ui;
        this.chno = chno;
        this.gain = gain;
        this.gaugeFactor = gaugeFactor;
    }

    public double getGain() {
        return gain;
    }

    public double getGaugeFactor() {
        return gaugeFactor;
    }

    public String getTopic() {
        return ui.getHardwareAddress() + "/0" + chno + "/str01";
    }

    public String getShortName() {
        return ui.getName() + "/0" + chno;
    }

    public String getLocation() {
        if (location == null) {
            return getShortName();
        }
        return location;
    }

    public static final StrainGaugeInfo GSLA_A_U_L_1 = new StrainGaugeInfo(EdefenseInfo.a01, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_L_2 = new StrainGaugeInfo(EdefenseInfo.a01, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_L_3 = new StrainGaugeInfo(EdefenseInfo.a01, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_R_1 = new StrainGaugeInfo(EdefenseInfo.a01, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_R_2 = new StrainGaugeInfo(EdefenseInfo.a01, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_R_3 = new StrainGaugeInfo(EdefenseInfo.a01, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_L_1 = new StrainGaugeInfo(EdefenseInfo.a01, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_L_2 = new StrainGaugeInfo(EdefenseInfo.a01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_L_3 = new StrainGaugeInfo(EdefenseInfo.a02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_R_1 = new StrainGaugeInfo(EdefenseInfo.a02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_R_2 = new StrainGaugeInfo(EdefenseInfo.a02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_R_3 = new StrainGaugeInfo(EdefenseInfo.a02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_L_1 = new StrainGaugeInfo(EdefenseInfo.a02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_L_2 = new StrainGaugeInfo(EdefenseInfo.a02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_R_1 = new StrainGaugeInfo(EdefenseInfo.a02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_R_2 = new StrainGaugeInfo(EdefenseInfo.a02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_L_1 = new StrainGaugeInfo(EdefenseInfo.a03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_L_2 = new StrainGaugeInfo(EdefenseInfo.a03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_R_1 = new StrainGaugeInfo(EdefenseInfo.a03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_R_2 = new StrainGaugeInfo(EdefenseInfo.a03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_B_N = new StrainGaugeInfo("2F_A3_B_N", EdefenseInfo.a03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_B_E = new StrainGaugeInfo("2F_A3_B_E", EdefenseInfo.a03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_B_W = new StrainGaugeInfo("2F_A3_B_W", EdefenseInfo.a03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_B_S = new StrainGaugeInfo("2F_A3_B_S", EdefenseInfo.a03, 8, 2.08, 128);

    public static final StrainGaugeInfo CS2F_A3_C_N = new StrainGaugeInfo("2F_A3_C_N", EdefenseInfo.b01, 1, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_C_E = new StrainGaugeInfo("2F_A3_C_E", EdefenseInfo.b01, 2, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_C_W = new StrainGaugeInfo("2F_A3_C_W", EdefenseInfo.b01, 3, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_C_S = new StrainGaugeInfo("2F_A3_C_S", EdefenseInfo.b01, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_T_N = new StrainGaugeInfo("2F_A3_T_N", EdefenseInfo.b01, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_T_E = new StrainGaugeInfo("2F_A3_T_E", EdefenseInfo.b01, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_T_W = new StrainGaugeInfo("2F_A3_T_W", EdefenseInfo.b01, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A3_T_S = new StrainGaugeInfo("2F_A3_T_S", EdefenseInfo.b01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_I_1 = new StrainGaugeInfo(EdefenseInfo.b02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_I_2 = new StrainGaugeInfo(EdefenseInfo.b02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_I_3 = new StrainGaugeInfo(EdefenseInfo.b02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_I_4 = new StrainGaugeInfo(EdefenseInfo.b02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_O_1 = new StrainGaugeInfo(EdefenseInfo.b02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_O_2 = new StrainGaugeInfo(EdefenseInfo.b02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_O_3 = new StrainGaugeInfo(EdefenseInfo.b02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A3_O_4 = new StrainGaugeInfo(EdefenseInfo.b02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_A3_I_1 = new StrainGaugeInfo(EdefenseInfo.b03, 1, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_I_2 = new StrainGaugeInfo(EdefenseInfo.b03, 2, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_I_3 = new StrainGaugeInfo(EdefenseInfo.b03, 3, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_I_4 = new StrainGaugeInfo(EdefenseInfo.b03, 4, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_O_1 = new StrainGaugeInfo(EdefenseInfo.b03, 5, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_O_2 = new StrainGaugeInfo(EdefenseInfo.b03, 6, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_O_3 = new StrainGaugeInfo(EdefenseInfo.b03, 7, 2.11, 128);
    public static final StrainGaugeInfo GSLO_3_A3_O_4 = new StrainGaugeInfo(EdefenseInfo.b03, 8, 2.11, 128);

    public static final StrainGaugeInfo GSLA_A_U_L_4 = new StrainGaugeInfo(EdefenseInfo.c01, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_L_5 = new StrainGaugeInfo(EdefenseInfo.c01, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_R_4 = new StrainGaugeInfo(EdefenseInfo.h04, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_U_R_5 = new StrainGaugeInfo(EdefenseInfo.c01, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_L_4 = new StrainGaugeInfo(EdefenseInfo.c01, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_L_5 = new StrainGaugeInfo(EdefenseInfo.c01, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_R_4 = new StrainGaugeInfo(EdefenseInfo.c01, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_A_L_R_5 = new StrainGaugeInfo(EdefenseInfo.c01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_L_1 = new StrainGaugeInfo(EdefenseInfo.c02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_L_2 = new StrainGaugeInfo(EdefenseInfo.c02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_L_3 = new StrainGaugeInfo(EdefenseInfo.c02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_R_1 = new StrainGaugeInfo(EdefenseInfo.c02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_R_2 = new StrainGaugeInfo(EdefenseInfo.c02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_R_3 = new StrainGaugeInfo(EdefenseInfo.c02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_L_1 = new StrainGaugeInfo(EdefenseInfo.c02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_L_2 = new StrainGaugeInfo(EdefenseInfo.c02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_L_3 = new StrainGaugeInfo(EdefenseInfo.c03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_R_1 = new StrainGaugeInfo(EdefenseInfo.c03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_R_2 = new StrainGaugeInfo(EdefenseInfo.c03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_R_3 = new StrainGaugeInfo(EdefenseInfo.c03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_B_N = new StrainGaugeInfo("2F_A4_B_N", EdefenseInfo.c03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_B_E = new StrainGaugeInfo("2F_A4_B_E", EdefenseInfo.c03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_B_W = new StrainGaugeInfo("2F_A4_B_W", EdefenseInfo.c03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_B_S = new StrainGaugeInfo("2F_A4_B_S", EdefenseInfo.c03, 8, 2.08, 128);

    public static final StrainGaugeInfo CS2F_A4_C_N = new StrainGaugeInfo("2F_A4_C_N", EdefenseInfo.d01, 1, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_C_E = new StrainGaugeInfo("2F_A4_C_E", EdefenseInfo.d01, 2, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_C_W = new StrainGaugeInfo("2F_A4_C_W", EdefenseInfo.d01, 3, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_C_S = new StrainGaugeInfo("2F_A4_C_S", EdefenseInfo.d01, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_T_N = new StrainGaugeInfo("2F_A4_T_N", EdefenseInfo.d01, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_T_E = new StrainGaugeInfo("2F_A4_T_E", EdefenseInfo.d01, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_A4_T_S = new StrainGaugeInfo("2F_A4_T_S", EdefenseInfo.d01, 7, 2.08, 128); // 2023/06/12 接続ミスを発見。修正。 (N-E-S-W の順にしてある）
    public static final StrainGaugeInfo CS2F_A4_T_W = new StrainGaugeInfo("2F_A4_T_W", EdefenseInfo.d01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLO_A_A4_I_1 = new StrainGaugeInfo(EdefenseInfo.d02, 1, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_I_2 = new StrainGaugeInfo(EdefenseInfo.d02, 2, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_I_3 = new StrainGaugeInfo(EdefenseInfo.d02, 3, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_I_4 = new StrainGaugeInfo(EdefenseInfo.d02, 4, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_O_1 = new StrainGaugeInfo(EdefenseInfo.d02, 5, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_O_2 = new StrainGaugeInfo(EdefenseInfo.d02, 6, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_O_3 = new StrainGaugeInfo(EdefenseInfo.d02, 7, 2.11, 128);
    public static final StrainGaugeInfo GSLO_A_A4_O_4 = new StrainGaugeInfo(EdefenseInfo.d02, 8, 2.11, 128);
    public static final StrainGaugeInfo GSLO_4_A4_I_1 = new StrainGaugeInfo(EdefenseInfo.d03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_I_2 = new StrainGaugeInfo(EdefenseInfo.d03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_I_3 = new StrainGaugeInfo(EdefenseInfo.d03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_I_4 = new StrainGaugeInfo(EdefenseInfo.d03, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_O_1 = new StrainGaugeInfo(EdefenseInfo.d03, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_O_2 = new StrainGaugeInfo(EdefenseInfo.d03, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_O_3 = new StrainGaugeInfo(EdefenseInfo.d03, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_A4_O_4 = new StrainGaugeInfo(EdefenseInfo.d03, 8, 2.08, 128);

    public static final StrainGaugeInfo GSLA_B_U_L_1 = new StrainGaugeInfo(EdefenseInfo.e01, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_L_2 = new StrainGaugeInfo(EdefenseInfo.e01, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_R_1 = new StrainGaugeInfo(EdefenseInfo.e01, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_R_2 = new StrainGaugeInfo(EdefenseInfo.e01, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_L_1 = new StrainGaugeInfo(EdefenseInfo.e01, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_L_2 = new StrainGaugeInfo(EdefenseInfo.e01, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_R_1 = new StrainGaugeInfo(EdefenseInfo.e01, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_R_2 = new StrainGaugeInfo(EdefenseInfo.e01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_L_3 = new StrainGaugeInfo(EdefenseInfo.e02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_L_4 = new StrainGaugeInfo(EdefenseInfo.e02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_L_5 = new StrainGaugeInfo(EdefenseInfo.e02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_R_3 = new StrainGaugeInfo(EdefenseInfo.e02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_R_4 = new StrainGaugeInfo(EdefenseInfo.e02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_U_R_5 = new StrainGaugeInfo(EdefenseInfo.e02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_L_3 = new StrainGaugeInfo(EdefenseInfo.e02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_L_4 = new StrainGaugeInfo(EdefenseInfo.e02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_L_5 = new StrainGaugeInfo(EdefenseInfo.e03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_R_3 = new StrainGaugeInfo(EdefenseInfo.e03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_R_4 = new StrainGaugeInfo(EdefenseInfo.e03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_3_L_R_5 = new StrainGaugeInfo(EdefenseInfo.e03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_B_N = new StrainGaugeInfo(EdefenseInfo.e03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_B_E = new StrainGaugeInfo(EdefenseInfo.e03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_B_W = new StrainGaugeInfo(EdefenseInfo.e03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_B_S = new StrainGaugeInfo(EdefenseInfo.e03, 8, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_C_N = new StrainGaugeInfo(EdefenseInfo.e04, 1, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_C_E = new StrainGaugeInfo(EdefenseInfo.e04, 2, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_C_W = new StrainGaugeInfo(EdefenseInfo.e04, 3, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_C_S = new StrainGaugeInfo(EdefenseInfo.e04, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_T_N = new StrainGaugeInfo(EdefenseInfo.e04, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_T_E = new StrainGaugeInfo(EdefenseInfo.e04, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_T_W = new StrainGaugeInfo(EdefenseInfo.e04, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B3_T_S = new StrainGaugeInfo(EdefenseInfo.e04, 8, 2.08, 128);

    public static final StrainGaugeInfo GSLO_B_B3_I_1 = new StrainGaugeInfo(EdefenseInfo.f01, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_I_2 = new StrainGaugeInfo(EdefenseInfo.f01, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_I_3 = new StrainGaugeInfo(EdefenseInfo.f01, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_I_4 = new StrainGaugeInfo(EdefenseInfo.f01, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_O_1 = new StrainGaugeInfo(EdefenseInfo.f01, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_O_2 = new StrainGaugeInfo(EdefenseInfo.f01, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_O_3 = new StrainGaugeInfo(EdefenseInfo.f01, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B3_O_4 = new StrainGaugeInfo(EdefenseInfo.f01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_I_1 = new StrainGaugeInfo(EdefenseInfo.f02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_I_2 = new StrainGaugeInfo(EdefenseInfo.f02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_I_3 = new StrainGaugeInfo(EdefenseInfo.f02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_I_4 = new StrainGaugeInfo(EdefenseInfo.f02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_O_1 = new StrainGaugeInfo(EdefenseInfo.f02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_O_2 = new StrainGaugeInfo(EdefenseInfo.f02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_O_3 = new StrainGaugeInfo(EdefenseInfo.f02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLO_3_B3_O_4 = new StrainGaugeInfo(EdefenseInfo.f02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSG3_U_L_1 = new StrainGaugeInfo(EdefenseInfo.f03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSG3_U_R_1 = new StrainGaugeInfo(EdefenseInfo.f03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSG3_L_R_1 = new StrainGaugeInfo(EdefenseInfo.f03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSG3_L_L_1 = new StrainGaugeInfo(EdefenseInfo.f03, 4, 2.08, 128);

    public static final StrainGaugeInfo GSLA_B_U_L_3 = new StrainGaugeInfo(EdefenseInfo.g01, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_L_4 = new StrainGaugeInfo(EdefenseInfo.g01, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_L_5 = new StrainGaugeInfo(EdefenseInfo.g01, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_R_3 = new StrainGaugeInfo(EdefenseInfo.g01, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_R_4 = new StrainGaugeInfo(EdefenseInfo.g01, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_U_R_5 = new StrainGaugeInfo(EdefenseInfo.g01, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_L_3 = new StrainGaugeInfo(EdefenseInfo.g01, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_L_4 = new StrainGaugeInfo(EdefenseInfo.g01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_L_5 = new StrainGaugeInfo(EdefenseInfo.g02, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_R_3 = new StrainGaugeInfo(EdefenseInfo.g02, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_R_4 = new StrainGaugeInfo(EdefenseInfo.g02, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_B_L_R_5 = new StrainGaugeInfo(EdefenseInfo.g02, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_L_4 = new StrainGaugeInfo(EdefenseInfo.g02, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_L_5 = new StrainGaugeInfo(EdefenseInfo.g02, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_R_4 = new StrainGaugeInfo(EdefenseInfo.g02, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_U_R_5 = new StrainGaugeInfo(EdefenseInfo.g02, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_L_4 = new StrainGaugeInfo(EdefenseInfo.g03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_L_5 = new StrainGaugeInfo(EdefenseInfo.g03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_R_4 = new StrainGaugeInfo(EdefenseInfo.g03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLA_4_L_R_5 = new StrainGaugeInfo(EdefenseInfo.g03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_B_N = new StrainGaugeInfo(EdefenseInfo.g03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_B_E = new StrainGaugeInfo(EdefenseInfo.g03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_B_W = new StrainGaugeInfo(EdefenseInfo.g03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_B_S = new StrainGaugeInfo(EdefenseInfo.g03, 8, 2.08, 128);

    public static final StrainGaugeInfo CS2F_B4_C_N = new StrainGaugeInfo(EdefenseInfo.h01, 1, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_C_E = new StrainGaugeInfo(EdefenseInfo.h01, 2, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_C_W = new StrainGaugeInfo(EdefenseInfo.h01, 3, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_C_S = new StrainGaugeInfo(EdefenseInfo.h01, 4, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_T_N = new StrainGaugeInfo(EdefenseInfo.h01, 5, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_T_E = new StrainGaugeInfo(EdefenseInfo.h01, 6, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_T_W = new StrainGaugeInfo(EdefenseInfo.h01, 7, 2.08, 128);
    public static final StrainGaugeInfo CS2F_B4_T_S = new StrainGaugeInfo(EdefenseInfo.h01, 8, 2.08, 128);
    public static final StrainGaugeInfo GSLO_B_B4_I_1 = new StrainGaugeInfo(EdefenseInfo.h02, 1, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_I_2 = new StrainGaugeInfo(EdefenseInfo.h02, 2, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_I_3 = new StrainGaugeInfo(EdefenseInfo.h02, 3, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_I_4 = new StrainGaugeInfo(EdefenseInfo.h02, 4, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_O_1 = new StrainGaugeInfo(EdefenseInfo.h02, 5, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_O_2 = new StrainGaugeInfo(EdefenseInfo.h02, 6, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_O_3 = new StrainGaugeInfo(EdefenseInfo.h02, 7, 2.11, 128);
    public static final StrainGaugeInfo GSLO_B_B4_O_4 = new StrainGaugeInfo(EdefenseInfo.h02, 8, 2.11, 128);
    public static final StrainGaugeInfo GSLO_4_B4_I_1 = new StrainGaugeInfo(EdefenseInfo.h03, 1, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_I_2 = new StrainGaugeInfo(EdefenseInfo.h03, 2, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_I_3 = new StrainGaugeInfo(EdefenseInfo.h03, 3, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_I_4 = new StrainGaugeInfo(EdefenseInfo.h03, 4, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_O_1 = new StrainGaugeInfo(EdefenseInfo.h03, 5, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_O_2 = new StrainGaugeInfo(EdefenseInfo.h03, 6, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_O_3 = new StrainGaugeInfo(EdefenseInfo.h03, 7, 2.08, 128);
    public static final StrainGaugeInfo GSLO_4_B4_O_4 = new StrainGaugeInfo(EdefenseInfo.h03, 8, 2.08, 128);

    public static final StrainGaugeInfo CS3F_A3_B_N = new StrainGaugeInfo("3F_A3_B_N", EdefenseInfo.i01, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_B_E = new StrainGaugeInfo("3F_A3_B_E", EdefenseInfo.i01, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_B_W = new StrainGaugeInfo("3F_A3_B_W", EdefenseInfo.i01, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_B_S = new StrainGaugeInfo("3F_A3_B_S", EdefenseInfo.i01, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_C_N = new StrainGaugeInfo("3F_A3_C_N", EdefenseInfo.i01, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_C_E = new StrainGaugeInfo("3F_A3_C_E", EdefenseInfo.i01, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_C_W = new StrainGaugeInfo("3F_A3_C_W", EdefenseInfo.i01, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_C_S = new StrainGaugeInfo("3F_A3_C_S", EdefenseInfo.i01, 8, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_T_N = new StrainGaugeInfo("3F_A3_T_N", EdefenseInfo.i02, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_T_E = new StrainGaugeInfo("3F_A3_T_E", EdefenseInfo.i02, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_T_W = new StrainGaugeInfo("3F_A3_T_W", EdefenseInfo.i02, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A3_T_S = new StrainGaugeInfo("3F_A3_T_S", EdefenseInfo.i02, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_B_N = new StrainGaugeInfo(EdefenseInfo.i02, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_B_E = new StrainGaugeInfo(EdefenseInfo.i02, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_B_W = new StrainGaugeInfo(EdefenseInfo.i02, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_B_S = new StrainGaugeInfo(EdefenseInfo.i02, 8, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_C_N = new StrainGaugeInfo(EdefenseInfo.i03, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_C_E = new StrainGaugeInfo(EdefenseInfo.i03, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_C_W = new StrainGaugeInfo(EdefenseInfo.i03, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_C_S = new StrainGaugeInfo(EdefenseInfo.i03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_T_N = new StrainGaugeInfo(EdefenseInfo.i03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_T_E = new StrainGaugeInfo(EdefenseInfo.i03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_T_W = new StrainGaugeInfo(EdefenseInfo.i03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B3_T_S = new StrainGaugeInfo(EdefenseInfo.i03, 8, 2.08, 128);

    public static final StrainGaugeInfo CS3F_A4_B_N = new StrainGaugeInfo("3F_A4_B_N", EdefenseInfo.j01, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_B_E = new StrainGaugeInfo("3F_A4_B_E", EdefenseInfo.j01, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_B_W = new StrainGaugeInfo("3F_A4_B_W", EdefenseInfo.j01, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_B_S = new StrainGaugeInfo("3F_A4_B_S", EdefenseInfo.j01, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_C_N = new StrainGaugeInfo("3F_A4_C_N", EdefenseInfo.j01, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_C_E = new StrainGaugeInfo("3F_A4_C_E", EdefenseInfo.j01, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_C_W = new StrainGaugeInfo("3F_A4_C_W", EdefenseInfo.j01, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_C_S = new StrainGaugeInfo("3F_A4_C_S", EdefenseInfo.j01, 8, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_T_N = new StrainGaugeInfo("3F_A4_T_N", EdefenseInfo.j02, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_T_E = new StrainGaugeInfo("3F_A4_T_E", EdefenseInfo.j02, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_T_W = new StrainGaugeInfo("3F_A4_T_W", EdefenseInfo.j02, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_A4_T_S = new StrainGaugeInfo("3F_A4_T_S", EdefenseInfo.j02, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_B_N = new StrainGaugeInfo(EdefenseInfo.j02, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_B_E = new StrainGaugeInfo(EdefenseInfo.j02, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_B_W = new StrainGaugeInfo(EdefenseInfo.j02, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_B_S = new StrainGaugeInfo(EdefenseInfo.j02, 8, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_C_N = new StrainGaugeInfo(EdefenseInfo.j03, 1, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_C_E = new StrainGaugeInfo(EdefenseInfo.j03, 2, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_C_W = new StrainGaugeInfo(EdefenseInfo.j03, 3, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_C_S = new StrainGaugeInfo(EdefenseInfo.j03, 4, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_T_N = new StrainGaugeInfo(EdefenseInfo.j03, 5, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_T_E = new StrainGaugeInfo(EdefenseInfo.j03, 6, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_T_W = new StrainGaugeInfo(EdefenseInfo.j03, 7, 2.08, 128);
    public static final StrainGaugeInfo CS3F_B4_T_S = new StrainGaugeInfo(EdefenseInfo.j03, 8, 2.08, 128);

    public static StrainGaugeInfo allstraingauges[] = {
        GSLA_A_U_L_1,
        GSLA_A_U_L_2,
        GSLA_A_U_L_3,
        GSLA_A_U_R_1,
        GSLA_A_U_R_2,
        GSLA_A_U_R_3,
        GSLA_A_L_L_1,
        GSLA_A_L_L_2,
        GSLA_A_L_L_3,
        GSLA_A_L_R_1,
        GSLA_A_L_R_2,
        GSLA_A_L_R_3,
        GSLA_3_U_L_1,
        GSLA_3_U_L_2,
        GSLA_3_U_R_1,
        GSLA_3_U_R_2,
        GSLA_3_L_L_1,
        GSLA_3_L_L_2,
        GSLA_3_L_R_1,
        GSLA_3_L_R_2,
        CS2F_A3_B_N,
        CS2F_A3_B_E,
        CS2F_A3_B_W,
        CS2F_A3_B_S,
        CS2F_A3_C_N,
        CS2F_A3_C_E,
        CS2F_A3_C_W,
        CS2F_A3_C_S,
        CS2F_A3_T_N,
        CS2F_A3_T_E,
        CS2F_A3_T_W,
        CS2F_A3_T_S,
        GSLO_A_A3_I_1,
        GSLO_A_A3_I_2,
        GSLO_A_A3_I_3,
        GSLO_A_A3_I_4,
        GSLO_A_A3_O_1,
        GSLO_A_A3_O_2,
        GSLO_A_A3_O_3,
        GSLO_A_A3_O_4,
        GSLO_3_A3_I_1,
        GSLO_3_A3_I_2,
        GSLO_3_A3_I_3,
        GSLO_3_A3_I_4,
        GSLO_3_A3_O_1,
        GSLO_3_A3_O_2,
        GSLO_3_A3_O_3,
        GSLO_3_A3_O_4,
        GSLA_A_U_L_4,
        GSLA_A_U_L_5,
        GSLA_A_U_R_4,
        GSLA_A_U_R_5,
        GSLA_A_L_L_4,
        GSLA_A_L_L_5,
        GSLA_A_L_R_4,
        GSLA_A_L_R_5,
        GSLA_4_U_L_1,
        GSLA_4_U_L_2,
        GSLA_4_U_L_3,
        GSLA_4_U_R_1,
        GSLA_4_U_R_2,
        GSLA_4_U_R_3,
        GSLA_4_L_L_1,
        GSLA_4_L_L_2,
        GSLA_4_L_L_3,
        GSLA_4_L_R_1,
        GSLA_4_L_R_2,
        GSLA_4_L_R_3,
        CS2F_A4_B_N,
        CS2F_A4_B_E,
        CS2F_A4_B_W,
        CS2F_A4_B_S,
        CS2F_A4_C_N,
        CS2F_A4_C_E,
        CS2F_A4_C_W,
        CS2F_A4_C_S,
        CS2F_A4_T_N,
        CS2F_A4_T_E,
        CS2F_A4_T_W,
        CS2F_A4_T_S,
        GSLO_A_A4_I_1,
        GSLO_A_A4_I_2,
        GSLO_A_A4_I_3,
        GSLO_A_A4_I_4,
        GSLO_A_A4_O_1,
        GSLO_A_A4_O_2,
        GSLO_A_A4_O_3,
        GSLO_A_A4_O_4,
        GSLO_4_A4_I_1,
        GSLO_4_A4_I_2,
        GSLO_4_A4_I_3,
        GSLO_4_A4_I_4,
        GSLO_4_A4_O_1,
        GSLO_4_A4_O_2,
        GSLO_4_A4_O_3,
        GSLO_4_A4_O_4,
        GSLA_B_U_L_1,
        GSLA_B_U_L_2,
        GSLA_B_U_R_1,
        GSLA_B_U_R_2,
        GSLA_B_L_L_1,
        GSLA_B_L_L_2,
        GSLA_B_L_R_1,
        GSLA_B_L_R_2,
        GSLA_3_U_L_3,
        GSLA_3_U_L_4,
        GSLA_3_U_L_5,
        GSLA_3_U_R_3,
        GSLA_3_U_R_4,
        GSLA_3_U_R_5,
        GSLA_3_L_L_3,
        GSLA_3_L_L_4,
        GSLA_3_L_L_5,
        GSLA_3_L_R_3,
        GSLA_3_L_R_4,
        GSLA_3_L_R_5,
        CS2F_B3_B_N,
        CS2F_B3_B_E,
        CS2F_B3_B_W,
        CS2F_B3_B_S,
        CS2F_B3_C_N,
        CS2F_B3_C_E,
        CS2F_B3_C_W,
        CS2F_B3_C_S,
        CS2F_B3_T_N,
        CS2F_B3_T_E,
        CS2F_B3_T_W,
        CS2F_B3_T_S,
        GSLO_B_B3_I_1,
        GSLO_B_B3_I_2,
        GSLO_B_B3_I_3,
        GSLO_B_B3_I_4,
        GSLO_B_B3_O_1,
        GSLO_B_B3_O_2,
        GSLO_B_B3_O_3,
        GSLO_B_B3_O_4,
        GSLO_3_B3_I_1,
        GSLO_3_B3_I_2,
        GSLO_3_B3_I_3,
        GSLO_3_B3_I_4,
        GSLO_3_B3_O_1,
        GSLO_3_B3_O_2,
        GSLO_3_B3_O_3,
        GSLO_3_B3_O_4,
        GSG3_U_L_1,
        GSG3_U_R_1,
        GSG3_L_R_1,
        GSG3_L_L_1,
        GSLA_B_U_L_3,
        GSLA_B_U_L_4,
        GSLA_B_U_L_5,
        GSLA_B_U_R_3,
        GSLA_B_U_R_4,
        GSLA_B_U_R_5,
        GSLA_B_L_L_3,
        GSLA_B_L_L_4,
        GSLA_B_L_L_5,
        GSLA_B_L_R_3,
        GSLA_B_L_R_4,
        GSLA_B_L_R_5,
        GSLA_4_U_L_4,
        GSLA_4_U_L_5,
        GSLA_4_U_R_4,
        GSLA_4_U_R_5,
        GSLA_4_L_L_4,
        GSLA_4_L_L_5,
        GSLA_4_L_R_4,
        GSLA_4_L_R_5,
        CS2F_B4_B_N,
        CS2F_B4_B_E,
        CS2F_B4_B_W,
        CS2F_B4_B_S,
        CS2F_B4_C_N,
        CS2F_B4_C_E,
        CS2F_B4_C_W,
        CS2F_B4_C_S,
        CS2F_B4_T_N,
        CS2F_B4_T_E,
        CS2F_B4_T_W,
        CS2F_B4_T_S,
        GSLO_B_B4_I_1,
        GSLO_B_B4_I_2,
        GSLO_B_B4_I_3,
        GSLO_B_B4_I_4,
        GSLO_B_B4_O_1,
        GSLO_B_B4_O_2,
        GSLO_B_B4_O_3,
        GSLO_B_B4_O_4,
        GSLO_4_B4_I_1,
        GSLO_4_B4_I_2,
        GSLO_4_B4_I_3,
        GSLO_4_B4_I_4,
        GSLO_4_B4_O_1,
        GSLO_4_B4_O_2,
        GSLO_4_B4_O_3,
        GSLO_4_B4_O_4,
        CS3F_A3_B_N,
        CS3F_A3_B_E,
        CS3F_A3_B_W,
        CS3F_A3_B_S,
        CS3F_A3_C_N,
        CS3F_A3_C_E,
        CS3F_A3_C_W,
        CS3F_A3_C_S,
        CS3F_A3_T_N,
        CS3F_A3_T_E,
        CS3F_A3_T_W,
        CS3F_A3_T_S,
        CS3F_B3_B_N,
        CS3F_B3_B_E,
        CS3F_B3_B_W,
        CS3F_B3_B_S,
        CS3F_B3_C_N,
        CS3F_B3_C_E,
        CS3F_B3_C_W,
        CS3F_B3_C_S,
        CS3F_B3_T_N,
        CS3F_B3_T_E,
        CS3F_B3_T_W,
        CS3F_B3_T_S,
        CS3F_A4_B_N,
        CS3F_A4_B_E,
        CS3F_A4_B_W,
        CS3F_A4_B_S,
        CS3F_A4_C_N,
        CS3F_A4_C_E,
        CS3F_A4_C_W,
        CS3F_A4_C_S,
        CS3F_A4_T_N,
        CS3F_A4_T_E,
        CS3F_A4_T_W,
        CS3F_A4_T_S,
        CS3F_B4_B_N,
        CS3F_B4_B_E,
        CS3F_B4_B_W,
        CS3F_B4_B_S,
        CS3F_B4_C_N,
        CS3F_B4_C_E,
        CS3F_B4_C_W,
        CS3F_B4_C_S,
        CS3F_B4_T_N,
        CS3F_B4_T_E,
        CS3F_B4_T_W,
        CS3F_B4_T_S

    };

    public static StrainGaugeInfo findStrainGaugeInfo(UnitInfo uni, int chno) {

//        logger.log(Level.INFO, "unit="+uni.getName()+" ("+uni+")"+", chno="+chno);
        for (StrainGaugeInfo gi : allstraingauges) {
//            logger.log(Level.INFO, "gi.unit="+gi.ui.getName()+" ("+gi.ui+"), gi.chno="+gi.chno);
            if ((uni == gi.ui) && (chno == gi.chno)) {
                return gi;
            }
        }
//        logger.log(Level.INFO, "not found.");
        return null;
    }

}

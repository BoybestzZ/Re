/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.util;

import jun.raspi.alive.UnitInfo;

/**
 *
 * @author jun
 */
public class EdefenseInfo {

    public static final String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", "D01Q10", "D01Q11",
        "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06", "D02Q07", "D02Q08",
        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08", "D03Q09"
    };

    public static final UnitInfo a01 = new UnitInfo("a01",
            "00:0e:c6:47:16:55", "GA-LA-A-U/GA-LA-A-L");

    public static final UnitInfo a02 = new UnitInfo("a02",
            "00:0e:c6:47:16:4f", "GA-LA-A-U/GA-LA-A-L");
    public static final UnitInfo a03 = new UnitInfo("a03", "00:0e:c6:47:16:4b", "GA-LA-3/CS-2F-A3-B");

    public static final UnitInfo b01 = new UnitInfo("b01", "00:0e:c6:46:33:17", "b01");
    public static final UnitInfo b02 = new UnitInfo("b02", "00:0e:c6:46:e1:03", "b02");
    public static final UnitInfo b03 = new UnitInfo("b03", "00:0e:c6:46:e0:be", "b03");
    public static final UnitInfo c01 = new UnitInfo("c01", "00:0e:c6:46:33:1f", "c01");
    public static final UnitInfo c02 = new UnitInfo("c02", "00:0e:c6:46:de:aa", "c02");
    public static final UnitInfo c03 = new UnitInfo("c03", "00:0e:c6:46:c5:93", "c03");
    public static final UnitInfo d01 = new UnitInfo("d01", "00:0e:c6:47:16:47", "d01");
    public static final UnitInfo d02 = new UnitInfo("d02", "00:0e:c6:47:16:60", "d02");
    public static final UnitInfo d03 = new UnitInfo("d03", "00:0e:c6:47:16:1f", "d03");
    public static final UnitInfo e01 = new UnitInfo("e01", "00:0e:c6:47:16:57", "e01");
    public static final UnitInfo e02 = new UnitInfo("e02", "00:0e:c6:47:16:4d", "e02");
    public static final UnitInfo e03 = new UnitInfo("e03", "00:0e:c6:46:fa:6c", "e03");
    public static final UnitInfo e04 = new UnitInfo("e04", "00:0e:c6:47:16:56", "e04");
    public static final UnitInfo f01 = new UnitInfo("f01", "00:0e:c6:46:c1:87", "f01");
    public static final UnitInfo f02 = new UnitInfo("f02", "00:0e:c6:46:e0:a8", "f02");
    public static final UnitInfo f03 = new UnitInfo("f03", "00:0e:c6:46:32:f3", "f03");
    public static final UnitInfo g01 = new UnitInfo("g01", "00:0e:c6:46:c1:a1", "g01");
    public static final UnitInfo g02 = new UnitInfo("g02", "00:0e:c6:47:15:8e", "g02");
    public static final UnitInfo g03 = new UnitInfo("g03", "00:0e:c6:46:df:04", "g03");
    public static final UnitInfo h01 = new UnitInfo("h01", "00:0e:c6:46:2a:a4", "h01");
    public static final UnitInfo h02 = new UnitInfo("h02", "00:0e:c6:46:c2:15", "h02");
    public static final UnitInfo h03 = new UnitInfo("h03", "00:0e:c6:46:c0:45", "h03");
    public static final UnitInfo h04 = new UnitInfo("h04", "00:0e:c6:46:c2:91", "h04");
    public static final UnitInfo i01 = new UnitInfo("i01", "00:0e:c6:46:e0:48", "i01");
    public static final UnitInfo i02 = new UnitInfo("i02", "00:0e:c6:46:e0:3b", "i02");
    public static final UnitInfo i03 = new UnitInfo("i03", "00:0e:c6:46:df:42", "i03");
    public static final UnitInfo j01 = new UnitInfo("j01", "00:0e:c6:46:e0:e8", "j01");
    public static final UnitInfo j02 = new UnitInfo("j02", "00:0e:c6:46:dd:bf", "j02");
    public static final UnitInfo j03 = new UnitInfo("j03", "00:0e:c6:46:de:df", "j03");

    public static final UnitInfo k01 = new UnitInfo("k01", "00:0e:c6:46:dc:70", "k01");
    public static final UnitInfo k02 = new UnitInfo("k02", "00:0e:c6:46:c5:94", "k02");
    public static final UnitInfo k03 = new UnitInfo("k03", "00:0e:c6:46:79:ec", "k03");
    public static final UnitInfo k04 = new UnitInfo("k04", "00:0e:c6:46:e1:41", "k04");
    public static final UnitInfo k05 = new UnitInfo("k05", "00:0e:c6:46:c7:fe", "k05");
    public static final UnitInfo k06 = new UnitInfo("k06", "00:0e:c6:46:e0:f1", "k06");

    public static final GaugeGroup GSLA_A_U
            = new GaugeGroup("GSLA_A_U", a01, 1, 6, c01, 1, 2, h04, 1, 1, c01, 4, 4);

    public static final GaugeGroup GSLA_A_L
            = new GaugeGroup("GSLA_A_L", a01, 7, 8, a02, 1, 4, c01, 5, 8);
    public static final GaugeGroup GSLA_3_U
            = new GaugeGroup("GSLA_3_U", a02, 5, 8, e02, 1, 6);
    public static final GaugeGroup GSLA_3_L
            = new GaugeGroup("GSLA_3_L", a03, 1, 4, e02, 7, 8, e03, 1, 4);
    public static final GaugeGroup GSLA_4_U
            = new GaugeGroup("GSLA_4_U", c02, 1, 6, g02, 5, 8);
    public static final GaugeGroup GSLA_4_L
            = new GaugeGroup("GSLA_4_L", c02, 7, 8, g03, 1, 4);
    public static final GaugeGroup GSLA_B_U
            = new GaugeGroup("GSLA_B_U", e01, 1, 4, g01, 1, 6);
    public static final GaugeGroup GSLA_B_L
            = new GaugeGroup("GSLA_B_L", e01, 5, 8, g01, 7, 8, g02, 1, 4);

    public static final GaugeGroup CS2F_A3_B = new GaugeGroup("CS2F_A3_B", a03, 5, 8);
    public static final GaugeGroup CS2F_A3_C = new GaugeGroup("CS2F_A3_C", b01, 1, 4);
    public static final GaugeGroup CS2F_A3_T = new GaugeGroup("CS2F_A3_T", b01, 5, 8);

    public static final GaugeGroup CS2F_A4_B = new GaugeGroup("CS2F_A4_B", c03, 5, 8);
    public static final GaugeGroup CS2F_A4_C = new GaugeGroup("CS2F_A4_C", d01, 1, 4);
    public static final GaugeGroup CS2F_A4_T = new GaugeGroup("CS2F_A4_T", d01, 5, 8);

    public static final GaugeGroup CS2F_B3_B = new GaugeGroup("CS2F_B3_B", e03, 5, 8);
    public static final GaugeGroup CS2F_B3_C = new GaugeGroup("CS2F_B3_C", e04, 1, 4);
    public static final GaugeGroup CS2F_B3_T = new GaugeGroup("CS2F_B3_T", e04, 5, 8);

    public static final GaugeGroup CS2F_B4_B = new GaugeGroup("CS2F_B4_B", g03, 5, 8);
    public static final GaugeGroup CS2F_B4_C = new GaugeGroup("CS2F_B4_C", h01, 1, 4);
    public static final GaugeGroup CS2F_B4_T = new GaugeGroup("CS2F_B4_T", h01, 5, 8);

    public static final GaugeGroup CS3F_A3_B = new GaugeGroup("CS3F_A3_B", i01, 1, 4);
    public static final GaugeGroup CS3F_A3_C = new GaugeGroup("CS3F_A3_C", i01, 5, 8);
    public static final GaugeGroup CS3F_A3_T = new GaugeGroup("CS3F_A3_T", i02, 1, 4);
    public static final GaugeGroup CS3F_B3_B = new GaugeGroup("CS3F_B3_B", i02, 5, 8);
    public static final GaugeGroup CS3F_B3_C = new GaugeGroup("CS3F_B3_C", i03, 1, 4);
    public static final GaugeGroup CS3F_B3_T = new GaugeGroup("CS3F_B3_T", i03, 5, 8);
    public static final GaugeGroup CS3F_A4_B = new GaugeGroup("CS3F_A4_B", j01, 1, 4);
    public static final GaugeGroup CS3F_A4_C = new GaugeGroup("CS3F_A4_C", j01, 5, 8);
    public static final GaugeGroup CS3F_A4_T = new GaugeGroup("CS3F_A4_T", j02, 1, 4);
    public static final GaugeGroup CS3F_B4_B = new GaugeGroup("CS3F_B4_B", j02, 5, 8);
    public static final GaugeGroup CS3F_B4_C = new GaugeGroup("CS3F_B4_C", j03, 1, 4);
    public static final GaugeGroup CS3F_B4_T = new GaugeGroup("CS3F_B4_T", j03, 5, 8);

    public static final GaugeGroup GSLO_A_A3 = new GaugeGroup("GSLO_A_A3", b02, 1, 8);
    public static final GaugeGroup GSLO_3_A3 = new GaugeGroup("GSLO_3_A3", b03, 1, 8);
    public static final GaugeGroup GSLO_A_A4 = new GaugeGroup("GSLO_A_A4", d02, 1, 8);
    public static final GaugeGroup GSLO_4_A4 = new GaugeGroup("GSLO_4_A4", d02, 1, 8);
    public static final GaugeGroup GSLO_B_B3 = new GaugeGroup("GSLO_B_B3", f01, 1, 8);
    public static final GaugeGroup GSLO_3_B3 = new GaugeGroup("GSLO_3_B3", f02, 1, 8);
    public static final GaugeGroup GSLO_B_B4 = new GaugeGroup("GSLO_B_B4", h02, 1, 8);
    public static final GaugeGroup GSLO_4_B4 = new GaugeGroup("GSLO_4_B4", h03, 1, 8);

    public static final GaugeGroup[] allGaugeGroup = {
        GSLA_3_L, GSLA_3_U,
        GSLA_4_L, GSLA_4_U,
        GSLA_A_L, GSLA_B_U,
        GSLA_B_L, GSLA_B_U,
        CS2F_A3_B, CS2F_A3_C, CS2F_A3_T,
        CS2F_A4_B, CS2F_A4_C, CS2F_A4_T,
        CS2F_B3_B, CS2F_B3_C, CS2F_B3_T,
        CS2F_B4_B, CS2F_B4_C, CS2F_B4_T,
        GSLO_3_A3, GSLO_3_B3,
        GSLO_4_A4, GSLO_4_B4,
        GSLO_A_A3, GSLO_A_A4,
        GSLO_B_B3, GSLO_B_B4
    };

    public static final UnitInfo[] allunits = {a01, a02, a03,
        b01, b02, b03,
        c01, c02, c03,
        d01, d02, d03,
        e01, e02, e03, e04,
        f01, f02, f03,
        g01, g02, g03,
        h01, h02, h03, h04,
        i01, i02, i03,
        j01, j02, j03,
        k01, k02, k03, k04, k05, k06};

    public static final double E = 2.05e11; // N/m2

    // 3 = G11 BH-350x200x9x12
    public static final double Area3 = 7734.0 * 1e-6; // m2
    public static final double Zx3Inner = 163134900.0 / (350.0 / 2 - 12) * 1e-9; // 1,000,827 mm3; ->m3
    public static final double Zx3Outer = 163134900.0 / (350.0 / 2) * 1e-9; // 1,000,827 mm3; ->m3
    public static final double Zy3 = 16019800.0 / ((200 - 40) / 2.0) * 1e-9;  // m3
    public static final double H3 = 0.350;
    // Ny=7734 mm2 * 235 N/mm2 = 1585 kN
    // My=1,000,827 mm2 * 235 N/mm2 = 235 kNm

    // A = G1 BH-350x200x9x12
    public static final double AreaA = 7734.0 * 1e-6; // m2
    public static final double ZxAInner = 163134900 / (350.0 / 2 - 12) * 1e-9; // 10236642 mm3; ->m3
    public static final double ZxAOuter = 163134900 / (350.0 / 2) * 1e-9; // 10236642 mm3; ->m3
    public static final double ZyA = 16019800.0 / ((200 - 40) / 2.0) * 1e-9;  // m3    
    public static final double HA = 0.350;
    // B = G3 H-244x175x7x11
    public static final double AreaB = 5549 * 1e-6; // m2
    public static final double ZxBInner = 6037.02e4 / (244.0 / 2 - 12) * 1e-9; // 10236642 mm3; ->m3
    public static final double ZxBOuter = 6037.02e4 / (244.0 / 2) * 1e-9; // 10236642 mm3; ->m3
    public static final double ZyB = 983.87e4 / ((175 - 40) / 2.0) * 1e-9;  // m3    
    public static final double HB = 0.244;

    // 3A,4A=C1(=C2) Box350x16
    public static final double AreaC1 = 207.2/*cm2*/ * 1e-4; // m2
    public static final double ZnsC1 = 2160/*cm3*/ * 1e-6; //m2
    public static final double ZewC1 = ZnsC1;
    public static final double HC1 = 0.350; //m

// 3A,4A=C3(=C2) Box250x9
    public static final double AreaC3 = 84.67/*cm2*/ * 1e-4; // m2
    public static final double ZnsC3 = 647/*cm3*/ * 1e-6; //m2
    public static final double ZewC3 = ZnsC3;
    public static final double HC3 = 0.250; //m    

    public static final BeamSectionInfo LA3S1 = new BeamSectionInfo("LA3S1", StrainGaugeInfo.GSLA_3_U_L_1, StrainGaugeInfo.GSLA_3_U_R_1, StrainGaugeInfo.GSLA_3_L_L_1, StrainGaugeInfo.GSLA_3_L_R_1, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA3S2 = new BeamSectionInfo("LA3S2", StrainGaugeInfo.GSLA_3_U_L_2, StrainGaugeInfo.GSLA_3_U_R_2, StrainGaugeInfo.GSLA_3_L_L_2, StrainGaugeInfo.GSLA_3_L_R_2, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA3S3 = new BeamSectionInfo("LA3S3", StrainGaugeInfo.GSLA_3_U_L_3, StrainGaugeInfo.GSLA_3_U_R_3, StrainGaugeInfo.GSLA_3_L_L_3, StrainGaugeInfo.GSLA_3_L_R_3, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA3S4 = new BeamSectionInfo("LA3S4", StrainGaugeInfo.GSLA_3_U_L_4, StrainGaugeInfo.GSLA_3_U_R_4, StrainGaugeInfo.GSLA_3_L_L_4, StrainGaugeInfo.GSLA_3_L_R_4, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA3S5 = new BeamSectionInfo("LA3S5", StrainGaugeInfo.GSLA_3_U_L_5, StrainGaugeInfo.GSLA_3_U_R_5, StrainGaugeInfo.GSLA_3_L_L_5, StrainGaugeInfo.GSLA_3_L_R_5, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);

    public static final BeamSectionInfo LA4S1 = new BeamSectionInfo("LA4S1", StrainGaugeInfo.GSLA_4_U_L_1, StrainGaugeInfo.GSLA_4_U_R_1, StrainGaugeInfo.GSLA_4_L_L_1, StrainGaugeInfo.GSLA_4_L_R_1, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA4S2 = new BeamSectionInfo("LA4S2", StrainGaugeInfo.GSLA_4_U_L_2, StrainGaugeInfo.GSLA_4_U_R_2, StrainGaugeInfo.GSLA_4_L_L_2, StrainGaugeInfo.GSLA_4_L_R_2, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA4S3 = new BeamSectionInfo("LA4S3", StrainGaugeInfo.GSLA_4_U_L_3, StrainGaugeInfo.GSLA_4_U_R_3, StrainGaugeInfo.GSLA_4_L_L_3, StrainGaugeInfo.GSLA_4_L_R_3, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA4S4 = new BeamSectionInfo("LA4S4", StrainGaugeInfo.GSLA_4_U_L_4, StrainGaugeInfo.GSLA_4_U_R_4, StrainGaugeInfo.GSLA_4_L_L_4, StrainGaugeInfo.GSLA_4_L_R_4, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);
    public static final BeamSectionInfo LA4S5 = new BeamSectionInfo("LA4S5", StrainGaugeInfo.GSLA_4_U_L_5, StrainGaugeInfo.GSLA_4_U_R_5, StrainGaugeInfo.GSLA_4_L_L_5, StrainGaugeInfo.GSLA_4_L_R_5, E, Area3, Zx3Inner, Zx3Outer, Zy3, H3);

    public static final BeamSectionInfo LAAS1 = new BeamSectionInfo("LAAS1", StrainGaugeInfo.GSLA_A_U_L_1, StrainGaugeInfo.GSLA_A_U_R_1, StrainGaugeInfo.GSLA_A_L_L_1, StrainGaugeInfo.GSLA_A_L_R_1, E, AreaA, ZxAInner, ZxAOuter, ZyA, HA);
    public static final BeamSectionInfo LAAS2 = new BeamSectionInfo("LAAS2", StrainGaugeInfo.GSLA_A_U_L_2, StrainGaugeInfo.GSLA_A_U_R_2, StrainGaugeInfo.GSLA_A_L_L_2, StrainGaugeInfo.GSLA_A_L_R_2, E, AreaA, ZxAInner, ZxAOuter, ZyA, HA);
    public static final BeamSectionInfo LAAS3 = new BeamSectionInfo("LAAS3", StrainGaugeInfo.GSLA_A_U_L_3, StrainGaugeInfo.GSLA_A_U_R_3, StrainGaugeInfo.GSLA_A_L_L_3, StrainGaugeInfo.GSLA_A_L_R_3, E, AreaA, ZxAInner, ZxAOuter, ZyA, HA);
    public static final BeamSectionInfo LAAS4 = new BeamSectionInfo("LAAS4", StrainGaugeInfo.GSLA_A_U_L_4, StrainGaugeInfo.GSLA_A_U_R_4, StrainGaugeInfo.GSLA_A_L_L_4, StrainGaugeInfo.GSLA_A_L_R_4, E, AreaA, ZxAInner, ZxAOuter, ZyA, HA);
    public static final BeamSectionInfo LAAS5 = new BeamSectionInfo("LAAS5", StrainGaugeInfo.GSLA_A_U_L_5, StrainGaugeInfo.GSLA_A_U_R_5, StrainGaugeInfo.GSLA_A_L_L_5, StrainGaugeInfo.GSLA_A_L_R_5, E, AreaA, ZxAInner, ZxAOuter, ZyA, HA);

    public static final BeamSectionInfo LABS1 = new BeamSectionInfo("LABS1", StrainGaugeInfo.GSLA_B_U_L_1, StrainGaugeInfo.GSLA_B_U_R_1, StrainGaugeInfo.GSLA_B_L_L_1, StrainGaugeInfo.GSLA_B_L_R_1, E, AreaB, ZxBInner, ZxBOuter, ZyB, HB);
    public static final BeamSectionInfo LABS2 = new BeamSectionInfo("LABS2", StrainGaugeInfo.GSLA_B_U_L_2, StrainGaugeInfo.GSLA_B_U_R_2, StrainGaugeInfo.GSLA_B_L_L_2, StrainGaugeInfo.GSLA_B_L_R_2, E, AreaB, ZxBInner, ZxBOuter, ZyB, HB);
    public static final BeamSectionInfo LABS3 = new BeamSectionInfo("LABS3", StrainGaugeInfo.GSLA_B_U_L_3, StrainGaugeInfo.GSLA_B_U_R_3, StrainGaugeInfo.GSLA_B_L_L_3, StrainGaugeInfo.GSLA_B_L_R_3, E, AreaB, ZxBInner, ZxBOuter, ZyB, HB);
    public static final BeamSectionInfo LABS4 = new BeamSectionInfo("LABS4", StrainGaugeInfo.GSLA_B_U_L_4, StrainGaugeInfo.GSLA_B_U_R_4, StrainGaugeInfo.GSLA_B_L_L_4, StrainGaugeInfo.GSLA_B_L_R_4, E, AreaB, ZxBInner, ZxBOuter, ZyB, HB);
    public static final BeamSectionInfo LABS5 = new BeamSectionInfo("LABS5", StrainGaugeInfo.GSLA_B_U_L_5, StrainGaugeInfo.GSLA_B_U_R_5, StrainGaugeInfo.GSLA_B_L_L_5, StrainGaugeInfo.GSLA_B_L_R_5, E, AreaB, ZxBInner, ZxBOuter, ZyB, HB);

    // 3F - A3 C1=C2
    public static final ColumnSectionInfo CS3FA3T = new ColumnSectionInfo("CS3FA3T",
            StrainGaugeInfo.CS3F_A3_T_N, StrainGaugeInfo.CS3F_A3_T_E, StrainGaugeInfo.CS3F_A3_T_W, StrainGaugeInfo.CS3F_A3_T_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS3FA3C = new ColumnSectionInfo("CS3FA3C",
            StrainGaugeInfo.CS3F_A3_C_N, StrainGaugeInfo.CS3F_A3_C_E, StrainGaugeInfo.CS3F_A3_C_W, StrainGaugeInfo.CS3F_A3_C_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS3FA3B = new ColumnSectionInfo("CS3FA3B",
            StrainGaugeInfo.CS3F_A3_B_N, StrainGaugeInfo.CS3F_A3_B_E, StrainGaugeInfo.CS3F_A3_B_W, StrainGaugeInfo.CS3F_A3_B_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);

    // 3F - A4    C1=C2
    public static final ColumnSectionInfo CS3FA4T = new ColumnSectionInfo("CS3FA4T",
            StrainGaugeInfo.CS3F_A4_T_N, StrainGaugeInfo.CS3F_A4_T_E, StrainGaugeInfo.CS3F_A4_T_W, StrainGaugeInfo.CS3F_A4_T_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS3FA4C = new ColumnSectionInfo("CS3FA4C",
            StrainGaugeInfo.CS3F_A4_C_N, StrainGaugeInfo.CS3F_A4_C_E, StrainGaugeInfo.CS3F_A4_C_W, StrainGaugeInfo.CS3F_A4_C_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS3FA4B = new ColumnSectionInfo("CS3FA4B",
            StrainGaugeInfo.CS3F_A4_B_N, StrainGaugeInfo.CS3F_A4_B_E, StrainGaugeInfo.CS3F_A4_B_W, StrainGaugeInfo.CS3F_A4_B_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);

    // 3F - B3 C3
    public static final ColumnSectionInfo CS3FB3T = new ColumnSectionInfo("CS3FB3T",
            StrainGaugeInfo.CS3F_B3_T_N, StrainGaugeInfo.CS3F_B3_T_E, StrainGaugeInfo.CS3F_B3_T_W, StrainGaugeInfo.CS3F_B3_T_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS3FB3C = new ColumnSectionInfo("CS3FB3C",
            StrainGaugeInfo.CS3F_B3_C_N, StrainGaugeInfo.CS3F_B3_C_E, StrainGaugeInfo.CS3F_B3_C_W, StrainGaugeInfo.CS3F_B3_C_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS3FB3B = new ColumnSectionInfo("CS3FB3B",
            StrainGaugeInfo.CS3F_B3_B_N, StrainGaugeInfo.CS3F_B3_B_E, StrainGaugeInfo.CS3F_B3_B_W, StrainGaugeInfo.CS3F_B3_B_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);

    // 3F - B4    C3
    public static final ColumnSectionInfo CS3FB4T = new ColumnSectionInfo("CS3FB4T",
            StrainGaugeInfo.CS3F_B4_T_N, StrainGaugeInfo.CS3F_B4_T_E, StrainGaugeInfo.CS3F_B4_T_W, StrainGaugeInfo.CS3F_B4_T_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS3FB4C = new ColumnSectionInfo("CS3FB4C",
            StrainGaugeInfo.CS3F_B4_C_N, StrainGaugeInfo.CS3F_B4_C_E, StrainGaugeInfo.CS3F_B4_C_W, StrainGaugeInfo.CS3F_B4_C_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS3FB4B = new ColumnSectionInfo("CS3FB4B",
            StrainGaugeInfo.CS3F_B4_B_N, StrainGaugeInfo.CS3F_B4_B_E, StrainGaugeInfo.CS3F_B4_B_W, StrainGaugeInfo.CS3F_B4_B_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    // 2023/06/11 上記 CS3F_B4_B_N となるべきところが CS3F_B4_CN となっていたので修正。

    // 2F- A3    C1=C2
    public static final ColumnSectionInfo CS2FA3T = new ColumnSectionInfo("CS2FA3T",
            StrainGaugeInfo.CS2F_A3_T_N, StrainGaugeInfo.CS2F_A3_T_E, StrainGaugeInfo.CS2F_A3_T_W, StrainGaugeInfo.CS2F_A3_T_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS2FA3C = new ColumnSectionInfo("CS2FA3C",
            StrainGaugeInfo.CS2F_A3_C_N, StrainGaugeInfo.CS2F_A3_C_E, StrainGaugeInfo.CS2F_A3_C_W, StrainGaugeInfo.CS2F_A3_C_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS2FA3B = new ColumnSectionInfo("CS2FA3B",
            StrainGaugeInfo.CS2F_A3_B_N, StrainGaugeInfo.CS2F_A3_B_E, StrainGaugeInfo.CS2F_A3_B_W, StrainGaugeInfo.CS2F_A3_B_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);

    // 2F- A4    C1=C2
    public static final ColumnSectionInfo CS2FA4T = new ColumnSectionInfo("CS2FA4T",
            StrainGaugeInfo.CS2F_A4_T_N, StrainGaugeInfo.CS2F_A4_T_E, StrainGaugeInfo.CS2F_A4_T_W, StrainGaugeInfo.CS2F_A4_T_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS2FA4C = new ColumnSectionInfo("CS2FA4C",
            StrainGaugeInfo.CS2F_A4_C_N, StrainGaugeInfo.CS2F_A4_C_E, StrainGaugeInfo.CS2F_A4_C_W, StrainGaugeInfo.CS2F_A4_C_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);
    public static final ColumnSectionInfo CS2FA4B = new ColumnSectionInfo("CS2FA4B",
            StrainGaugeInfo.CS2F_A4_B_N, StrainGaugeInfo.CS2F_A4_B_E, StrainGaugeInfo.CS2F_A4_B_W, StrainGaugeInfo.CS2F_A4_B_S, E,
            AreaC1, ZnsC1, ZewC1, HC1);

    // 2F- B3    C3
    public static final ColumnSectionInfo CS2FB3T = new ColumnSectionInfo("CS2FB3T",
            StrainGaugeInfo.CS2F_B3_T_N, StrainGaugeInfo.CS2F_B3_T_E, StrainGaugeInfo.CS2F_B3_T_W, StrainGaugeInfo.CS2F_B3_T_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS2FB3C = new ColumnSectionInfo("CS2FB3C",
            StrainGaugeInfo.CS2F_B3_C_N, StrainGaugeInfo.CS2F_B3_C_E, StrainGaugeInfo.CS2F_B3_C_W, StrainGaugeInfo.CS2F_B3_C_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS2FB3B = new ColumnSectionInfo("CS2FB3B",
            StrainGaugeInfo.CS2F_B3_B_N, StrainGaugeInfo.CS2F_B3_B_E, StrainGaugeInfo.CS2F_B3_B_W, StrainGaugeInfo.CS2F_B3_B_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);

    // 2F- B4    C3
    public static final ColumnSectionInfo CS2FB4T = new ColumnSectionInfo("CS2FB4T",
            StrainGaugeInfo.CS2F_B4_T_N, StrainGaugeInfo.CS2F_B4_T_E, StrainGaugeInfo.CS2F_B4_T_W, StrainGaugeInfo.CS2F_B4_T_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS2FB4C = new ColumnSectionInfo("CS2FB4C",
            StrainGaugeInfo.CS2F_B4_C_N, StrainGaugeInfo.CS2F_B4_C_E, StrainGaugeInfo.CS2F_B4_C_W, StrainGaugeInfo.CS2F_B4_C_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);
    public static final ColumnSectionInfo CS2FB4B = new ColumnSectionInfo("CS2FB4B",
            StrainGaugeInfo.CS2F_B4_B_N, StrainGaugeInfo.CS2F_B4_B_E, StrainGaugeInfo.CS2F_B4_B_W, StrainGaugeInfo.CS2F_B4_B_S, E,
            AreaC3, ZnsC3, ZewC3, HC3);

    public static final BeamSectionInfo[] beamSectionsEW = {
        LAAS1, LAAS2, LAAS3, LAAS4, LAAS5,
        LABS1, LABS2, LABS3, LABS4, LABS5
    };
    public static final BeamSectionInfo[] beamSectionsNS = {
        LA3S1, LA3S2, LA3S3, LA3S4, LA3S5,
        LA4S1, LA4S2, LA4S3, LA4S4, LA4S5
    };

    public static final ColumnSectionInfo[] columnSections = {
        CS2FA3B, CS2FA3C, CS2FA3T,
        CS2FA4B, CS2FA4C, CS2FA4T,
        CS2FB3B, CS2FB3C, CS2FB3T,
        CS2FB4B, CS2FB4C, CS2FB4T,
        CS3FA3B, CS3FA3C, CS3FA3T,
        CS3FA4B, CS3FA4C, CS3FA4T,
        CS3FB3B, CS3FB3C, CS3FB3T,
        CS3FB4B, CS3FB4C, CS3FB4T
    };

    public static final BeamInfo Beam3 = new BeamInfo("Beam3", 3.1, 0.435, LA3S1, 0.520, LA3S2, 0.620, LA3S3, 0.620, LA3S4, 0.520, LA3S5, 0.385);
    public static final BeamInfo Beam4 = new BeamInfo("Beam4", 3.1, 0.435, LA4S1, 0.520, LA4S2, 0.620, LA4S3, 0.620, LA4S4, 0.520, LA4S5, 0.385);
    public static final BeamInfo BeamA = new BeamInfo("BeamA", 4.0, 0.370, LAAS1, 0.815, LAAS2, 0.815, LAAS3, 0.815, LAAS4, 0.815, LAAS5, 0.370);
    public static final BeamInfo BeamB = new BeamInfo("BeamB", 4.0, 0.370, LABS1, 0.815, LABS2, 0.815, LABS3, 0.815, LABS4, 0.815, LABS5, 0.370);

    public static ColumnInfo Column2FA3 = new ColumnInfo("Column2F3A", 2.6,
            0.11 + 0.35 / 2 + 0.465,
            CS2FA3B, 1.04 - 0.465, CS2FA3C, 1.615 - 1.040, CS2FA3T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column3FA3 = new ColumnInfo("Column3F3A", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS3FA3B, 1.04 - 0.465, CS3FA3C, 1.615 - 1.040, CS3FA3T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column2FB3 = new ColumnInfo("Column2F3B", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS2FB3B, 1.04 - 0.465, CS2FB3C, 1.615 - 1.040, CS2FB3T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column3FB3 = new ColumnInfo("Column3F3B", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS3FB3B, 1.04 - 0.465, CS3FB3C, 1.615 - 1.040, CS3FB3T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。    


    // 柱曲げモーメントが直線じゃない。####
    public static ColumnInfo Column2FA4 = new ColumnInfo("Column2F4A", 2.6,
            0.11 + 0.35 / 2 + 0.465,
            CS2FA4B, 1.04 - 0.465, CS2FA4C, 1.615 - 1.040, CS2FA4T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column3FA4 = new ColumnInfo("Column3F4A", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS3FA4B, 1.04 - 0.465, CS3FA4C, 1.615 - 1.040, CS3FA4T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column2FB4 = new ColumnInfo("Column2F4B", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS2FB4B, 1.04 - 0.465, CS2FB4C, 1.615 - 1.040, CS2FB4T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。

    public static ColumnInfo Column3FB4 = new ColumnInfo("Column3F4B", 2.6, 0.11 + 0.35 / 2 + 0.465,
            CS3FB4B, 1.04 - 0.465, CS3FB4C, 1.615 - 1.040, CS3FB4T, 0.700);
    // スラブ厚110、梁せい 350、 すなわち梁心=FL-110-350/2,  ゲージは FL+465, FL+1040, FL+ 1615 に貼ってある。    

    public static final UnitInfo lookForUnit(String macaddress) {
        for (UnitInfo unit : allunits) {
            if (unit.getHardwareAddress().equals(macaddress)) {
                return unit;
            }
        }
        return null;
    }

// 最後の tmrTimeDiffSecondsは、    t 時刻歴とTMRのTIME[s]との差分。 TMRのTIME[s]からこの時間（秒）を引けばt時刻歴と合致する。
    // 最後の tmrTimeDiffSecondsは、    t 時刻歴とNIEDのTIME[s]との差分。 NiedのTIME[s]からこの時間（秒）を引けばt時刻歴と合致する。
    public static final EdefenseKasinInfo D01Q01 = new EdefenseKasinInfo("D01Q01", 1, "Random", 311.6, -7.86);
    public static final EdefenseKasinInfo D01Q02 = new EdefenseKasinInfo("D01Q02", 2, "KMMH02", 287.43, 6.94);
    public static final EdefenseKasinInfo D01Q03 = new EdefenseKasinInfo("D01Q03", 3, "FKS020", 263.64, -11);
    public static final EdefenseKasinInfo D01Q04 = new EdefenseKasinInfo("D01Q04", 4, "Kobe25", 279.73, -7.98);
    public static final EdefenseKasinInfo D01Q05 = new EdefenseKasinInfo("D01Q05", 5, "Kobe25X", 276.6, -5.97);
    public static final EdefenseKasinInfo D01Q06 = new EdefenseKasinInfo("D01Q06", 6, "Kobe25Y", 273.29, -9);
    public static final EdefenseKasinInfo D01Q08 = new EdefenseKasinInfo("D01Q08", 7, "Kobe50", 292.295, -2.936);
    public static final EdefenseKasinInfo D01Q09 = new EdefenseKasinInfo("D01Q09", 8, "Random", 279.98, -6.902);
    public static final EdefenseKasinInfo D01Q10 = new EdefenseKasinInfo("D01Q10", 9, "Kobe75", 267.845, -1.865);
    public static final EdefenseKasinInfo D01Q11 = new EdefenseKasinInfo("D01Q11", 10, "Random", 266.375, -7.012);
    public static final EdefenseKasinInfo D02Q01 = new EdefenseKasinInfo("D02Q01", 11, "KMMH02", 887.79, -2.424);
    public static final EdefenseKasinInfo D02Q02 = new EdefenseKasinInfo("D02Q02", 12, "FKS020", 279.59, -1.368);
    public static final EdefenseKasinInfo D02Q03 = new EdefenseKasinInfo("D02Q03", 13, "Kobe100", 310.40, +0.191 + 3.297);
    public static final EdefenseKasinInfo D02Q05 = new EdefenseKasinInfo("D02Q05", 14, "Random", 275.63, -1.481);
    public static final EdefenseKasinInfo D02Q06 = new EdefenseKasinInfo("D02Q06", 15, "KMMH02", 286.475, +7.653);
    public static final EdefenseKasinInfo D02Q07 = new EdefenseKasinInfo("D02Q07", 0, "FKS020", 376.015);
    public static final EdefenseKasinInfo D02Q08 = new EdefenseKasinInfo("D02Q08", 16, "FKS020", 292.02, 0.556);
    public static final EdefenseKasinInfo D03Q01 = new EdefenseKasinInfo("D03Q01", 17, "Random", 294.94, -3.958);
    public static final EdefenseKasinInfo D03Q02 = new EdefenseKasinInfo("D03Q02", 18, "KMMH02", 292.625, +7.799);
    public static final EdefenseKasinInfo D03Q03 = new EdefenseKasinInfo("D03Q03", 19, "FKS020", 300.75, 2.061);
    public static final EdefenseKasinInfo D03Q04 = new EdefenseKasinInfo("D03Q04", 20, "Kobe25", 284.935, 3.775);
    public static final EdefenseKasinInfo D03Q05 = new EdefenseKasinInfo("D03Q05", 21, "Kobe25X", 289.43, 8.207);
    public static final EdefenseKasinInfo D03Q06 = new EdefenseKasinInfo("D03Q06", 22, "Kobe25Y", 290.36, 3.845);
    public static final EdefenseKasinInfo D03Q08 = new EdefenseKasinInfo("D03Q08", 23, "Kobe75", 323.785, 3.942);
    public static final EdefenseKasinInfo D03Q09 = new EdefenseKasinInfo("D03Q09", 24, "Random", 280.31, 1.809);

//    // 各加振前の微動
    public static final EdefenseKasinInfo D01Q01s = new EdefenseKasinInfo("D01Q01s");
    public static final EdefenseKasinInfo D01Q02s = new EdefenseKasinInfo("D01Q02s");
    public static final EdefenseKasinInfo D01Q03s = new EdefenseKasinInfo("D01Q03s");
    public static final EdefenseKasinInfo D01Q04s = new EdefenseKasinInfo("D01Q04s");
    public static final EdefenseKasinInfo D01Q05s = new EdefenseKasinInfo("D01Q05s");
    public static final EdefenseKasinInfo D01Q06s = new EdefenseKasinInfo("D01Q06s");
    public static final EdefenseKasinInfo D01Q08s = new EdefenseKasinInfo("D01Q08s");
    public static final EdefenseKasinInfo D01Q09s = new EdefenseKasinInfo("D01Q09s");
    public static final EdefenseKasinInfo D01Q10s = new EdefenseKasinInfo("D01Q10s");
    public static final EdefenseKasinInfo D01Q11s = new EdefenseKasinInfo("D01Q11s");
    public static final EdefenseKasinInfo D02Q01s = new EdefenseKasinInfo("D02Q01s");
    public static final EdefenseKasinInfo D02Q02s = new EdefenseKasinInfo("D02Q02s");
    public static final EdefenseKasinInfo D02Q03s = new EdefenseKasinInfo("D02Q03s");
    public static final EdefenseKasinInfo D02Q05s = new EdefenseKasinInfo("D02Q05s");
    public static final EdefenseKasinInfo D02Q06s = new EdefenseKasinInfo("D02Q06s");
    public static final EdefenseKasinInfo D02Q07s = new EdefenseKasinInfo("D02Q07s");
    public static final EdefenseKasinInfo D02Q08s = new EdefenseKasinInfo("D02Q08s");
    public static final EdefenseKasinInfo D03Q01s = new EdefenseKasinInfo("D03Q01s");
    public static final EdefenseKasinInfo D03Q02s = new EdefenseKasinInfo("D03Q02s");
    public static final EdefenseKasinInfo D03Q03s = new EdefenseKasinInfo("D03Q03s");
    public static final EdefenseKasinInfo D03Q04s = new EdefenseKasinInfo("D03Q04s");
    public static final EdefenseKasinInfo D03Q05s = new EdefenseKasinInfo("D03Q05s");
    public static final EdefenseKasinInfo D03Q06s = new EdefenseKasinInfo("D03Q06s");
    public static final EdefenseKasinInfo D03Q08s = new EdefenseKasinInfo("D03Q08s");
    public static final EdefenseKasinInfo D03Q09s = new EdefenseKasinInfo("D03Q09s");

    public static EdefenseKasinInfo lookForTestName(String testname) {
        if (testname.length() > 6) {
            testname = testname.substring(0, 6);
        }
        for (EdefenseKasinInfo kasin : alltests) {
            if (kasin.getTestName().equals(testname)) {
                return kasin;
            }
        }
        return null;
    }

    public static final EdefenseKasinInfo[] alltests = {
        D01Q01,
        D01Q02,
        D01Q03,
        D01Q04,
        D01Q05,
        D01Q06,
        D01Q08,
        D01Q09,
        D01Q10,
        D01Q11,
        D02Q01,
        D02Q02,
        D02Q03,
        D02Q05,
        D02Q06,
        //        D02Q07,
        D02Q08,
        D03Q01,
        D03Q02,
        D03Q03,
        D03Q04,
        D03Q05,
        D03Q06,
        D03Q08,
        D03Q09};

    public static final EdefenseKasinInfo[] alltestsS = {
        D01Q01s,
        D01Q02s,
        D01Q03s,
        D01Q04s,
        D01Q05s,
        D01Q06s,
        D01Q08s,
        D01Q09s,
        D01Q10s,
        D01Q11s,
        D02Q01s,
        D02Q02s,
        D02Q03s,
        D02Q05s,
        D02Q06s,
        D02Q07s,
        D02Q08s,
        D03Q01s,
        D03Q02s,
        D03Q03s,
        D03Q04s,
        D03Q05s,
        D03Q06s,
        D03Q08s,
        D03Q09s};

    public static enum Direction {
        NS, EW
    }

    public static BeamEndInfo BeamEnd3A = new BeamEndInfo("BeamEnd3A", GSLO_3_A3);
}

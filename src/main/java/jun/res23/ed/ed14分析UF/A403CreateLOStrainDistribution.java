/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import jun.util.JunShapes;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ed06T265→ed14A400→ed14.A401 →ed14A402→ A403 左端は 12、右端は45
 *
 * @author jun
 */
public class A403CreateLOStrainDistribution {

    private static final Logger logger = Logger.getLogger(A403CreateLOStrainDistribution.class.getName());
    public static Path inputDatabaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ");
    public static String fourierSchema = "R155FourierU"; //"R152FourierS";
    public static String driftTable = "R175StoryDriftU";
    private static final String outputDburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/ed14";
    public static String A300SectionNM = A310SectionNM.outputTable; // "A310SectionNM";
    private static final String outputTableName = "A403LOStrainDistribution";

    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/A403LOStrainDistribution");

    public static void main(String[] args) {

        try {
            logger.log(Level.INFO, "Opening database ");
            Connection outcon = DriverManager.getConnection(outputDburl, "junapp", "");
            logger.log(Level.INFO, "Opened.");
            Statement outst = outcon.createStatement();
            outst.executeUpdate("drop table if exists \"" + outputTableName + "\" ");

//                + ")"            ｖ
            outst.executeUpdate("create table if not exists \"" + outputTableName + "\" (TESTNAME varchar,WAVENAME varchar, EDGENAME varchar, "
                    + "\"Freq[Hz]\" real,\"Inter2StoryDispA[mm*s]\" real,\"Inter2StoryDispP[rad]\" real, "
                    + "\"LocationGauge1[m]\" real, \"OuterLocalStiffness1R[με/mm]\" real,\"OuterLocalStiffness1I[με/mm]\" real,\"InnerLocalStiffness1R[με/mm]\" real,\"InnerLocalStiffness1I[με/mm]\" real, "
                    + "\"LocationGauge2[m]\" real, \"OuterLocalStiffness2R[με/mm]\" real,\"OuterLocalStiffness2I[με/mm]\" real,\"InnerLocalStiffness2R[με/mm]\" real,\"InnerLocalStiffness2I[με/mm]\" real, "
                    + "\"LocationGauge3[m]\" real, \"OuterLocalStiffness3R[με/mm]\" real,\"OuterLocalStiffness3I[με/mm]\" real,\"InnerLocalStiffness3R[με/mm]\" real,\"InnerLocalStiffness3I[με/mm]\" real, "
                    + "\"LocationGauge4[m]\" real, \"OuterLocalStiffness4R[με/mm]\" real,\"OuterLocalStiffness4I[με/mm]\" real,\"InnerLocalStiffness4R[με/mm]\" real,\"InnerLocalStiffness4I[με/mm]\" real, "
                    + "\"LocationS1[m]\" real, \"MS1A[Nm*s]\" real,\"MS1P[rad]\" real,\"NS1A[Nm*s]\" real,\"NS1P[rad]\" real,"
                    + "\"LocationS2[m]\" real, \"MS2A[Nm*s]\" real,\"MS2P[rad]\" real,\"NS2A[Nm*s]\" real,\"NS2P[rad]\" real,"
                    + " \"QA[N*s]\" real, \"QP[rad]\" real,"
                    + "\"LocationEstimate0[m]\" real, \"OuterLocalStiffnessEstimate0R[με/mm]\" real,\"OuterLocalStiffnessEstimate0I[με/mm]\" real,\"InnerLocalStiffnessEstimate0R[με/mm]\" real,\"InnerLocalStiffnessEstimate0I[με/mm]\" real, "
                    + "\"LocationEstimate1[m]\" real, \"OuterLocalStiffnessEstimate1R[με/mm]\" real,\"OuterLocalStiffnessEstimate1I[με/mm]\" real,\"InnerLocalStiffnessEstimate1R[με/mm]\" real,\"InnerLocalStiffnessEstimate1I[με/mm]\" real, "
                    + "\"LocationEstimate2[m]\" real, \"OuterLocalStiffnessEstimate2R[με/mm]\" real,\"OuterLocalStiffnessEstimate2I[με/mm]\" real,\"InnerLocalStiffnessEstimate2R[με/mm]\" real,\"InnerLocalStiffnessEstimate2I[με/mm]\" real, "
                    + "\"LocationEstimate3[m]\" real, \"OuterLocalStiffnessEstimate3R[με/mm]\" real,\"OuterLocalStiffnessEstimate3I[με/mm]\" real,\"InnerLocalStiffnessEstimate3R[με/mm]\" real,\"InnerLocalStiffnessEstimate3I[με/mm]\" real, "
                    + "\"LocationEstimate4[m]\" real, \"OuterLocalStiffnessEstimate4R[με/mm]\" real,\"OuterLocalStiffnessEstimate4I[με/mm]\" real,\"InnerLocalStiffnessEstimate4R[με/mm]\" real,\"InnerLocalStiffnessEstimate4I[με/mm]\" real, "
                    + "\"LocationEstimate5[m]\" real, \"OuterLocalStiffnessEstimate5R[με/mm]\" real,\"OuterLocalStiffnessEstimate5I[με/mm]\" real,\"InnerLocalStiffnessEstimate5R[με/mm]\" real,\"InnerLocalStiffnessEstimate5I[με/mm]\" real, "
                    + "\"OuterStrain1A[με*s]\" real,\"OuterStrain1P[rad]\" real," + "\"InnerStrain1A[με*s]\" real,\"InnerStrain1P[rad]\" real,"
                    + "\"OuterStrain2A[με*s]\" real,\"OuterStrain2P[rad]\" real," + "\"InnerStrain2A[με*s]\" real,\"InnerStrain2P[rad]\" real,"
                    + "\"OuterStrain3A[με*s]\" real,\"OuterStrain3P[rad]\" real," + "\"InnerStrain3A[με*s]\" real,\"InnerStrain3P[rad]\" real,"
                    + "\"OuterStrain4A[με*s]\" real, \"OuterStrain4P[rad]\" real, " + "\"InnerStrain4A[με*s]\" real, \"InnerStrain4P[rad]\" real "
                    + ")");
            outst.close();

            // LASection は 0-based.
            EdefenseKasinInfo[] kasins = new EdefenseKasinInfo[]{
                EdefenseInfo.D01Q01, EdefenseInfo.D01Q09, EdefenseInfo.D01Q11, EdefenseInfo.D02Q05,
                EdefenseInfo.D03Q01, EdefenseInfo.D03Q09};
            BeamEnd beamEndLeft[] = {BeamEnd3A, BeamEnd4A,  BeamEndA3,  BeamEndB3};
            for (EdefenseKasinInfo kasin : kasins) {
                for (jun.res23.ed.ed14分析UF.A403CreateLOStrainDistribution.BeamEnd beamend : beamEndLeft) {
                    main(outcon, kasin, beamend, 0, 1, 1);
                }
            }

            BeamEnd beamEndRight[] = {BeamEnd3B, BeamEnd4B,BeamEndA4,  BeamEndB4};
            for (EdefenseKasinInfo kasin : kasins) {
                for (jun.res23.ed.ed14分析UF.A403CreateLOStrainDistribution.BeamEnd beamend : beamEndRight) {
                    main(outcon, kasin, beamend, 3, 4 , 1);
                }
            }

//            main(outcon, EdefenseInfo.D01Q09, BeamEnd3A, 1,2,1);
//            main(outcon, EdefenseInfo.D01Q11, BeamEnd3A, 1,2,1);
//            main(outcon, EdefenseInfo.D02Q05, BeamEnd3A, 1,2,1);
            outcon.close();
        } catch (SQLException ex) {
            Logger.getLogger(A403CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static BeamEnd BeamEnd3A = new BeamEnd("3A", EdefenseInfo.Beam3, 3.1,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_3_A3_O_1, StrainGaugeInfo.GSLO_3_A3_O_2, StrainGaugeInfo.GSLO_3_A3_O_3, StrainGaugeInfo.GSLO_3_A3_O_4
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_3_A3_I_1, StrainGaugeInfo.GSLO_3_A3_I_2, StrainGaugeInfo.GSLO_3_A3_I_3, StrainGaugeInfo.GSLO_3_A3_I_4
            }, new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}
    );

    public static BeamEnd BeamEnd3B = new BeamEnd("3B", EdefenseInfo.Beam3, -3.1,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_3_B3_O_4, StrainGaugeInfo.GSLO_3_B3_O_3, StrainGaugeInfo.GSLO_3_B3_O_2, StrainGaugeInfo.GSLO_3_B3_O_1
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_3_B3_I_4, StrainGaugeInfo.GSLO_3_B3_I_3, StrainGaugeInfo.GSLO_3_B3_I_2, StrainGaugeInfo.GSLO_3_B3_I_1
            }, new double[]{3.1 - (0.125 + 0.070 + 0.075), 3.1 - (0.125 + 0.070 + 0.050), 3.1 - (0.125 + 0.070 + 0.025), 3.1 - (0.125 + 0.070 + 0.000)}
    );

    public static BeamEnd BeamEnd4A = new BeamEnd("4A", EdefenseInfo.Beam4, 3.1,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_4_A4_O_1, StrainGaugeInfo.GSLO_4_A4_O_2, StrainGaugeInfo.GSLO_4_A4_O_3, StrainGaugeInfo.GSLO_4_A4_O_4
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_4_A4_I_1, StrainGaugeInfo.GSLO_4_A4_I_2, StrainGaugeInfo.GSLO_4_A4_I_3, StrainGaugeInfo.GSLO_4_A4_I_4
            }, new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.07 + 0.075}
    );

    public static BeamEnd BeamEnd4B = new BeamEnd("4B", EdefenseInfo.Beam4, -3.1,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_4_B4_O_4, StrainGaugeInfo.GSLO_4_B4_O_3, StrainGaugeInfo.GSLO_4_B4_O_2, StrainGaugeInfo.GSLO_4_B4_O_1
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_4_B4_I_4, StrainGaugeInfo.GSLO_4_B4_I_3, StrainGaugeInfo.GSLO_4_B4_I_2, StrainGaugeInfo.GSLO_4_B4_I_1
            }, new double[]{3.1 - (0.125 + 0.070 + 0.075), 3.1 - (0.125 + 0.070 + 0.050), 3.1 - (0.125 + 0.070 + 0.025), 3.1 - (0.125 + 0.070 + 0.0)}
    );

    public static BeamEnd BeamEndA3 = new BeamEnd("A3", EdefenseInfo.BeamA, 4.0,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_A_A3_O_1, StrainGaugeInfo.GSLO_A_A3_O_2, StrainGaugeInfo.GSLO_A_A3_O_3, StrainGaugeInfo.GSLO_A_A3_O_4
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_A_A3_I_1, StrainGaugeInfo.GSLO_A_A3_I_2, StrainGaugeInfo.GSLO_A_A3_I_3, StrainGaugeInfo.GSLO_A_A3_I_4
            }, new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}
    );

    public static BeamEnd BeamEndA4 = new BeamEnd("A4", EdefenseInfo.BeamA, -4.0,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_A_A4_O_4, StrainGaugeInfo.GSLO_A_A4_O_3, StrainGaugeInfo.GSLO_A_A4_O_2, StrainGaugeInfo.GSLO_A_A4_O_1
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_A_A4_I_4, StrainGaugeInfo.GSLO_A_A4_I_3, StrainGaugeInfo.GSLO_A_A4_I_2, StrainGaugeInfo.GSLO_A_A4_I_1
            }, new double[]{4.0 - (0.175 + 0.070 + 0.075), 4.0 - (0.175 + 0.070 + 0.050), 4.0 - (0.175 + 0.070 + 0.025), 4.0 - (0.175 + 0.070)}
    );

    public static BeamEnd BeamEndB3 = new BeamEnd("B3", EdefenseInfo.BeamB, 4.0,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_B_B3_O_1, StrainGaugeInfo.GSLO_B_B3_O_2, StrainGaugeInfo.GSLO_B_B3_O_3, StrainGaugeInfo.GSLO_B_B3_O_4
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_B_B3_I_1, StrainGaugeInfo.GSLO_B_B3_I_2, StrainGaugeInfo.GSLO_B_B3_I_3, StrainGaugeInfo.GSLO_B_B3_I_4
            }, new double[]{0.125 + 0.045, 0.125 + 0.045 + 0.025, 0.125 + 0.045 + 0.050, 0.125 + 0.045 + 0.075}
    );

    public static BeamEnd BeamEndB4 = new BeamEnd("B4", EdefenseInfo.BeamB, -4.0,
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_B_B4_O_4, StrainGaugeInfo.GSLO_B_B4_O_3, StrainGaugeInfo.GSLO_B_B4_O_2, StrainGaugeInfo.GSLO_B_B4_O_1
            },
            new StrainGaugeInfo[]{
                StrainGaugeInfo.GSLO_B_B4_I_4, StrainGaugeInfo.GSLO_B_B4_I_3, StrainGaugeInfo.GSLO_B_B4_I_2, StrainGaugeInfo.GSLO_B_B4_I_1
            }, new double[]{4.0 - (0.125 + 0.045 + 0.075), 4.0 - (0.125 + 0.045 + 0.050), 4.0 - (0.125 + 0.045 + 0.025), 4.0 - (0.125 + 0.045)}
    );

    public static class BeamEnd {

//        private final int edge;,EdefenseInfo.BeamB
        private final String name;
        private final BeamInfo beam;
        private final StrainGaugeInfo[] inner, outer;
        private final double[] locations;
        private final double length;

        /**
         *
         * @param name
         * @param outer
         * @param inner
         * @param gaugeLocations 梁端からの距離（柱心からの距離）
         * @parma beamlength 梁の全長および方向。　柱心間の距離。　また、0端側は正値、1端側は負値とする。
         */
        public BeamEnd(String name, BeamInfo beam, double beamlength, StrainGaugeInfo[] outer,
                StrainGaugeInfo[] inner, double[] gaugeLocations) {
//            this.edge = edge;
            this.name = name;
            this.beam = beam;
            this.inner = inner;
            this.outer = outer;
            this.locations = gaugeLocations;
            this.length = beamlength;
        }

        public StrainGaugeInfo[] getInnerGauges() {
            return inner;
        }

        public StrainGaugeInfo[] getOuterGauges() {
            return outer;
        }

    }

    public static void main(Connection con, EdefenseKasinInfo kasin, BeamEnd beamEnd, int LASection1, int LASection2, int LASectionFindMax) {
        logger.log(Level.INFO, "beamEnd =" + beamEnd.name + " " + kasin.getName());
        Color[] co = new Color[]{
            new Color(1f, 0, 0),
            new Color(0, 0.6f, 0),
            new Color(0f, 0, 1f),
            new Color(1f, 0, 1f),
            new Color(0, 0.8f, 0.8f),
            new Color(0.6f, 0.3f, 0)};

        try {

            XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);
            re.setDrawSeriesLineAsPath(true);
            re.setAutoPopulateSeriesPaint(false);
            re.setDefaultPaint(Color.BLACK);

            //      EdefenseKasinInfo kasin = EdefenseInfo.D01Q01;
            XYSeries[] ss = createXYSeries(con, beamEnd, kasin, LASection1, LASection2, LASectionFindMax); // random
            XYSeriesCollection c = new XYSeriesCollection();
            //svg(ss,kasin);

            c.addSeries(ss[0]); // outer
            c.addSeries(ss[1]); // inner
            c.addSeries(ss[2]); // outer etimate
            c.addSeries(ss[3]); // inner etimate            
            //   re.setSeriesPaint(c.getSeriesCount() - 3, co[0]);
//            re.setSeriesPaint(c.getSeriesCount() - 2, co[0]);
//            re.setSeriesPaint(c.getSeriesCount() - 1, co[0]);
            re.setSeriesShapesVisible(c.getSeriesCount() - 2, true);
            re.setSeriesShapesVisible(c.getSeriesCount() - 1, true);
            re.setSeriesStroke(c.getSeriesCount() - 4, JunShapes.MEDIUM_LINE);
            re.setSeriesStroke(c.getSeriesCount() - 3, JunShapes.THIN_LINE);
            re.setSeriesStroke(c.getSeriesCount() - 2, JunShapes.MEDIUM_DASHED);
            re.setSeriesStroke(c.getSeriesCount() - 1, JunShapes.THIN_DASHED);

            JFreeChart chart = new JunXYChartCreator2().setDataset(c)
                    .setRenderer(re)
                    .create();
            XYPlot plot = chart.getXYPlot();
            NumberAxis ra = (NumberAxis) plot.getRangeAxis();
            ra.setAutoRangeIncludesZero(false);
            ra.setInverted(true);
            ra.setLabel("Strain [με] (tension)");
            NumberAxis da = (NumberAxis) plot.getDomainAxis();
            da.setLabel("");
            da.setAutoRangeIncludesZero(false);
            double flangeEdgeLocation;
            if (beamEnd.name.endsWith("B")) {
                flangeEdgeLocation = -beamEnd.length - 0.125 - 0.025;
            } else if (beamEnd.name.equals("3A")) {
                flangeEdgeLocation = 0.125;
            } else if (beamEnd.name.equals("3B")) {
                flangeEdgeLocation = -beamEnd.length - 0.125;
            } else {
                flangeEdgeLocation = 0.175 + 0.025;
            }

            ValueMarker marker = new ValueMarker(flangeEdgeLocation); // フランジ端部。ダイアフラムあるいは柱フェイスとの溶接位置
            plot.addDomainMarker(marker);

            if (svgdir == null) {
                JunChartUtil.show(new Object() {
                }.getClass().getEnclosingClass().getName(), chart);

            } else {
                try {
                    if (!Files.exists(svgdir)) {
                        Files.createDirectory(svgdir);
                    }
                    Path svgdir2 = svgdir.resolve(beamEnd.name);
                    if (!Files.exists(svgdir2)) {
                        Files.createDirectory(svgdir2);
                    }

                    Path svgfile = svgdir2.resolve(kasin.getName() + "_" + beamEnd.name + ".svg");
                    JunChartUtil.svg(svgfile, 300, 200, chart);
                } catch (IOException ex) {
                    Logger.getLogger(A403CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(A403CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param outcon
     * @param edgeName
     * @param kasin
     * @param beam
     * @param outers
     * @param inners
     * @param LOlocations 梁端（柱心）からの距離が入ったものが入ってくる。　ただし、現状のプログラムは、梁の
     * 0端（柱心）からの距離を前提としている。表示だけならばまあ大きな問題はないが。。。
     * @param LASection1
     * @param LASection2
     * @param kasin
     * @param beam
     * @param outers
     * @param inners
     * @param LASectionFindMax
     * @return
     * @throws SQLException
     */
    public static XYSeries[] createXYSeries(Connection outcon, BeamEnd beamEnd, EdefenseKasinInfo kasin, int LASection1, int LASection2, int LASectionFindMax) throws SQLException {
//        BeamInfo beam = EdefenseInfo.Beam3;
        //String edgeName=beamEnd.
//         BeamInfo beam, StrainGaugeInfo[] outers, StrainGaugeInfo[] inners, double[] LOlocations,
        BeamInfo beam = beamEnd.beam;

        BeamSectionInfo maxFindSecetion = beam.getBeamSections()[LASectionFindMax];
        BeamSectionInfo beamSection2 = beam.getBeamSections()[LASection1];
        BeamSectionInfo beamSection3 = beam.getBeamSections()[LASection2];

        Statement st = outcon.createStatement();

        st.executeUpdate("delete from \"" + outputTableName + "\" where TESTNAME='" + kasin.getTestName() + "' and EDGENAME='" + beamEnd.name + "'");

        String freqPeak;
        Complex momentSection2, axialSection2;
        Complex momentSection3, axialSection3;
        XYSeries estimateInner = new XYSeries(kasin.getTestName() + "INNER");
        XYSeries estimateOuter = new XYSeries(kasin.getTestName() + "OUTER");

        // この加振、この方向のピーク振動数と変位（実際は加速度）を取得する。A300から取得している。（A100でも同じとは思うが。）
        // section2 と section3 の最大変異時刻時値を取得
        ResultSet rs = st.executeQuery("select \"AxialA[N*s]\",\"AxialP[rad]\", \"MomentXA[Nm*s]\",\"MomentXP[rad]\" ,\"Freq[Hz]\",\"StoryDriftA[gal*s]\", \"StoryDriftP[rad]\" from \"" + A300SectionNM + "\" where SECTION='" + beamSection2.getName() + "' "
                + " and TESTNAME='" + kasin.getTestName() + "'");
        rs.next();

        momentSection2 = ComplexUtils.polar2Complex(rs.getDouble(3), rs.getDouble(4)); //Nm*s
        axialSection2 = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2)); // N*s
        freqPeak = rs.getString(5);
        Complex storyDrift = ComplexUtils.polar2Complex(rs.getDouble(6), rs.getDouble(7));

        rs = st.executeQuery("select \"AxialA[N*s]\",\"AxialP[rad]\", \"MomentXA[Nm*s]\",\"MomentXP[rad]\"  from \"" + A300SectionNM + "\" where SECTION='" + beamSection3.getName() + "' "
                + " and TESTNAME='" + kasin.getTestName() + "'");

        rs.next();

        momentSection3 = ComplexUtils.polar2Complex(rs.getDouble(3), rs.getDouble(4));// Nm*s
        axialSection3 = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2)); // N*s

        rs.close();

        // ここでは推定ひずみを計算している。
        // S2と S3の曲げモーメントおよび軸力を計算し、それを外装して、 LO1 断面での曲げモーメントと軸力を算出。これらのひずみを足し合わせている。
        double locationSection2 = beam.getLocation(LASection1); //m
        double locationSection3 = beam.getLocation(LASection2); //m

//            double locationSectionLO1 = 0.070;
        Complex shearForce = (momentSection3.subtract(momentSection2)).divide(locationSection3 - locationSection2); //N*s
        Complex axialSlope = (axialSection3.subtract(axialSection2)).divide(locationSection3 - locationSection2); // N*s/m

        //     double momentLO1 = momentSection2 - shearForce * (locationSection2 - locationSectionLO1);
//            double axialLO1 = axialSection2 - axialSlope * (locationSection2 - locationSectionLO1);
//            double bendingStrainL01 = 1e9 * momentLO1 / (beamSection2.getE() * beamSection2.getZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με
//            double axialStrainLO1 = 1e9 * axialLO1 / (beamSection2.getE() * beamSection2.getA()); // [kN]/[N/m2]/[m] =kε → ×1e9 = με
        double zs[] = {beamEnd.locations[0] - 0.010, beamEnd.locations[0], beamEnd.locations[1], beamEnd.locations[2], beamEnd.locations[3], beamEnd.locations[3] + 0.010};

        Complex estimateTotalInner[] = new Complex[zs.length];
        Complex estimateTotalOuter[] = new Complex[zs.length];

        for (int iz = 0; iz < zs.length; iz++) {
            double z0 = zs[iz];
            Complex momentAtZ0 = momentSection2.subtract(shearForce.multiply(locationSection2 - z0)); // Nm*s 
            Complex axialAtZ0 = axialSection2.subtract(axialSlope.multiply(locationSection2 - z0)); // N*s
            Complex strainInnerAtZ0 = momentAtZ0.divide(beamSection2.getE() * beamSection2.getInnerZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
            Complex strainOuterAtZ0 = momentAtZ0.divide(beamSection2.getE() * beamSection2.getOuterZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
            Complex axialStrainAtZ0 = axialAtZ0.divide(beamSection2.getE() * beamSection2.getA()).multiply(1e6); // [N*s]/[N/m2]/[m] =ε*s → ×1e6 = με*s
            Complex totalInnerStrainZ0 = strainInnerAtZ0.add(axialStrainAtZ0);
            Complex totalOuterStrainZ0 = strainOuterAtZ0.add(axialStrainAtZ0);
            Complex totalInnerStiffnessZ0 = totalInnerStrainZ0.divide(storyDrift);
            Complex totalOuterStiffnessZ0 = totalOuterStrainZ0.divide(storyDrift);

            estimateInner.add((z0) * 1000, totalInnerStiffnessZ0.getReal()); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。
            estimateOuter.add((z0) * 1000, totalOuterStiffnessZ0.getReal()); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。

            estimateTotalInner[iz] = totalInnerStiffnessZ0;
            estimateTotalOuter[iz] = totalOuterStiffnessZ0;
        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        String gaugeNames[] = {"b03/05", "b03/06", "b03/07", "b03/08"};
        XYSeries outer = new XYSeries(kasin.getTestName() + "O");

        Complex outerStrain[] = new Complex[4];
        Complex innerStrain[] = new Complex[4];
        Complex innerStiffness[] = new Complex[4];
        Complex outerStiffness[] = new Complex[4];

        String inputDburl = "jdbc:h2:file:/" + inputDatabaseDir.resolve(kasin.getTestName() + "q");
        Connection incon = DriverManager.getConnection(inputDburl, "junapp", "");
        Statement inst = incon.createStatement();

        for (int i = beamEnd.outer.length - 1; i >= 0; i--) { // これなんで外側から書いてる？わざわざ逆にする意味がわからない。。。2023/07/14
            String gaugeName = beamEnd.outer[i].getShortName();

            String sql = "select \"Amp[με*s]\",\"Phase[rad]\" from \"" + fourierSchema + "\".\"" + gaugeName + "\" where \"Freq[Hz]\"=" + freqPeak;

            rs = inst.executeQuery(sql);
            rs.next();
            Complex strain = outerStrain[i] = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2));
//            double strain = outerStrain[i] = rs.getDouble(2);

            Complex localStiffness = strain.divide(storyDrift).multiply(-1);// マイナスにして引張を正としている。 

            outer.add(1000 * (beamEnd.locations[i]), localStiffness.getReal()/*strain.getReal()*/); //位置は 柱心からの距離のまま、 mm 表示としている。

            outerStiffness[i] = localStiffness;
        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        gaugeName = new String[]{"b03/01", "b03/02", "b03/03", "b03/04"};
        XYSeries inner = new XYSeries(kasin.getTestName() + "I");

        for (int i = beamEnd.inner.length - 1; i >= 0; i--) {
            String gaugeName = beamEnd.inner[i].getShortName();

            String sql = "select \"Amp[με*s]\",\"Phase[rad]\" from \"" + fourierSchema + "\".\"" + gaugeName + "\" where \"Freq[Hz]\"=" + freqPeak;

            rs = inst.executeQuery(sql);
            rs.next();
            Complex strain = innerStrain[i] = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2));
//            double strain = outerStrain[i] = rs.getDouble(2);

            Complex localStiffness = strain.divide(storyDrift);

            inner.add(1000 * (beamEnd.locations[i]), -localStiffness.getReal()/*strain.getReal()*/); // マイナスにして引張を正としている。 位置は柱心からのまま、、 mm 表示としている。

            innerStiffness[i] = localStiffness;
        }
        st.executeUpdate("insert into \"" + outputTableName + "\" values ("
                + "'" + kasin.getTestName() + "','" + kasin.getWaveName() + "','" + beamEnd.name + "'," + freqPeak + ","
                + storyDrift.abs() + "," + storyDrift.getArgument() + ","
                + beamEnd.locations[0] + "," + outerStiffness[0].getReal() + "," + outerStiffness[0].getImaginary() + "," + innerStiffness[0].getReal() + "," + innerStiffness[0].getImaginary() + ","
                + beamEnd.locations[1] + "," + outerStiffness[1].getReal() + "," + outerStiffness[1].getImaginary() + "," + innerStiffness[1].getReal() + "," + innerStiffness[1].getImaginary() + ","
                + beamEnd.locations[2] + "," + outerStiffness[2].getReal() + "," + outerStiffness[2].getImaginary() + "," + innerStiffness[2].getReal() + "," + innerStiffness[2].getImaginary() + ","
                + beamEnd.locations[3] + "," + outerStiffness[3].getReal() + "," + outerStiffness[3].getImaginary() + "," + innerStiffness[3].getReal() + "," + innerStiffness[3].getImaginary() + ","
                + locationSection2 + "," + momentSection2.abs() + "," + momentSection2.getArgument() + "," + axialSection2.abs() + "," + axialSection2.getArgument() + ","
                + locationSection3 + "," + momentSection3.abs() + "," + momentSection3.getArgument() + "," + axialSection3.abs() + "," + axialSection3.getArgument() + ","
                + shearForce.abs() + "," + shearForce.getArgument() + ","
                + zs[0] + "," + estimateTotalOuter[0].getReal() + "," + estimateTotalOuter[0].getImaginary() + "," + estimateTotalInner[0].getReal() + "," + estimateTotalInner[0].getImaginary() + ","
                + zs[1] + "," + estimateTotalOuter[1].getReal() + "," + estimateTotalOuter[1].getImaginary() + "," + estimateTotalInner[1].getReal() + "," + estimateTotalInner[1].getImaginary() + ","
                + zs[2] + "," + estimateTotalOuter[2].getReal() + "," + estimateTotalOuter[2].getImaginary() + "," + estimateTotalInner[2].getReal() + "," + estimateTotalInner[2].getImaginary() + ","
                + zs[3] + "," + estimateTotalOuter[3].getReal() + "," + estimateTotalOuter[3].getImaginary() + "," + estimateTotalInner[3].getReal() + "," + estimateTotalInner[3].getImaginary() + ","
                + zs[4] + "," + estimateTotalOuter[4].getReal() + "," + estimateTotalOuter[4].getImaginary() + "," + estimateTotalInner[4].getReal() + "," + estimateTotalInner[4].getImaginary() + ","
                + zs[5] + "," + estimateTotalOuter[5].getReal() + "," + estimateTotalOuter[5].getImaginary() + "," + estimateTotalInner[5].getReal() + "," + estimateTotalInner[5].getImaginary() + ","
                + outerStrain[0].abs() + "," + outerStrain[0].getArgument() + "," + innerStrain[0].abs() + "," + innerStrain[0].getArgument() + ","
                + outerStrain[1].abs() + "," + outerStrain[1].getArgument() + "," + innerStrain[1].abs() + "," + innerStrain[1].getArgument() + ","
                + outerStrain[2].abs() + "," + outerStrain[2].getArgument() + "," + innerStrain[2].abs() + "," + innerStrain[2].getArgument() + ","
                + outerStrain[3].abs() + "," + outerStrain[3].getArgument() + "," + innerStrain[3].abs() + "," + innerStrain[3].getArgument()
                + ")"
        );

        st.close();
        incon.close();
        return new XYSeries[]{outer, inner, estimateOuter, estimateInner};
    }

}

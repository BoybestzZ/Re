/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.awt.Color;
import java.io.IOException;
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
import jun.util.JunShapes;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ed06T265→ed14A400
 *
 * @author jun
 */
public class A400CreateLOStrainDistribution {

    private static final Logger logger = Logger.getLogger(A400CreateLOStrainDistribution.class.getName());
    public static Path inputDatabaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ");
    public static String fourierSchema = "R155FourierU"; //"R152FourierS";
    public static String driftTable = "R175StoryDriftU";
    private static final String outputDburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/ed14";
    public static String A300SectionNM = "A300SectionNM";
    private static final String outputTableName = "A400LOStrainDistribution";

    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/A400LOStrainDistribution");

    public static void main(String[] args) {

        try {
            logger.log(Level.INFO, "Opening database");
            Connection outcon = DriverManager.getConnection(outputDburl, "junapp", "");
            logger.log(Level.INFO, "Opened.");
            Statement outst = outcon.createStatement();
            outst.executeUpdate("drop table if exists \"" + outputTableName + "\" ");
            outst.executeUpdate("create table if not exists \"" + outputTableName + "\" (TESTNAME varchar,WAVENAME varchar, EDGENAME varchar, "
                    + "\"Freq[Hz]\" real,\"Inter2StoryDispA[mm*s]\" real,\"Inter2StoryDispP[rad]\" real, "
                    + "\"MS1A[Nm*s]\" real,\"MS1P[rad]\" real,"
                    + "\"MS2A[Nm*s]\" real,\"MS2P[rad]\" real,"
                    + " \"QA[N*s]\" real, \"QP[rad]\" real,"
                    + "\"LocationS1[m]\" real, \"LocationS2[m]\" real ,"
                    + "\"BendingStrainZ0A[με]\" real,\"BendingStrainZ0P[rad]\" real,"
                    + "\"AxialStrainZ0A[με]\" real,\"AxialStrainZ0P[rad]\" real,"
                    + "\"TotalStrainZ0A[με]\" real,\"TotalStrainZ0P[rad]\" real,"
                    + "\"BendingStrainZ1A[με]\" real,\"BendingStrainZ1P[rad]\" real,"
                    + "\"AxialStrainZ1A[με]\" real, \"AxialStrainZ1P[rad]\" real, "
                    + "\"TotalStrainZ1A[με]\" real,\"TotalStrainZ1P[rad]\" real,"
                    + "\"LocationZ1[m]\" real,\"LocationZ2[m]\" real, "
                    + "\"OuterStrain1A[με*s]\" real,\"OuterStrain1P[rad]\" real,"
                    + "\"OuterStrain2A[με*s]\" real,\"OuterStrain2P[rad]\" real,"
                    + "\"OuterStrain3A[με*s]\" real,\"OuterStrain3P[rad]\" real,"
                    + "\"OuterStrain4A[με*s]\" real, \"OuterStrain4P[rad]\" real, "
                    + "\"InnerStrain1A[με*s]\" real,\"InnerStrain1P[rad]\" real,"
                    + "\"InnerStrain2A[με*s]\" real,\"InnerStrain2P[rad]\" real,"
                    + "\"InnerStrain3A[με*s]\" real,\"InnerStrain3P[rad]\" real,"
                    + "\"InnerStrain4A[με*s]\" real, \"InnerStrain4P[rad]\" real, "
                    + "\"LocationStrain1[m]\" real,\"LocationStrain2[m]\" real,\"LocationStrain3[m]\" real,\"LocationStrain4[m]\" real"
                    + ")");
            outst.close();

            // LASection は 0-based.
            main(outcon, EdefenseInfo.D01Q01, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                    new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
            main(outcon, EdefenseInfo.D01Q09, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                    new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
            main(outcon, EdefenseInfo.D01Q11, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                    new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
            main(outcon, EdefenseInfo.D02Q05, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                    new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);

            String[] outerGauge3B = new String[]{"f02/08", "f02/07", "f02/06", "f02/05"};
            String[] innerGauge3B = new String[]{"f02/04", "f02/03", "f02/02", "f02/01"};
            double[] gaugeLocations3B = new double[]{3.10 - (0.125 + 0.070) - 0.075, 3.10 - (0.125 + 0.070) - 0.050, 3.10 - (0.125 + 0.070) - 0.025, 3.10 - (0.125 + 0.070) - 0.0};

            main(outcon, EdefenseInfo.D01Q01, "3B", EdefenseInfo.Beam3, outerGauge3B, innerGauge3B,
                    gaugeLocations3B, 1, 2, 1);
            main(outcon, EdefenseInfo.D01Q09, "3B", EdefenseInfo.Beam3, outerGauge3B, innerGauge3B,
                    gaugeLocations3B, 1, 2, 1);
            main(outcon, EdefenseInfo.D01Q11, "3B", EdefenseInfo.Beam3, outerGauge3B, innerGauge3B,
                    gaugeLocations3B, 1, 2, 1);
            main(outcon,
                    EdefenseInfo.D02Q05, "3B", EdefenseInfo.Beam3, outerGauge3B, innerGauge3B,
                    gaugeLocations3B, 1, 2, 1);

            outcon.close();
        } catch (SQLException ex) {
            Logger.getLogger(A400CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(Connection con, EdefenseKasinInfo kasin, String edgeName, BeamInfo beam,
            String[] outerGauges, String[] innerGauges, double[] LOlocations, int LASection1, int LASection2, int LASectionFindMax) {
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
            XYSeries[] ss = createXYSeries(con, edgeName, kasin, beam, outerGauges, innerGauges, LOlocations, LASection1, LASection2, LASectionFindMax); // random
            XYSeriesCollection c = new XYSeriesCollection();
            //svg(ss,kasin);

            c.addSeries(ss[0]); // outer
            c.addSeries(ss[1]); // inner
            c.addSeries(ss[2]); // outer etimate
            c.addSeries(ss[3]); // inner etimate            
            //   re.setSeriesPaint(c.getSeriesCount() - 3, co[0]);
//            re.setSeriesPaint(c.getSeriesCount() - 2, co[0]);
//            re.setSeriesPaint(c.getSeriesCount() - 1, co[0]);
            re.setSeriesShapesVisible(c.getSeriesCount() - 2, Boolean.FALSE);
            re.setSeriesShapesVisible(c.getSeriesCount() - 1, Boolean.FALSE);
            re.setSeriesStroke(c.getSeriesCount() - 4, JunShapes.MEDIUM_LINE);
            re.setSeriesStroke(c.getSeriesCount() - 3, JunShapes.THIN_LINE);
            re.setSeriesStroke(c.getSeriesCount() - 2, JunShapes.MEDIUM_DASHED);
            re.setSeriesStroke(c.getSeriesCount() - 1, JunShapes.THIN_DASHED);

            JFreeChart chart = new JunXYChartCreator2().setDataset(c)
                    .setRenderer(re)
                    .create();
            NumberAxis ra = (NumberAxis) chart.getXYPlot().getRangeAxis();
            ra.setAutoRangeIncludesZero(false);
            ra.setInverted(true);
            ra.setLabel("Strain [με] (tension)");
            NumberAxis da = (NumberAxis) chart.getXYPlot().getDomainAxis();
            da.setLabel("");
            da.setAutoRangeIncludesZero(false);

            if (svgdir == null) {
                JunChartUtil.show(new Object() {
                }.getClass().getEnclosingClass().getName(), chart);

            } else {
                try {
                    Path svgfile = svgdir.resolve(kasin.getName() + "_" + edgeName + ".svg");
                    JunChartUtil.svg(svgfile, 300, 200, chart);
                } catch (IOException ex) {
                    Logger.getLogger(A400CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(A400CreateLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static XYSeries[] createXYSeries(Connection outcon, String edgeName, EdefenseKasinInfo kasin, BeamInfo beam, String[] outers, String[] inners, double[] LOlocations, int LASection1, int LASection2, int LASectionFindMax) throws SQLException {
//        BeamInfo beam = EdefenseInfo.Beam3;

        BeamSectionInfo maxFindSecetion = beam.getBeamSections()[LASectionFindMax];
        BeamSectionInfo beamSection2 = beam.getBeamSections()[LASection1];
        BeamSectionInfo beamSection3 = beam.getBeamSections()[LASection2];

        Statement st = outcon.createStatement();

        st.executeUpdate("delete from \"" + outputTableName + "\" where TESTNAME='" + kasin.getTestName() + "' and EDGENAME='" + edgeName + "'");

        String freqPeak;
        Complex momentSection2, axialSection2;
        Complex momentSection3, axialSection3;
        XYSeries estimateInner = new XYSeries(kasin.getTestName() + "INNER");
        XYSeries estimateOuter = new XYSeries(kasin.getTestName() + "OUTER");

        // この加振、この方向のピーク振動数と変位（実際は加速度）を取得する。A300から取得している。（A100でも同じとは思うが。）
        // section2 と section3 の最大変異時刻時値を取得
        ResultSet rs = st.executeQuery("select \"AxialA[N*s]\",\"AxialP[rad]\", \"MomentXA[Nm*s]\",\"MomentXP[rad]\" ,\"Freq[Hz]\",\"StoryDriftA[gal*s]\", \"StoryDriftP[rad]\" from \"A300SectionNM\" where SECTION='" + beamSection2.getName() + "' "
                + " and TESTNAME='" + kasin.getTestName() + "'");
        rs.next();

        momentSection2 = ComplexUtils.polar2Complex(rs.getDouble(3), rs.getDouble(4)); //Nm*s
        axialSection2 = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2)); // N*s
        freqPeak = rs.getString(5);
        Complex storyDrift = ComplexUtils.polar2Complex(rs.getDouble(6), rs.getDouble(7));

        rs = st.executeQuery("select \"AxialA[N*s]\",\"AxialP[rad]\", \"MomentXA[Nm*s]\",\"MomentXP[rad]\"  from \"A300SectionNM\" where SECTION='" + beamSection3.getName() + "' "
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
        // z=0での値計算
        double z0 = LOlocations[0] - 0.010;
        Complex momentAtZ0 = momentSection2.subtract(shearForce.multiply(locationSection2 - z0)); // Nm*s 
        Complex axialAtZ0 = axialSection2.subtract(axialSlope.multiply(locationSection2 - z0)); // N*s
        Complex strainInnerAtZ0 = momentAtZ0.divide(beamSection2.getE() * beamSection2.getInnerZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
        Complex strainOuterAtZ0 = momentAtZ0.divide(beamSection2.getE() * beamSection2.getOuterZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
        Complex axialStrainAtZ0 = axialAtZ0.divide(beamSection2.getE() * beamSection2.getA()).multiply(1e6); // [N*s]/[N/m2]/[m] =ε*s → ×1e6 = με*s

        // z=での値計算
        double z1 = LOlocations[3] + 0.010;
        Complex momentAtZ1 = momentSection2.subtract(shearForce.multiply(locationSection2 - z1));
        Complex axialAtZ1 = axialSection2.subtract(axialSlope.multiply(locationSection2 - z1));
        Complex strainInnerAtZ1 = momentAtZ1.divide(beamSection2.getE() * beamSection2.getInnerZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
        Complex strainOuterAtZ1 = momentAtZ1.divide(beamSection2.getE() * beamSection2.getOuterZx()).multiply(1e6); // [Nm*s]/[N/m2]/[m3] =ε*s → ×1e6 = με*s
        Complex axialStrainAtZ1 = axialAtZ1.divide(beamSection2.getE() * beamSection2.getA()).multiply(1e6); // [N*s]/[N/m2]/[m] =ε*s → ×1e6 = με*s
        Complex totalInnerStrainZ0 = strainInnerAtZ0.add(axialStrainAtZ0);
        Complex totalInnerStrainZ1 = strainInnerAtZ1.add(axialStrainAtZ1);
        Complex totalOuterStrainZ0 = strainOuterAtZ0.add(axialStrainAtZ0);
        Complex totalOuterStrainZ1 = strainOuterAtZ1.add(axialStrainAtZ1);

        Complex totalInnerStiffnessZ0 = totalInnerStrainZ0.divide(storyDrift);
        Complex totalInnerStiffnessZ1 = totalInnerStrainZ1.divide(storyDrift);
        Complex totalOuterStiffnessZ0 = totalOuterStrainZ0.divide(storyDrift);
        Complex totalOuterStiffnessZ1 = totalOuterStrainZ1.divide(storyDrift);

        estimateInner.add((z0 - 0.2) * 1000, totalInnerStiffnessZ0.getReal()); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。
        estimateInner.add((z1 - 0.2) * 1000, totalInnerStiffnessZ1.getReal()); // 位置は 175mm+25mm を引いて、ダイアフラムエッジからの距離にしている。

        estimateOuter.add((z0 - 0.2) * 1000, totalOuterStiffnessZ0.getReal()); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。
        estimateOuter.add((z1 - 0.2) * 1000, totalOuterStiffnessZ1.getReal()); // 位置は 175mm+25mm を引いて、ダイアフラムエッジからの距離にしている。

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        String gaugeNames[] = {"b03/05", "b03/06", "b03/07", "b03/08"};
        XYSeries outer = new XYSeries(kasin.getTestName() + "O");

        Complex outerStrain[] = new Complex[4];
        Complex innerStrain[] = new Complex[4];

        String inputDburl = "jdbc:h2:file:/" + inputDatabaseDir.resolve(kasin.getTestName() + "q");
        Connection incon = DriverManager.getConnection(inputDburl, "junapp", "");
        Statement inst = incon.createStatement();

        for (int i = outers.length - 1; i >= 0; i--) {
            String gaugeName = outers[i];

            String sql = "select \"Amp[με*s]\",\"Phase[rad]\" from \"" + fourierSchema + "\".\"" + gaugeName + "\" where \"Freq[Hz]\"=" + freqPeak;

            rs = inst.executeQuery(sql);
            rs.next();
            Complex strain = outerStrain[i] = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2));
//            double strain = outerStrain[i] = rs.getDouble(2);

            Complex localStiffness = strain.divide(storyDrift);

            outer.add(1000 * (LOlocations[i] - 0.2), -localStiffness.getReal()/*strain.getReal()*/); // マイナスにして引張を正としている。 位置は 175+25mm=200mm引いて、 mm 表示としている。

        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        gaugeName = new String[]{"b03/01", "b03/02", "b03/03", "b03/04"};
        XYSeries inner = new XYSeries(kasin.getTestName() + "I");

        for (int i = inners.length - 1; i >= 0; i--) {
            String gaugeName = inners[i];

            String sql = "select \"Amp[με*s]\",\"Phase[rad]\" from \"" + fourierSchema + "\".\"" + gaugeName + "\" where \"Freq[Hz]\"=" + freqPeak;

            rs = inst.executeQuery(sql);
            rs.next();
            Complex strain = innerStrain[i] = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2));
//            double strain = outerStrain[i] = rs.getDouble(2);

            Complex localStiffness = strain.divide(storyDrift);

            inner.add(1000 * (LOlocations[i] - 0.2), -localStiffness.getReal()/*strain.getReal()*/); // マイナスにして引張を正としている。 位置は 175+25mm=200mm引いて、 mm 表示としている。

        }
        st.executeUpdate("insert into \"" + outputTableName + "\" values ("
                + "'" + kasin.getTestName() + "','" + kasin.getWaveName() + "','" + edgeName + "'," + freqPeak + ","
                + storyDrift.abs() + "," + storyDrift.getArgument() + ","
                + momentSection2.abs() + "," + momentSection2.getArgument() + ","
                + momentSection3.abs() + "," + momentSection3.getArgument() + ","
                + shearForce.abs() + "," + shearForce.getArgument() + ","
                + locationSection2 + "," + locationSection3 + ","
                + strainInnerAtZ0.abs() + "," + strainInnerAtZ0.getArgument() + ","
                + axialStrainAtZ0.abs() + "," + axialStrainAtZ0.getArgument() + ","
                + totalInnerStrainZ0.abs() + "," + totalInnerStrainZ0.getArgument() + ","
                + strainInnerAtZ1.abs() + "," + strainInnerAtZ1.getArgument() + ","
                + axialStrainAtZ1.abs() + "," + axialStrainAtZ1.getArgument()+ ","
                + totalInnerStrainZ1.abs() + "," + totalInnerStrainZ1.getArgument() + ","
                + z0 + "," + z1 + ","
                + outerStrain[0].abs() + "," + outerStrain[0].getArgument() + ","
                + outerStrain[1].abs() + "," + outerStrain[1].getArgument() + ","
                + outerStrain[2].abs() + "," + outerStrain[2].getArgument() + ","
                + outerStrain[3].abs() + "," + outerStrain[3].getArgument() + ","
                + innerStrain[0].abs()+ "," + innerStrain[0].getArgument() + ","
                + innerStrain[1].abs() + "," + innerStrain[1].getArgument() + ","
                + innerStrain[2].abs() + "," + innerStrain[2].getArgument() + ","
                + innerStrain[3] .abs()+ "," + innerStrain[3].getArgument() + ","
                + LOlocations[0] + ","
                + LOlocations[1] + ","
                + LOlocations[2] + ","
                + LOlocations[3] + ")"
        );
         
        st.close();
        incon.close();
        return new XYSeries[]{outer, inner, estimateOuter, estimateInner};
    }

}

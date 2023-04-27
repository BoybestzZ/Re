/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * T260→T263
 *
 * @author jun
 */
public class T264GraphLOStrainDistribution {

    private static final Logger logger = Logger.getLogger(T264GraphLOStrainDistribution.class.getName());
    private static final String dburl = T231CreateTimeHistoryBeamColumnNM.outputDb;
    private static final String readSchema = T231CreateTimeHistoryBeamColumnNM.outputSchema;//"T231TimeHistoryBeamColumnNM";
    private static final String outputTableName = "T264LOStrainDistribution";

    private static final Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T264LocalStrainDistribution");

    public static void main(String[] args) {

        // LASection は 0-based.
        main(EdefenseInfo.D01Q01, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
        main(EdefenseInfo.D01Q09, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
        main(EdefenseInfo.D01Q11, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);
        main(EdefenseInfo.D02Q05, "3A", EdefenseInfo.Beam3, new String[]{"b03/05", "b03/06", "b03/07", "b03/08"}, new String[]{"b03/01", "b03/02", "b03/03", "b03/04"},
                new double[]{0.175 + 0.070, 0.175 + 0.070 + 0.025, 0.175 + 0.070 + 0.050, 0.175 + 0.070 + 0.075}, 1, 2, 1);


    }

    public static void main(EdefenseKasinInfo kasin, String edgeName, BeamInfo beam,
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
            XYSeries[] ss = createXYSeries(edgeName, kasin, beam, outerGauges, innerGauges, LOlocations, LASection1, LASection2, LASectionFindMax); // random
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
                    Logger.getLogger(T264GraphLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(T264GraphLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static XYSeries[] createXYSeries(String edgeName, EdefenseKasinInfo kasin, BeamInfo beam, String[] outers, String[] inners, double[] LOlocations, int LASection1, int LASection2, int LASectionFindMax) throws SQLException {
//        BeamInfo beam = EdefenseInfo.Beam3;

        BeamSectionInfo maxFindSecetion = beam.getBeamSections()[LASectionFindMax];
        BeamSectionInfo beamSection2 = beam.getBeamSections()[LASection1];
        BeamSectionInfo beamSection3 = beam.getBeamSections()[LASection2];

        logger.log(Level.INFO, "Opening " + kasin.getTestName());
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        logger.log(Level.INFO, "Opened.");
        Statement st = con.createStatement();

        st.executeUpdate("create table if not exists \"" + outputTableName + "\" (TESTNAME varchar,EDGENAME varchar, "
                + "\"TIME[s]\" real,\"MS1[kNm]\" real,\"MS2[kNm]\" real, \"Q[kN]\" real,"
                //                + " \"MLO1[kNm]\" real, \"NLO1[kN]\" real,"
                //                + "\"BendingStrainLO1[με]\" real,\"AxialStrainLO1[με]\" real, \"TotalStrainLO1[με]\" real,"
                + "\"BendingStrainZ0[με]\" real,\"AxialStrainZ0[με]\" real, \"TotalStrainZ0[με]\" real,"
                + "\"BendingStrainZ1[με]\" real,\"AxialStrainZ1[με]\" real, \"TotalStrainZ1[με]\" real"
                + ")");
        st.executeUpdate("delete from \"" + outputTableName + "\" where TESTNAME='" + kasin.getTestName() + "t' and EDGENAME='"+edgeName+"'");

        String timeMax;
        double momentSection2;
        double momentSection3;
        XYSeries estimateInner = new XYSeries(kasin.getTestName() + "INNER");
        XYSeries estimateOuter = new XYSeries(kasin.getTestName() + "OUTER");
        {

            // この加振においてモーメントが最大となる時刻を探している。
            ResultSet rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\", \"AxialForcePerTest[kN]\" from \"" + readSchema + "\".\"" + maxFindSecetion.getName() + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' order by 2  desc limit 1");
            rs.next();
            timeMax = rs.getString(1);

            // ここまでで最大時刻が確定。
            // section2 と section3 の最大変異時刻時値を取得
            rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\",\"AxialForcePerTest[kN]\" from \"" + readSchema + "\".\"" + beamSection2.getName() + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' and \"TimePerTest[s]\"=" + timeMax);
            rs.next();

            momentSection2 = (rs.getDouble(2));
            double axialSection2 = (rs.getDouble(3));

            rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\",\"AxialForcePerTest[kN]\" from \"" + readSchema + "\".\"" + beamSection3.getName() + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' and \"TimePerTest[s]\"=" + timeMax);
            rs.next();

            momentSection3 = (rs.getDouble(2));
            double axialSection3 = (rs.getDouble(3));

            rs.close();

            // ここでは推定ひずみを計算している。
            // S2と S3の曲げモーメントおよび軸力を計算し、それを外装して、 LO1 断面での曲げモーメントと軸力を算出。これらのひずみを足し合わせている。
            double locationSection2 = beam.getLocation(LASection1);
            double locationSection3 = beam.getLocation(LASection2);

//            double locationSectionLO1 = 0.070;
            double shearForce = (momentSection3 - momentSection2) / (locationSection3 - locationSection2);
            double axialSlope = (axialSection3 - axialSection2) / (locationSection3 - locationSection2);

            //     double momentLO1 = momentSection2 - shearForce * (locationSection2 - locationSectionLO1);
//            double axialLO1 = axialSection2 - axialSlope * (locationSection2 - locationSectionLO1);
//            double bendingStrainL01 = 1e9 * momentLO1 / (beamSection2.getE() * beamSection2.getZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με
//            double axialStrainLO1 = 1e9 * axialLO1 / (beamSection2.getE() * beamSection2.getA()); // [kN]/[N/m2]/[m] =kε → ×1e9 = με
            // z=0での値計算
            double z0 = LOlocations[0] - 0.010;
            double momentAtZ0 = momentSection2 - shearForce * (locationSection2 - z0);
            double axialAtZ0 = axialSection2 - axialSlope * (locationSection2 - z0);
            double strainInnerAtZ0 = 1e9 * momentAtZ0 / (beamSection2.getE() * beamSection2.getInnerZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με
            double strainOuterAtZ0 = 1e9 * momentAtZ0 / (beamSection2.getE() * beamSection2.getOuterZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με
            double axialStrainAtZ0 = 1e9 * axialAtZ0 / (beamSection2.getE() * beamSection2.getA()); // [kN]/[N/m2]/[m] =kε → ×1e9 = με

            // z=での値計算
            double z1 = LOlocations[3] + 0.010;
            double momentAtZ1 = momentSection2 - shearForce * (locationSection2 - z1);
            double axialAtZ1 = axialSection2 - axialSlope * (locationSection2 - z1);
            double strainInnerAtZ1 = 1e9 * momentAtZ1 / (beamSection2.getE() * beamSection2.getInnerZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με            double strainInnerAtZ1 = 1e9 * momentAtZ1 / (beamSection2.getE() * beamSection2.getInnerZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με
            double strainOuterAtZ1 = 1e9 * momentAtZ1 / (beamSection2.getE() * beamSection2.getOuterZx()); // [kNm]/[N/m2]/[m3] =kε → ×1e9 = με            
            double axialStrainAtZ1 = 1e9 * axialAtZ1 / (beamSection2.getE() * beamSection2.getA()); // [kN]/[N/m2]/[m] =kε → ×1e9 = με

            st.executeUpdate("insert into \"" + outputTableName + "\" values ("
                    + "'" + kasin.getTestName() + "t', '" + edgeName + "'," + timeMax + "," + momentSection2 + "," + momentSection3 + "," + shearForce + ","
                    + strainInnerAtZ0 + "," + axialStrainAtZ0 + "," + (strainInnerAtZ0 + axialStrainAtZ0) + ","
                    + strainInnerAtZ1 + "," + axialStrainAtZ1 + "," + (strainInnerAtZ1 + axialStrainAtZ1)
                    + ")");

            estimateInner.add((z0 - 0.2) * 1000, (strainInnerAtZ0 + axialStrainAtZ0)); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。
            estimateInner.add((z1 - 0.2) * 1000, (strainInnerAtZ1 + axialStrainAtZ1)); // 位置は 175mm+25mm を引いて、ダイアフラムエッジからの距離にしている。

            estimateOuter.add((z0 - 0.2) * 1000, (strainOuterAtZ0 + axialStrainAtZ0)); // 曲げモーメントは下側引張が正なので、下側フランジが引っ張りならば正の値がでる。
            estimateOuter.add((z1 - 0.2) * 1000, (strainOuterAtZ1 + axialStrainAtZ1)); // 位置は 175mm+25mm を引いて、ダイアフラムエッジからの距離にしている。

        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        String gaugeNames[] = {"b03/05", "b03/06", "b03/07", "b03/08"};
        XYSeries outer = new XYSeries(kasin.getTestName() + "O");

        for (int i = outers.length - 1; i >= 0; i--) {
            String gaugeName = outers[i];

            // timeMax の値を計算
            ResultSet rs = st.executeQuery("select \"TimePerTest[s]\", \"StrainPerTest[με]\" from \"T220TimeHistoryStrain\".\"" + gaugeName + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' "
                    + "and \"TimePerTest[s]\"=" + timeMax);
            rs.next();
            double strain = rs.getDouble(2);
            outer.add(1000 * (LOlocations[i] - 0.2), -strain); // マイナスにして引張を正としている。 位置は 175+25mm=200mm引いて、 mm 表示としている。

        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
//        gaugeName = new String[]{"b03/01", "b03/02", "b03/03", "b03/04"};
        XYSeries inner = new XYSeries(kasin.getTestName() + "I");

        for (int i = inners.length - 1; i >= 0; i--) {
            String gaugeName = inners[i];

            // timeMax の値を計算
            ResultSet rs = st.executeQuery("select \"TimePerTest[s]\", \"StrainPerTest[με]\" from \"T220TimeHistoryStrain\".\"" + gaugeName + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' "
                    + "and \"TimePerTest[s]\"=" + timeMax);
            rs.next();
            double strain = rs.getDouble(2);
            inner.add(1000 * (LOlocations[i] - 0.2), -strain); // マイナスしにして引張を正としている。

        }
        con.close();
        return new XYSeries[]{outer, inner, estimateOuter, estimateInner};
    }

}

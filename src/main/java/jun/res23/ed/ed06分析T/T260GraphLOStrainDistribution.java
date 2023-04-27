/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T260GraphLOStrainDistribution {

    private static final Logger logger = Logger.getLogger(T260GraphLOStrainDistribution.class.getName());
    private static final String dburl = T231CreateTimeHistoryBeamColumnNM.outputDb;
    private static final String readSchema = T231CreateTimeHistoryBeamColumnNM.outputSchema;//"T231TimeHistoryBeamColumnNM";
    
    private static final Path svgfile=Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T260GraphLOStrainDistribution/T260LOStrainDistribution.svg");

    public static void main(String[] args) {
        try {
            XYSeriesCollection c = new XYSeriesCollection();
            c.addSeries(createXYSeries(EdefenseInfo.D01Q01)); // random
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q02));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q03));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q04));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q05));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q06));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q08));
            c.addSeries(createXYSeries(EdefenseInfo.D01Q09));
//            c.addSeries(createXYSeries(EdefenseInfo.D01Q10));
            c.addSeries(createXYSeries(EdefenseInfo.D01Q11));
            //          c.addSeries(createXYSeries(EdefenseInfo.D02Q01));
//            c.addSeries(createXYSeries(EdefenseInfo.D02Q02));
//            c.addSeries(createXYSeries(EdefenseInfo.D02Q03));

            c.addSeries(createXYSeries(EdefenseInfo.D02Q05));
//            c.addSeries(createXYSeries(EdefenseInfo.D02Q06));
//            c.addSeries(createXYSeries(EdefenseInfo.D02Q07));
//            c.addSeries(createXYSeries(EdefenseInfo.D02Q08));

            c.addSeries(createXYSeries(EdefenseInfo.D03Q01));
//            c.addSeries(createXYSeries(EdefenseInfo.D03Q02));
//            c.addSeries(createXYSeries(EdefenseInfo.D03Q03));
//            c.addSeries(createXYSeries(EdefenseInfo.D03Q04));
//            c.addSeries(createXYSeries(EdefenseInfo.D03Q05));
//            c.addSeries(createXYSeries(EdefenseInfo.D03Q06));

//            c.addSeries(createXYSeries(EdefenseInfo.D03Q08));
            c.addSeries(createXYSeries(EdefenseInfo.D03Q09));

            JFreeChart chart = new JunXYChartCreator2().setDataset(c)
                    .setRangeAxisAutoRangeIncludesZero(false)
                    .setRangeAxisLabel("Strain [με]")
                    .setDomainAxisLabel("Location z (from edge of diaphragm) [mm]")
                    .setLinesAndShapesVisible(true, true).create();

            if (svgfile == null) {
                JunChartUtil.show(new Object() {
                }.getClass().getEnclosingClass().getName(), chart);

            } else {
                try {
                    JunChartUtil.svg(svgfile,500,250,chart);
                } catch (IOException ex) {
                    Logger.getLogger(T260GraphLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(T260GraphLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static XYSeries createXYSeries(EdefenseKasinInfo kasin) throws SQLException {
        BeamInfo beam = EdefenseInfo.Beam3;
        logger.log(Level.INFO, "Opening " + kasin.getTestName());
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        logger.log(Level.INFO, "Opened.");
        Statement st = con.createStatement();
        String timeMax;
        double momentMax;

        {
            // beam.getSection()[0] の加振直後2秒間の平均を得る。
            ResultSet rs = st.executeQuery("select avg(\"BendingMoment[kNm]\")"
                    + " from \"" + readSchema + "\".\"" + beam.getSections()[1].getName() + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' and \"TIME[s]\"<2.0"
            );
            rs.next();
            double avg = rs.getDouble(1);

            // モーメントが最大となる時刻を探している。
            rs = st.executeQuery("select \"TIME[s]\", \"BendingMoment[kNm]\" from \"" + readSchema + "\".\"" + beam.getSections()[1].getName() + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' order by (\"BendingMoment[kNm]\"-(" + avg
                    + ") ) desc limit 1");

            rs.next();
            timeMax = rs.getString(1);
            momentMax = Math.abs(rs.getDouble(2) - avg);

            rs.close();
        }

        // GS-LO-3A3-O-1〜4 b03/04〜08
        String gaugeNames[] = {"b03/05", "b03/06", "b03/07", "b03/08"};
        XYSeries s = new XYSeries(kasin.getTestName());
        double strain1 = 0.0;
        double strain2 = 0.0;
        int j = 0;
        for (int i = gaugeNames.length - 1; i >= 0; i--) {
            String gaugeName = gaugeNames[i];

            // 最初2秒の平均を計算
            ResultSet rs = st.executeQuery("select avg(\"Strain[με]\") from \"T220TimeHistoryStrain\".\"" + gaugeName + "\""
                    + " where TESTNAME='" + kasin.getTestName() + "t' "
                    + " and \"TIME[s]\" < 2.0 "
                    + "");
            rs.next();
            double avg = rs.getDouble(1);

            // timeMax の値を計算
            rs = st.executeQuery("select \"TIME[s]\", \"Strain[με]\" from \"T220TimeHistoryStrain\".\"" + gaugeName + "\" "
                    + "where TESTNAME='" + kasin.getTestName() + "t' "
                    + "and \"TIME[s]\"=" + timeMax);
            rs.next();
            double strain = rs.getDouble(2) - avg;
            s.add(25 * i + 45, strain);

//            if (j == 0) {
//                strain1 = strain;
//                s.add(i, 0);
//            } else if (j == 1) {
//                strain2 = strain;
//                s.add(i, 1);
//            } else {
//                s.add(i, (strain1 + (strain2 - strain1) * j)/strain2);
//            }
            j++;
        }
        con.close();
        return s;
    }

}

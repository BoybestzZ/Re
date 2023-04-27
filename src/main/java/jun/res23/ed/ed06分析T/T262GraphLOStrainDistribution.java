/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author jun
 */
public class T262GraphLOStrainDistribution {

    private static final Logger logger = Logger.getLogger(T262GraphLOStrainDistribution.class.getName());
    private static final String dburl = T231CreateTimeHistoryBeamColumnNM.outputDb;
    private static final String readSchema = T231CreateTimeHistoryBeamColumnNM.outputSchema;//"T231TimeHistoryBeamColumnNM";

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

            new JunXYChartCreator2().setDataset(c)
                    .setRangeAxisAutoRangeIncludesZero(false)
                    .setLinesAndShapesVisible(true, true).show(new Object() {
            }.getClass().getEnclosingClass().getName());
        } catch (SQLException ex) {
            Logger.getLogger(T262GraphLOStrainDistribution.class.getName()).log(Level.SEVERE, null, ex);
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
        double values[] = new double[4];

        int j = 0;
        for (int i = 0; i < gaugeNames.length; i++) {
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
            values[i] = rs.getDouble(2) - avg;
        }
        con.close();

        double slope = (1.0 - values[2] / values[3]); //
        logger.log(Level.INFO, "slope="+slope);
        XYSeries s = new XYSeries(kasin.getTestName(), false);

        s.add(0, values[0] / values[3] + slope * 3);
        s.add(1, values[1] / values[3] + slope * 2);
        s.add(2, values[2] / values[3] + slope * 1);
        s.add(3, values[3] / values[3] + slope * 0);

        return s;
    }

}

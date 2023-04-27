/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed05分析S;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import jun.fourier.FourierUtils;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class S112GraphTimeHistoryAccAndStrain {

    private static final Logger logger = Logger.getLogger(S112GraphTimeHistoryAccAndStrain.class.getName());

    final static String direction = "X"; // or Y
    final static UnitInfo accUnit = EdefenseInfo.k03;
    final static String gaugeName = "g02/01"; // or a03/01
    final static String inputSchema = "R103TimeHistory";

    public static void main(String[] args) {
        try {
            final EdefenseKasinInfo testname = EdefenseInfo.D01Q04s;
            final String dburl = "jdbc:h2:tcp://localhost/" + S100SectionNM.inputDatabaseDir.resolve(testname.getTestName() + "q");

            logger.log(Level.INFO, "connecting.");
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            logger.log(Level.INFO, "connected.");

            ResultSet rs = st.executeQuery("select \"STARTTIMEMILLIS\",\"ENDTIMEMILLIS\" from \"R150Duration\" where TYPE='S'");
            rs.next();
            double start = rs.getDouble(1);
            double end = rs.getDouble(2);

            rs = st.executeQuery("select \"T[ms]\", -\"" + direction + "[gal]\" from \"" + accUnit.getHardwareAddress() + "/01/acc02" + "\""
                    + " where \"T[ms]\" between " + start + " and " + end
                    + "");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);
            DefaultXYDataset dataset = new DefaultXYDataset();

            dataset.addSeries("Acc", new double[][]{ar[0], ar[1]});

            rs = st.executeQuery("SELECT \"T[ms]\", \"Strain[ε]\" FROM \"" + inputSchema + "\".\"" + gaugeName + "\""
                    + " where \"T[ms]\" between " + start + " and " + end
                    + "");

            double[][] ar2 = ResultSetUtils.createSeriesArray(rs);

            dataset.addSeries("Strain", new double[][]{ar2[0], ar2[1]});

            DateAxis xa = new DateAxis("Time");
            NumberAxis y1a = new NumberAxis("Amp");

            y1a.setFixedDimension(40);

            JFreeChart chart1 = new JunXYChartCreator2()
                    .setDomainAxis(xa).setRangeAxis(y1a)
                    .setDataset(dataset).create();

            JunChartUtil.show("S112" + testname + direction, chart1);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(S112GraphTimeHistoryAccAndStrain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

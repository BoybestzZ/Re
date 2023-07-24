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
import java.text.ChoiceFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * T280 → T281 : T270から最大時刻のものを取り出す。T280よりも多くの情報が出力される。
 *
 * @author jun
 *
 */
public class T282GraphLowerFlangeLateralMomentRatio {

    public static void main(String[] args) {
        try {
            Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T282LowerFlangeLateralMomentRatio");
            String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            String sections[] = {
                "LA3S1", "LA3S2", "LA3S3", "LA3S4", "LA3S5",
                "LA4S1", "LA4S2", "LA4S3", "LA4S4", "LA4S5",
                "LAAS1", "LAAS2", "LAAS3", "LAAS4", "LAAS5",
                "LABS1", "LABS2", "LABS3", "LABS4", "LABS5"
            };

            String types[][] = {{"Random", "Random"}, {"KMMH", "Kumamoto"}, {"FKS", "Tohoku"}, {"Kobe", "Kobe"}};

            String section = "LA3S1";
            String direction = "NEGATIVE";

            HashMap<Integer, String> labelmap = new HashMap();
            DefaultXYDataset dataset = new DefaultXYDataset();

            for (String[] tt : types) {

                ResultSet rs = st.executeQuery("select SECTION , \"Location[m]\", TESTNAME,ALTNAME, \"UpperBendingRatioPerTest\",\"LowerBendingRatioPerTest\""
                        + " from \"T281MaxFlangeLateralMoment\" "
                        + " where SECTION='" + section + "' and DIRECTION='" + direction + "' and ALTNAME like '%" + tt[0] + "%' "
                        + "and ALTNAME not like '%Y]' order by ALTNAME");

                XYSeries s = new XYSeries("key");

                while (rs.next()) {
                    double lowerRatio = rs.getDouble(6);

                    String testname = rs.getString("TESTNAME");
                    String altname = rs.getString("ALTNAME");
                    int no = Integer.parseInt(altname.substring(0, 2));
                    s.add(no, lowerRatio);

                    if (!labelmap.containsKey(no)) {

                        labelmap.put(no, altname);
                    }

                }

                dataset.addSeries(tt[1], s.toArray());

            }

            ResultSet rs = st.executeQuery("select count(distinct ALTNAME) from \"T281MaxFlangeLateralMoment\" order by 1");
            rs.next();
            int size = rs.getInt(1);
            rs = st.executeQuery("select distinct ALTNAME from \"T281MaxFlangeLateralMoment\" order by 1");
            double[] limits = new double[size];
            String[] labels = new String[size];
            int i = 0;
            while (rs.next()) {
                String altname = rs.getString(1);
                int no = Integer.parseInt(altname.substring(0, 2));
                limits[i] = no;
                labels[i] = altname;
                i++;
            }

            NumberAxis na = new NumberAxis("Test No.");
            na.setTickUnit(new NumberTickUnit(1));
            ChoiceFormat cf = new ChoiceFormat(limits, labels);
            cf.setMinimumFractionDigits(0);
            cf.setParseIntegerOnly(true);

            na.setNumberFormatOverride(cf);
            na.setVerticalTickLabels(true);

            XYLineAndShapeRenderer re = new XYLineAndShapeRenderer(true, true);

            JFreeChart chart = new JunXYChartCreator2().setDomainAxis(na).setDataset(dataset)
                    .setRangeZeroBaselineVisible(true)
                    .setRangeAxisRange(-0.1, 0.1)
                    .setRangeAxisLabel("Bending strain ratio")
                    .setRenderer(re).create();

            if (svgdir != null) {
                Path svgfile = svgdir.resolve(section + "_" + direction.charAt(0)
                        + ".svg");
                try {
                    JunChartUtil.svg(svgfile, 400, 300, chart);
                } catch (IOException ex) {
                    Logger.getLogger(T282GraphLowerFlangeLateralMomentRatio.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JunChartUtil.show(section, chart);
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(T282GraphLowerFlangeLateralMomentRatio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

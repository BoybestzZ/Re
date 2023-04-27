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
import jun.chart.JunChartCreator;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import jun.res23.ed.ed07東京測器.A100ReadTMRcsv;
import jun.res23.ed.util.EdefenseInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T242JointEquibrium {

    private static final String dburl = T231CreateTimeHistoryBeamColumnNM.outputDb;
    
    private static final Path svgfile=null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T242JointEquibrium");

    public static void main(String[] args) {
        try {
            String sql
                    = "select rownum(), a.TESTNAME, column,  beam, abs(column/beam) from (\n"
                    + "SELECT TESTNAME, sum(\"BendingMoment[kNm]\") column FROM \"T241BendingMomentDiagram\"  where LOCATION in ('Column2F3A_T','Column3F3A_B') group by TESTNAME\n"
                    + ") a ,(\n"
                    + "SELECT TESTNAME, \"BendingMoment[kNm]\"  beam  FROM \"T241BendingMomentDiagram\"  where LOCATION in ('Beam3_L') )b\n"
                    + "where a.TESTNAME=b.TESTNAME";

            Connection con = DriverManager.getConnection(dburl, "junapp", "");

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);
            DefaultCategoryDataset cd = new DefaultCategoryDataset();

            while (rs.next()) {
                String testname = rs.getString(2);
                testname = testname + EdefenseInfo.lookForTestName(testname).getWaveName();

                double value = rs.getDouble(5);
                cd.addValue(value, "test", testname);
            }

            JFreeChart chart = new JunChartCreator()
                    .setCategoryLabelPositions(CategoryLabelPositions.DOWN_90)
                    .setDataset(cd).create();
            if (svgfile!=null){
            try {
                // JunChartUtil.show("title", chart);
                JunChartUtil.svg(svgfile.resolve("Joint3A.svg"), 600,400,chart);
            } catch (IOException ex) {
                Logger.getLogger(T242JointEquibrium.class.getName()).log(Level.SEVERE, null, ex);
            }} else {
                JunChartUtil.show(chart);
            }

            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(T242JointEquibrium.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

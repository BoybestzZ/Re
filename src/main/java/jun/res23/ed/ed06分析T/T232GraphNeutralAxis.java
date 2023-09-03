/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartCreator;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author jun
 */
public class T232GraphNeutralAxis {

    private static final Logger logger = Logger.getLogger(T232GraphNeutralAxis.class.getName());
    private static final String outputTable = "T232NeutralAxis";

    public static void main(String[] args) {
        try {
            final BeamSectionInfo[] sections = {EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5, 
                                                EdefenseInfo.LA4S1, EdefenseInfo.LA4S2, EdefenseInfo.LA4S3, EdefenseInfo.LA4S4, EdefenseInfo.LA4S5,
                                                EdefenseInfo.LAAS1, EdefenseInfo.LAAS2, EdefenseInfo.LAAS3, EdefenseInfo.LAAS4, EdefenseInfo.LAAS5,
                                                EdefenseInfo.LABS1, EdefenseInfo.LABS2, EdefenseInfo.LABS3, EdefenseInfo.LABS4, EdefenseInfo.LABS5};
            final String dburl = "jdbc:h2:C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed06v230815J";

            boolean directionPositive = false;
            final Path svgfile = null;// Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T232NeutralAxis/beam3" + (directionPositive ? "Positive" : "Negative") + ".svg");

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            st.executeUpdate("create table if not exists \"" + outputTable + "\" (SECTION varchar, TESTNAME varchar, \"DirectionPositive\" boolean, \"TimePerTest[s]\" real,\"NeutralAxis[mm]\" real)");
            st.executeUpdate("delete from \"" + outputTable + "\" where \"DirectionPositive\"=" + directionPositive);
            logger.log(Level.INFO, "connected");

//            EdefenseKasinInfo[] tests = new EdefenseKasinInfo[]{EdefenseInfo.D01Q01, EdefenseInfo.D01Q02,
//                EdefenseInfo.D01Q03, EdefenseInfo.D01Q04, EdefenseInfo.D01Q08,
//                EdefenseInfo.D01Q09, EdefenseInfo.D01Q10,
//                EdefenseInfo.D01Q11, EdefenseInfo.D02Q01, EdefenseInfo.D02Q03, EdefenseInfo.D02Q05, EdefenseInfo.D02Q06,
//                EdefenseInfo.D03Q01, EdefenseInfo.D03Q02, EdefenseInfo.D03Q04, EdefenseInfo.D03Q08, EdefenseInfo.D03Q09};

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            PreparedStatement ps = con.prepareStatement("insert into \"" + outputTable + "\" (SECTION,TESTNAME,\"DirectionPositive\",\"TimePerTest[s]\",\"NeutralAxis[mm]\") "
                    + "values (?,?,?,?,?)");
            for (BeamSectionInfo section : sections) {
                for (EdefenseKasinInfo test :EdefenseInfo.alltests) {

                    // 時間を求める。
                    String desc = directionPositive ? "desc" : "";
                    ResultSet rs = st.executeQuery("select \"TimePerTest[s]\",\"BendingMomentPerTest[kNm]\", \"AxialForcePerTest[kN]\""
                            + " from \"T231TimeHistoryNM\".\"" + EdefenseInfo.LA3S2.getName() + "\" "
                            + " where TESTNAME='" + test.getName() + "t' "
                            + "order by \"BendingMomentPerTest[kNm]\" " + desc + " limit 1");

                    rs.next();
                    double time = rs.getDouble(1);
                    time = Math.round(time * 100) / 100.0;

                    rs = st.executeQuery("select 350+110- 350.0/(1- (\"StrainULPerTest[με]\"+ \"StrainURPerTest[με]\")/(\"StrainLLPerTest[με]\"+ \"StrainLRPerTest[με]\") ) \"xn(fromSlabTop)[mm]\" "
                            + "from \"T231TimeHistoryNM\".\"" + section.getName() + "\" where TESTNAME='" + test.getTestName() + "t' and \"TimePerTest[s]\"=" + time);
                    rs.next();
                    double xn = rs.getDouble(1); // スラブ上端からの中立軸位置(mm)

                    dataset.addValue(xn, section.getName(), test.getTestName() + "(" + test.getWaveName() + ")");

                    int psi = 1;
                    ps.setString(psi++, section.getName());
                    ps.setString(psi++, test.getTestName());
                    ps.setBoolean(psi++, directionPositive);
                    ps.setDouble(psi++, time);
                    ps.setDouble(psi++, xn);
                    ps.executeUpdate();
                }
            }
            JFreeChart chart = new JunChartCreator().setValueAxisLabel("NeutralAxisLocation (from slab top) [mm]").setDataset(dataset).create();
            CategoryPlot plot = chart.getCategoryPlot();
            plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(2.0f);
            plot.getRangeAxis().setInverted(true);
            plot.getRangeAxis().setRange(0, 350);
            plot.setDomainGridlinesVisible(true);

            if (svgfile != null) {
                try {
                    JunChartUtil.svg(svgfile, 400, 400, chart);
                } catch (IOException ex) {
                    Logger.getLogger(T232GraphNeutralAxis.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                JunChartUtil.show(directionPositive ? "Positive" : "Negative", chart);
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(T232GraphNeutralAxis.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

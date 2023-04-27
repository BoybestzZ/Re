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
import jun.data.ResultSetUtils;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T250CheckStrainTimeHistory {

    private static final Logger logger = Logger.getLogger(T250CheckStrainTimeHistory.class.getName());

    public static void main(String[] args) {
        try {
            
            String testname="D01Q01";
            Connection con1 = DriverManager.getConnection("jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ/"+testname+"q", "junapp", "");
            Statement st1 = con1.createStatement();
            ResultSet rs1 = st1.executeQuery("select \"TIME[s]\", \"b01/05_Strain[με]\"-\"b01/08_Strain[με]\" from \"R210Resample\"");

            double[][] ar1 = ResultSetUtils.createSeriesArray(rs1);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("個別T", ar1);

            rs1 = st1.executeQuery("select \"TIME[s]\", \"i01/01_Strain[με]\"-\"i01/04_Strain[με]\" from \"R210Resample\"");
            ar1 = ResultSetUtils.createSeriesArray(rs1);
            dataset.addSeries("個別B", ar1);

            Connection con2 = DriverManager.getConnection("jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06", "junapp", "");
            Statement st2 = con2.createStatement();

            logger.log(Level.INFO, "start");
            ResultSet rs2 = st2.executeQuery("select N.\"TIME[s]\", N.\"Strain[με]\"-S.\"Strain[με]\" "
                    + "from \"T220TimeHistoryStrain\".\"b01/05\" N , \"T220TimeHistoryStrain\".\"b01/08\" S"
                    + " where N.testname='"+testname+"t' and S.testname='"+testname+"t' and N.\"TIME[s]\" =S.\"TIME[s]\"");
            logger.log(Level.INFO, "completed");
            double[][] ar2 = ResultSetUtils.createSeriesArray(rs2);

            dataset.addSeries("全体T", ar2);

            JunChartUtil.show(dataset);

            con1.close();
            con2.close();

        } catch (SQLException ex) {
            Logger.getLogger(T250CheckStrainTimeHistory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

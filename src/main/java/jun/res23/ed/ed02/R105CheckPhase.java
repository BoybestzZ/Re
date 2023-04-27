/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class R105CheckPhase {

    public static void main(String[] args) {

        try {
            String d02q05="jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q05_20230217_144801";            
            String d02q06 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q06_20230217_150350";
            String dburl = d02q05;

            Connection con = DriverManager.getConnection(dburl, "junapp", "");

            Statement st = con.createStatement();
                UnitInfo unit = EdefenseInfo.f02;
            DefaultXYDataset dataset = new DefaultXYDataset();
            {

                int chno = 1;
                String tablename = "\"R103Fourier\".\"" + unit.getName() + "/0" + chno + "\"";
                ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"Amp[ε*s]\", \"Phase[rad]\" from " + tablename);
                double[][] ar = ResultSetUtils.createSeriesArray(rs);

                dataset.addSeries("ch01", new double[][]{ar[0],ar[2]});
            }
            {
                int chno = 2;
                String tablename = "\"R103Fourier\".\"" + unit.getName() + "/0" + chno + "\"";
                ResultSet rs = st.executeQuery("select \"Freq[Hz]\",\"Amp[ε*s]\", \"Phase[rad]\" from " + tablename);
                double[][] ar = ResultSetUtils.createSeriesArray(rs);

                dataset.addSeries("ch02", new double[][] {ar[0],ar[2]});
            }
            
            JunChartUtil.show(dataset);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R105CheckPhase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

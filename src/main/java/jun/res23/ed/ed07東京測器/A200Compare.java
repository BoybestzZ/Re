/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed07東京測器;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import jun.res23.ed.ed06分析T.T220CreateTimeHistoryStrain;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class A200Compare {

    // これは EdefenseKasinInfo に書き込んである。
//    public static final double D01Q01TimeDiff = 311.6; // これをTMRのTIMEから引いてください。
//    public static final double D01Q02TimeDiff = 287.43; // これをTMRのTIMEから引いてください。    
//    public static final double D01Q03TimeDiff = 263.64; // これをTMRのTIMEから引いてください。    
//    public static final double D01Q04TimeDiff = 279.73; // これをTMRのTIMEから引いてください。     
//    public static final double D01Q11TimeDiff = 266.37; // これをTMRのTIMEから引いてください。         
//
//    public static final double D02Q01TimeDiff = 887.79; // これをTMRのTIMEから引いてください。         
//    public static final double D02Q05TimeDiff = 275.64; // これをTMRのTIMEから引いてください。       
//    public static final double D02Q08TimeDiff = 292.02; // これをTMRのTIMEから引いてください。           
//
//    public static final double D03Q01TimeDiff = 294.94; // これをTMRのTIMEから引いてください。         
//    public static final double D03Q05TimeDiff = 289.43; // これをTMRのTIMEから引いてください。         
//    public static final double D03Q09TimeDiff = 280.31; // これをTMRのTIMEから引いてください。         
    public static final String tmrdb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed07TMR/res22ed07TMR";

    
    public static void main(String[] args) {
        main(EdefenseInfo.D03Q09);
    }
    
    public static void main(EdefenseKasinInfo kasin) {
        try {
            Connection contmr = DriverManager.getConnection(tmrdb, "junapp", "");

            Statement sttmr = contmr.createStatement();

//            EdefenseKasinInfo kasin = EdefenseInfo.D03Q09;

            ResultSet rs = sttmr.executeQuery("select \"TIME[s]\"-(" + kasin.getTmrTimeDiffSeconds() + "), \"CK-LA-A-L-L-4μStrain\" from \"" + kasin.getTestName() + "\" order by 1");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("TMR", ar);

            contmr.close();

            Connection con = DriverManager.getConnection(T220CreateTimeHistoryStrain.outputDb, "junapp", "");
            StrainGaugeInfo gauge = StrainGaugeInfo.GSLA_A_L_L_4;

            Statement st = con.createStatement();
            rs = st.executeQuery("select \"TIME[s]\",-\"Strain[με]\" from \"" + T220CreateTimeHistoryStrain.outputSchema + "\".\"" + gauge.getShortName() + "\""
                    + " where TESTNAME='" + kasin.getTestName() + "t' order by 1");
            ar = ResultSetUtils.createSeriesArray(rs);

            dataset.addSeries("Raspi", ar);

            con.close();
            new JunXYChartCreator2()
                    .setDomainAxisLabel("Time[s]")
                    .setRangeAxisLabel("Strain[με]")
                    .setDataset(dataset).show();

        } catch (SQLException ex) {
            Logger.getLogger(A200Compare.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

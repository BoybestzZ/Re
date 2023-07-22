/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import jun.fourier.FFTv2;
import jun.fourier.FourierTransformV2;
import jun.fourier.HanningWindow;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.res23.ed.util.StrainGaugeInfo;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author jun
 */
public class Z100Check {

    static final String databaseQdir = "/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ";
    private static final Logger logger = Logger.getLogger(Z100Check.class.getName());

    public static void main(String[] args) {

        try {
            String dburl = "jdbc:h2:tcp://localhost//" + databaseQdir + "/D02Q05q";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            ResultSet rs
                    = st.executeQuery("select STARTTIMEMILLIS,ENDTIMEMILLIS from \"B300Duration\" where TYPE='U'");
            rs.next();
            long startTimeMillis = rs.getLong(1);
            long endTimeMillis = rs.getLong(2);
            DefaultXYDataset thDataset = new DefaultXYDataset();
            DefaultXYDataset specDataset = new DefaultXYDataset();

            add(EdefenseInfo.k01, st, startTimeMillis, endTimeMillis, thDataset, specDataset);
            add(EdefenseInfo.k03, st, startTimeMillis, endTimeMillis, thDataset, specDataset);


            JunChartUtil.show("spec", specDataset);
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(Z100Check.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void add(UnitInfo k01, Statement st, long startTimeMillis, long endTimeMillis, DefaultXYDataset th, DefaultXYDataset spec) throws SQLException {

        String topic = k01.getHardwareAddress() + "/01/acc02";

//            long startTimeMillis = 1676432110000L;
//            long endTimeMillis = 1676432200000L;
        ResultSet rs = st.executeQuery("select \"T[ms]\", \"X[gal]\" from \"" + topic + "\" where \"T[ms]\" between " + startTimeMillis + " and " + endTimeMillis);

        double[][] ar = ResultSetUtils.createSeriesArray(rs);

        ar[1] = FFTv2.zeroAverage(ar[1]);
//        ar[1] = HanningWindow.apply(k01[1]);
//        ar[1] = FFTv2.zeroAverage(k01[1]);

        th.addSeries(k01.getName(), ar);

        int len = ar[0].length;
        double dtSec = (ar[0][len - 1] - ar[0][0]) / (len - 1) / 1000.0;
        logger.log(Level.INFO, "dt=" + dtSec);
        FourierTransformV2 ft = new FourierTransformV2(dtSec, ar[1]);
        double[][] specar = ft.getAmplitudeSpectrum(0.1, 10.0, 0.01, false);
        spec.addSeries(k01.getName(), specar);

    }

   

}

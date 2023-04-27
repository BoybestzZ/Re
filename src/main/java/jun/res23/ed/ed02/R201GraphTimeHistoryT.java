/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.chart.JunXYChartCreator2;
import jun.data.ResultSetUtils;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.StrainGaugeInfo;

/**
 *
 * @author jun
 */
public class R201GraphTimeHistoryT {

    private static final Logger logger = Logger.getLogger(R201GraphTimeHistoryT.class.getName());
    public static Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
    public static Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R201TimeHistoryT");

    public static void main(String[] args) {

        try {
            main("D01Q01", 15, 12, 34, 45);
        main("D01Q02", 15, 12, 53, 0);
        main("D01Q03", 15, 13, 9, 45);
        main("D01Q04", 15, 13, 26, 10);
        main("D01Q05", 15, 13, 37, 0);
        main("D01Q06", 15, 13, 49, 0);
        main("D01Q08", 15, 14, 0, 40);
        main("D01Q09", 15, 14, 11, 40);
        main("D01Q10", 15, 16, 58, 40);
        main("D01Q11", 15, 17, 17, 50);
        main("D02Q01", 17, 13, 39, 40);
        main("D02Q02", 17, 13, 51, 55);
        main("D02Q03", 17, 14, 8, 40);
        main("D02Q05", 17, 14, 48, 20);
        main("D02Q06", 17, 15, 3, 50);
        main("D02Q07", 17, 15, 16, 15);
        main("D02Q08", 17, 15, 35, 30);
        main("D03Q01", 24, 13, 37, 30);
        main("D03Q02", 24, 13, 55, 20);
        main("D03Q03", 24, 14, 7, 10);
        main("D03Q04", 24, 14, 22, 30);
        main("D03Q05", 24, 14, 33, 15);
        main("D03Q06", 24, 14, 44, 25);
        main("D03Q08", 24, 15, 38, 20);
        main("D03Q09", 24, 15, 50, 10);
        } catch (IOException ex) {
            Logger.getLogger(R201GraphTimeHistoryT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String testname, int day, int hour, int minute, int second) throws IOException {
//        String testname = EdefenseInfo.testnames[0];
        ZoneId zone = ZoneId.systemDefault();
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try {
            // OpenDatabase
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select \"TIME[s]\",\"X[gal]\",\"Y[gal]\" from \"R200Resample\".\"k01/01\" order by 1");

            double[][] ar = ResultSetUtils.createSeriesArray(rs);


            if (!Files.exists(svgdir)) {
                Files.createDirectory(svgdir);
            }
            Path svgfile = svgdir.resolve(testname + "t.svg");

            new JunXYChartCreator2().setDataset(ar).setDomainAxisLabel("TIME[s]").setRangeAxisLabel("Acc[gal]").svg(svgfile, 400, 200);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R201GraphTimeHistoryT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

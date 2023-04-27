/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.realtime.h2.PayloadFileToH2;

/**
 * R103からデータベースに読み込む部分だけを作成。 rデータベースは、datの開始から5分後以後を読み取る。
 *
 * @author jun
 */
public class R130CreateDatabaseR {

    private static final Logger logger = Logger.getLogger(R130CreateDatabaseR.class.getName());

    public static Path datdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/datfiles");
    public static Path dbdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R130DatabaseR");
    public static DateTimeFormatter pat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void main(String[] args) {
        try {
            ZoneId zone = ZoneId.systemDefault();

            //   When rerun this, remove the database file, or it won't overwrite it.'
            // D01 and D02 have been created by HeFang.

//            main1("D03Q01", 315, 90);
//            main1("D03Q02", 270, -1);
//            main1("D03Q03", 270, -1);
//            main1("D03Q04", 270, -1);
//            main1("D03Q05", 270, -1);
//            main1("D03Q06", 270, -1);
//            main1("D03Q08", 270+40, -1);
            main1("D03Q09", 300, 90);

        } catch (IOException ex) {
            Logger.getLogger(R130CreateDatabaseR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(R130CreateDatabaseR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main1(String testname, int plusSeconds, int durationSeconds) throws SQLException, IOException {
        Path datfile = Files.list(datdir).filter((t) -> {
            return t.getFileName().toString().startsWith(testname);
        }).findAny().get();
        logger.log(Level.INFO, "filename=" + datfile.toString());
        if (!datfile.toString().endsWith(".dat")) {
            return;
        }

        String filename = datfile.getFileName().toString();

        String kasinName = filename.substring(0, 6);
        String databaseName = kasinName + "r";
        String dateTimeString = filename.substring(7, 22);
        ZonedDateTime datStartTime = LocalDateTime.parse(dateTimeString, pat).atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = datStartTime.plusSeconds(plusSeconds);
        ZonedDateTime endTime = startTime.plusSeconds(durationSeconds);
        if (durationSeconds < 0) {
            endTime = null;
        }
        Path dbfile = dbdir.resolve(databaseName);
        String dburl = "jdbc:h2:file:/" + dbfile.toString();
        logger.log(Level.INFO, "dburl=" + dburl);

        PayloadFileToH2.read(datfile, dburl, startTime, endTime);

        R102CalculateMinimumDuration.main1(dburl);

    }

}

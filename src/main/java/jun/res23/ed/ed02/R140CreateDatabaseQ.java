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
 * R103からデータベースに読み込む部分だけを作成。 qデータベースは、すべてを読み取る。
 *
 * @author jun
 */
public class R140CreateDatabaseQ {

    private static final Logger logger = Logger.getLogger(R140CreateDatabaseQ.class.getName());

    public static Path datdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/datfiles");
    public static Path dbdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
    public static DateTimeFormatter pat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void main(String[] args) {
        try {
            ZoneId zone = ZoneId.systemDefault();

            //   When rerun this, remove the database file, or it won't overwrite it.'
            // D01 and D02 have been created by HeFang.
//            main1("D01Q01", 0, -1);
//            main1("D01Q02", 0, -1);
//            main1("D01Q03", 0, -1);
//            main1("D01Q04", 0, -1);
//            main1("D01Q05", 0, -1);
//            main1("D01Q06", 0, -1);
//n            main1("D01Q08", 0, -1);
//            main1("D01Q09", 0, -1);
//            main1("D01Q10", 0, -1);
//            main1("D01Q11", 0, -1);
            
//            main1("D02Q01", 0, -1);
//            main1("D02Q02", 0, -1);
//            main1("D02Q03", 0, -1);
 //         main1("D02Q05", 0, -1);// これだけ特殊なので、後ほどR140で読み直す。
//            main1("D02Q06", 0, -1);
//            main1("D02Q07", 0, -1);
            main1("D02Q08", 0, -1);

            

            //   
//            main1("D03Q02", 0, -1);
//            main1("D03Q03", 0, -1);
//            main1("D03Q04", 0, -1);
//            main1("D03Q05", 0, -1);
//            main1("D03Q06", 0, -1);
//            main1("D03Q08", 0, -1);
//           main1("D03Q09",0,-1);
        } catch (IOException ex) {
            Logger.getLogger(R140CreateDatabaseQ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(R140CreateDatabaseQ.class.getName()).log(Level.SEVERE, null, ex);
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
        String databaseName = kasinName + "q";
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

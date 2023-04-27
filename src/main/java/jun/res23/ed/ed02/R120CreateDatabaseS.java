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
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.realtime.h2.PayloadFileToH2;

/**
 * R103からデータベースに読み込む部分だけを作成。
 *
 *
 * @author jun
 */
public class R120CreateDatabaseS {

    private static final Logger logger = Logger.getLogger(R120CreateDatabaseS.class.getName());

    public static Path datdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/datfiles");
    public static Path dbdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R120DatabaseS");
    public static DateTimeFormatter pat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void main(String[] args) {
        try {
            ZoneId zone = ZoneId.systemDefault();

            Iterator<Path> iter = Files.list(datdir).iterator();


            while (iter.hasNext()) {
                Path item = iter.next();
                logger.log(Level.INFO, item.toString());
                main1(item);
            }
        } catch (IOException ex) {
            Logger.getLogger(R120CreateDatabaseS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(R120CreateDatabaseS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main1(Path datfile) throws SQLException, IOException {
        logger.log(Level.INFO, "filename="+datfile.toString());
        if (!datfile.toString().endsWith(".dat")) return;


        String filename = datfile.getFileName().toString();

        String kasinName = filename.substring(0, 6);
                if (!kasinName.startsWith("D03")) return;        
        String databaseName = kasinName + "s";
        String dateTimeString = filename.substring(7, 22);
        ZonedDateTime datStartTime = LocalDateTime.parse(dateTimeString, pat).atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = datStartTime.plusMinutes(1);
        ZonedDateTime endTime = startTime.plusMinutes(3);
        Path dbfile = dbdir.resolve(databaseName);
        String dburl = "jdbc:h2:file:/"+dbfile.toString();
        logger.log(Level.INFO, "dburl="+dburl);

        PayloadFileToH2.read(datfile, dburl, startTime, endTime);
        
        R102CalculateMinimumDuration.main1(dburl);

    }

}

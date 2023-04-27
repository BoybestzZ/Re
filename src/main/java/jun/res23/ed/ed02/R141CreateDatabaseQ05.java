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
 * R103からデータベースに読み込む部分だけを作成。 qデータベースは、すべてを読み取る。 D02Q05はエラーがあったので、
 * サーバのdatファイルから読み出す。 D02Q05 = 20230217_144401から
 *
 * @author jun
 */
public class R141CreateDatabaseQ05 {

    private static final Logger logger = Logger.getLogger(R141CreateDatabaseQ05.class.getName());

    //   public static Path datdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/datfiles");
//    public static Path dbdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");
//    public static DateTimeFormatter pat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static void main(String[] args) {
        ZoneId zone = ZoneId.systemDefault();
        Path datfile = Path.of("/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/20230217_1400.dat");
        String dburl = "jdbc:h2:file:///home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ/D02Q05q";
        ZonedDateTime startTime = ZonedDateTime.of(2023, 2, 17, 14, 44, 1, 0, zone);
        ZonedDateTime endTime = ZonedDateTime.of(2023, 2, 17, 14, 50, 56, 0, zone);
        try {
            PayloadFileToH2.read(datfile, dburl, startTime, endTime);
        } catch (SQLException ex) {
            Logger.getLogger(R141CreateDatabaseQ05.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(R141CreateDatabaseQ05.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        R102CalculateMinimumDuration.main1(dburl);

    }

}

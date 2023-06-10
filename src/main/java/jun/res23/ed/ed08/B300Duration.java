/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 *
 * @author jun
 */
public class B300Duration {

    private static final Logger logger = Logger.getLogger(B300Duration.class.getName());
    public static final Path databaseQdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ");
    public static final String dburlNied = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed08防災科研/res22ed08";

    public static void main(String[] args) {
        try {
            // この時刻の数値は R200Resample からコピペ。　これが t 時刻の原点。
            main(EdefenseInfo.D01Q01, 15, 12, 34, 45);
            main(EdefenseInfo.D01Q02, 15, 12, 53, 0);
            main(EdefenseInfo.D01Q03, 15, 13, 9, 45);
            main(EdefenseInfo.D01Q04, 15, 13, 26, 10);
            main(EdefenseInfo.D01Q05, 15, 13, 37, 0);
            main(EdefenseInfo.D01Q06, 15, 13, 49, 0);
            main(EdefenseInfo.D01Q08, 15, 14, 0, 40);
            main(EdefenseInfo.D01Q09, 15, 14, 11, 40);
            main(EdefenseInfo.D01Q10, 15, 16, 58, 40);
            main(EdefenseInfo.D01Q11, 15, 17, 17, 50);
            main(EdefenseInfo.D02Q01, 17, 13, 39, 40);
            main(EdefenseInfo.D02Q02, 17, 13, 51, 55);
            main(EdefenseInfo.D02Q03, 17, 14, 8, 40);
            main(EdefenseInfo.D02Q05, 17, 14, 48, 20);
            main(EdefenseInfo.D02Q06, 17, 15, 3, 50);
            main(EdefenseInfo.D02Q07, 17, 15, 16, 15);
            main(EdefenseInfo.D02Q08, 17, 15, 35, 30);
            main(EdefenseInfo.D03Q01, 24, 13, 37, 30);
            main(EdefenseInfo.D03Q02, 24, 13, 55, 20);
            main(EdefenseInfo.D03Q03, 24, 14, 7, 10);
            main(EdefenseInfo.D03Q04, 24, 14, 22, 30);
            main(EdefenseInfo.D03Q05, 24, 14, 33, 15);
            main(EdefenseInfo.D03Q06, 24, 14, 44, 25);
            main(EdefenseInfo.D03Q08, 24, 15, 38, 20);
            main(EdefenseInfo.D03Q09, 24, 15, 50, 10);
        } catch (IOException ex) {
            Logger.getLogger(B300Duration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(B300Duration.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // day hour とかは t 時刻の原点。
    public static void main(EdefenseKasinInfo test, int day, int hour, int min, int sec) throws IOException, SQLException {
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime startTimeT = ZonedDateTime.of(2023, 2, day, hour, min, sec, 0, zone);
        long startTimeMillisT = startTimeT.toInstant().toEpochMilli(); // startTimeT.toEpochSecond() * 1000 + startTimeT.getNano() / 1000;

        String dbQ = "jdbc:h2:tcp://localhost/" + databaseQdir.resolve(test.getTestName() + "q");
        logger.log(Level.INFO, "start " + test.getTestName());

        {
            Connection con = DriverManager.getConnection(dbQ, "junapp", "");
            Statement st = con.createStatement();
            Connection conN = DriverManager.getConnection(dburlNied, "junapp", "");
            Statement stN = conN.createStatement();

            // R時刻の範囲を取得
            ResultSet rs = st.executeQuery("select * from \"R150Duration\" where TYPE='R'");
            rs.next();
            long startTimeMillisR = rs.getLong("STARTTIMEMILLIS");
            long endTimeMillisR = rs.getLong("ENDTIMEMILLIS");
            Timestamp startTimeR = rs.getTimestamp("STARTTIME");
            Timestamp endTimeR = rs.getTimestamp("ENDTIME");

            // Nied時刻の範囲を取得
            double diffTimeSec = test.getNiedTimeDiffSeconds();// 7.9; // Nied 時刻からこの値を引くと t時刻に一致する。
            long startTimeMillisU;
            long endTimeMillisU;

            ZonedDateTime startTimeN = null, endTimeN = null;
            long startTimeMillisN = 0L, endTimeMillisN = 0L;

            if (!Double.isNaN(diffTimeSec)) { // 一部には Niedのデータが無いものも存在する。そのときは NaNが帰ってくるはず。
                startTimeN = startTimeT.minusNanos((long) (diffTimeSec * 1e9));

                ResultSet rsN = stN.executeQuery("SELECT max(\"Time[s]\") FROM \"" + test.getTestName() + "\"");
                rsN.next();
                double durationSecN = rsN.getDouble(1); // 秒間
                endTimeN = startTimeN.plusNanos((long) (durationSecN * 1e9));

                startTimeMillisN = startTimeN.toInstant().toEpochMilli();//startTimeN.toEpochSecond() * 1000 + startTimeN.getNano() / 1000;
                endTimeMillisN = endTimeN.toInstant().toEpochMilli(); // endTimeN.toEpochSecond() * 1000 + endTimeN.getNano() / 1000;

                // R時刻とN時刻の共通範囲を取得
                if (startTimeMillisN < startTimeMillisR) {
                    startTimeMillisU = startTimeMillisR;
                } else {
                    startTimeMillisU = startTimeMillisN;
                }
                if (endTimeMillisN > endTimeMillisR) {
                    endTimeMillisU = endTimeMillisR;
                } else {
                    endTimeMillisU = endTimeMillisN;
                }
                if (endTimeMillisU < startTimeMillisU) {
                    // 共通範囲が存在しない！！

                }
            } else {
                startTimeMillisU = startTimeMillisR;
                endTimeMillisU = endTimeMillisR;
            }

            ZonedDateTime startTimeU = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTimeMillisU), zone);
            ZonedDateTime endTimeU = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTimeMillisU), zone);

            st.executeUpdate("drop table if exists \"B300Duration\"");
            st.executeUpdate("create table \"B300Duration\" (TYPE char,STARTTIME timestamp, STARTTIMEMILLIS long, ENDTIME timestamp, ENDTIMEMILLIS long, REMARKS varchar)");
            st.executeUpdate("insert into \"B300Duration\" select * from \"R150Duration\"");
            st.executeUpdate("insert into \"B300Duration\" values ('T','" + startTimeT.toLocalDateTime() + "'," + startTimeMillisT + ",null,null,'R200Resample')");
            if (startTimeN != null) {
                st.executeUpdate("insert into \"B300Duration\" values ('N','" + startTimeN.toLocalDateTime() + "'," + startTimeMillisN + ",'" + endTimeN.toLocalDateTime() + "'," + endTimeMillisN + ",'Nied')");
            }
            st.executeUpdate("insert into \"B300Duration\" values ('U','" + startTimeU.toLocalDateTime() + "'," + startTimeMillisU + ",'" + endTimeU.toLocalDateTime() + "'," + endTimeMillisU + ",'Common')");

            con.close();
        }
        logger.log(Level.INFO, "finish");

    }

}

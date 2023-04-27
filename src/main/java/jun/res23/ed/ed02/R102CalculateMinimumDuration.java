/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.awt.Color;
import java.awt.Paint;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.alive.UnitInfo;
import static jun.res23.ed.ed02.R103GraphUnitTimeHistory.main;
import jun.res23.ed.util.EdefenseInfo;

/**
 * R100から派生。各加振データの先頭と最後の時刻を得る。
 *
 * @author jun
 */
public class R102CalculateMinimumDuration {

    private static final Logger logger = Logger.getLogger(R102CalculateMinimumDuration.class.getName());

    public static void main(String[] args) {
        String d01q01 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q01_20230215_123406";
        String d01q02 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q02_20230215_125245";
        String d01q03 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q03_20230215_130954";
        String d01q04 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q04_20230215_132603";
        String d01q05 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q05_20230215_133656";
        String d01q06 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q06_20230215_134900";
        String d01q08 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q08_20230215_140021";
        String d01q09 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q09_20230215_141133";
        String d01q10 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q10_20230215_165846";
        String d01q11 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q11_20230215_171757";

//        main1(d01q01);
//        main1(d01q02);
//        main1(d01q03);
//        main1(d01q04);
//        main1(d01q05);
//        main1(d01q06);
//        main1(d01q08);
//        main1(d01q09);
//        main1(d01q10);
//        main1(d01q11);
        String d02q01 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q01_20230217_134008";
        String d02q02 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q02_20230217_135131";
        String d02q03 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q03_20230217_140846";
        String d02q05 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q05_20230217_144801";
        String d02q06 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q06_20230217_150350";
        String d02q07 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q07_20230217_151615";
        String d02q08 = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230217day2/database_HE/D02Q08_20230217_153524";
//          main1(d02q01);
//        main1(d02q02);
//        main1(d02q03);
        main1(d02q05);
//        main1(d02q06);
//        main1(d02q07);
//        main1(d02q08);
    }

    public static void main1(String dburl) {
        ZoneId zone = ZoneId.systemDefault();

// データベースの名前
        // たくさんの図を出力するフォルダ。 null は許容されない。
        // 自動的に上の dburlから実験番号 (D01Q03とか）を抽出する。
        int slashindex = dburl.lastIndexOf("/");
        String testname = dburl.substring(slashindex + 1, slashindex + 7);

        R102CalculateMinimumDuration d = new R102CalculateMinimumDuration(dburl);
        try {
            //          d.setBasePhase("b8:27:eb:97:87:39/01/acc02", "X[gal]");
            d.get();
        } catch (SQLException ex) {
            Logger.getLogger(R102CalculateMinimumDuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final String dburl;
    private final Connection con;

    double latestStartTimeMillis = 0;
    double earliestEndTimeMillis = Double.MAX_VALUE;

    public R102CalculateMinimumDuration(String dburl) {

        this.dburl = dburl;
        this.con = null;

    }

    public R102CalculateMinimumDuration(Connection con, ZonedDateTime start, ZonedDateTime end) {

        this.dburl = null;
        this.con = con;

    }

    // 作ったけど使わなくてもいいかも。
    public ZonedDateTime[] getZonedDateTimes() throws SQLException {
        Connection con;
        if (dburl != null) {
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        HashMap<String, String> map = new HashMap<>();
        for (UnitInfo uni : EdefenseInfo.allunits) {
            map.put(uni.getHardwareAddress(), uni.getName());
        }

        Set<Map.Entry<String, String>> entryset = map.entrySet();
        for (Map.Entry<String, String> s : entryset) {
            String macaddress = s.getKey();
            String name = s.getValue();
            getSingleUnit(con, macaddress, name);
        }

        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime latestStartTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) latestStartTimeMillis + 1), zone);
        ZonedDateTime earliestEndTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) earliestEndTimeMillis - 1), zone);
        con.close();
        return new ZonedDateTime[] {latestStartTime, earliestEndTime};

    }

    public void get() throws SQLException {
        Connection con;
        if (dburl != null) {
            con = DriverManager.getConnection(dburl, "junapp", "");
        } else {
            con = this.con;
        }
        HashMap<String, String> map = new HashMap<>();
        for (UnitInfo uni : EdefenseInfo.allunits) {
            map.put(uni.getHardwareAddress(), uni.getName());
        }

        Set<Map.Entry<String, String>> entryset = map.entrySet();
        for (Map.Entry<String, String> s : entryset) {
            String macaddress = s.getKey();
            String name = s.getValue();
            getSingleUnit(con, macaddress, name);
        }

        Statement st = con.createStatement();
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime latestStartTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) latestStartTimeMillis + 1), zone);
        ZonedDateTime earliestEndTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli((long) earliestEndTimeMillis - 1), zone);
        st.executeUpdate("drop table if exists \"R102MinimumDuration\"");
        st.executeUpdate("create table if not exists  \"R102MinimumDuration\" "
                + "(\"LatestStartTime\" timestamp,\"LatestStartTimeMillis\" long,\"EarliestEndTime\" timestamp,\"EarliestEndTimeMillis\" long) ");

        st.executeUpdate("insert into \"R102MinimumDuration\" (\"LatestStartTime\",\"LatestStartTimeMillis\",\"EarliestEndTime\",\"EarliestEndTimeMillis\") "
                + " values ('" + latestStartTime + "'," + (long) (latestStartTimeMillis + 1) + ",'" + earliestEndTime + "'," + (long) (earliestEndTimeMillis - 1) + ")");

        if (this.dburl != null) {
            con.close();
        }
    }

    public void getSingleUnit(Connection con, String macaddress, String name) throws SQLException {
        DatabaseMetaData md = con.getMetaData();
        // まずは acc02を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/acc02", null);
            if (tables.next()) { // もし acc02が見つかったら
                getSingleAcc02(con, macaddress, name);
            }
        }
        //次にstr01を探す
        {
            ResultSet tables = md.getTables(null, null, macaddress + "%/str01", null);
            if (tables.next()) { // もし str01が見つかったら
                getSingleStr01(con, macaddress, name);
            }
        }
    }

    public void getSingleAcc02(Connection con, String macaddress, String name) throws SQLException {

        Statement st = con.createStatement();

        ResultSet rs;

        rs = st.executeQuery("select min(\"T[ms]\"), max(\"T[ms]\") from \"" + macaddress + "/01/acc02\" ");
        rs.next();
        double min = rs.getDouble(1);
        double max = rs.getDouble(2);
        if (min > latestStartTimeMillis) {
            latestStartTimeMillis = min;
        }
        if (max < earliestEndTimeMillis) {
            earliestEndTimeMillis = max;

        }

    }

    public static Paint colors[] = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.pink, Color.CYAN, Color.LIGHT_GRAY, Color.ORANGE};

    public void getSingleStr01(Connection con, String macaddress, String name) throws SQLException {

        Statement st = con.createStatement();

        ResultSet rs;
        for (int chno = 1; chno <= 8; chno++) {
            rs = st.executeQuery("select min(\"T[ms]\"), max(\"T[ms]\") from \"" + macaddress + "/0" + chno + "/str01\" ");
            rs.next();
            double min = rs.getDouble(1);
            double max = rs.getDouble(2);
            if (min > latestStartTimeMillis) {
                latestStartTimeMillis = min;
            }
            if (max < earliestEndTimeMillis) {
                earliestEndTimeMillis = max;
            }
        }
    }

}

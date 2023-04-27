/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.ed02.R200Resample;
import static jun.res23.ed.ed02.T101SectionHisteresisT.timeHistoryTTable;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 * ひずみの連続データを作成。
 * 2023/03/19 テーブルの列名を変えちゃったのであとのプログラムがちょっと動かないかもしれない。 TIME[s] を TimePerTest[s]にする必要があるかも。
 * @author jun
 */
public class T220CreateTimeHistoryStrain {

    private static final Logger logger = Logger.getLogger(T220CreateTimeHistoryStrain.class.getName());
    public static final String outputSchema = "T220TimeHistoryStrain";
    public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

    public static void main(String[] args) {

        try {
            // 各データベースを読み取って計算する。
            T220CreateTimeHistoryStrain o = new T220CreateTimeHistoryStrain();

            boolean first = true;
//            KasinInfo[] tests = new KasinInfo[]{EdefenseInfo.D01Q01, EdefenseInfo.D01Q02, EdefenseInfo.D01Q03};
            EdefenseKasinInfo[] tests = EdefenseInfo.alltests;
            for (EdefenseKasinInfo test : tests) {
                o.create(test, first);
                first = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(T220CreateTimeHistoryStrain.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private final Connection cono;

    public void create(EdefenseKasinInfo test, boolean first) throws SQLException {
        final String inputDb = "jdbc:h2:tcp://localhost/" + R200Resample.databaseDir.resolve(test.getTestName() + "q");
        logger.log(Level.INFO, "Opening input database " + inputDb);
        Connection coni = DriverManager.getConnection(inputDb, "junapp", "");
        logger.log(Level.INFO, "Opened.");
        DatabaseMetaData md = coni.getMetaData();
        Statement st = coni.createStatement();
        Statement sto = cono.createStatement();
        // 最初の時刻を算出する。
        double startTime = 0;
        if (!first) {
            ResultSet rs = sto.executeQuery("select max(\"TotalTime[s]\") from  \"" + outputSchema + "\".\"k01/01\"");
            rs.next();
            startTime = rs.getDouble(1) + 0.02; // 50Hzとおもっていr。
        }
        // 

        for (UnitInfo unit : EdefenseInfo.allunits) {
            if (unit.getName().startsWith("k")) {
                processAcc(st, sto, test, startTime, unit, md, first);
            } else {
                processStrain(st, sto, test, startTime, unit, md, first);
            }

        }
        st.close();
        sto.close();

        coni.close();
    }

    private void processStrain(Statement st, Statement sto, EdefenseKasinInfo test, double startTime, UnitInfo unit, DatabaseMetaData md, boolean first) throws SQLException {
        for (int i = 0; i < 8; i++) {
            String shortname = unit.getName() + "/0" + (i + 1) + "";

            ResultSet rs = md.getTables(null, "R200Resample", shortname, null);
            if (!rs.next()) {
                logger.log(Level.INFO, "Not found " + shortname);
                rs.close();
                continue;
            }
            rs.close();

            double zeroStrainTotal, zeroStrainPerTest;

            //初期値を計算する。
            rs = st.executeQuery("select avg(\"Strain[με]\") from \"R200Resample\".\"" + shortname + "\""
                    + " where \"TIME[s]\"<2.0");
            rs.next();
            zeroStrainPerTest = rs.getDouble(1);
            rs.close();

            if (first) { // 初めてだったら
                //テーブルを作成する。
                sto.executeUpdate("create schema if not exists \"" + outputSchema + "\"");
                sto.executeUpdate("drop table if exists \"" + outputSchema + "\".\"" + shortname + "\" ");
                sto.executeUpdate("create table \"" + outputSchema + "\".\"" + shortname + "\" "
                        + "(TESTNAME varchar,  \"TotalTime[s]\" real, \"Strain[με]\" real,\"TimePerTest[s]\" real, \"StrainPerTest[με]\" real)");
                zeroStrainTotal = zeroStrainPerTest;
                zeroStrainMap.put(shortname, zeroStrainTotal);
            } else {
                zeroStrainTotal = zeroStrainMap.get(shortname);
            }

            PreparedStatement pso = cono.prepareStatement("insert into  \"" + outputSchema + "\".\"" + shortname + "\""
                    + " (TESTNAME,\"TimePerTest[s]\",\"TotalTime[s]\",\"Strain[με]\", \"StrainPerTest[με]\") "
                    + "values ('" + test.getTestName() + "t',?,?,?,?)");

            rs = st.executeQuery("select \"TIME[s]\",\"Strain[με]\" from \"R200Resample\".\"" + shortname + "\" order by 1");
            while (rs.next()) {
                double time = rs.getDouble(1);
                double strain = rs.getDouble(2);
                double totaltime = time + startTime;
                pso.setDouble(1, time);
                pso.setDouble(2, totaltime);
                pso.setDouble(3, strain - zeroStrainTotal);
                pso.setDouble(4, strain - zeroStrainPerTest);
                pso.addBatch();
            }
            pso.executeBatch();

        }
    }

    private void processAcc(Statement st, Statement sto, EdefenseKasinInfo test, double startTime, UnitInfo unit, DatabaseMetaData md, boolean first) throws SQLException {

        String shortname = unit.getName() + "/01";

        double[] zeroAcc;
        if (first) { // 初めてだったら
            //テーブルを作成する。
            sto.executeUpdate("create schema if not exists \"" + outputSchema + "\"");
            sto.executeUpdate("drop table if exists \"" + outputSchema + "\".\"" + shortname + "\" ");
            sto.executeUpdate("create table \"" + outputSchema + "\".\"" + shortname + "\" "
                    + "(TESTNAME varchar, \"TIME[s]\" real, \"TotalTime[s]\" real, \"X[gal]\" real, \"Y[gal]\" real,\"Z[gal]\" real)");
            //初期値を計算する。
            ResultSet rs = st.executeQuery("select avg(\"X[gal]\") ,avg(\"Y[gal]\") ,avg(\"Z[gal]\") from \"R200Resample\".\"" + shortname + "\""
                    + " where \"TIME[s]\"<2.0");
            rs.next();
            zeroAcc = new double[3];
            zeroAcc[0] = rs.getDouble(1);
            zeroAcc[1] = rs.getDouble(2);
            zeroAcc[2] = rs.getDouble(3);
            zeroAccMap.put(shortname, zeroAcc);
            rs.close();
        } else {
            zeroAcc = zeroAccMap.get(shortname);
        }

        PreparedStatement pso = cono.prepareStatement("insert into  \"" + outputSchema + "\".\"" + shortname + "\""
                + " (TESTNAME,\"TIME[s]\",\"TotalTime[s]\",\"X[gal]\",\"Y[gal]\",\"Z[gal]\") "
                + "values ('" + test.getTestName() + "t',?,?,?,?,?)");

        ResultSet rs = st.executeQuery("select \"TIME[s]\",\"X[gal]\",\"Y[gal]\",\"Z[gal]\" from \"R200Resample\".\"" + shortname + "\" order by 1");
        while (rs.next()) {
            double time = rs.getDouble(1);
            double x = rs.getDouble(2) - zeroAcc[0];
            double y = rs.getDouble(3) - zeroAcc[1];
            double z = rs.getDouble(4) - zeroAcc[2];
            double totaltime = time + startTime;
            pso.setDouble(1, time);
            pso.setDouble(2, totaltime);
            pso.setDouble(3, x);
            pso.setDouble(4, y);
            pso.setDouble(5, z);
            pso.addBatch();
        }
        pso.executeBatch();

    }

    final HashMap<String, Double> zeroStrainMap;
    final HashMap<String, double[]> zeroAccMap;

    private T220CreateTimeHistoryStrain() throws SQLException {

        cono = DriverManager.getConnection(outputDb, "junapp", "");

        zeroStrainMap = new HashMap<>();
        zeroAccMap = new HashMap<>();
    }

    public void close() {
        try {
            cono.close();

        } catch (SQLException ex) {
            Logger.getLogger(T220CreateTimeHistoryStrain.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import jun.res23.ed.ed02.R200Resample;
import static jun.res23.ed.ed02.T101SectionHisteresisT.timeHistoryTTable;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 *
 * @author jun
 */
@Deprecated
public class T200CreateTimeHistoryNM {

    private static final Logger logger = Logger.getLogger(T200CreateTimeHistoryNM.class.getName());
    public static final String outputSchema = "T200TimeHistoryNM";
    public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

    public static void main(String[] args) {
        main(EdefenseInfo.LA3S1);
        main(EdefenseInfo.LA3S2);
        main(EdefenseInfo.LA3S3);
        main(EdefenseInfo.LA3S4);
        main(EdefenseInfo.LA3S5);
    }

    public static void main(BeamSectionInfo section) {
        try {
            // 各データベースを読み取って計算する。
            T200CreateTimeHistoryNM o = new T200CreateTimeHistoryNM(section);
            o.clearTable();
            boolean first = true;
            for (EdefenseKasinInfo test : EdefenseInfo.alltests) {
                o.create(test, first);
                first = false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(T200CreateTimeHistoryNM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private final BeamSectionInfo section;
    public double zeroMoment, zeroAxial;

    private void clearTable() throws SQLException {
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        sto.executeUpdate("create schema if not exists \""+outputSchema+"\"");
        sto.executeUpdate("create table if not exists \"" + outputSchema + "\".\"" + section.getName() + "\" "
                + "(TESTNAME varchar,\"TIME[s]\" real ,\"TotalTime[s]\" real ,\"AxialForce[kN]\" real , \"BendingMoment[kNm]\" real) ");
        sto.executeUpdate("truncate table   \"" + outputSchema + "\".\"" + section.getName() + "\"");
        cono.close();

    }

    private void create(EdefenseKasinInfo test, boolean initializeToZero) throws SQLException {

        final String inputDb = "jdbc:h2:tcp://localhost/" + R200Resample.databaseDir.resolve(test.getTestName() + "q");
        logger.log(Level.INFO, "Opening input database " + inputDb);
        Connection coni = DriverManager.getConnection(inputDb, "junapp", "");
        Statement sti = coni.createStatement();
        logger.log(Level.INFO, "opened. Opening output database " + outputDb);
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        logger.log(Level.INFO, "opened.");

        sto.executeUpdate("create table if not exists  \"" + outputSchema + "\".\"" + section.getName() + "\" "
                + "(TESTNAME varchar,\"TIME[s]\" real ,\"TotalTime[s]\" real ,\"AxialForce[kN]\" real , \"BendingMoment[kNm]\" real) ");
        sto.executeUpdate("delete from  \"" + outputSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + test.getTestName() + "t'");

        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZ4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getInnerZx();//  kNm /με

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"+"
                + "\"" + section.getLLname() + "_Strain[με]\"+"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMoment = "(" + EZ4 + ")*("
                + "\"" + section.getULname() + "_Strain[με]\"+"
                + "\"" + section.getURname() + "_Strain[με]\"-"
                + "\"" + section.getLLname() + "_Strain[με]\"-"
                + "\"" + section.getLRname() + "_Strain[με]\""
                + ")"; // 上側圧縮、すなわち下側引張が正。

//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        if (initializeToZero) {
            rs = sti.executeQuery("select avg(" + axialForce + "),avg(" + bendingMoment + ") from \"" + timeHistoryTTable + "\""
                    + " where \"TIME[s]\"<2.0");
            rs.next();
            zeroAxial = rs.getDouble(1);
            zeroMoment = rs.getDouble(2);
            rs.close();
        }
        rs = sto.executeQuery("select max(\"TotalTime[s]\") from  \"" + outputSchema + "\".\"" + section.getName() + "\"");
        rs.next();
        double startTime = rs.getDouble(1) + 0.02; // 50Hzとおもっていr。
        if (startTime == 0.02) {
            startTime = 0.0;
        }

        rs = sti.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + zeroAxial + "),(" + bendingMoment + ")-(" + zeroMoment + ") from \"" + timeHistoryTTable + "\"");
        PreparedStatement pso = cono.prepareStatement("insert into  \"" + outputSchema + "\".\"" + section.getName() + "\" (TESTNAME,\"TIME[s]\",\"TotalTime[s]\",\"AxialForce[kN]\", \"BendingMoment[kNm]\") "
                + "values ('" + test.getTestName() + "t',?,?,?,?)");

        while (rs.next()) {
            double time = rs.getDouble(1);
            double axial = rs.getDouble(2);
            double moment = rs.getDouble(3);
            double totaltime = time + startTime;
            pso.setDouble(1, time);
            pso.setDouble(2, totaltime);
            pso.setDouble(3, axial);
            pso.setDouble(4, moment);
            pso.addBatch();
        }

        pso.executeBatch();

        coni.close();
        cono.close();

    }

    private T200CreateTimeHistoryNM(BeamSectionInfo section) {
        this.section = section;
        this.zeroMoment = 0.0;
        this.zeroAxial = 0.0;

    }

}

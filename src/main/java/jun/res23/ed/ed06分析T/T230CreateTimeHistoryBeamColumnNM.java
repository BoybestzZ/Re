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
import jun.res23.ed.util.ColumnSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 * R200Resampleで作成された同期時刻歴データを読み込んで、 res22ed06.mv.dbに出力する。
 *
 * @author jun
 * @deprecated 内容的には間違ってはいない。が、CS2FA3T という名前が CS2FA4T に使われちゃってて、データが混在しちゃったので、
 * ひずみデータを出力するようにして、T231で実行した。
 */
@Deprecated
public class T230CreateTimeHistoryBeamColumnNM {

    private static final Logger logger = Logger.getLogger(T230CreateTimeHistoryBeamColumnNM.class.getName());
    public static final String outputSchema = "T230TimeHistoryNM";
    public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

    public static void main(String[] args) {
//        BeamSectionInfo[] sections = new BeamSectionInfo[]{EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};

        BeamSectionInfo[] beamsections = { //            EdefenseInfo.LAAS1, EdefenseInfo.LAAS2, EdefenseInfo.LAAS3, EdefenseInfo.LAAS4, EdefenseInfo.LAAS5,
        //            EdefenseInfo.LABS1, EdefenseInfo.LABS2, EdefenseInfo.LABS3, EdefenseInfo.LABS4, EdefenseInfo.LABS5,
        //            EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5
        //                EdefenseInfo.LA4S1, EdefenseInfo.LA4S2, EdefenseInfo.LA4S3, EdefenseInfo.LA4S4, EdefenseInfo.LA4S5
        };
        ColumnSectionInfo[] columnsections = {};
//            EdefenseInfo.CS2FA3B, EdefenseInfo.CS2FA3C, EdefenseInfo.CS2FA3T,
//            EdefenseInfo.CS3FA3B, EdefenseInfo.CS3FA3C, EdefenseInfo.CS3FA3T,
//            EdefenseInfo.CS2FA4B, EdefenseInfo.CS2FA4C, EdefenseInfo.CS2FA4T,
//            EdefenseInfo.CS3FA4B, EdefenseInfo.CS3FA4C, EdefenseInfo.CS3FA4T,
//            EdefenseInfo.CS2FB3B, EdefenseInfo.CS2FB3C, EdefenseInfo.CS2FB3T,
//            EdefenseInfo.CS3FB3B, EdefenseInfo.CS3FB3C, EdefenseInfo.CS3FB3T,
//            EdefenseInfo.CS2FB4B, EdefenseInfo.CS2FB4C, EdefenseInfo.CS2FB4T,
//            EdefenseInfo.CS3FB4B, EdefenseInfo.CS3FB4C, EdefenseInfo.CS3FB4T};
        try {
            // 各データベースを読み取って計算する。
            T230CreateTimeHistoryBeamColumnNM o = new T230CreateTimeHistoryBeamColumnNM(beamsections, columnsections);
            o.clearTable();
            boolean first = true;
            for (EdefenseKasinInfo test : EdefenseInfo.alltests) {
                o.create(test, first);
                first = false;

            }

        } catch (SQLException ex) {
            Logger.getLogger(T230CreateTimeHistoryBeamColumnNM.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }
    public double[] zeroMomentBeam, zeroAxialBeam;
    private final BeamSectionInfo[] beamsections;
    private final Connection cono;
    private final ColumnSectionInfo[] columnsections;
    private final double[] zeroMomentNSColumn;
    private final double[] zeroMomentEWColumn;
    private final double[] zeroAxialColumn;

    private void clearTable() throws SQLException {
        Statement sto = cono.createStatement();
        sto.executeUpdate("create schema if not exists \"" + outputSchema + "\"");
        for (BeamSectionInfo section : beamsections) {
            sto.executeUpdate("create table if not exists \"" + outputSchema + "\".\"" + section.getName() + "\" "
                    + "(TESTNAME varchar,\"TIME[s]\" real ,\"TotalTime[s]\" real ,\"AxialForce[kN]\" real , \"BendingMoment[kNm]\" real) ");
            sto.executeUpdate("truncate table   \"" + outputSchema + "\".\"" + section.getName() + "\"");
        }

        for (ColumnSectionInfo section : columnsections) {
            sto.executeUpdate("create table if not exists  \"" + outputSchema + "\".\"" + section.getName() + "\" "
                    + "(TESTNAME varchar,\"TIME[s]\" real ,\"TotalTime[s]\" real ,\"AxialForce[kN]\" real , \"BendingMomentNS[kNm]\" real,"
                    + "\"BendingMomentEW[kNm]\" real, \"StrainN[με]\" real,\"StrainE[με]\" real,\"StrainW[με]\" real,\"StrainS[με]\" real"
                    + ") ");
            sto.executeUpdate("truncate table   \"" + outputSchema + "\".\"" + section.getName() + "\"");
        }
        sto.close();

    }

    public void create(EdefenseKasinInfo test, boolean first) throws SQLException {
        final String inputDb = "jdbc:h2:tcp://localhost/" + R200Resample.databaseDir.resolve(test.getTestName() + "q");
        logger.log(Level.INFO, "Opening input database " + inputDb);
        Connection coni = DriverManager.getConnection(inputDb, "junapp", "");
        logger.log(Level.INFO, "Opened.");

        for (int i = 0; i < beamsections.length; i++) {
            createBeam(coni, test, i, first);
        }

        for (int i = 0; i < columnsections.length; i++) {
            createColumn(coni, test, i, first);
        }
        coni.close();
    }

    private void createBeam(Connection coni, EdefenseKasinInfo test, int sectionNo, boolean initializeToZero) throws SQLException {

        Statement sti = coni.createStatement();

        Statement sto = cono.createStatement();
        BeamSectionInfo section = beamsections[sectionNo];

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
            zeroAxialBeam[sectionNo] = rs.getDouble(1);
            zeroMomentBeam[sectionNo] = rs.getDouble(2);
            rs.close();
        }
        rs = sto.executeQuery("select max(\"TotalTime[s]\") from  \"" + outputSchema + "\".\"" + section.getName() + "\"");
        rs.next();
        double startTime = rs.getDouble(1) + 0.02; // 50Hzとおもっていr。
        if (startTime == 0.02) {
            startTime = 0.0;
        }

        rs = sti.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + zeroAxialBeam[sectionNo] + "),(" + bendingMoment + ")-(" + zeroMomentBeam[sectionNo] + ") from \"" + timeHistoryTTable + "\"");
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

    }

    private void createColumn(Connection coni, EdefenseKasinInfo test, int sectionNo, boolean initializeToZero) throws SQLException {

        Statement sti = coni.createStatement();

        Statement sto = cono.createStatement();
        ColumnSectionInfo section = columnsections[sectionNo];

        sto.executeUpdate("create table if not exists  \"" + outputSchema + "\".\"" + section.getName() + "\" "
                + "(TESTNAME varchar,\"TIME[s]\" real ,\"TotalTime[s]\" real ,\"AxialForce[kN]\" real , \"BendingMomentNS[kNm]\" real,"
                + "\"BendingMomentEW[kNm]\" real, \"StrainN[με]\" real,\"StrainE[με]\" real,\"StrainW[με]\" real,\"StrainS[με]\" real"
                + ") ");
        sto.executeUpdate("delete from  \"" + outputSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + test.getTestName() + "t'");

        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZns2 = 1e-3 * 1e-6 * 0.5 * section.getE() * section.getZns();//  kNm /με
        double EZew2 = 1e-3 * 1e-6 * 0.5 * section.getE() * section.getZew();//  kNm /με

        String axialForce = "(" + EA4 + ")*("
                + "\"" + section.getNorthName() + "_Strain[με]\"+"
                + "\"" + section.getEastName() + "_Strain[με]\"+"
                + "\"" + section.getWestName() + "_Strain[με]\"+"
                + "\"" + section.getSouthName() + "_Strain[με]\""
                + ")"; // 引張が正。 (ひずみ値は圧縮が正であるが、EA4を負にしているため、引張軸力が正となる。）
        String bendingMomentNS = "(" + EZns2 + ")*("
                + "\"" + section.getNorthName() + "_Strain[με]\"-"
                + "\"" + section.getSouthName() + "_Strain[με]\""
                + ")"; // N側圧縮、すなわちS側引張が正。
        String bendingMomentEW = "(" + EZew2 + ")*("
                + "\"" + section.getEastName() + "_Strain[με]\"-"
                + "\"" + section.getWestName() + "_Strain[με]\""
                + ")"; // E側圧縮、すなわちW側引張が正。

//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        if (initializeToZero) {
            rs = sti.executeQuery("select avg(" + axialForce + "),avg(" + bendingMomentNS + ")"
                    + ",avg(" + bendingMomentEW + ") from \"" + timeHistoryTTable + "\""
                    + " where \"TIME[s]\"<2.0");
            rs.next();
            zeroAxialColumn[sectionNo] = rs.getDouble(1);
            zeroMomentNSColumn[sectionNo] = rs.getDouble(2);
            zeroMomentEWColumn[sectionNo] = rs.getDouble(3);
            rs.close();
        }
        rs = sto.executeQuery("select max(\"TotalTime[s]\") from  \"" + outputSchema + "\".\"" + section.getName() + "\"");
        rs.next();
        double startTime = rs.getDouble(1) + 0.02; // 50Hzとおもっていr。
        if (startTime == 0.02) {
            startTime = 0.0;
        }

        rs = sti.executeQuery("select \"TIME[s]\",(" + axialForce + ")-(" + zeroAxialColumn[sectionNo] + "),"
                + "(" + bendingMomentNS + ")-(" + zeroMomentNSColumn[sectionNo] + "),"
                + "(" + bendingMomentEW + ")-(" + zeroMomentEWColumn[sectionNo] + "),"
                + "\"" + section.getNorthName() + "_Strain[με]\","
                + "\"" + section.getEastName() + "_Strain[με]\","
                + "\"" + section.getWestName() + "_Strain[με]\","
                + "\"" + section.getSouthName() + "_Strain[με]\""
                + " from \"" + timeHistoryTTable + "\"");
        PreparedStatement pso = cono.prepareStatement("insert into  \"" + outputSchema + "\".\"" + section.getName() + "\" "
                + "(TESTNAME,\"TIME[s]\",\"TotalTime[s]\",\"AxialForce[kN]\", \"BendingMomentNS[kNm]\", \"BendingMomentEW[kNm]\","
                + "\"StrainN[με]\" ,\"StrainE[με]\" ,\"StrainW[με]\" ,\"StrainS[με]\" "
                + ") "
                + "values ('" + test.getTestName() + "t',?,?,?,?,?,?,?,?,?)");

        while (rs.next()) {
            double time = rs.getDouble(1);
            double axial = rs.getDouble(2);
            double momentNS = rs.getDouble(3);
            double momentEW = rs.getDouble(4);
            double north = rs.getDouble(5);
            double east = rs.getDouble(6);
            double west = rs.getDouble(7);
            double south = rs.getDouble(8);
            double totaltime = time + startTime;
            pso.setDouble(1, time);
            pso.setDouble(2, totaltime);
            pso.setDouble(3, axial);
            pso.setDouble(4, momentNS);
            pso.setDouble(5, momentEW);
            pso.addBatch();
        }

        pso.executeBatch();

    }

    private T230CreateTimeHistoryBeamColumnNM(BeamSectionInfo[] sections, ColumnSectionInfo[] columnsections) throws SQLException {
        this.beamsections = sections;
        this.columnsections = columnsections;
        this.zeroMomentBeam = new double[sections.length];
        this.zeroAxialBeam = new double[sections.length];
        this.zeroMomentNSColumn = new double[columnsections.length];
        this.zeroMomentEWColumn = new double[columnsections.length];
        this.zeroAxialColumn = new double[columnsections.length];

        cono = DriverManager.getConnection(outputDb, "junapp", "");

    }

    public void close() {
        try {
            cono.close();

        } catch (SQLException ex) {
            Logger.getLogger(T230CreateTimeHistoryBeamColumnNM.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}

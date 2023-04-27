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
import jun.res23.ed.ed02.R200Resample;
import static jun.res23.ed.ed02.T101SectionHisteresisT.timeHistoryTTable;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 * T231を直して、フランジごとの面内曲げモーメントを計算する。
 *
 * @author jun
 */
public class T270CreateTimeHistoryLAMomnet {

    private static final Logger logger = Logger.getLogger(T270CreateTimeHistoryLAMomnet.class.getName());
    public static final String outputSchema = "T270TimeHistoryLAM";
    public static final String outputDb = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";

    public static void main(String[] args) {
//        BeamSectionInfo[] sections = new BeamSectionInfo[]{EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5};

        BeamSectionInfo[] beamsections = {
            EdefenseInfo.LAAS1, EdefenseInfo.LAAS2, EdefenseInfo.LAAS3, EdefenseInfo.LAAS4, EdefenseInfo.LAAS5,
            EdefenseInfo.LABS1, EdefenseInfo.LABS2, EdefenseInfo.LABS3, EdefenseInfo.LABS4, EdefenseInfo.LABS5,
            EdefenseInfo.LA3S1, EdefenseInfo.LA3S2, EdefenseInfo.LA3S3, EdefenseInfo.LA3S4, EdefenseInfo.LA3S5,
            EdefenseInfo.LA4S1, EdefenseInfo.LA4S2, EdefenseInfo.LA4S3, EdefenseInfo.LA4S4, EdefenseInfo.LA4S5
        };

        try {
            // 各データベースを読み取って計算する。
            T270CreateTimeHistoryLAMomnet o = new T270CreateTimeHistoryLAMomnet(beamsections);
            o.clearTable();
            boolean first = true;
            for (EdefenseKasinInfo test : EdefenseInfo.alltests) {
                o.create(test, first);
                first = false;

            }
            o.close();

        } catch (SQLException ex) {
            Logger.getLogger(T270CreateTimeHistoryLAMomnet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private final BeamSectionInfo[] beamsections;
    private final Connection cono;
    private final double[] zeroStrainBeamUL;
    private final double[] zeroStrainBeamUR;
    private final double[] zeroStrainBeamLL;
    private final double[] zeroStrainBeamLR;

    private void clearTable() throws SQLException {
        Statement sto = cono.createStatement();
        sto.executeUpdate("create schema if not exists \"" + outputSchema + "\"");
        for (BeamSectionInfo section : beamsections) {
            createTable(sto, section);

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

        coni.close();
    }

    private final void createTable(Statement sto, BeamSectionInfo section) throws SQLException {
        sto.executeUpdate("create table if not exists  \"" + outputSchema + "\".\"" + section.getName() + "\" "
                + " (TESTNAME varchar,\"TimePerTest[s]\" real,\"TotalTime[s]\" real,\"TMRTime[s]\" real, "
                + "\"UpperAxialStrain[με]\" real, \"LowerAxialStrain[με]\" real,"
                + "\"UpperBendingStrain[με]\" real, \"LowerBendingStrain[με]\" real,"
                + "\"UpperAxialStrainPerTest[με]\" real, \"LowerAxialStrainPerTest[με]\" real,"
                + "\"UpperBendingStrainPerTest[με]\" real, \"LowerBendingStrainPerTest[με]\" real,"
                + "\"UpperBendingRatioPerTest\" real, \"LowerBendingRatioPerTest\" real)"
        );

    }

    /**
     *
     * @param coni
     * このtest用の時刻歴データが含まれたデータベースへのコネクション。（本来はこの関数の中でconnectionを張るべきとは思うが、接続に時間がかかるので、、、）
     * @param test 加振情報。上記の coni に対応したものである必要がある。
     * @param sectionNo
     * @param initializeToZero
     * @throws SQLException
     */
    private void createBeam(Connection coni, EdefenseKasinInfo test, int sectionNo, boolean initializeToZero) throws SQLException {

        Statement sti = coni.createStatement();

        Statement sto = cono.createStatement();
        BeamSectionInfo section = beamsections[sectionNo];
        createTable(sto, section);

        sto.executeUpdate("delete from  \"" + outputSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + test.getTestName() + "t'");

        double EA4 = -1e-3 * 1e-6 * 0.25 * section.getE() * section.getA(); // kN / με
        double EZ4 = 1e-3 * 1e-6 * 0.25 * section.getE() * section.getInnerZx();//  kNm /με
//
//        String upperBen = "(" + EZ4 + ")*("
//                + "\"" + section.getULname() + "_Strain[με]\"-"
//                + "\"" + section.getURname() + "_Strain[με]\""
//                + ")"; // 左側圧縮、すなわち右側引張が正。
//
//        String lowerMoment = "(" + EZ4 + ")*("
//                + "\"" + section.getLLname() + "_Strain[με]\"-"
//                + "\"" + section.getLRname() + "_Strain[με]\""
//                + ")"; // 左側圧縮、すなわち右側引張が正。

//            aveAxial=-516.8244321318311; aveMoment=30.26672208983202;
        ResultSet rs;
        double zeroAxialPerTest, zeroMomentPerTest;
// ひずみの初期値を計算する。
        rs = sti.executeQuery("select " // avg(" + upperBen + "),avg(" + lowerMoment + "), "
                + "avg(\"" + section.getULname() + "_Strain[με]\"),"
                + "avg(\"" + section.getURname() + "_Strain[με]\"),"
                + "avg(\"" + section.getLLname() + "_Strain[με]\"),"
                + "avg(\"" + section.getLRname() + "_Strain[με]\"),"
                + "from \"" + timeHistoryTTable + "\""
                + " where \"TIME[s]\"<2.0");
        rs.next();
        double zeroULPerTest = rs.getDouble(1);
        double zeroURPerTest = rs.getDouble(2);
        double zeroLLPerTest = rs.getDouble(3);
        double zeroLRPerTest = rs.getDouble(4);

        rs.close();

        if (initializeToZero) {
            //   zeroAxialBeam[sectionNo] = zeroAxialPerTest;
            //  zeroMomentBeam[sectionNo] = zeroMomentPerTest;
            zeroStrainBeamUL[sectionNo] = zeroULPerTest;
            zeroStrainBeamUR[sectionNo] = zeroURPerTest;
            zeroStrainBeamLL[sectionNo] = zeroLLPerTest;
            zeroStrainBeamLR[sectionNo] = zeroLRPerTest;
        }
        rs = sto.executeQuery("select max(\"TotalTime[s]\") from  \"" + outputSchema + "\".\"" + section.getName() + "\"");
        rs.next();
        double startTime = rs.getDouble(1) + 0.02; // 50Hzとおもっていr。
        if (startTime == 0.02) {
            startTime = 0.0;
        }

        rs = sti.executeQuery("select \"TIME[s]\", "
                + "\"" + section.getULname() + "_Strain[με]\","
                + "\"" + section.getURname() + "_Strain[με]\","
                + "\"" + section.getLLname() + "_Strain[με]\","
                + "\"" + section.getLRname() + "_Strain[με]\""
                + "from \"" + timeHistoryTTable + "\"");
        PreparedStatement pso = cono.prepareStatement("insert into  \"" + outputSchema + "\".\"" + section.getName() + "\""
                + " (TESTNAME,\"TimePerTest[s]\",\"TotalTime[s]\",\"TMRTime[s]\", "
                + "\"UpperAxialStrain[με]\", \"LowerAxialStrain[με]\","
                + "\"UpperBendingStrain[με]\", \"LowerBendingStrain[με]\","
                + "\"UpperAxialStrainPerTest[με]\", \"LowerAxialStrainPerTest[με]\","
                + "\"UpperBendingStrainPerTest[με]\", \"LowerBendingStrainPerTest[με]\","
                + "\"UpperBendingRatioPerTest\", \"LowerBendingRatioPerTest\""
                + ") values ('" + test.getTestName() + "t',?,?,?,?,?,?,?,?,?,?,?,?,?)"
        );

        double tmrTimeDiffSeconds = test.getTmrTimeDiffSeconds();
        while (rs.next()) {
            double time = rs.getDouble(1);
            double ul = rs.getDouble(2);
            double ur = rs.getDouble(3);
            double ll = rs.getDouble(4);
            double lr = rs.getDouble(5);

            double ul0 = ul - zeroStrainBeamUL[sectionNo];
            double ur0 = ur - zeroStrainBeamUR[sectionNo];
            double ll0 = ll - zeroStrainBeamLL[sectionNo];
            double lr0 = lr - zeroStrainBeamLR[sectionNo];

            double ul1 = ul - zeroULPerTest;
            double ur1 = ur - zeroURPerTest;
            double ll1 = ll - zeroLLPerTest;
            double lr1 = lr - zeroLRPerTest;

            double totaltime = time + startTime;
            double tmrtime = time + tmrTimeDiffSeconds;

            double upperBendingStrain0 = (ul0 - ur0) * 0.5; // 左側圧縮正
            double lowerBendingStrain0 = (ll0 - lr0) * 0.5; // 左側圧縮正
            double upperAxialStrain0 = (ul0 + ur0) * 0.5; // 左側圧縮正
            double lowerAxialStrain0 = (ll0 + lr0) * 0.5; // 左側圧縮正

            double upperBendingStrain1 = (ul1 - ur1) * 0.5; // 左側圧縮正
            double lowerBendingStrain1 = (ll1 - lr1) * 0.5; // 左側圧縮正
            double upperAxialStrain1 = (ul1 + ur1) * 0.5; // 左側圧縮正
            double lowerAxialStrain1 = (ll1 + lr1) * 0.5; // 左側圧縮正
            double upperBendingRatio1 = (ul1 - ur1) / (ul1 + ur1); // 左側圧縮正
            double lowerBendingRatio1 = (ll1 - lr1) / (ll1 + lr1); // 左側圧縮正

            pso.setDouble(1, time);
            pso.setDouble(2, totaltime);
            pso.setDouble(3, tmrtime);
            pso.setDouble(4, upperAxialStrain0);
            pso.setDouble(5, lowerAxialStrain0);
            pso.setDouble(6, upperBendingStrain0);
            pso.setDouble(7, lowerBendingStrain0);
            
            pso.setDouble(8, upperAxialStrain1);
            pso.setDouble(9, lowerAxialStrain1);
            pso.setDouble(10, upperBendingStrain1);
            pso.setDouble(11, lowerBendingStrain1);
            
            pso.setDouble(12, upperBendingRatio1);
            pso.setDouble(13, lowerBendingRatio1);
            pso.addBatch();
        }

        pso.executeBatch();

    }

    private T270CreateTimeHistoryLAMomnet(BeamSectionInfo[] sections) throws SQLException {
        this.beamsections = sections;

        this.zeroStrainBeamUL = new double[sections.length];
        this.zeroStrainBeamUR = new double[sections.length];
        this.zeroStrainBeamLL = new double[sections.length];
        this.zeroStrainBeamLR = new double[sections.length];

        cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement st = cono.createStatement();
        st.executeUpdate("create schema if not exists \"" + outputSchema + "\"");

    }

    public void close() {
        try {
            cono.close();

        } catch (SQLException ex) {
            Logger.getLogger(T270CreateTimeHistoryLAMomnet.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.data.ResultSetUtils;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.ColumnSectionInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 * A100からA300を作成。A100ではピーク周波数を一つのひずみゲージのスペクトルだけから判断していたが、
 * A300では、断面の曲げモーメントを算出してそのピーク値を得ている。 が、結果としては A100と同じ結果となっているので、どちらでもよい。
 * 2023/06/11 : A300からA310を作成。 ひずみの値を個別に出力する。
 *
 * @author jun
 */
public class A310SectionNMtest {

    public static Path databaseDir = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\R140DatabaseQ");
    public static String fourierTable = "R155FourierU"; //"R152FourierS";
    public static String driftTable = "R176StoryDriftU";
    // ↑これは U のフーリエ変換による層間変形を示している。ここのなかでは (k01+k02-k03-k04)*0.5 として相対加速度を計算している。すなわち、2層分の相対加速度である。
    // なお、R175は k01とk02のEW方向の符号が逆転していることを考慮している。
    public static String dburl = "jdbc:h2:C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230721";
    public static String outputTable = "A310SectionNMtest22";

    public static String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", "D01Q10", "D01Q11",
        "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06", "D02Q07", "D02Q08",
        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08", "D03Q09"};
//    public static String[] testnames = {"D01Q01"};

    private static final Logger logger = Logger.getLogger(A310SectionNMtest.class.getName());

    public static void main(String[] args) {

        try {
            try (Connection con = DriverManager.getConnection(dburl, "junapp", ""); Statement st = con.createStatement()) {
                st.executeUpdate("drop table if exists \"" + outputTable + "\"");
                st.executeUpdate("create table \"" + outputTable + "\" (_NO identity, TESTNAME varchar, \"Freq[Hz]\" double, SECTION varchar, "
                        + "\"AxialA[N*s]\" real , \"AxialP[rad]\" real ,"
                        + "\"MomentXA[Nm*s]\"real , \"MomentXP[rad]\" real ,"
                        + "\"MomentYA[Nm*s]\"real , \"MomentYP[rad]\" real ,"
                        + "\"StoryDriftA[gal*s]\" real,\"StoryDriftP[rad]\" real,"
                        + "\"StoryDriftA2[gal*s]\" real,\"StoryDriftP2[rad]\" real,"
                        + "\"StoryDriftA3[gal*s]\" real,\"StoryDriftP3[rad]\" real,"
                        + "\"StoryDriftRatio(3/2)\" real,"
                        + " \"NMsatio[m]\" real,"
                        + "\"StiffnessAxialA[N/m]\" real, \"StiffnessAxialP[rad]\" real, "
                        + "\"StiffnessMomentXA[Nm/m]\" real, \"StiffnessMomentXP[rad]\" real, "
                        + "\"StiffnessMomentYA[Nm/m]\" real, \"StiffnessMomentYP[rad]\" real,"
                        + "\"Strain1A[με*s]\" real, \"Strain1P[rad]\" real,"
                        + "\"Strain2A[με*s]\" real, \"Strain2P[rad]\" real,"
                        + "\"Strain3A[με*s]\" real, \"Strain3P[rad]\" real,"
                        + "\"Strain4A[με*s]\" real, \"Strain4P[rad]\" real"
                        + ")"
                );
                for (String testname : testnames) {
                    logger.log(Level.INFO, testname);
                    double freqNS = getPeakFreq(testname, fourierTable, EdefenseInfo.LA3S2, con);
                    double freqEW = getPeakFreq(testname, fourierTable, EdefenseInfo.LABS2, con);
                    Complex[] storyDrift = getStoryDrift(testname, driftTable/* "R170StoryDriftR"*/, freqNS, freqEW); // この時点では [gal*s]

                    Complex storyDriftNS = storyDrift[0];
                    Complex storyDriftEW = storyDrift[1];
                    Complex storyDriftNS2 = storyDrift[2];
                    Complex storyDriftEW2 = storyDrift[3];
                    Complex storyDriftNS3 = storyDrift[4];
                    Complex storyDriftEW3 = storyDrift[5];
                    for (BeamSectionInfo section : EdefenseInfo.beamSectionsNS) {
//                        logger.log(Level.INFO, section.getName());
                        Complex[] nm = getBeamNM(testname, freqNS, fourierTable, section);
                        double omega2 = 4 * Math.PI * Math.PI * freqNS * freqNS;
                        Complex stiffnessAxial = nm[0].divide(storyDriftNS).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                        Complex stiffnessMomentX = nm[1].divide(storyDriftNS).multiply(omega2 * 100); //[Nm/(cm/s2) * (100/s2) = [Nm/m]
                        Complex stiffnessMomentY = nm[2].divide(storyDriftNS).multiply(omega2 * 100);

                        st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME,\"Freq[Hz]\", SECTION,"
                                + "\"AxialA[N*s]\", \"AxialP[rad]\", "
                                + "\"MomentXA[Nm*s]\", \"MomentXP[rad]\", "
                                + "\"MomentYA[Nm*s]\", \"MomentYP[rad]\", "
                                + "\"NMsatio[m]\","
                                + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\","
                                + "\"StoryDriftA2[gal*s]\",\"StoryDriftP2[rad]\","
                                + "\"StoryDriftA3[gal*s]\",\"StoryDriftP3[rad]\","
                                + "\"StoryDriftRatio(3/2)\","
                                + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                + ") values ('" + testname + "'," + freqNS + ",'" + section.getName() + "'"
                                + "," + nm[0].abs() + "," + nm[0].getArgument()
                                + "," + nm[1].abs() + "," + nm[1].getArgument()
                                + "," + nm[2].abs() + "," + nm[2].getArgument()
                                + "," + (nm[1].abs() / nm[0].abs())
                                + "," + storyDriftNS.abs() + "," + storyDriftNS.getArgument()
                                + "," + storyDriftNS2.abs() + "," + storyDriftNS2.getArgument()
                                + "," + storyDriftNS3.abs() + "," + storyDriftNS3.getArgument()
                                + "," + (storyDriftNS3.abs() / storyDriftNS2.abs())
                                + "," + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                + ")");
                    }
                    for (BeamSectionInfo section : EdefenseInfo.beamSectionsEW) {
//                        logger.log(Level.INFO, section.getName());
                        Complex[] nm = getBeamNM(testname, freqEW, fourierTable/* "R152FourierS"*/, section);
                        double omega2 = 4 * Math.PI * Math.PI * freqEW * freqEW;
                        Complex stiffnessAxial = nm[0].divide(storyDriftEW).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                        Complex stiffnessMomentX = nm[1].divide(storyDriftEW).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]
                        Complex stiffnessMomentY = nm[2].divide(storyDriftEW).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]

                        st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME,\"Freq[Hz]\", SECTION,"
                                + "\"AxialA[N*s]\", \"AxialP[rad]\", "
                                + "\"MomentXA[Nm*s]\", \"MomentXP[rad]\", "
                                + "\"MomentYA[Nm*s]\", \"MomentYP[rad]\", "
                                + "\"NMsatio[m]\","
                                + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\","
                                + "\"StoryDriftA2[gal*s]\",\"StoryDriftP2[rad]\","
                                + "\"StoryDriftA3[gal*s]\",\"StoryDriftP3[rad]\","
                                + "\"StoryDriftRatio(3/2)\","
                                + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                + ") values ('" + testname + "'," + freqEW + ",'" + section.getName() + "'"
                                + "," + nm[0].abs() + "," + nm[0].getArgument()
                                + "," + nm[1].abs() + "," + nm[1].getArgument()
                                + "," + nm[2].abs() + "," + nm[2].getArgument()
                                + "," + (nm[1].abs() / nm[0].abs())
                                + "," + storyDriftEW.abs() + "," + storyDriftEW.getArgument()
                                + "," + storyDriftEW2.abs() + "," + storyDriftEW2.getArgument()
                                + "," + storyDriftEW3.abs() + "," + storyDriftEW3.getArgument() 
                                + "," + (storyDriftEW3.abs() / storyDriftEW2.abs())
                                + "," + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                + ")");
                    }

                    for (ColumnSectionInfo section : EdefenseInfo.columnSections) {
                        //                        logger.log(Level.INFO, section.getName());
                        {
                            Complex strains[] = getColumnStrainsEW(testname, freqEW, fourierTable/* "R152FourierS"*/, section);

                            Complex[] nm = convertColumnStrainsToNM(strains, section.getE(), section.getA(), section.getZew(), section.getZns());
                            double omega2 = 4 * Math.PI * Math.PI * freqEW * freqEW;
                            Complex stiffnessAxial = nm[0].divide(storyDriftEW).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                            Complex stiffnessMomentX = nm[1].divide(storyDriftEW).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]
                            Complex stiffnessMomentY = nm[2].divide(storyDriftEW).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]

                            st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME,\"Freq[Hz]\", SECTION,"
                                    + "\"AxialA[N*s]\", \"AxialP[rad]\", "
                                    + "\"MomentXA[Nm*s]\", \"MomentXP[rad]\", "
                                    + "\"MomentYA[Nm*s]\", \"MomentYP[rad]\", "
                                    + "\"NMsatio[m]\","
                                    + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\","
                                    + "\"StoryDriftA2[gal*s]\",\"StoryDriftP2[rad]\","
                                    + "\"StoryDriftA3[gal*s]\",\"StoryDriftP3[rad]\","
                                    + "\"StoryDriftRatio(3/2)\","
                                    + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                    + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                    + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\", "
                                    + "\"Strain1A[με*s]\" , \"Strain1P[rad]\" ,"
                                    + "\"Strain2A[με*s]\" , \"Strain2P[rad]\" ,"
                                    + "\"Strain3A[με*s]\" , \"Strain3P[rad]\" ,"
                                    + "\"Strain4A[με*s]\" , \"Strain4P[rad]\" "
                                    + ") values ('" + testname + "'," + freqEW + ",'" + section.getName() + "ew'"
                                    + "," + nm[0].abs() + "," + nm[0].getArgument()
                                    + "," + nm[1].abs() + "," + nm[1].getArgument()
                                    + "," + nm[2].abs() + "," + nm[2].getArgument()
                                    + "," + (nm[1].abs() / nm[0].abs())
                                    + "," + storyDriftEW.abs() + "," + storyDriftEW.getArgument()
                                    + "," + storyDriftEW2.abs() + "," + storyDriftEW2.getArgument()
                                    + "," + storyDriftEW3.abs() + "," + storyDriftEW3.getArgument()
                                    + "," + (storyDriftEW3.abs() / storyDriftEW2.abs())
                                    + "," + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                    + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                    + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument() + ","
                                    + strains[0].abs() + "," + strains[0].getArgument() + ","
                                    + strains[1].abs() + "," + strains[1].getArgument() + ","
                                    + strains[2].abs() + "," + strains[2].getArgument() + ","
                                    + strains[3].abs() + "," + strains[3].getArgument()
                                    + ")");
                        }
                        {
                            Complex strains[] = getColumnStrainsNS(testname, freqNS, fourierTable/* "R152FourierS"*/, section);
                            Complex[] nm = convertColumnStrainsToNM(strains, section.getE(), section.getA(), section.getZns(), section.getZew());
                            double omega2 = 4 * Math.PI * Math.PI * freqNS * freqNS;
                            Complex stiffnessAxial = nm[0].divide(storyDriftNS).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                            Complex stiffnessMomentX = nm[1].divide(storyDriftNS).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]
                            Complex stiffnessMomentY = nm[2].divide(storyDriftNS).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]

                            st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME,\"Freq[Hz]\", SECTION,"
                                    + "\"AxialA[N*s]\", \"AxialP[rad]\", "
                                    + "\"MomentXA[Nm*s]\", \"MomentXP[rad]\", "
                                    + "\"MomentYA[Nm*s]\", \"MomentYP[rad]\", "
                                    + "\"NMsatio[m]\","
                                    + "\"StoryDriftA[gal*s]\",\"StoryDriftP[rad]\","
                                    + "\"StoryDriftA2[gal*s]\",\"StoryDriftP2[rad]\","
                                    + "\"StoryDriftA3[gal*s]\",\"StoryDriftP3[rad]\","
                                    + "\"StoryDriftRatio(3/2)\","
                                    + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                    + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                    + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\", "
                                    + "\"Strain1A[με*s]\" , \"Strain1P[rad]\" ,"
                                    + "\"Strain2A[με*s]\" , \"Strain2P[rad]\" ,"
                                    + "\"Strain3A[με*s]\" , \"Strain3P[rad]\" ,"
                                    + "\"Strain4A[με*s]\" , \"Strain4P[rad]\" "
                                    + ") values ('" + testname + "'," + freqNS + ",'" + section.getName() + "ns'"
                                    + "," + nm[0].abs() + "," + nm[0].getArgument()
                                    + "," + nm[1].abs() + "," + nm[1].getArgument()
                                    + "," + nm[2].abs() + "," + nm[2].getArgument()
                                    + "," + (nm[1].abs() / nm[0].abs())
                                    + "," + storyDriftNS.abs() + "," + storyDriftNS.getArgument()
                                    + "," + storyDriftNS2.abs() + "," + storyDriftNS2.getArgument()
                                    + "," + storyDriftNS3.abs() + "," + storyDriftNS3.getArgument()
                                    + "," + (storyDriftNS3.abs() / storyDriftNS2.abs())
                                    + "," + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                    + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                    + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument() + ","
                                    + strains[0].abs() + "," + strains[0].getArgument() + ","
                                    + strains[1].abs() + "," + strains[1].getArgument() + ","
                                    + strains[2].abs() + "," + strains[2].getArgument() + ","
                                    + strains[3].abs() + "," + strains[3].getArgument()
                                    + ")");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(A310SectionNMtest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static Complex get(String testname, String schema, String sensor, String ampcolumn, String phasecolumn, double freq) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
            double amp;
            double phase;
            try (Statement st = con.createStatement()) {
                String target = "\"" + schema + "\".\"" + sensor + "\"";
                ResultSet rs = st.executeQuery("select \"" + ampcolumn + "\" ,\"" + phasecolumn + "\" from " + target + " where \"Freq[Hz]\" = " + freq);
                rs.next();
                amp = rs.getDouble(1);
                phase = rs.getDouble(2);
            }
            return ComplexUtils.polar2Complex(amp, phase);
        }

    }

    public static double getPeakFreq(String testname, String schema, BeamSectionInfo section, Connection conout) throws SQLException {
        logger.log(Level.INFO, testname + " " + section.getName());
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");

        // get coonection to the database.
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        double peakfreq = 0;
        double peakmoment = 0;

        // get statement for each database (con=input database, conout=output database)
        Statement stout = conout.createStatement();
        Statement st = con.createStatement();
        // create table
        stout.executeUpdate("create table if not exists  \"A300FourierSectionM\" (TESTNAME varchar,SECTION varchar,\"Freq[Hz]\" real, \"MomentXA[kNm*s]\" real, \"MomentXP[kNm*s]\" real)");
        // delete the existing data if any
        stout.executeUpdate("delete from  \"A300FourierSectionM\" where TESTNAME='" + testname + "' and SECTION='" + section.getName() + "'");

        String[] sensors = {section.getULname(), section.getURname(), section.getLLname(), section.getLRname()};
        double[][][] arrays = new double[4][][];
        for (int i = 0; i < sensors.length; i++) {
            String target = "\"" + schema + "\".\"" + sensors[i] + "\"";
            ResultSet rs = st.executeQuery("select \"Freq[Hz]\", \"Amp[με*s]\", \"Phase[rad]\" from " + target + " ");
            arrays[i] = ResultSetUtils.createSeriesArray(rs);
        }
        double[][] ul = arrays[0]; // ul[0] = freq, ul[1]=amp, ul[2]=phase 
        double[][] ur = arrays[1];
        double[][] ll = arrays[2];
        double[][] lr = arrays[3];

        // for each frequnecy
        for (int i = 0; i < ul[0].length; i++) {
            double freq = ul[0][i];
            Complex ulc = ComplexUtils.polar2Complex(ul[1][i], ul[2][i]); // create complex from amp and phase.
            Complex urc = ComplexUtils.polar2Complex(ur[1][i], ur[2][i]);
            Complex llc = ComplexUtils.polar2Complex(ll[1][i], ll[2][i]);
            Complex lrc = ComplexUtils.polar2Complex(lr[1][i], lr[2][i]);
            Complex moment = (ulc.add(urc).subtract(llc).subtract(lrc)).multiply(1e-9 * section.getE() * section.getInnerZx() / 4.0);
            // 下側引張正。μ*s*Nm →(*1e-9)→ kNm*s
            double momentAmp = moment.abs();
            stout.executeUpdate("insert into \"A300FourierSectionM\" (TESTNAME,SECTION,\"Freq[Hz]\", \"MomentXA[kNm*s]\", \"MomentXP[kNm*s]\") "
                    + " values "
                    + "('" + testname + "','" + section.getName() + "'," + freq + "," + momentAmp + "," + moment.getArgument() + ")");
            if (freq < 2.0) {
                if (momentAmp > peakmoment) {
                    peakmoment = momentAmp;
                    peakfreq = freq;
                }

            }
        }
        stout.close();
        st.close();
        con.close();
        return ((int) (peakfreq * 1000)) / 1000.0;

    }

    public static Complex[] getBeamNM(String testname, double freq, String schema, BeamSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return CalculateBeamNM(con, schema, freq, section.getULname(), section.getURname(), section.getLLname(), section.getLRname(), section.getE(), section.getA(), section.getInnerZx(), section.getZy());
    }

    // Nが上、Sが下、Wが左、Eが右
    public static Complex[] getColumnStrainsNS(String testname, double freq, String schema, ColumnSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return getColumnStrains(con, schema, freq, section.getNorthName(), section.getSouthName(), section.getWestName(), section.getEastName(), section.getE(),
                section.getA(), section.getZns(), section.getZew());
    }

    // Eが上、Wが下、Nが左、Sが右    
    public static Complex[] getColumnStrainsEW(String testname, double freq, String schema, ColumnSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return getColumnStrains(con, schema, freq, section.getEastName(), section.getWestName(), section.getNorthName(), section.getSouthName(), section.getE(),
                section.getA(), section.getZew(), section.getZns());
    }

    /**
     * この時点では
     *
     * @param testname
     * @param freq
     * @param freqNS
     * @param freqEW
     * @param tablename
     * @return [0]=NS, [1]=EW いずれも、まだ、単位は gal*s
     * @throws SQLException
     */
    public static Complex[] getStoryDrift(String testname, String tablename, double freqNS, double freqEW) throws SQLException {
        Complex[] ans = new Complex[6];
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        {
            String sql;
            ResultSet rs = st.executeQuery(sql = "select \"RelAmpNS[gal*s]\", \"RelPhaseNS[rad]\", \"RelAmp2NS[gal*s]\", "
                    + "\"RelPhase2NS[rad]\", \"RelAmp3NS[gal*s]\", \"RelPhase3NS[rad]\" from \"" + tablename
                    + "\" where \"Freq[Hz]\"=" + freqNS);
            logger.log(Level.INFO, sql);
            rs.next();
            double abs = rs.getDouble(1);
            double phase = rs.getDouble(2);
            double abs2 = rs.getDouble(3);
            double phase2 = rs.getDouble(4);
            double abs3 = rs.getDouble(5);
            double phase3 = rs.getDouble(6);
            ans[0] = ComplexUtils.polar2Complex(abs, phase);
            ans[2] = ComplexUtils.polar2Complex(abs2, phase2);
            ans[4] = ComplexUtils.polar2Complex(abs3, phase3);
            rs.close();
        }
        {
            String sql;
            ResultSet rs = st.executeQuery(sql = "select \"RelAmpEW[gal*s]\", \"RelPhaseEW[rad]\", \"RelAmp2EW[gal*s]\", "
                    + "\"RelPhase2EW[rad]\", \"RelAmp3EW[gal*s]\", \"RelPhase3EW[rad]\" from \"" + tablename
                    + "\" where \"Freq[Hz]\"=" + freqEW);
            rs.next();
            logger.log(Level.INFO, sql);
            double abs = rs.getDouble(1);
            double phase = rs.getDouble(2);
            double abs2 = rs.getDouble(3);
            double phase2 = rs.getDouble(4);
            double abs3 = rs.getDouble(5);
            double phase3 = rs.getDouble(6);
            ans[1] = ComplexUtils.polar2Complex(abs, phase);
            ans[3] = ComplexUtils.polar2Complex(abs2, phase2);
            ans[5] = ComplexUtils.polar2Complex(abs3, phase3);
            rs.close();
        }
        return ans;
    }

    /**
     *
     * @param con
     * @param schema R151FourierR or R152FourierS
     * @param freq
     * @param TL a01/01とか
     * @param TR
     * @param BL
     * @param BR
     * @param Zx Ix をゲージ距離の半分で除した値。
     * @throws SQLException
     */
    private static Complex[] CalculateBeamNM(Connection con, String schema, double freq, String TL, String TR, String BL, String BR, double E, double A, double Zx, double Zy) throws SQLException {
        Statement st = con.createStatement();

        String[] tables = {TL, TR, BL, BR};
        Complex[] c = new Complex[4];
        for (int i = 0; i < c.length; i++) {
            String sql;
            ResultSet rs = st.executeQuery(sql = "select \"Amp[με*s]\" , \"Phase[rad]\" from \"" + schema + "\".\"" + tables[i] + "\" "
                    + "where \"Freq[Hz]\" between " + (freq - 0.001) + " and " + (freq + 0.001));
            logger.log(Level.INFO, sql);
            rs.next();
            double amp = rs.getDouble(1);
            double phase = rs.getDouble(2);
            c[i] = ComplexUtils.polar2Complex(amp, phase);
        }
        st.close();
        // ひずみは圧縮が正として出力されている。
        Complex axialStrain = (c[0].add(c[1]).add(c[2]).add(c[3])).multiply(-0.25); // 引張が正となる (ように 0.25に-を付している）
        Complex momentxStrain = (c[0].add(c[1]).subtract(c[2]).subtract(c[3])).multiply(0.25); // 上から下を引いている。上側圧縮（つまり下側引張）が正となる。
        Complex momentyStrain = (c[0].subtract(c[1]).add(c[2]).subtract(c[3])).multiply(0.25);// 上から下を引いている。上側圧縮（つまり下側引張）が正となる。

        Complex axialForce = axialStrain.multiply(1e-6 * E * A); // ひずみはμεなので、1e-6を乗じてμを消す。　単位は N*sとなる。
        Complex bendingMomentX = momentxStrain.multiply(1e-6 * E * Zx);// 単位は Nm
        Complex bendingMomentY = momentyStrain.multiply(1e-6 * E * Zy); // 単位はNm

        return new Complex[]{axialForce, bendingMomentX, bendingMomentY};

    }

//    private static Complex[] CalculateColumnNM(Connection con, String schema, double freq, String top, String bottom, String left, String right, double E, double A, double Zx, double Zy) throws SQLException {
//        Statement st = con.createStatement();
//
//        String[] tables = {top, bottom, left, right};
//        Complex[] c = new Complex[4];
//        for (int i = 0; i < c.length; i++) {
//            ResultSet rs = st.executeQuery("select \"Amp[με*s]\" , \"Phase[rad]\" from \"" + schema + "\".\"" + tables[i] + "\" where \"Freq[Hz]\"=" + freq);
//            rs.next();
//            double amp = rs.getDouble(1);
//            double phase = rs.getDouble(2);
//            c[i] = ComplexUtils.polar2Complex(amp, phase);
//        }
//        st.close();
//        // ひずみは圧縮が正として出力されている。
//        Complex axialStrain = (c[0].add(c[1]).add(c[2]).add(c[3])).multiply(-0.25); // 引張が正となる (ように 0.25に-を付している）
//        Complex momentxStrain = (c[0].subtract(c[1])).multiply(0.50); // 上から下を引いている。上側圧縮（つまり下側引張）が正となる。
//        Complex momentyStrain = (c[2].subtract(c[3])).multiply(0.50);// 左から右を引いている。左側圧縮（つまり右側引張）が正となる。
//
//        Complex axialForce = axialStrain.multiply(1e-6 * E * A); // ひずみはμεなので、1e-6を乗じてμを消す。　単位は N*sとなる。
//        Complex bendingMomentX = momentxStrain.multiply(1e-6 * E * Zx);// 単位は Nm
//        Complex bendingMomentY = momentyStrain.multiply(1e-6 * E * Zy); // 単位はNm
//
//        return new Complex[]{axialForce, bendingMomentX, bendingMomentY};
//
//    }
    private static Complex[] convertColumnStrainsToNM(Complex[] c, double E, double A, double Zx, double Zy) {
        // ひずみは圧縮が正として出力されている。
        Complex axialStrain = (c[0].add(c[1]).add(c[2]).add(c[3])).multiply(-0.25); // 引張が正となる (ように 0.25に-を付している）
        Complex momentxStrain = (c[0].subtract(c[1])).multiply(0.50); // 上から下を引いている。上側圧縮（つまり下側引張）が正となる。
        Complex momentyStrain = (c[2].subtract(c[3])).multiply(0.50);// 左から右を引いている。左側圧縮（つまり右側引張）が正となる。

        Complex axialForce = axialStrain.multiply(1e-6 * E * A); // ひずみはμεなので、1e-6を乗じてμを消す。　単位は N*sとなる。
        Complex bendingMomentX = momentxStrain.multiply(1e-6 * E * Zx);// 単位は Nm
        Complex bendingMomentY = momentyStrain.multiply(1e-6 * E * Zy); // 単位はNm

        return new Complex[]{axialForce, bendingMomentX, bendingMomentY};
    }

    private static Complex[] getColumnStrains(Connection con, String schema, double freq, String top, String bottom, String left, String right, double E, double A, double Zx, double Zy) throws SQLException {
        Statement st = con.createStatement();

        String[] tables = {top, bottom, left, right};
        Complex[] c = new Complex[4];
        for (int i = 0; i < c.length; i++) {
            ResultSet rs = st.executeQuery("select \"Amp[με*s]\" , \"Phase[rad]\" from \"" + schema + "\".\"" + tables[i] + "\" "
                    + "where \"Freq[Hz]\" between (" + freq + "-0.001) and (" + freq + "+0.001)");
            rs.next();
            double amp = rs.getDouble(1);
            double phase = rs.getDouble(2);
            c[i] = ComplexUtils.polar2Complex(amp, phase);
        }
        st.close();

        return c;
    }

}

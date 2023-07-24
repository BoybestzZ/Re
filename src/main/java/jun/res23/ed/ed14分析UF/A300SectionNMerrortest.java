/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import com.google.zxing.common.detector.MathUtils;
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
* A300では、断面の曲げモーメントを算出してそのピーク値を得ている。
* なお、2023/06/11の時点でCS3F_B4_B_N を C_N としていたのを修正しているので、
* データとしてもA300は誤り。　なので、今後はA310を使うこと！
 *
 * @deprecated A310 will output the strain data. The results should be the same as A300
 * @author jun
 */
public class A300SectionNMerrortest {

    public static Path databaseDir = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\R140DatabaseQ");
    public static String fourierTable = "R155FourierU"; //"R152FourierS";
    public static String driftTable = "R175StoryDriftU";
    // ↑これは U のフーリエ変換による層間変形を示している。ここのなかでは (k01+k02-k03-k04)*0.5 として相対加速度を計算している。すなわち、2層分の相対加速度である。
    // なお、R175は k01とk02のEW方向の符号が逆転していることを考慮している。
    public static String dburl = "jdbc:h2:C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230722";
    public static String outputTable = "A300SectionNM";

    public static String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", "D01Q10", "D01Q11",
        "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06", "D02Q07", "D02Q08",
        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08", "D03Q09"   };
//    public static String[] testnames = {"D01Q01"};

    private static final Logger logger = Logger.getLogger(A300SectionNMerrortest.class.getName());

    public static void main(String[] args) {

        try {
            try (Connection con = DriverManager.getConnection(dburl, "junapp", ""); Statement st = con.createStatement()) {
                st.executeUpdate("drop table if exists \"" + outputTable + "\"");
                st.executeUpdate("create table \"" + outputTable + "\" (_NO identity, TESTNAME varchar, \"Freq[Hz]\" double, SECTION varchar, "
                        + "\"AxialA[N*s]\"double , \"AxialP[rad]\" double ,"
                        + "\"MomentXA[Nm*s]\"double , \"MomentXP[rad]\" double ,"
                        + "\"MomentYA[Nm*s]\"double , \"MomentYP[rad]\" double ,"
                        + "\"StoryDriftA[gal*s]\" double,\"StoryDriftP[rad]\" double,"
                        + " \"NMsatio[m]\" double,"
                        + "\"StiffnessAxialA[N/m]\" double, \"StiffnessAxialP[rad]\" double, "
                        + "\"StiffnessMomentXA[Nm/m]\" double, \"StiffnessMomentXP[rad]\" double, "
                        + "\"StiffnessMomentYA[Nm/m]\" double, \"StiffnessMomentYP[rad]\" double"
                        + ")");
                for (String testname : testnames) {
                    logger.log(Level.INFO, testname);
                    double freqNS = getPeakFreq(testname, fourierTable, EdefenseInfo.LA3S2, con);
                    double freqEW = getPeakFreq(testname, fourierTable, EdefenseInfo.LABS2, con);
                    Complex[] storyDrift = getStoryDrift(testname, driftTable/* "R170StoryDriftR"*/, freqNS, freqEW); // この時点では [gal*s]

                    Complex storyDriftNS = storyDrift[0];
                    Complex storyDriftEW = storyDrift[1];
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
                                + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                + ") values ('" + testname + "'," + freqNS + ",'" + section.getName() + "'"
                                + "," + nm[0].abs() + "," + nm[0].getArgument()
                                + "," + nm[1].abs() + "," + nm[1].getArgument()
                                + "," + nm[2].abs() + "," + nm[2].getArgument()
                                + "," + (nm[1].abs() / nm[0].abs())
                                + "," + storyDriftNS.abs() + "," + storyDriftNS.getArgument() + ","
                                + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
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
                                + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                + ") values ('" + testname + "'," + freqEW + ",'" + section.getName() + "'"
                                + "," + nm[0].abs() + "," + nm[0].getArgument()
                                + "," + nm[1].abs() + "," + nm[1].getArgument()
                                + "," + nm[2].abs() + "," + nm[2].getArgument()
                                + "," + (nm[1].abs() / nm[0].abs())
                                + "," + storyDriftEW.abs() + "," + storyDriftEW.getArgument() + ","
                                + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                + ")");
                    }

                    for (ColumnSectionInfo section : EdefenseInfo.columnSections) {
                        //                        logger.log(Level.INFO, section.getName());
                        {
                            Complex[] nm = getColumnNMew(testname, freqEW, fourierTable/* "R152FourierS"*/, section);
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
                                    + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                    + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                    + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                    + ") values ('" + testname + "'," + freqEW + ",'" + section.getName() + "ew'"
                                    + "," + nm[0].abs() + "," + nm[0].getArgument()
                                    + "," + nm[1].abs() + "," + nm[1].getArgument()
                                    + "," + nm[2].abs() + "," + nm[2].getArgument()
                                    + "," + (nm[1].abs() / nm[0].abs())
                                    + "," + storyDriftEW.abs() + "," + storyDriftEW.getArgument() + ","
                                    + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                    + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                    + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                    + ")");
                        }
                        {
                            Complex[] nm = getColumnNMns(testname, freqNS, fourierTable/* "R152FourierS"*/, section);
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
                                    + "\"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", "
                                    + "\"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\", "
                                    + "\"StiffnessMomentYA[Nm/m]\", \"StiffnessMomentYP[rad]\" "
                                    + ") values ('" + testname + "'," + freqNS + ",'" + section.getName() + "ns'"
                                    + "," + nm[0].abs() + "," + nm[0].getArgument()
                                    + "," + nm[1].abs() + "," + nm[1].getArgument()
                                    + "," + nm[2].abs() + "," + nm[2].getArgument()
                                    + "," + (nm[1].abs() / nm[0].abs())
                                    + "," + storyDriftNS.abs() + "," + storyDriftNS.getArgument() + ","
                                    + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                    + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                    + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                    + ")");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(A300SectionNMerrortest.class.getName()).log(Level.SEVERE, null, ex);
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

        try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
            double peakfreq = 0;
            double peakmoment = 0;

            try (Statement stout = conout.createStatement(); Statement st = con.createStatement()) {
                stout.executeUpdate("create table if not exists  \"A300FourierSectionM\" (TESTNAME varchar,SECTION varchar,\"Freq[Hz]\" real, \"MomentXA[kNm*s]\" real, \"MomentXP[kNm*s]\" real)");
                stout.executeUpdate("delete from  \"A300FourierSectionM\" where TESTNAME='" + testname + "' and SECTION='" + section.getName() + "'");

                String[] sensors = {section.getULname(), section.getURname(), section.getLLname(), section.getLRname()};
                double[][][] arrays = new double[4][][];
                for (int i = 0; i < sensors.length; i++) {
                    String target = "\"" + schema + "\".\"" + sensors[i] + "\"";
                    ResultSet rs = st.executeQuery("select \"Freq[Hz]\", \"Amp[με*s]\", \"Phase[rad]\" from " + target + " ");
                    arrays[i] = ResultSetUtils.createSeriesArray(rs);
                }
                double[][] ul = arrays[0];
                double[][] ur = arrays[1];
                double[][] ll = arrays[2];
                double[][] lr = arrays[3];

                for (int i = 0; i < ul[0].length; i++) {
                    double freq = ul[0][i];
                    Complex ulc = ComplexUtils.polar2Complex(ul[1][i], ul[2][i]);
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

            }
            return peakfreq;

        }

    }

    public static Complex[] getBeamNM(String testname, double freq, String schema, BeamSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return CalculateBeamNM(con, schema, freq, section.getULname(), section.getURname(), section.getLLname(), section.getLRname(), section.getE(), section.getA(), section.getInnerZx(), section.getZy());
    }

    // Nが上、Sが下、Wが左、Eが右
    public static Complex[] getColumnNMns(String testname, double freq, String schema, ColumnSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return CalculateColumnNM(con, schema, freq, section.getNorthName(), section.getSouthName(), section.getWestName(), section.getEastName(), section.getE(),
                section.getA(), section.getZns(), section.getZew());
    }

    // Eが上、Wが下、Nが左、Sが右    
    public static Complex[] getColumnNMew(String testname, double freq, String schema, ColumnSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return CalculateColumnNM(con, schema, freq, section.getEastName(), section.getWestName(), section.getNorthName(), section.getSouthName(), section.getE(),
                section.getA(), section.getZew(), section.getZns());
    }

    /**
     * この時点では
     *
     * @param testname
     * @param freq
     * @param tablename
     * @return [0]=NS, [1]=EW いずれも、まだ、単位は gal*s
     * @throws SQLException
     */
    public static Complex[] getStoryDrift(String testname, String tablename, double freqNS, double freqEW) throws SQLException {
        Complex[] ans = new Complex[2];
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        Statement st = con.createStatement();
        {

            ResultSet rs = st.executeQuery("select \"RelAmpNS[gal*s]\", \"RelPhaseNS[rad]\" from \"" + tablename + "\" where \"Freq[Hz]\"=" + freqNS);
            rs.next();
            double abs = rs.getDouble(1);
            double phase = rs.getDouble(2);
            ans[0] = ComplexUtils.polar2Complex(abs, phase);
            rs.close();
        }
        {
            ResultSet rs = st.executeQuery("select \"RelAmpEW[gal*s]\", \"RelPhaseEW[rad]\" from \"" + tablename + "\" where \"Freq[Hz]\"=" + freqEW);
            rs.next();
            double abs = rs.getDouble(1);
            double phase = rs.getDouble(2);
            ans[1] = ComplexUtils.polar2Complex(abs, phase);
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
            ResultSet rs = st.executeQuery("select \"Amp[με*s]\" , \"Phase[rad]\" from \"" + schema + "\".\"" + tables[i] + "\" where \"Freq[Hz]\"=" + freq);
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

    private static Complex[] CalculateColumnNM(Connection con, String schema, double freq, String top, String bottom, String left, String right, double E, double A, double Zx, double Zy) throws SQLException {
        Statement st = con.createStatement();

        String[] tables = {top, bottom, left, right};
        Complex[] c = new Complex[4];
        for (int i = 0; i < c.length; i++) {
            ResultSet rs = st.executeQuery("select \"Amp[με*s]\" , \"Phase[rad]\" from \"" + schema + "\".\"" + tables[i] + "\" where \"Freq[Hz]\"=" + freq);
            rs.next();
            double amp = rs.getDouble(1);
            double phase = rs.getDouble(2);
            c[i] = ComplexUtils.polar2Complex(amp, phase);
        }
        st.close();
        // ひずみは圧縮が正として出力されている。
        Complex axialStrain = (c[0].add(c[1]).add(c[2]).add(c[3])).multiply(-0.25); // 引張が正となる (ように 0.25に-を付している）
        Complex momentxStrain = (c[0].subtract(c[1])).multiply(0.50); // 上から下を引いている。上側圧縮（つまり下側引張）が正となる。
        Complex momentyStrain = (c[2].subtract(c[3])).multiply(0.50);// 左から右を引いている。左側圧縮（つまり右側引張）が正となる。

        Complex axialForce = axialStrain.multiply(1e-6 * E * A); // ひずみはμεなので、1e-6を乗じてμを消す。　単位は N*sとなる。
        Complex bendingMomentX = momentxStrain.multiply(1e-6 * E * Zx);// 単位は Nm
        Complex bendingMomentY = momentyStrain.multiply(1e-6 * E * Zy); // 単位はNm

        return new Complex[]{axialForce, bendingMomentX, bendingMomentY};

    }

//
//    private static void CalculateMNQSpecColumn(Connection con, String inputSchema, double peakfreq,
//            UnitInfo lowUnit1, int lowGauge1, UnitInfo lowUnit2, int lowGauge2, double EAlow, double EZlow,
//            UnitInfo upUnit1, int upGauge1, UnitInfo upUnit2, int upGauge2, double EAup, double EZup,
//            double[] gaugeLocations, double[] sectionLocations, String[] sectionNames,
//            String schema, String table, String memberName
//    ) throws SQLException {
//        Statement st = con.createStatement();
//        UnitInfo[] units = {lowUnit1, lowUnit2, upUnit1, upUnit2};
//        int[] gauges = {lowGauge1, lowGauge2, upGauge1, upGauge2};
//        // 今回は gaugeFactor=2.1, gain=128だから定数でいいけど。
//        final double gain = 128;
//        final double gaugeFactor = 2.10;
//        Complex cc[] = new Complex[4]; // micro*s の複素数
//        for (int i = 0; i < 4; i++) {
//            UnitInfo unit = units[i];
//            final int gaugeno = gauges[i];
//            String sql = "select  \"AMP[LSB*s]\",\"PHASE[rad]\" from \"" + inputSchema + "\".\"" + unit.getHardwareAddress() + "/0" + gaugeno + "/str01\""
//                    + " where \"FREQ[Hz]\"=" + peakfreq;
//            ResultSet rs = st.executeQuery(sql);
//            rs.next();
//            final double amp = rs.getDouble(1) / gain / Math.pow(2, 24) * 4 / gaugeFactor * 1e6; // amplitue [micro*s]
//            final double phase = rs.getDouble(2);
//            final double cos = Math.cos(phase);
//            final double sin = Math.sin(phase);
//            cc[i] = Complex.valueOf(amp * cos, amp * sin);
//        }
//
//        // 右側ひずみの符号と曲げひずみの符号が一緒になるようにしている。（ひずみが引張正ならば、右側引っ張りが正となる。）
//        Complex bendingStrainLow = (cc[1].subtract(cc[0])).divide(2); // (上-下)/(2)            
//        Complex bendingStrainUp = (cc[3].subtract(cc[2])).divide(2); // (上-下)/(2)            
//        // ひずみの符号と軸力ひずみの符号が一緒になるようにしている。
//        Complex axialStrainLow = (cc[0].add(cc[1])).divide(2); // (上+下)/(2)            
//        Complex axialStrainUp = (cc[3].add(cc[2])).divide(2); // (上+下)/(2)            
//        // ひずみの符号と軸力ひずみの符号が一緒になるようにしている。
//
//        //軸力は N=EAε
//        //曲げモーメントは M=EZε
//        Complex Nlow = axialStrainLow.multiply(EAlow * 1e-6); // ひずみ[με]×EA[N] = 軸力[N]
//        Complex Nup = axialStrainUp.multiply(EAup * 1e-6); // ひずみ[με]×EA[N] = 軸力[N]a
//        Complex Mlow = bendingStrainLow.multiply(EZlow * 1e-6); // ひずみ[με]×EZ[Nm] = 曲げモーメント[Nm]
//        Complex Mup = bendingStrainUp.multiply(EZup * 1e-6); // ひずみ[με]×EZ[Nm] = 曲げモーメント[Nm]
//
//        // せん断力は ( Mup-Mlow ) /distance 
//        Complex Q = (Mup.subtract(Mlow)).divide(gaugeLocations[1] - gaugeLocations[0]);
//        Complex w = (Nup.subtract(Nlow)).divide(gaugeLocations[1] - gaugeLocations[0]);
//        double lowLocation = gaugeLocations[0];
//        Complex[] M = new Complex[sectionLocations.length];
//        Complex[] N = new Complex[sectionLocations.length];
//
//        st.executeUpdate("create table if not exists \"" + schema + "\".\"" + table + "\" (MEMBER varchar, LOC double, KEY varchar, AMP double, PHASE double)");
//
//        for (int i = 0; i < sectionLocations.length; i++) {
//            double loc = sectionLocations[i];
//            M[i] = Mlow.add(Q.multiply(loc - lowLocation));
//            N[i] = Nlow.add(w.multiply(loc - lowLocation));
//            st.executeUpdate("insert into \"" + schema + "\".\"" + table + "\" (MEMBER,LOC, KEY,AMP,PHASE) values "
//                    + "('" + memberName + "'," + sectionLocations[i] + ",'M" + sectionNames[i] + "'," + M[i].abs() + "," + M[i].getArgument() + ")");
//            st.executeUpdate("insert into \"" + schema + "\".\"" + table + "\" (MEMBER,LOC, KEY,AMP,PHASE) values "
//                    + "('" + memberName + "'," + sectionLocations[i] + ",'N" + sectionNames[i] + "'," + N[i].abs() + "," + N[i].getArgument() + ")");
//        }
//        st.executeUpdate("insert into \"" + schema + "\".\"" + table + "\" (MEMBER,KEY,AMP,PHASE) values "
//                + "('" + memberName + "','Q'," + Q.abs() + "," + Q.getArgument() + ")");
//        st.close();
//
//    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed11分析F;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 *　ピーク振動数をひずみ（）で判断している。a03/01とかg02/01で判断している。
 * @author jun
 */
public class F190SectionNM {

    public static Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed02/R140DatabaseQ");
    public static String fourierTable="R151FourierR"; //"R152FourierS";
    public static String driftTable= "R170StoryDriftR"; // ここのなかでは (k01+k02-k03-k04)*0.5 として相対加速度を計算している。すなわち、2層分の相対加速度である。
    public static String dburl="jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed11分析F/ed11";
    public static String outputTable="F190SectionNM";

    public static String[] testnames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09", "D01Q10", "D01Q11",
        "D02Q01", "D02Q02", "D02Q03", "D02Q05", "D02Q06", "D02Q07", "D02Q08",
        "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05", "D03Q06", "D03Q08", "D03Q09"
    };
    private static final Logger logger = Logger.getLogger(F190SectionNM.class.getName());

    public static void main(String[] args) {

        try {
            try (Connection con = DriverManager.getConnection(dburl, "junapp", ""); Statement st = con.createStatement()) {
                st.executeUpdate("drop table if exists \""+outputTable+"\"");
                st.executeUpdate("create table \""+outputTable+"\" (_NO identity, TESTNAME varchar, \"Freq[Hz]\" double, SECTION varchar, "
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
                    double freqNS = getPeakFreq(testname, fourierTable, "a03/01", "Amp[με*s]");
                    double freqEW = getPeakFreq(testname,  fourierTable, "g02/01", "Amp[με*s]");
                    Complex[] storyDrift = getStoryDrift(testname, driftTable/* "R170StoryDriftR"*/, freqNS, freqEW); // この時点では [gal*s]
                    for (BeamSectionInfo section : EdefenseInfo.beamSectionsNS) {
//                        logger.log(Level.INFO, section.getName());
                        Complex[] nm = getNM(testname, freqNS, fourierTable, section);
                        double omega2 = 4 * Math.PI * Math.PI * freqNS * freqNS;
                        Complex stiffnessAxial = nm[0].divide(storyDrift[0]).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                        Complex stiffnessMomentX = nm[1].divide(storyDrift[0]).multiply(omega2 * 100); //[Nm/(cm/s2) * (100/s2) = [Nm/m]
                        Complex stiffnessMomentY = nm[2].divide(storyDrift[0]).multiply(omega2 * 100);

                        st.executeUpdate("insert into \""+outputTable+"\" (TESTNAME,\"Freq[Hz]\", SECTION,"
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
                                + "," + storyDrift[0].abs() + "," + storyDrift[0].getArgument() + ","
                                + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                + ")");
                    }
                    for (BeamSectionInfo section : EdefenseInfo.beamSectionsEW) {
//                        logger.log(Level.INFO, section.getName());
                        Complex[] nm = getNM(testname, freqEW, fourierTable/* "R152FourierS"*/, section);
                        double omega2 = 4 * Math.PI * Math.PI * freqEW * freqEW;
                        Complex stiffnessAxial = nm[0].divide(storyDrift[1]).multiply(omega2 * 100); // [N/(cm/s2)  * (100/s2) = N/m]
                        Complex stiffnessMomentX = nm[1].divide(storyDrift[1]).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]
                        Complex stiffnessMomentY = nm[2].divide(storyDrift[1]).multiply(omega2 * 100); // [Nm/(cm/s2)  * (100/s2) = Nm/m]

                        st.executeUpdate("insert into \""+outputTable+"\" (TESTNAME,\"Freq[Hz]\", SECTION,"
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
                                + "," + storyDrift[1].abs() + "," + storyDrift[1].getArgument() + ","
                                + stiffnessAxial.abs() + "," + stiffnessAxial.getArgument() + ","
                                + stiffnessMomentX.abs() + "," + stiffnessMomentX.getArgument() + ","
                                + stiffnessMomentY.abs() + "," + stiffnessMomentY.getArgument()
                                + ")");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(F190SectionNM.class.getName()).log(Level.SEVERE, null, ex);
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

    public static double getPeakFreq(String testname, String schema, String sensor, String ampcolumn) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
            try (Statement st = con.createStatement()) {
                String target = "\"" + schema + "\".\"" + sensor + "\"";
                ResultSet rs = st.executeQuery("select \"" + ampcolumn + "\" ,\"Freq[Hz]\" from " + target + " where \"Freq[Hz]\" < 2.0 order by 1 desc limit 1;");
                rs.next();
//                amp = rs.getDouble(2);
                double freq = rs.getDouble(2);
                return freq;
            }

        }

    }

    public static Complex[] getNM(String testname, double freq, String schema, BeamSectionInfo section) throws SQLException {
        String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
        Connection con = DriverManager.getConnection(dburl, "junapp", "");
        return CalculateNM(con, schema, freq, section.getULname(), section.getURname(), section.getLLname(), section.getLRname(), section.getE(), section.getA(), section.getInnerZx(), section.getZy());
    }

    /**
     * この時点では
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

            ResultSet rs = st.executeQuery("select \"RelAmpY[gal*s]\", \"RelPhaseY[rad]\" from \"" + tablename + "\" where \"Freq[Hz]\"=" + freqNS);
            rs.next();
            double abs = rs.getDouble(1);
            double phase = rs.getDouble(2);
            ans[0] = ComplexUtils.polar2Complex(abs, phase);
            rs.close();
        }
        {
            ResultSet rs = st.executeQuery("select \"RelAmpX[gal*s]\", \"RelPhaseX[rad]\" from \"" + tablename + "\" where \"Freq[Hz]\"=" + freqEW);
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
    private static Complex[] CalculateNM(Connection con, String schema, double freq, String TL, String TR, String BL, String BR, double E, double A, double Zx, double Zy) throws SQLException {
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

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01;

import jun.res23.ed.ed14分析UF.*;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.MomentDiagramCreator2;
import jun.chart.MomentDiagramCreator2.Element;
import jun.res23.ed.ed06分析T.T231CreateTimeHistoryBeamColumnNM;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.ColumnInfo;
import jun.res23.ed.util.ColumnSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import jun.util.JunShapes;
import jun.util.SVGWriter;
import org.apache.batik.svggen.SVGGraphics2D;

/**
 * T240はひずみ実測値を用いているが、T241は梁 S1 は用いずに S2とS3の結果から梁端の値を推定する。
 * T230で作成した柱および梁のMNを用いて、最大値および最小値の曲げモーメント図を描く。
 * T230はD01Q01以前を初期値とした値を計算している。このため、値には残留ひずみを含んでしまうため、D01くらいはよいが、D02になると不思議な図となる。
 * このような図を書くためには、各加振時前に初期化した値を用いる必要があるが、このプログラムでは対応していない。 → ed14A120
 * A120 -> A125 Draw bending moment diagram of frame 4.
 * @author jun
 */
public class ed14A126BendingMomentDiagramA {

  //  private static final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed14分析UF/ed14";
       static final String dburl = "jdbc:h2:tcp://localhost/C://Users\\75496\\Documents\\E-Defense\\Columnshearforce/ed14v230614";
    private static final String readTable = "A310SectionNM";

    private static final Path svgdir = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\sea01\\A126BendingMomentDiagramA");

    private static final String outputTable = "A126BendingMomentA";
    private static final Logger logger = Logger.getLogger(ed14A126BendingMomentDiagramA.class.getName());

    public static void main(String[] args) {
        main(EdefenseInfo.D01Q01); //random
        main(EdefenseInfo.D01Q02); // kumamot
        main(EdefenseInfo.D01Q03); // tohoku
        main(EdefenseInfo.D01Q04); // Kobe25 XYZ
        main(EdefenseInfo.D01Q05); // Kobe25 X
        main(EdefenseInfo.D01Q06); // Kobe25 Y
        main(EdefenseInfo.D01Q08); // Kobe50 XYZ
        main(EdefenseInfo.D01Q09); //random
        main(EdefenseInfo.D01Q10); // Kobe75 XYZ
        main(EdefenseInfo.D01Q11); //random
        main(EdefenseInfo.D02Q01); // kumamoto
        main(EdefenseInfo.D02Q02); // tohoku
        main(EdefenseInfo.D02Q03); // Kobe100
        main(EdefenseInfo.D02Q05);//random
        main(EdefenseInfo.D02Q06); // kumamot
        main(EdefenseInfo.D02Q07); // tohoku
        main(EdefenseInfo.D02Q08); // tohoku
        main(EdefenseInfo.D03Q01); // random
        main(EdefenseInfo.D03Q02); // kumamot
        main(EdefenseInfo.D03Q03); // tohoku
        main(EdefenseInfo.D03Q04); // Kobe25-XYZ
        main(EdefenseInfo.D03Q05); // kobe25-X
        main(EdefenseInfo.D03Q06); // Kobe25-Y
        main(EdefenseInfo.D03Q08); // Kobe75-XYZ
        main(EdefenseInfo.D03Q09); // random

    }

    public static void main(EdefenseKasinInfo kasin) {

        // Frame 4
        try {
            // 準備
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q01; // random
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q09; //random
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q11; //random
//            EdefenseKasinInfo kasin = EdefenseInfo.D02Q05; //random

            BeamInfo beam = EdefenseInfo.BeamA;
            ColumnInfo columnLU = EdefenseInfo.Column3FA3;
            ColumnInfo columnLL = EdefenseInfo.Column2FA3;
            ColumnInfo columnRU = EdefenseInfo.Column3FA4;
            ColumnInfo columnRL = EdefenseInfo.Column2FA4;
            // beam            
            ArrayList<double[]> nodePositionList = new ArrayList<>();

            nodePositionList.add(new double[]{0, -columnLL.getLength()});
            nodePositionList.add(new double[]{0, 0});
            nodePositionList.add(new double[]{0, +columnLU.getLength()});
            nodePositionList.add(new double[]{beam.getLength(), -columnLL.getLength()});
            nodePositionList.add(new double[]{beam.getLength(), 0});
            nodePositionList.add(new double[]{beam.getLength(), +columnLU.getLength()});

            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            MomentDiagramCreator2 md = new MomentDiagramCreator2(nodePositionList, Color.BLACK, JunShapes.MEDIUM_LINE);
            double scale = 0.05;
            if (true) {

                ArrayList<Element> elementList = new ArrayList<>();
                ArrayList<Element> steelElementList = new ArrayList<>();
                processBeam(con, nodePositionList, elementList, steelElementList, beam, 1, 4, kasin);
                processColumn(con, nodePositionList, elementList, columnLU, 1, 2, kasin, "ew");
                processColumn(con, nodePositionList, elementList, columnLL, 0, 1, kasin, "ew");
                processColumn(con, nodePositionList, elementList, columnRU, 4, 5, kasin, "ew");
                processColumn(con, nodePositionList, elementList, columnRL, 3, 4, kasin, "ew");
                md.addElementList(elementList, scale, new Color(0f, 0, 1f, 0.2f), new Color(0f, 0f, 1f), JunShapes.NORMAL_LINE, true);
                md.addElementList(steelElementList, scale, new Color(1f, 0, 0, 0.2f), new Color(1f, 0f, 0f), JunShapes.NORMAL_LINE,/*showValue*/ false);
            }

            con.close();

            md.setValueFormat("%.2f");
            md.autoscale(600, 600);
            md.setFontSize(8);

            SVGGraphics2D g = SVGWriter.prepareGraphics2D(400, 400);
            md.paintToGraphics(g, new Rectangle2D.Double(0, 0, 400, 400));
            Path svgfile = svgdir.resolve(kasin.getTestName() + ".svg");
            try {
                SVGWriter.outputSVG(g, svgfile);
            } catch (IOException ex) {
                Logger.getLogger(ed14A126BendingMomentDiagramA.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ed14A126BendingMomentDiagramA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void processColumn(Connection con, ArrayList<double[]> nodePositionList, ArrayList<Element> elementList,
            ColumnInfo column, int nodei, int nodej, EdefenseKasinInfo kasin, String ns) throws SQLException {

        ColumnSectionInfo[] sections = column.getColumnSections();
        double locations[] = new double[sections.length + 2];
        double moments[] = new double[sections.length + 2];
        Statement st = con.createStatement();
        for (int i = 0; i < sections.length; i++) {
            ColumnSectionInfo section = sections[i];
//            String schema="T231TimeHistoryNM";
            ResultSet rs
                    = st.executeQuery("select \"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\"  from"
                            + " \"" + readTable + "\" where SECTION='" + section.getName() + ns + "' and TESTNAME='" + kasin.getTestName() + "'");
            rs.next();
            double aa = rs.getDouble(1)/1e6; // N/m -> kN/mm
            double ap = rs.getDouble(2);
            double ma = rs.getDouble(3)/1e6; // Nm/m -> kNm/mm
            double mp = rs.getDouble(4);
            if (Math.abs(ap) > 1.59) {
                aa *= -1;
            }
            if (Math.abs(mp) > 1.59) {
                ma *= -1;
            }

            moments[i + 1] = ma;
            locations[i + 1] = column.getLocation(i);
            rs.close();

        }
        int mlen = moments.length;
        locations[mlen - 1] = column.getLength();
        moments[0] = moments[1] - (moments[2] - moments[1]) / (locations[2] - locations[1]) * locations[1];
        moments[mlen - 1] = moments[mlen - 2] + (moments[mlen - 2] - moments[mlen - 3]) / (locations[mlen - 2] - locations[mlen - 3]) * (locations[mlen - 1] - locations[mlen - 2]);

        MomentDiagramCreator2.insertElement(nodePositionList, elementList, nodei, nodej, locations, moments);
        st.executeUpdate("create table if not exists \"" + outputTable + "\" "
                + "(TESTNAME varchar, LOCATION varchar, \"BendingMoment[kNm]\" real, \"BendingMomentSteel[kNm]\" real, \"MS2[kNm]\" real)");

        st.executeUpdate("delete from \"" + outputTable + "\" where TESTNAME='" + kasin.getTestName() + "' and LOCATION like '" + column.getName() + "%'");
        st.executeUpdate("insert into \"" + outputTable + "\"  (TESTNAME, LOCATION,\"BendingMoment[kNm]\") values ('" + kasin.getTestName() + "',"
                + "'" + column.getName() + "_T'," + moments[mlen - 1] + ")");
        st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME, LOCATION,\"BendingMoment[kNm]\") values ('" + kasin.getTestName() + "',"
                + "'" + column.getName() + "_B'," + -moments[0] + ")"); // moment の値は曲げモーメント（右側引張正）なので、本当は正負が逆かもしれない。。。。

        st.close();

    }

    public static void processBeam(Connection con, ArrayList<double[]> nodePositionList, ArrayList<Element> elementList,
            ArrayList<Element> steelElementList, BeamInfo beam, int nodei, int nodej, EdefenseKasinInfo kasin) throws SQLException {

        // このプログラムでは locations[0]は節点、locations[1]は S2、location[2]はS3を表す。
        BeamSectionInfo[] sections = beam.getBeamSections();
        double locations[] = new double[sections.length + 1];
        double steelmoments[] = new double[sections.length + 1];
        double totalmoments[] = new double[sections.length + 1];
        Statement st = con.createStatement();

        for (int i = 1; i < sections.length; i++) {
            BeamSectionInfo section = sections[i];
            String sql;
            ResultSet rs = st.executeQuery(sql="select \"StiffnessAxialA[N/m]\", \"StiffnessAxialP[rad]\", \"StiffnessMomentXA[Nm/m]\", \"StiffnessMomentXP[rad]\"  from"
                    + " \"" + readTable + "\" where SECTION='" + section.getName() + "' and TESTNAME='" + kasin.getTestName() + "'");
            rs.next();
            double aa = rs.getDouble(1)/1e6; // N/m -> kN/mm
            double ap = rs.getDouble(2);
            double ma = rs.getDouble(3)/1e6;// Nm/m -> kNm/mm
            double mp = rs.getDouble(4);

            if (Math.abs(ap) > 1.59) {
                aa *= -1;
            }
            if (Math.abs(mp) > 1.59) {
                ma *= -1;
            }

            double axial = aa;// rs.getDouble(1) - avgAxial;
            double moment = ma;// rs.getDouble(2) - avgBending;
            double total = moment + axial * (section.getHeight() * 0.5 + 0.055);
            totalmoments[i + 1 - 1] = total;
            steelmoments[i + 1 - 1] = moment;
            locations[i + 1 - 1] = beam.getLocation(i);
            rs.close();
        }

        int mlen = steelmoments.length;
        locations[mlen - 1] = beam.getLength();
        steelmoments[0] = steelmoments[1] - (steelmoments[2] - steelmoments[1]) / (locations[2] - locations[1]) * locations[1];
        steelmoments[mlen - 1] = steelmoments[mlen - 2] + (steelmoments[mlen - 2] - steelmoments[mlen - 3]) / (locations[mlen - 2] - locations[mlen - 3]) * (locations[mlen - 1] - locations[mlen - 2]);
        totalmoments[0] = totalmoments[1] - (totalmoments[2] - totalmoments[1]) / (locations[2] - locations[1]) * locations[1];
        totalmoments[mlen - 1] = totalmoments[mlen - 2] + (totalmoments[mlen - 2] - totalmoments[mlen - 3]) / (locations[mlen - 2] - locations[mlen - 3]) * (locations[mlen - 1] - locations[mlen - 2]);

        // total を追加
        MomentDiagramCreator2.insertElement(nodePositionList, steelElementList, nodei, nodej, locations, steelmoments,/*showValue*/ false);
        MomentDiagramCreator2.insertElement(nodePositionList, elementList, nodei, nodej, locations, totalmoments);

        st.executeUpdate("create table if not exists \"" + outputTable + "\" "
                + "(TESTNAME varchar,LOCATION varchar, \"BendingMoment[kNm]\" real, \"BendingMomentSteel[kNm]\" real, \"MS2[kNm]\" real)");

        st.executeUpdate("delete from \"" + outputTable + "\" where TESTNAME='" + kasin.getTestName() + "' and LOCATION like '" + beam.getName() + "%'");
        st.executeUpdate("insert into \"" + outputTable + "\"  (TESTNAME,LOCATION,\"BendingMoment[kNm]\",\"BendingMomentSteel[kNm]\" ) values "
                + "('" + kasin.getTestName() + "',"
                + "'" + beam.getName() + "_R'," + totalmoments[mlen - 1] + "," + steelmoments[mlen - 1] + ")");
        st.executeUpdate("insert into \"" + outputTable + "\" (TESTNAME, LOCATION,\"BendingMoment[kNm]\",\"BendingMomentSteel[kNm]\", \"MS2[kNm]\") values ('" + kasin.getTestName() + "',"
                + "'" + beam.getName() + "_L'," + (-totalmoments[0]) + "," + (-steelmoments[0]) + "," + steelmoments[1] + ")");
// moment自体は下側引張正の曲げモーメントであるが、ここで符号を変えているのは、節点反力モーメントにするため。こうすることで、 節点でのモーメントを加算することができる。
// プログラム的に、右端（J端）の曲げモーメントが節点モーメントに一致するようになっているので、左端(I端）の曲げモーメントの符号を変えることで、節点モーメントにしている。

// 前述のように stellmoments[1]は S2 のモーメントが入っている。（このプログラムでは S1は使わないため。）
        st.close();

    }

}

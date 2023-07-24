/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
import javax.swing.JFrame;
import jun.chart.JunChartUtil;
import jun.chart.MomentDiagramCreator2;
import jun.chart.MomentDiagramCreator2.Element;
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
 * T230で作成した柱および梁のMNを用いて、最大値および最小値の曲げモーメント図を描く。
 * T230はD01Q01以前を初期値とした値を計算している。このため、値には残留ひずみを含んでしまうため、D01くらいはよいが、D02になると不思議な図となる。
 * このような図を書くためには、各加振時前に初期化した値を用いる必要があるが、このプログラムでは対応していない。 T240→T243 :
 * T240を改良してすべての骨組の曲げモーメント図を描く。
 *
 *
 * @author jun
 */
public class T410GraphBendingMomentDiagram3 {

    public static final String dburl = "jdbc:h2:tcp://localhost///home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
//    private static final String readSchema = T231CreateTimeHistoryBeamColumnNM.outputSchema;//"T231TimeHistoryBeamColumnNM";
    public static final String readSchema = "T400TimeHistoryNM";
    private static final Logger logger = Logger.getLogger(T410GraphBendingMomentDiagram3.class.getName());

    private static Path svgdir = Path.of("/home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/T410BendingMomentDiagram3");

    public static void main(String[] args) {
        boolean directionPositive = false;
        main(EdefenseInfo.D01Q01, directionPositive); //random
        main(EdefenseInfo.D01Q02, directionPositive); // kumamot
        main(EdefenseInfo.D01Q03, directionPositive); // tohoku
        main(EdefenseInfo.D01Q04, directionPositive); // Kobe25 XYZ
        main(EdefenseInfo.D01Q05, directionPositive); // Kobe25 X
        main(EdefenseInfo.D01Q06, directionPositive); // Kobe25 Y
        main(EdefenseInfo.D01Q08, directionPositive); // Kobe50 XYZ
        main(EdefenseInfo.D01Q09, directionPositive); //random
        main(EdefenseInfo.D01Q10, directionPositive); // Kobe75 XYZ
        main(EdefenseInfo.D01Q11, directionPositive); //random
        main(EdefenseInfo.D02Q01, directionPositive);
        main(EdefenseInfo.D02Q02, directionPositive);
        main(EdefenseInfo.D02Q03, directionPositive); // Kobe100
        main(EdefenseInfo.D02Q05, directionPositive);//random
        main(EdefenseInfo.D02Q06, directionPositive); // kumamot
////        main(EdefenseInfo.D02Q07, directionPositive); // tohoku
        main(EdefenseInfo.D02Q08, directionPositive); // tohoku
        main(EdefenseInfo.D03Q01, directionPositive); // random
        main(EdefenseInfo.D03Q02, directionPositive); // kumamot
        main(EdefenseInfo.D03Q03, directionPositive); // tohoku
        main(EdefenseInfo.D03Q04, directionPositive); // Kobe25-XYZ
        main(EdefenseInfo.D03Q05, directionPositive); // kobe25-X
        main(EdefenseInfo.D03Q06, directionPositive); // Kobe25-Y
        main(EdefenseInfo.D03Q08, directionPositive); // Kobe75-XYZ
        main(EdefenseInfo.D03Q09, directionPositive); // random

    }

    public static void main(EdefenseKasinInfo kasin, boolean directionPositive) {

        // Frame 3
        try {
            // 準備
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q01; // random
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q09; //random
//            EdefenseKasinInfo kasin = EdefenseInfo.D01Q11; //random
//            EdefenseKasinInfo kasin = EdefenseInfo.D02Q05; //random

            String NS = "NS";
            BeamInfo beam = EdefenseInfo.Beam3;
            ColumnInfo columnLU = EdefenseInfo.Column3FA3;
            ColumnInfo columnLL = EdefenseInfo.Column2FA3;
            ColumnInfo columnRU = EdefenseInfo.Column3FB3;
            ColumnInfo columnRL = EdefenseInfo.Column2FB3;
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

            String timeMax;
            double scale;
            {
                // beam.getSection()[0] の加振直後2秒間の平均を得る。
                //  ResultSet rs = st.executeQuery("select avg(\"BendingMoment[kNm]\")"
//                        + " from \"" + readSchema + "\".\"" + beam.getSections()[1].getName() + "\" "
//                        + "where TESTNAME='" + kasin.getTestName() + "t' and \"TIME[s]\"<2.0"
//                );
//                rs.next();
//                double avg = rs.getDouble(1);
                // 最大変形が生じる時刻を取得。
                String desc = (directionPositive) ? "desc" : "";
                ResultSet rs = st.executeQuery("select \"TimePerTest[s]\", \"BendingMomentPerTest[kNm]\" from \"" + readSchema + "\".\"" + beam.getSections()[1].getName() + "\" "
                        + "where TESTNAME='" + kasin.getTestName() + "t' order by \"BendingMoment[kNm]\" " + desc + " limit 1");

                rs.next();
                timeMax = rs.getString(1);
                double bendingMax = rs.getDouble(2);

                rs.close();

                scale = 0.01 * 17 / Math.abs(bendingMax);

            }
            MomentDiagramCreator2 md = new MomentDiagramCreator2(nodePositionList, Color.BLACK, JunShapes.MEDIUM_LINE);

            if (true) {

                ArrayList<Element> elementList = new ArrayList<>();
                ArrayList<Element> steelElementList = new ArrayList<>();
                processBeam(con, nodePositionList, elementList, steelElementList, beam, 1, 4, kasin, timeMax);
                processColumn(con, nodePositionList, elementList, columnLU, 1, 2, kasin, NS, timeMax);
                processColumn(con, nodePositionList, elementList, columnLL, 0, 1, kasin, NS, timeMax);
                processColumn(con, nodePositionList, elementList, columnRU, 4, 5, kasin, NS, timeMax);
                processColumn(con, nodePositionList, elementList, columnRL, 3, 4, kasin, NS, timeMax);
                md.addElementList(elementList, scale, new Color(0f, 0, 1f, 0.2f), new Color(0f, 0f, 1f), JunShapes.NORMAL_LINE, true);
                md.addElementList(steelElementList, scale, new Color(1f, 0, 0, 0.2f), new Color(1f, 0f, 0f), JunShapes.NORMAL_LINE,/*showValue*/ false);
            }
//            if (false) {
//                String where = " where \"TIME[s]\"=" + timeMin + " and TESTNAME='" + kasin.getTestName() + "t'";
//                ArrayList<Element> elementList = new ArrayList<>();
//                ArrayList<Element> steelElementList = new ArrayList<>();
//                processBeam(con, nodePositionList, elementList, steelElementList, beam, 1, 4, where);
//                processColumn(con, nodePositionList, elementList, columnLU, 1, 2, where);
//                processColumn(con, nodePositionList, elementList, columnLL, 0, 1, where);
//                processColumn(con, nodePositionList, elementList, columnRU, 4, 5, where);
//                processColumn(con, nodePositionList, elementList, columnRL, 3, 4, where);
//                md.addElementList(elementList, .02, new Color(1f, 0, 0, 0.3f), new Color(1f, 0f, 0f), JunShapes.NORMAL_LINE);
//            }
            con.close();

            md.setValueFormat("%.2f");
            md.autoscale(600, 600);
            md.setFontSize(8);

            if (svgdir != null) {
                Path svgfile = svgdir.resolve(beam.getName() + "_" + kasin.getTestName() + (directionPositive ? "P" : "N") + ".svg");
                try {
                    SVGGraphics2D g = SVGWriter.prepareGraphics2D(400, 400);
                    md.paintToGraphics(g, new Rectangle2D.Double(0, 0, 400, 400));
                    SVGWriter.outputSVG(g, svgfile);
                } catch (IOException ex) {
                    Logger.getLogger(T410GraphBendingMomentDiagram3.class.getName()).log(Level.SEVERE, null, ex);

                }
            } else {
                md.setVisible(true);
            }

        } catch (SQLException ex) {
            Logger.getLogger(T410GraphBendingMomentDiagram3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void processColumn(Connection con, ArrayList<double[]> nodePositionList, ArrayList<Element> elementList,
            ColumnInfo column, int nodei, int nodej, EdefenseKasinInfo kasin, String NS, String timeMax) throws SQLException {
        String where = " where \"TIME[s]\"=" + timeMax + " and TESTNAME='" + kasin.getTestName() + "t'";
        ColumnSectionInfo[] sections = column.getColumnSections();
        double locations[] = new double[sections.length + 2];
        double moments[] = new double[sections.length + 2];
        Statement st = con.createStatement();
        for (int i = 0; i < sections.length; i++) {
            ColumnSectionInfo section = sections[i];
//            String schema="T231TimeHistoryNM";
            ResultSet rs;
//            rs = st.executeQuery("select avg(\"AxialForce[kN]\"), avg(\"BendingMomentNS[kNm]\")  from"
//                    + " \"" + readSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + kasin.getTestName() + "t' and \"TIME[s]\"<2.0 ");
//            rs.next();
//            double avgAxial = rs.getDouble(1);
//            double avgBending = rs.getDouble(2);
            rs
                    = st.executeQuery("select \"AxialForcePerTest[kN]\", \"BendingMoment" + NS + "PerTest[kNm]\"  from"
                            + " \"" + readSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + kasin.getTestName() + "t' and \"TimePerTest[s]\"=" + timeMax);
            rs.next();
            double axial = rs.getDouble(1);
            double moment = rs.getDouble(2);
            moments[i + 1] = moment;
            locations[i + 1] = column.getLocation(i);
            rs.close();

        }
        int mlen = moments.length;
        locations[mlen - 1] = column.getLength();
        moments[0] = moments[1] - (moments[2] - moments[1]) / (locations[2] - locations[1]) * locations[1];
        moments[mlen - 1] = moments[mlen - 2] + (moments[mlen - 2] - moments[mlen - 3]) / (locations[mlen - 2] - locations[mlen - 3]) * (locations[mlen - 1] - locations[mlen - 2]);

        MomentDiagramCreator2.insertElement(nodePositionList, elementList, nodei, nodej, locations, moments);

        //   st.executeUpdate("insert into T240 (TESTNAME,LOCATION,TYPE, BendingMoment[kNm]) values ('"+kasin.getTestName()+"t',")
        st.close();

    }

    public static void processBeam(Connection con, ArrayList<double[]> nodePositionList, ArrayList<Element> elementList,
            ArrayList<Element> steelElementList, BeamInfo beam, int nodei, int nodej, EdefenseKasinInfo kasin, String timeMax) throws SQLException {
        BeamSectionInfo[] sections = beam.getBeamSections();
        double locations[] = new double[sections.length + 2];
        double steelmoments[] = new double[sections.length + 2];
        double totalmoments[] = new double[sections.length + 2];
        Statement st = con.createStatement();

        for (int i = 0; i < sections.length; i++) {
            BeamSectionInfo section = sections[i];
            String sql;
            ResultSet rs;
//            rs= st.executeQuery(sql = "select avg(\"AxialForce[kN]\"), avg(\"BendingMoment[kNm]\")  from"
//                    + " \"" + readSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + kasin.getTestName() + "t'"
//                    + " and \"TIME[s]\"<2.0");
//            rs.next();
//            double avgAxial = rs.getDouble(1);
//            double avgBending = rs.getDouble(2);

            rs = st.executeQuery(sql = "select \"AxialForcePerTest[kN]\", \"BendingMomentPerTest[kNm]\"  from"
                    + " \"" + readSchema + "\".\"" + section.getName() + "\" where TESTNAME='" + kasin.getTestName() + "t' and \"TimePerTest[s]\"=" + timeMax);
            logger.log(Level.INFO, sql); // + " : total@" + locations[i + 1] + " = " + total);
            rs.next();
            double axial = rs.getDouble(1);
            double moment = rs.getDouble(2);
            double total = moment + axial * (section.getHeight() * 0.5 + 0.055);
            totalmoments[i + 1] = total;
            steelmoments[i + 1] = moment;
            locations[i + 1] = beam.getLocation(i);
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
        st.close();

    }

}

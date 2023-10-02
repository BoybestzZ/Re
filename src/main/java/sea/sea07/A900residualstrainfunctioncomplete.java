
/**
 *
 * @author 75496
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea07;

import sea.sea04.InflectionPoint.*;
import sea.sea01.Inflectionpoint.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import static jun.res23.ed.ed14分析UF.A310SectionNM.outputTable;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;

/**
 * modified by Iyama. Use XYdataset. THe xaxisl will be number.
 *
 *
 * @author 75496
 */
public class A900residualstrainfunctioncomplete {
    

    public static void main(String[] args) throws IOException, SQLException{
        
//        double distance1 = 0.326; // distance between inner web (Beam3 = 350 - 2*12)
//        double distance2 = 1.63;  // distance between inner web (BeamB = 244 - 2*...)
//        double slab = 0.11;
           createResidualStrain("a01/01", "1");
           createResidualStrain("a01/02", "2");
           createResidualStrain("a01/03", "3");
           createResidualStrain("a01/04", "4");
           createResidualStrain("a01/05", "5");
           createResidualStrain("a01/06", "6");
           createResidualStrain("a01/07", "7");
           createResidualStrain("a01/08", "8");
           
           createResidualStrain("a02/01", "9");
           createResidualStrain("a02/02", "10");
           createResidualStrain("a02/03", "11");
           createResidualStrain("a02/04", "12");
           createResidualStrain("a02/05", "13");
           createResidualStrain("a02/06", "14");
           createResidualStrain("a02/07", "15");
           createResidualStrain("a02/08", "16");
           
           createResidualStrain("a03/01", "17");
           createResidualStrain("a03/02", "18");
           createResidualStrain("a03/03", "19");
           createResidualStrain("a03/04", "20");
           createResidualStrain("a03/05", "21");
           createResidualStrain("a03/06", "22");
           createResidualStrain("a03/07", "23");
           createResidualStrain("a03/08", "24");
           
                      createResidualStrain("b01/01", "25");
           createResidualStrain("b01/02", "26");
           createResidualStrain("b01/03", "27");
           createResidualStrain("b01/04", "28");
           createResidualStrain("b01/05", "29");
           createResidualStrain("b01/06", "30");
           createResidualStrain("b01/07", "31");
           createResidualStrain("b01/08", "32");
           
           createResidualStrain("b02/01", "33");
           createResidualStrain("b02/02", "34");
           createResidualStrain("b02/03", "35");
           createResidualStrain("b02/04", "36");
           createResidualStrain("b02/05", "37");
           createResidualStrain("b02/06", "38");
           createResidualStrain("b02/07", "39");
           createResidualStrain("b02/08", "40");
           
           createResidualStrain("b03/01", "41");
           createResidualStrain("b03/02", "42");
           createResidualStrain("b03/03", "43");
           createResidualStrain("b03/04", "44");
           createResidualStrain("b03/05", "45");
           createResidualStrain("b03/06", "46");
           createResidualStrain("b03/07", "47");
           createResidualStrain("b03/08", "48");
           
                      createResidualStrain("c01/01", "49");
           createResidualStrain("c01/02", "50");
           createResidualStrain("c01/03", "51");
           createResidualStrain("c01/04", "52");
           createResidualStrain("c01/05", "53");
           createResidualStrain("c01/06", "54");
           createResidualStrain("c01/07", "55");
           createResidualStrain("c01/08", "56");
           
           createResidualStrain("c02/01", "57");
           createResidualStrain("c02/02", "58");
           createResidualStrain("c02/03", "59");
           createResidualStrain("c02/04", "60");
           createResidualStrain("c02/05", "61");
           createResidualStrain("c02/06", "62");
           createResidualStrain("c02/07", "63");
           createResidualStrain("c02/08", "64");
           
           createResidualStrain("c03/01", "65");
           createResidualStrain("c03/02", "66");
           createResidualStrain("c03/03", "67");
           createResidualStrain("c03/04", "68");
           createResidualStrain("c03/05", "69");
           createResidualStrain("c03/06", "70");
           createResidualStrain("c03/07", "71");
           createResidualStrain("c03/08", "72");
           
                      createResidualStrain("d01/01", "73");
           createResidualStrain("d01/02", "74");
           createResidualStrain("d01/03", "75");
           createResidualStrain("d01/04", "76");
           createResidualStrain("d01/05", "77");
           createResidualStrain("d01/06", "78");
           createResidualStrain("d01/07", "79");
           createResidualStrain("d01/08", "80");
           
           createResidualStrain("d02/01", "81");
           createResidualStrain("d02/02", "82");
           createResidualStrain("d02/03", "83");
           createResidualStrain("d02/04", "84");
           createResidualStrain("d02/05", "85");
           createResidualStrain("d02/06", "86");
           createResidualStrain("d02/07", "87");
           createResidualStrain("d02/08", "88");
          
           createResidualStrain("d03/01", "89");
           createResidualStrain("d03/02", "90");
           createResidualStrain("d03/03", "91");
           createResidualStrain("d03/04", "92");
           createResidualStrain("d03/05", "93");
           createResidualStrain("d03/06", "94");
           createResidualStrain("d03/07", "95");
           createResidualStrain("d03/08", "96");
           
                      createResidualStrain("e01/01", "97");
           createResidualStrain("e01/02", "98");
           createResidualStrain("e01/03", "99");
           createResidualStrain("e01/04", "100");
           createResidualStrain("e01/05", "101");
           createResidualStrain("e01/06", "102");
           createResidualStrain("e01/07", "103");
           createResidualStrain("e01/08", "104");
           
           createResidualStrain("e02/01", "105");
           createResidualStrain("e02/02", "106");
           createResidualStrain("e02/03", "107");
           createResidualStrain("e02/04", "108");
           createResidualStrain("e02/05", "109");
           createResidualStrain("e02/06", "110");
           createResidualStrain("e02/07", "111");
           createResidualStrain("e02/08", "112");
          
           createResidualStrain("e03/01", "113");
           createResidualStrain("e03/02", "114");
           createResidualStrain("e03/03", "115");
           createResidualStrain("e03/04", "116");
           createResidualStrain("e03/05", "117");
           createResidualStrain("e03/06", "118");
           createResidualStrain("e03/07", "119");
           createResidualStrain("e03/08", "120");
           
                      createResidualStrain("f01/01", "121");
           createResidualStrain("f01/02", "122");
           createResidualStrain("f01/03", "123");
           createResidualStrain("f01/04", "124");
           createResidualStrain("f01/05", "125");
           createResidualStrain("f01/06", "126");
           createResidualStrain("f01/07", "127");
           createResidualStrain("f01/08", "128");
           
           createResidualStrain("f02/01", "129");
           createResidualStrain("f02/02", "130");
           createResidualStrain("f02/03", "131");
           createResidualStrain("f02/04", "132");
           createResidualStrain("f02/05", "133");
           createResidualStrain("f02/06", "134");
           createResidualStrain("f02/07", "135");
           createResidualStrain("f02/08", "136");
           
           createResidualStrain("f03/01", "137");
           createResidualStrain("f03/02", "138");
           createResidualStrain("f03/03", "139");
           createResidualStrain("f03/04", "140");
           createResidualStrain("f03/05", "141");
           createResidualStrain("f03/06", "142");
           createResidualStrain("f03/07", "143");
           createResidualStrain("f03/08", "144");
           
                      createResidualStrain("a01/01", "145");
           createResidualStrain("g01/02", "146");
           createResidualStrain("g01/03", "147");
           createResidualStrain("g01/04", "148");
           createResidualStrain("g01/05", "149");
           createResidualStrain("g01/06", "150");
           createResidualStrain("g01/07", "151");
           createResidualStrain("g01/08", "152");
           
           createResidualStrain("g02/01", "153");
           createResidualStrain("g02/02", "154");
           createResidualStrain("g02/03", "155");
           createResidualStrain("g02/04", "156");
           createResidualStrain("g02/05", "157");
           createResidualStrain("g02/06", "158");
           createResidualStrain("g02/07", "159");
           createResidualStrain("g02/08", "160");
           
           createResidualStrain("g03/01", "161");
           createResidualStrain("g03/02", "162");
           createResidualStrain("g03/03", "163");
           createResidualStrain("g03/04", "164");
           createResidualStrain("g03/05", "165");
           createResidualStrain("g03/06", "166");
           createResidualStrain("g03/07", "167");
           createResidualStrain("g03/08", "168");
           
                      createResidualStrain("h01/01", "169");
           createResidualStrain("h01/02", "170");
           createResidualStrain("h01/03", "171");
           createResidualStrain("h01/04", "172");
           createResidualStrain("h01/05", "173");
           createResidualStrain("h01/06", "174");
           createResidualStrain("h01/07", "175");
           createResidualStrain("h01/08", "176");
           
           createResidualStrain("h02/01", "177");
           createResidualStrain("h02/02", "178");
           createResidualStrain("h02/03", "179");
           createResidualStrain("h02/04", "180");
           createResidualStrain("h02/05", "181");
           createResidualStrain("h02/06", "182");
           createResidualStrain("h02/07", "183");
           createResidualStrain("h02/08", "184");
           
           createResidualStrain("h03/01", "185");
           createResidualStrain("h03/02", "186");
           createResidualStrain("h03/03", "187");
           createResidualStrain("h03/04", "188");
           createResidualStrain("h03/05", "189");
           createResidualStrain("h03/06", "190");
           createResidualStrain("h03/07", "191");
           createResidualStrain("h03/08", "192");
          
                      createResidualStrain("i01/01", "193");
           createResidualStrain("i01/02", "194");
           createResidualStrain("i01/03", "195");
           createResidualStrain("i01/04", "196");
           createResidualStrain("i01/05", "197");
           createResidualStrain("i01/06", "198");
           createResidualStrain("i01/07", "199");
           createResidualStrain("i01/08", "200");
          
           createResidualStrain("i02/01", "201");
           createResidualStrain("i02/02", "202");
           createResidualStrain("i02/03", "203");
           createResidualStrain("i02/04", "204");
           createResidualStrain("i02/05", "205");
           createResidualStrain("i02/06", "206");
           createResidualStrain("i02/07", "207");
           createResidualStrain("i02/08", "208");
          
           createResidualStrain("i03/01", "209");
           createResidualStrain("i03/02", "210");
           createResidualStrain("i03/03", "211");
           createResidualStrain("i03/04", "212");
           createResidualStrain("i03/05", "213");
           createResidualStrain("i03/06", "214");
           createResidualStrain("i03/07", "215");
           createResidualStrain("i03/08", "216");
           
                      createResidualStrain("j01/01", "217");
           createResidualStrain("j01/02", "218");
           createResidualStrain("j01/03", "219");
           createResidualStrain("j01/04", "220");
           createResidualStrain("j01/05", "221");
           createResidualStrain("j01/06", "222");
           createResidualStrain("j01/07", "223");
           createResidualStrain("j01/08", "224");
           
           createResidualStrain("j02/01", "225");
           createResidualStrain("j02/02", "226");
           createResidualStrain("j02/03", "227");
           createResidualStrain("j02/04", "228");
           createResidualStrain("j02/05", "229");
           createResidualStrain("j02/06", "230");
           createResidualStrain("j02/07", "231");
           createResidualStrain("j02/08", "232");
           
           createResidualStrain("j03/01", "233");
           createResidualStrain("j03/02", "234");
           createResidualStrain("j03/03", "235");
           createResidualStrain("j03/04", "236");
           createResidualStrain("j03/05", "237");
           createResidualStrain("j03/06", "238");
           createResidualStrain("j03/07", "239");
           createResidualStrain("j03/08", "240");

           

    }

    public static void createResidualStrain(String table, String tableName) throws IOException, SQLException {

        try {
//            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            String ed06dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/res22ed06";

            
            // Connect to database
            Connection con = DriverManager.getConnection(ed06dburl, "junapp", "");
            Statement st = con.createStatement();
//            Connection con08 = DriverManager.getConnection(ed06dburl, "junapp", "");
//            Statement st = con08.createStatement();
//            Statement st09 = con.createStatement();

//            String[] testNames = {"D01Q01", "D01Q02", "D01Q03", "D01Q04", "D01Q05", "D01Q06", "D01Q08", "D01Q09",
//                "D01Q10", "D01Q11", "D02Q01", "D02Q01", "D02Q02", "D02Q03", "D02Q05",
//                "D02Q06", "D02Q07", "D02Q08", "D03Q01", "D03Q02", "D03Q03", "D03Q04", "D03Q05",
//                "D03Q06", "D03Q08", "D03Q09"};
//            
            EdefenseKasinInfo[] kasins = EdefenseInfo.alltests;

            // Prepare dataset
            DefaultXYDataset dataset = new DefaultXYDataset();
            XYSeries random = new XYSeries("random");
            XYSeries kumamoto = new XYSeries("kumamoto");
            XYSeries tohoku = new XYSeries("tohoku");
            XYSeries kobe = new XYSeries("kobe");
            
//            DefaultXYDataset dataset2 = new DefaultXYDataset();
//            XYSeries randomp = new XYSeries("random+");
//            XYSeries kumamotop = new XYSeries("kumamoto+");
//            XYSeries tohokup = new XYSeries("tohoku+");
//            XYSeries kobep = new XYSeries("kobe+");
//            
//
//            XYSeries randomn = new XYSeries("random-");
//            XYSeries kumamoton = new XYSeries("kumamoto-");
//            XYSeries tohokun = new XYSeries("tohoku-");
//            XYSeries koben = new XYSeries("kobe-");
            
//            String beamName = beamInfo.getName();
            
            // Create table to store results if it doesn't exist
            st.executeUpdate("DROP TABLE IF EXISTS \"ResidualStrain" + table + "\"");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS \"ResidualStrain" + table + "\" (TestName VARCHAR(20), ResidualStrain DOUBLE)");

            

            for (int i = 0; i < kasins.length; i++) {
                String testName = kasins[i].getTestName();  //D01Q01
                String waveName = kasins[i].getWaveName();  // Random

                // Execute query and get result set
//                    ResultSet rs = st.executeQuery("SELECT \"INFLECTIONPOINT\" FROM \"INFLECTIONPOINTDATA\" where TESTNAME='" + testName + "' and BEAMNAME='"+beamName+"';");
                    
                    ResultSet rs = st.executeQuery("SELECT TESTNAME, AVG(\"Strain[με]\") AS avg_strain FROM \"T220TimeHistoryStrain\".\"" + table + "\" WHERE TESTNAME='" + testName + "t' GROUP BY TESTNAME ORDER BY avg_strain DESC LIMIT 10");
//                    ResultSet rs = st.executeQuery("select TESTNAME, avg(\"Strain[με]\") from (select * from \"T220TimeHistoryStrain\".\"a01/" where TESTNAME='" + testName + "t' order by \"TotalTime[s]\" desc limit 10)");
                    
//                    select TESTNAME, avg('Strain[με]') from (select * from "T220TimeHistoryStrain"."a01/01" where TESTNAME='" + testName + "t' order by 'TotalTime[s]' desc limit 10)
                    
                    rs.next();

                    // get results
                    double residualStrain = rs.getDouble(2);
                    
                    
                // Insert the result into the table
                String insertQuery = "INSERT INTO \"ResidualStrain" + table + "\" (TestName, ResidualStrain) VALUES ('" + testName + "', " + residualStrain + ")";
                st.executeUpdate(insertQuery);
                System.out.println("Record for TestName '" + testName + "' inserted into the table.");



                if (waveName.equals("Random")) {
                    random.add(i + 1, residualStrain);
                } else if (waveName.equals("KMMH02")) {
                    kumamoto.add(i + 1, residualStrain);
                } else if (waveName.equals("FKS020")) {
                    tohoku.add(i + 1, residualStrain);
                } else if (waveName.startsWith("Kobe")) {
                    kobe.add(i + 1, residualStrain);

                }

            }

          
            dataset.addSeries("Random", random.toArray());
            dataset.addSeries("tohoku", tohoku.toArray());
            dataset.addSeries("kumamoto", kumamoto.toArray());
            dataset.addSeries("kobe", kobe.toArray());
//            
//             EdefenseKasinInfo[] kasins2 = EdefenseInfo.alltests;
//            
//            // Create table to store results if it doesn't exist
//            st.executeUpdate("Alter table InflPoi"+beamName+" add positive double;");
//            
//            for (int i = 0; i < kasins2.length; i++) {
//                String testName = kasins2[i].getTestName();  //D01Q01
//                String waveName = kasins2[i].getWaveName();  // Random
//                
//                ResultSet rs08 = st08.executeQuery("SELECT \"InflectionPoint[m]\" FROM \"T122BeamInflectionPoint\" where TESTNAME = '" + testName + "t' and \"BEAMNAME\" = '"+beamName+"' and \"DIRECTION\" = 'POSITIVE';");
//                rs08.next();
//                    
//
//                // get results
//                double shearforcestiffnesspositive = rs08.getDouble(1);
//                
//                String insertQuery = "INSERT INTO InflPoi"+beamName+" (TestName, positive) VALUES ('" + testName + "'," + shearforcestiffnesspositive + ")";
//                st.executeUpdate(insertQuery);
//                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
//                
//                if (waveName.equals("Random")) {
//                    randomp.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.equals("KMMH02")) {
//                    kumamotop.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.equals("FKS020")) {
//                    tohokup.add(i + 1, shearforcestiffnesspositive);
//                } else if (waveName.startsWith("Kobe")) {
//                    kobep.add(i + 1, shearforcestiffnesspositive);
//                }
//
//            }
//                dataset.addSeries("Random+", randomp.toArray());
//                dataset.addSeries("tohoku+", tohokup.toArray());
//                dataset.addSeries("kumamoto+", kumamotop.toArray());
////                dataset.addSeries("kobe+", kobep.toArray());
//                
//                
//            // Create table to store results if it doesn't exist
//            st.executeUpdate("Alter table InflPoi"+beamName+" add negative double;");
//                
//                EdefenseKasinInfo[] kasins3 = EdefenseInfo.alltests;
//            
//            for (int i = 0; i < kasins3.length; i++) {
//                String testName = kasins3[i].getTestName();  //D01Q01
//                String waveName = kasins3[i].getWaveName();  // Random
//                
//                ResultSet rs08 = st08.executeQuery("SELECT \"InflectionPoint[m]\" FROM \"T122BeamInflectionPoint\" where TESTNAME = '" + testName + "t' and \"BEAMNAME\" = '"+beamName+"' and \"DIRECTION\" = 'NEGATIVE';");
//                rs08.next();
//                    
//
//                // get results
//                double shearforcestiffnessnegative = rs08.getDouble(1);
//                
//                String insertQuery = "INSERT INTO InflPoi"+beamName+" (TestName, negative) VALUES ('" + testName + "'," + shearforcestiffnessnegative + ")";
//                st.executeUpdate(insertQuery);
//                System.out.println("Record for TestName '" + testName + "' inserted into the table.");
//
//
//                if (waveName.equals("Random")) {
//                    randomn.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.equals("KMMH02")) {
//                    kumamoton.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.equals("FKS020")) {
//                    tohokun.add(i + 1, shearforcestiffnessnegative);
//                } else if (waveName.startsWith("Kobe")) {
//                    koben.add(i + 1, shearforcestiffnessnegative);
//                }
//
//            }
//                dataset.addSeries("Random-", randomn.toArray());
//                dataset.addSeries("tohoku-", tohokun.toArray());
//                dataset.addSeries("kumamoto-", kumamoton.toArray());
//                dataset.addSeries("kobetime-", koben.toArray());
                
                
                
                
            
            
            
            
            // Create the chart
            JFreeChart chart = ChartFactory.createXYLineChart("", "Test No.", "Residual Strain (m)",
                    dataset, PlotOrientation.VERTICAL, true, true, false);

            // Customize the chart
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
           
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
            domainAxis.setVerticalTickLabels(true);
            domainAxis.setLowerBound(0.1);
            domainAxis.setUpperBound(24.9);
            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.

//            double lowerBound = 0.1; // Set the lower bound for the x-axis
//            double upperBound = kasins.length + 0.9; // Set the upper bound for the x-axis
//
//            domainAxis.setLowerBound(lowerBound);
//            domainAxis.setUpperBound(upperBound);
            
            // Prepare the mapping of test names to labels dynamically
            String[] testNames = new String[kasins.length];
            double[] testValues = new double[kasins.length];

            for (int i = 0; i < kasins.length; i++) {
                testNames[i] = kasins[i].getTestName() + kasins[i].getWaveName();
                testValues[i] = i + 1.0;
            }

            // Create the ChoiceFormat
            ChoiceFormat formatter = new ChoiceFormat(testValues, testNames);


//            NumberFormat formatter=new ChoiceFormat(
//                    new double[] {1.0,2.0,3.0}, 
//                        new String[]{"D01Q01random", "D02Q02kumamoto","D03Q03tohoku"});


            domainAxis.setNumberFormatOverride(formatter);
            
            
            
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(-10000, 10000); // Set the y-axis range
            rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesPaint(0, Color.RED);
            plot.setRenderer(renderer);
            
            // insert legend to the plot
            LegendTitle legend = chart.getLegend(); // obtain legend box
            XYTitleAnnotation ta=new XYTitleAnnotation(1.00 ,0.05, legend, RectangleAnchor.BOTTOM_RIGHT);
            legend.setBorder(1, 1, 1, 1); // frame around legend
            plot.addAnnotation(ta);
            chart.removeLegend();

            // Export the chart as PNG
            int width = 650;
            int height = 300;
//            String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\Inflectionpoint\\ip_LA3.png";
//            File chartFile = new File(filePath);
//            ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

              String filePath = "C:\\Users\\75496\\Documents\\E-Defense\\residualstrain\\residualstrain_"+tableName+".svg";
              JunChartUtil.svg(filePath, width, height, chart);


            // Display the chart in a frame
            ChartFrame frame = new ChartFrame("Inflection Point", chart);
            frame.setPreferredSize(new Dimension(1200, 800));
            frame.pack();
            frame.setVisible(true);

            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A900residualstrainfunctioncomplete.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
    




//                // Execute query and get result set
//                    ResultSet rs = st.executeQuery("SELECT \"INFLECTIONPOINT\" FROM \"INFLECTIONPOINTDATA\" where TESTNAME='" + testName + "' and BEAMNAME='Beam3';");
//                    rs.next();

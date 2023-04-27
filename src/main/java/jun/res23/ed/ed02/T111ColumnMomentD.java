/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.fourier.FFTv2;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.ColumnInfo;
import jun.res23.ed.util.ColumnSectionInfo;
import static jun.res23.ed.util.EdefenseInfo.D02Q06;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T111ColumnMomentD {

    private static final Logger logger = Logger.getLogger(T111ColumnMomentD.class.getName());

    public static void main(String[] args) {
        EdefenseKasinInfo kasin = D02Q06;
        try {

            double[][] column2F3AB = T100chartNMTimeHistory.calculateColumnNM(kasin.toString(), EdefenseInfo.CS2FA3B);
            double[][] column2F3AC = T100chartNMTimeHistory.calculateColumnNM(kasin.toString(), EdefenseInfo.CS2FA3C);
            double[][] column2F3AT = T100chartNMTimeHistory.calculateColumnNM(kasin.toString(), EdefenseInfo.CS2FA3T);

            DefaultXYDataset dataset = new DefaultXYDataset();
            column2F3AB[2] = FFTv2.zeroAverage(column2F3AB[2]);
            column2F3AC[2] = FFTv2.zeroAverage(column2F3AC[2]);
            column2F3AT[2] = FFTv2.zeroAverage(column2F3AT[2]);
            


            dataset.addSeries("2F3AB", new double[][]{column2F3AB[0], column2F3AB[2]});
            dataset.addSeries("2F3AC", new double[][]{column2F3AC[0], column2F3AC[2]});
            dataset.addSeries("2F3AT", new double[][]{column2F3AT[0], column2F3AT[2]});
            JunChartUtil.show("dataset", dataset);

        } catch (SQLException ex) {
            Logger.getLogger(T111ColumnMomentD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main1(String[] args) {
        EdefenseKasinInfo kasin = D02Q06;
        try {

            double[][] column2F3AB = calculateColumnMoment(kasin.toString(), EdefenseInfo.Column2FA3, 0.0/*edge-i*/, EdefenseInfo.Direction.NS);
            double[][] column2F3AT = calculateColumnMoment(kasin.toString(), EdefenseInfo.Column2FA3, 1.0/*edge-i*/, EdefenseInfo.Direction.NS);

            DefaultXYDataset dataset = new DefaultXYDataset();

            dataset.addSeries("2F3AB", column2F3AB);
            dataset.addSeries("3F3AT", column2F3AT);
            JunChartUtil.show("dataset", dataset);

        } catch (SQLException ex) {
            Logger.getLogger(T111ColumnMomentD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param testname
     * @param beam
     * @param location
     * @return [0]time [1]moment
     * @throws SQLException
     */
    public static double[][] calculateBeamMoment(String testname, BeamInfo beam, double locationRatio) throws SQLException {
        BeamSectionInfo[] sections = beam.getBeamSections();
        double location = locationRatio * beam.getLength();

        double x0 = beam.getLocation(0);
        double h0 = sections[0].getHeight() * 0.5 + 0.11 * 0.5;
        double x1 = beam.getLocation(1);
        double h1 = sections[1].getHeight() * 0.5 + 0.11 * 0.5;
        double span = x1 - x0;
        logger.log(Level.INFO, "h0=" + h0 + "," + h1);
        double[][] section0 = T100chartNMTimeHistory.calculateBeamNM(testname, sections[0]);
        double[][] section1 = T100chartNMTimeHistory.calculateBeamNM(testname, sections[1]);

        double[][] ans = new double[2][section0[0].length];

        for (int i = 0; i < section0[0].length; i++) {
            double mtotal0 = section0[1][i] * h0 + section0[2][i]; // N*h+M
            double mtotal1 = section1[1][i] * h1 + section1[2][i]; // N*h+M
            double qtotal = (mtotal1 - mtotal0) / span;
            double mend = mtotal0 + qtotal * (location - x0);
            ans[0][i] = section0[0][i];
            ans[1][i] = mend;
        }
        return ans;
    }

    /**
     *
     * @param testname
     * @param beam
     * @param location
     * @return [0]time [1]moment
     * @throws SQLException
     */
    public static double[][] calculateColumnMoment(String testname, ColumnInfo beam, double locationRatio, EdefenseInfo.Direction direction) throws SQLException {
        ColumnSectionInfo[] sections = beam.getColumnSections();
        double location = locationRatio * beam.getLength();
        logger.log(Level.INFO, "location=" + location);
        double x0 = beam.getLocation(0);
        double h0 = sections[0].getHeight() * 0.5 + 0.11 * 0.5;
        double x1 = beam.getLocation(1);
        double h1 = sections[1].getHeight() * 0.5 + 0.11 * 0.5;
        double span = x1 - x0;
        double[][] section0 = T100chartNMTimeHistory.calculateColumnNM(testname, sections[0]);
        double[][] section1 = T100chartNMTimeHistory.calculateColumnNM(testname, sections[1]);

        double[][] ans = new double[2][section0[0].length];
        int mindex = (direction == EdefenseInfo.Direction.NS) ? (2) : (3);

        for (int i = 0; i < section0[0].length; i++) {
            double mtotal0 =  section0[mindex][i]; // N*h+M
            double mtotal1 =  section1[mindex][i]; // N*h+M
            double qtotal = (mtotal1 - mtotal0) / span;
            double mend = mtotal0 + qtotal * (location - x0);
            ans[0][i] = section0[0][i];
            ans[1][i] = mend;
        }
        return ans;
    }

}

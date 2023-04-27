/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import static jun.res23.ed.ed02.T111ColumnMomentD.calculateBeamMoment;
import static jun.res23.ed.ed02.T111ColumnMomentD.calculateColumnMoment;
import jun.res23.ed.util.EdefenseInfo;
import static jun.res23.ed.util.EdefenseInfo.D02Q06;
import jun.res23.ed.util.EdefenseKasinInfo;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author jun
 */
public class T110BeamEndMoment {

    private static final Logger logger = Logger.getLogger(T110BeamEndMoment.class.getName());

    public static void main(String[] args) {
        EdefenseKasinInfo kasin = D02Q06;
        try {

            double[][] beam3 = calculateBeamMoment(kasin.toString(), EdefenseInfo.Beam3, 0.0/*edge-i*/);
            double[][] column2F3A = calculateColumnMoment(kasin.toString(), EdefenseInfo.Column2FA3, 0.0/*edge-i*/, EdefenseInfo.Direction.NS);
            double[][] column3F3A = calculateColumnMoment(kasin.toString(), EdefenseInfo.Column3FA3, 1.0/*edge-i*/, EdefenseInfo.Direction.NS);

            DefaultXYDataset dataset = new DefaultXYDataset();
            dataset.addSeries("beam3", beam3);
            dataset.addSeries("2F",column2F3A);
            dataset.addSeries("3F",column3F3A);            
            JunChartUtil.show("dataset", dataset);

        } catch (SQLException ex) {
            Logger.getLogger(T110BeamEndMoment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunXYChartCreator2;
import static jun.res23.ed.ed06分析T.T200CreateTimeHistoryNM.outputDb;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import jun.res23.ed.util.EdefenseKasinInfo;

/**
 *
 * @author jun
 */
@Deprecated
public class T201GraphTimeHistoryNM {

    private static final Logger logger = Logger.getLogger(T201GraphTimeHistoryNM.class.getName());

    public static void main(String[] args) {
        try {
            // 各データベースを読み取って計算する。
//            T201GraphTimeHistoryNM.show(EdefenseInfo.LA3S1);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LA3S2);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LA3S3);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LA3S4);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LA3S5);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LABS1);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LABS2);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LABS3);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LABS4);
            T201GraphTimeHistoryNM.show(EdefenseInfo.LABS5);

        } catch (SQLException ex) {
            Logger.getLogger(T200CreateTimeHistoryNM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void show(BeamSectionInfo section) throws SQLException {
        logger.log(Level.INFO, "opened. Opening output database " + outputDb);
        Connection cono = DriverManager.getConnection(outputDb, "junapp", "");
        Statement sto = cono.createStatement();
        logger.log(Level.INFO, "opened.");

        ResultSet rs = sto.executeQuery("select \"TotalTime[s]\",\"AxialForce[kN]\",\"BendingMoment[kNm]\" from \"" + section.getName() + "\" order by 1");

        new JunXYChartCreator2().setDataset(rs).show(section.getName());

        cono.close();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.ed02;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.raspi.alive.UnitInfo;
import jun.res23.ed.util.EdefenseInfo;

/**
 *
 * @author jun
 */
public class R101ShowUnitTImeHIstory {

    public static void main(String[] args) {
        try {
            String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230215day1/database_HE/D01Q11_20230215_171757";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            R100GraphUnitTimeHistory r100 = new R100GraphUnitTimeHistory(con, null, null);
            UnitInfo unit=EdefenseInfo.f01;
            r100.graphSingleUnit(con, unit.getHardwareAddress(), unit.getName(), /*svgfile*/ null, 0.01, 10, 0.01, /*basephase*/ null);
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(R101ShowUnitTImeHIstory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

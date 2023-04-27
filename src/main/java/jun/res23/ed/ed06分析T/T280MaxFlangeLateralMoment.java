/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jun
 */
public class T280MaxFlangeLateralMoment {

    public static void main(String[] args) {
        try {
            String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res23/ed/ed06分析T/res22ed06";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            st.executeUpdate("drop table if exists \"T280MaxFlangeLateralMoment\"");
            st.executeUpdate("create table \"T280MaxFlangeLateralMoment\" "
                    + "(SECTION varchar, TESTNAME varchar, ALTNAME varchar, \"TimePerTest[s]\" real,\"DIRECTION\" varchar,\"LowerAxialStrainPerTest[με]\" real"
                    + ",\"LowerBendingStrainPerTest[με]\" real)"
                    + "");

            String sections[] = {
                "LA3S1", "LA3S2", "LA3S3", "LA3S4", "LA3S5",
                "LA4S1", "LA4S2", "LA4S3", "LA4S4", "LA4S5",
                "LAAS1", "LAAS2", "LAAS3", "LAAS4", "LAAS5",
                "LABS1", "LABS2", "LABS3", "LABS4", "LABS5"
            };

            for (String s : sections) {
                st.executeUpdate("insert into \"T280MaxFlangeLateralMoment\" SELECT SECTIONNAME, m.TESTNAME, ALTNAME, "
                        + "m.\"TimePerTest[s]\", \"DIRECTION\",\"LowerAxialStrainPerTest[με]\", "
                        + "\"LowerBendingStrainPerTest[με]\" FROM \"T122BeamNMDistribution\" n join "
                        + "\"T270TimeHistoryLAM\"." + s + " m where SECTIONNAME='" + s + "' and "
                        + "n.TESTNAME=m.TESTNAME and n.\"TimePerTest[s]\"=m.\"TimePerTest[s]\"");
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(T280MaxFlangeLateralMoment.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

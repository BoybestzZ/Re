/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed02;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jun
 */
public class R154ModifyStrainUnit {

    private static final Logger logger = Logger.getLogger(R154ModifyStrainUnit.class.getName());

    public static void main(String[] args) {
        String[] testnames = R190SectionNMs.testnames;
        Path databaseDir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed02/R140DatabaseQ");

        for (String testname : testnames) {
            String dburl = "jdbc:h2:tcp://localhost/" + databaseDir.resolve(testname + "q");
            logger.log(Level.INFO, testname);
            try (Connection con = DriverManager.getConnection(dburl, "junapp", "")) {
                try (Statement st = con.createStatement()) {
                    DatabaseMetaData md = con.getMetaData();
                    ResultSet rs = md.getColumns(null, "R152FourierS", null, "Amp[ε*s]");
                    while (rs.next()) {
                        String tablename = rs.getString("TABLE_NAME");
//                        logger.log(Level.INFO, tablename);
                        st.executeUpdate("alter table \"R152FourierS\".\"" + tablename + "\" alter column \"Amp[ε*s]\" rename to \"Amp[με*s]\"");
                    }

                }

            } catch (SQLException ex) {
                Logger.getLogger(R154ModifyStrainUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

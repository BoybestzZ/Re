/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed06分析T;

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
public class T231csvTimeHistoryNM {

    private static final Logger logger = Logger.getLogger(T231csvTimeHistoryNM.class.getName());
    private static final Path csvdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/T231TimeHistoryNM");

    public static void main(String[] args) {

        try {

            final String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed06分析T/res22ed06";
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();
            DatabaseMetaData md = con.getMetaData();

            ResultSet rs = md.getTables(null, "T231TimeHistoryNM", "%", null);

            while (rs.next()) {

                String sectionName = rs.getString("TABLE_NAME");
                logger.log(Level.INFO, sectionName);
                String csvfilename = csvdir.resolve(sectionName + ".csv").toString();
                st.executeUpdate("call csvwrite('" + csvfilename + "','select * from \"T231TimeHistoryNM\".\"" + sectionName + "\"')");
            }
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(T231csvTimeHistoryNM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

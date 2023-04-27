/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed08;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 *
 * @author jun
 */
public class B100ReadCSV {

    public static final String dburl = "jdbc:h2:tcp://localhost//home/jun/Dropbox (SSLUoT)/res22/ed/ed08防災科研/res22ed08";
    private static final Path basedir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/ed/04_収録データ-selected/00_防災科研収録データ");

    private static final Logger logger = Logger.getLogger(B100ReadCSV.class.getName());

    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            Pattern p = Pattern.compile(".*-(.*)_(D..Q..)$");

            Iterator<Path> iter = Files.walk(basedir).iterator();

            while (iter.hasNext()) {
                Path path = iter.next();
                Matcher m = p.matcher(path.toString());
                if (m.matches()) {
                    String wavename = m.group(1);
                    String testname = m.group(2);

                    logger.log(Level.INFO, wavename + ":" + testname);

                    Path isd2 = path.resolve("isd_2-3F_" + wavename + ".csv");
                    Path isd3 = path.resolve("isd_3-4F_" + wavename + ".csv");
                    st.executeUpdate("drop table if exists \"" + testname + "_isd2\"");
                    st.executeUpdate("create table \"" + testname + "_isd2\" (\"Time[s]\" real ,\"StoryDispE_X[mm]\" real,\"StoryDispE_Y[mm]\"real, \"StoryDispW_X[mm]\"real,\"StoryDispW_Y[mm]\" real)");
                    st.executeUpdate("insert into \"" + testname + "_isd2\" select * from csvread('" + isd2.toString() + "')");
                    st.executeUpdate("drop table if exists \"" + testname + "_isd3\"");
                    st.executeUpdate("create table \"" + testname + "_isd3\" (\"Time[s]\" real ,\"StoryDispE_X[mm]\" real,\"StoryDispE_Y[mm]\"real, \"StoryDispW_X[mm]\"real,\"StoryDispW_Y[mm]\" real)");
                    st.executeUpdate("insert into \"" + testname + "_isd3\" select * from csvread('" + isd3.toString() + "')");

                    st.executeUpdate("create index \"" + testname + "2\" on \"" + testname + "_isd2\" (\"Time[s]\")");
                    st.executeUpdate("create index \"" + testname + "3\" on \"" + testname + "_isd3\" (\"Time[s]\")");
                    logger.log(Level.INFO, "Index Created.");
                    String sql
                            = "create table \"" + testname + "\" as "
                            + "select i2.\"Time[s]\","
                            + "i2.\"StoryDispE_X[mm]\" \"Story2DispE_X[mm]\","
                            + "i2.\"StoryDispE_Y[mm]\" \"Story2DispE_Y[mm]\", "
                            + "i2.\"StoryDispW_X[mm]\" \"Story2DispW_X[mm]\","
                            + "i2.\"StoryDispW_Y[mm]\" \"Story2DispW_Y[mm]\","
                            + "i3.\"StoryDispE_X[mm]\" \"Story3DispE_X[mm]\", "
                            + "i3.\"StoryDispE_Y[mm]\" \"Story3DispE_Y[mm]\","
                            + "i3.\"StoryDispW_X[mm]\" \"Story3DispW_X[mm]\","
                            + "i3.\"StoryDispW_Y[mm]\" \"Story3DispW_Y[mm]\""
                            + " from \"" + testname + "_isd2\" i2, \"" + testname + "_isd3\" i3 where i2.\"Time[s]\"=i3.\"Time[s]\"";
                    st.executeUpdate(sql);
                    logger.log(Level.INFO, "Table Created.");
                }
            }
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(B100ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(B100ReadCSV.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

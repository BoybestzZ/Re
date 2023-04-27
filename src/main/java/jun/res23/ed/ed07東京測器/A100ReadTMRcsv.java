/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed07東京測器;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jun.db.CsvRead4;

/**
 *
 * @author jun
 */
public class A100ReadTMRcsv {

    public static final String dburl = "jdbc:h2:///home/jun/Dropbox (SSLUoT)/res22/ed/ed07TMR/res22ed07TMR";

    private static final Logger logger = Logger.getLogger(A100ReadTMRcsv.class.getName());

    static final Pattern pat = Pattern.compile("(D0\\dQ\\d\\d)_1_1.CSV");

    public static void main(String[] args) {
        try {
            Path tmrdir = Path.of("/home/jun/Dropbox (SSLUoT)/res22/コンソ設計法/230308_東京測器ロガー_生データ");

            Files.walk(tmrdir).forEach((t) -> {
                read(t);
            });

        } catch (IOException ex) {
            Logger.getLogger(A100ReadTMRcsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void read(Path csvfile) {
        try {
            String filename = csvfile.getFileName().toString();
            logger.log(Level.INFO, filename);
            Matcher matcher = pat.matcher(filename);
            if (!matcher.matches()) {
                return;
            }

            String testname = matcher.group(1);
            CsvRead4 csvread = new CsvRead4(csvfile, Charset.forName("Shift_JIS"), 3,
                    2);

            Connection con = DriverManager.getConnection(dburl, "junapp", "");

            csvread.createTable(con, testname, null);
            csvread.read(con, testname, csvfile, null, testname, 8);

            Statement st = con.createStatement();

            st.executeUpdate("alter table \"" + testname + "\" add column \"TIME[s]\" real using (cast (\"名前単位\" as int)-1) * 0.010 before \"計測時間hour\"");

            con.close();

        } catch (IOException ex) {
            Logger.getLogger(A100ReadTMRcsv.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(A100ReadTMRcsv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

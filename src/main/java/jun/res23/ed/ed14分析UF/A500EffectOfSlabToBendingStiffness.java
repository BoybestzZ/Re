/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.ed14分析UF;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.res23.ed.util.BeamInfo;
import jun.res23.ed.util.BeamSectionInfo;
import jun.res23.ed.util.EdefenseInfo;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 *
 * @author jun
 */
public class A500EffectOfSlabToBendingStiffness {

    public static void main(String[] args) {
        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";

            Connection con = DriverManager.getConnection(dburl, "junapp", "");

            Statement st = con.createStatement();

            BeamSectionInfo section = EdefenseInfo.LA3S3;

            ResultSet rs = st.executeQuery("select \"Strain1A[με*s]\",\"Strain1P[rad]\","
                    + "\"Strain2A[με*s]\",\"Strain2P[rad]\","
                    + "\"Strain3A[με*s]\",\"Strain3P[rad]\","
                    + "\"Strain4A[με*s]\",\"Strain4P[rad]\","
                    + "\"AxialA[N*s]\", \"AxialP[rad]\", "
                    + "\"MomentXA[Nm*s]\",\"MomentXP[rad]\""
                    + " from \"A310SectionNM\" where TESTNAME='D01Q03' and section like '"+section.getName()+"'");

            rs.next();
            Complex ul = ComplexUtils.polar2Complex(rs.getDouble(1), rs.getDouble(2)); // με*s
            Complex ur = ComplexUtils.polar2Complex(rs.getDouble(3), rs.getDouble(4));
            Complex ll = ComplexUtils.polar2Complex(rs.getDouble(5), rs.getDouble(6));
            Complex lr = ComplexUtils.polar2Complex(rs.getDouble(7), rs.getDouble(8));
            // 梁は H350x200x9x12 (BeamBのみ H-244x175x7x11)  この数字は梁を変えたら変える必要がある。
            // I = 16313.49 cm4;
            double innerHeight = 0.35 - 2 * 0.012; // m  section.getInnerHeight();
            double distance = section.getHeight() * 0.5 + 0.055; // m 

            double EIs = section.getE() * 16313.49 / 100000000.0; // N/m2 * m4 = N*m2

            // 曲率
            Complex phi = (ul.add(ur).subtract(ll).subtract(lr)).divide(2 * innerHeight * 1e6);  // ε/m
            // 鉄骨の軸力と曲げモーメント
            Complex as = ComplexUtils.polar2Complex(rs.getDouble(9), rs.getDouble(10)); // N*s
            Complex ms = ComplexUtils.polar2Complex(rs.getDouble(11), rs.getDouble(12)); // Nm*s
            // 合計曲げモーメント
            Complex mt = ms.add(as.multiply(distance)); //  Nm*s
            // 等価剛性 Nm*s / (1/m) = N*m2
            Complex EIequiv = mt.divide(phi);

            System.out.println("EIequiv=" + EIequiv.abs() + " [N*m2]    " + EIequiv+" PhaseError ="+String.format("%.2f",(EIequiv.abs()/EIequiv.getReal()-1.0)*100)+" [%]");
            System.out.println("EIs=" + EIs + " [N*m2]");
            System.out.println("Ratio=" + (EIequiv.abs() / EIs));
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(A500EffectOfSlabToBendingStiffness.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

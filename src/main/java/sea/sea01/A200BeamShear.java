/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 *
 * @author 75496
 */
public class A200BeamShear {

    public static void main(String[] args) {

        try {
            String TESTNAME = "D01Q05";
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\test/ed14v230614";
            
            double distance=1.24; // distance between Section 2 and Section 4;

            // Connect to database
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            Statement st = con.createStatement();

            // Execute query and get result set
            ResultSet rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where testname='"+TESTNAME+"' and section like 'LA3S2'");

            rs.next(); // goto the 1st line
            // get results
            double amplitudeS2 = rs.getDouble(1);
            double phaseS2 = rs.getDouble(2);

            // Execute query and get result set
            rs = st.executeQuery("SELECT \"StiffnessMomentXA[Nm/m]\"*0.000001, \"StiffnessMomentXP[rad]\"*0.000001 FROM \"A310SectionNM\" where testname='"+TESTNAME+"' and section like 'LA3S4'");

            rs.next();
            double amplitudeS4 = rs.getDouble(1);
            double phaseS4 = rs.getDouble(2);
            
            // if you want to ignore phase
            double shearForce=(amplitudeS2+amplitudeS4)/distance;
            
            
            // if you want to consider phaser
            Complex momentS2=ComplexUtils.polar2Complex(amplitudeS2, phaseS2);
            Complex momentS4=ComplexUtils.polar2Complex(amplitudeS4, phaseS4);
            Complex shearForceComplex= (momentS2.add(momentS4)).divide(distance);
            
            // display the results 
//            System.out.println("Shear force (ignore phase)=" + shearForce);
            System.out.println("Shear force (consider phase)="+shearForceComplex.getReal()); // Extract only real part of the complex value.
//            System.out.println("Shear force imag part="+shearForceComplex.getImaginary());
//             System.out.println("Shear force phase="+shearForceComplex.getArgument());
            con.close();
            

        } catch (SQLException ex) {
            Logger.getLogger(A200BeamShear.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

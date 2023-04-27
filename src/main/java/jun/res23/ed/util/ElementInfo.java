/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public interface ElementInfo {

    public double getLength();

    public String getName();

    public double getLocation(int i);

    public SectionInfo[] getSections();
    
    public boolean isColumn();

}

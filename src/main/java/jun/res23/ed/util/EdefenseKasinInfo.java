/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jun.res23.ed.util;

/**
 *
 * @author jun
 */
public class EdefenseKasinInfo {

    private final String name;
    private final String testname; // qとかsとかついてないやつ
    private double tmrTimeDiffSeconds; // t 時刻歴とTMRのTIME[s]との差分。 TMRのTIME[s]からこの時間（秒）を引けばt時刻歴と合致する。
    private double niedTimeDiffSeconds; // t 時刻歴とNiedのTIME[s]との差分。 NiedのTIME[s]からこの時間（秒）をひけばt時刻歴と合致する。
    private final String wavename;
    private final int no;

    public EdefenseKasinInfo(String name,int no,  String wavename, double tmrTimeDiffSeconds, double niedTimeDiffSeconds) {
        this.tmrTimeDiffSeconds = tmrTimeDiffSeconds;
        this.niedTimeDiffSeconds = niedTimeDiffSeconds;
        this.name = name;
        this.wavename = wavename;
        this.no=no;
        if (name.endsWith("s")) {
            testname = name.substring(0, name.length() - 1);
        } else {
            testname = name;
        }
    }

    public EdefenseKasinInfo(String name,int no,  String wavename, double tmrTimeDiffSeconds) {
        this(name,no, wavename, tmrTimeDiffSeconds, 0.0);

    }

    public EdefenseKasinInfo(String name) {
        this(name, 0, "", 0.0, 0.0);
    }

    //    public EdefenseKasinInfo(String name, double tmrTimeDiffSeconds) {
    //        this(name, "", tmrTimeDiffSeconds, 0.0);
    //    }
    public double getTmrTimeDiffSeconds() {
        return tmrTimeDiffSeconds;
    }

    public double getNiedTimeDiffSeconds() {
        return niedTimeDiffSeconds;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getWaveName() {
        return wavename;
    }

    // q とか s とかついてないやつ。
    public String getTestName() {
        return testname;
    }
    public int getTestNo() {
        return no;
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/package-info.java to edit this template
 */
package jun.res23.ed.ed11分析F;

/**
 * 分析 F は主要動範囲(R)での　周波数領域での分析を行う。
 * データ範囲は R を用いる。
 * （Qが計測の全体。Rが主要動範囲。Sが加振前の微小振動
 * データベースはed02R140DatabaseQ を用いる。
 * 
 * 参考になるものとしては ed02R190 があるが、これは 微動範囲(S)での、各梁断面の軸力、曲げモーメントおよび局所剛性を求めている。
 * これに対し、分析Fでは、主要動範囲 R を対象とする。
 * そういう意味では ed02R190プログラムを修正して、 対象を S から R に変更すればとりあえず各断面の値は計算できる。
 * 
 * ただし、最終目標は 　ed06分析T/T121あるいはT122のような梁のみの曲げモーメント図、あるいはT241のような骨組の曲げモーメント図を描くのが目標
 * である。
 * T121とかはres22ed06.mv.dbのなかのT231TimeHistoryNMテーブルを参照しているので、これに似た形にしておけば作業が楽な気がする。
 * というわけで、F231FourierNM というテーブルを作成することにする。次に T121→ F121。さらにT241→ F241を作成する。
 * 
 * 
 * 
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea01;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import jun.chart.JunChartUtil;
import jun.data.ResultSetUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author 75496
 */
public class A001GraphFourierSpectrum {

    private static final Logger logger = Logger.getLogger(A001GraphFourierSpectrum.class.getName());

    public static void main(String[] args) {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\R140DatabaseQ/D01Q01q";
            String sql = "SELECT \"Freq[Hz]\",\"Amp[με*s]\" FROM \"R151FourierR\".\"c01/05\"";

            // データベースへの接続
            Connection con = DriverManager.getConnection(dburl, "junapp", "");
            logger.info("Database opened."); // 単にメッセージを表示するだけ。
            // コマンド発行の準備
            Statement st = con.createStatement();

            //クエリ発行
            ResultSet rs = st.executeQuery(sql);
            //↑ これを実行することで、rsのなかに結果が入ってくる。
            logger.info("Query completed."); // クエリの中に日本語が入っているとうまくいかないので注意。
            // rs の中身を配列に書き出す。array[0] には Freq が、array[1]にはAmpが入ってくる。
            double[][] array = ResultSetUtils.createSeriesArray(rs);

            // 図を作成するためのデータセットを準備する。
            DefaultXYDataset dataset = new DefaultXYDataset();
            // データセットの中に先ほどのarrayをセットする。これが一つの曲線となる。
            dataset.addSeries("Spectrum", array);

            // グラフを書くための準備を行う。まず、x軸とy軸を定義する。
            NumberAxis xaxis = new NumberAxis("Freq[Hz]");
            NumberAxis yaxis = new NumberAxis("Amp[με*s]");
            // グラフを書く人(rendererを定義する。これはプロットと線を書くrenderer。（ここを変えれば棒グラフとかも可能。）
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

            // 上記の4つを使ってグラフの中身を作る。
            XYPlot plot = new XYPlot(dataset, xaxis, yaxis, renderer);

            // グラフの外見を作る。
            JFreeChart chart = new JFreeChart(plot);
            // デフォルトだと周りがグレーになっちゃうのでそれを白に
            chart.setBackgroundPaint(Color.WHITE);

            // グラフを表示する。（これは伊山が作ったライブラリを使っている。デフォルトの方法はちょっと面倒なので。）
            //  JunChartUtil.show(chart);
            
            // Output the graph to SVG file
            Path svgfile = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\sea01\\A001FourierSpectrum.svg");
            try {
                JunChartUtil.svg(svgfile, 400, 200, chart);
            } catch (IOException ex) {
                Logger.getLogger(A001GraphFourierSpectrum.class.getName()).log(Level.SEVERE, null, ex);
            }

            // やらなくても大丈夫だけど、やっといた方がいい。データベースを閉じる。
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A001GraphFourierSpectrum.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

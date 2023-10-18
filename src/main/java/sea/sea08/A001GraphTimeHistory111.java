/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sea.sea08;

import java.awt.BasicStroke;
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
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYDataset;

/**
 *
 * @author 75496
 */
public class A001GraphTimeHistory111 {

    private static final Logger logger = Logger.getLogger(A001GraphTimeHistory111.class.getName());

    public static void main(String[] args) throws IOException, SQLException{
        
//        double distance1 = 0.326; // distance between inner web (Beam3 = 350 - 2*12)
//        double distance2 = 1.63;  // distance between inner web (BeamB = 244 - 2*...)
//        double slab = 0.11;
           createResidualStrain("a01/01", "a11");
//           createResidualStrain("a01/02", "a12");
//           createResidualStrain("a01/03", "a13");
//           createResidualStrain("a01/04", "a14");
//           createResidualStrain("a01/05", "a15");
//           createResidualStrain("a01/06", "a16");
//           createResidualStrain("a01/07", "a17");
//           createResidualStrain("a01/08", "a18");
//           
//           createResidualStrain("a02/01", "a21");
//           createResidualStrain("a02/02", "a22");
//           createResidualStrain("a02/03", "a23");
//           createResidualStrain("a02/04", "a24");
//           createResidualStrain("a02/05", "a25");
//           createResidualStrain("a02/06", "a26");
//           createResidualStrain("a02/07", "a27");
//           createResidualStrain("a02/08", "a28");
//           
//           createResidualStrain("a03/01", "a31");
//           createResidualStrain("a03/02", "a32");
//           createResidualStrain("a03/03", "a33");
//           createResidualStrain("a03/04", "a34");
//           createResidualStrain("a03/05", "a35");
//           createResidualStrain("a03/06", "a36");
//           createResidualStrain("a03/07", "a37");
//           createResidualStrain("a03/08", "a38");
//           
//           createResidualStrain("b01/01", "b11");
//           createResidualStrain("b01/02", "b12");
//           createResidualStrain("b01/03", "b13");
//           createResidualStrain("b01/04", "b14");
//           createResidualStrain("b01/05", "b15");
//           createResidualStrain("b01/06", "b16");
//           createResidualStrain("b01/07", "b17");
//           createResidualStrain("b01/08", "b18");
//           
//           createResidualStrain("b02/01", "b21");
//           createResidualStrain("b02/02", "b22");
//           createResidualStrain("b02/03", "b23");
//           createResidualStrain("b02/04", "b24");
//           createResidualStrain("b02/05", "b25");
//           createResidualStrain("b02/06", "b26");
//           createResidualStrain("b02/07", "b27");
//           createResidualStrain("b02/08", "b28");
//           
//           createResidualStrain("b03/01", "b31");
//           createResidualStrain("b03/02", "b32");
//           createResidualStrain("b03/03", "b33");
//           createResidualStrain("b03/04", "b34");
//           createResidualStrain("b03/05", "b35");
//           createResidualStrain("b03/06", "b36");
//           createResidualStrain("b03/07", "b37");
//           createResidualStrain("b03/08", "b38");
//           
//           createResidualStrain("c01/01", "c11");
//           createResidualStrain("c01/02", "c12");
//           createResidualStrain("c01/03", "c13");
//           createResidualStrain("c01/04", "c14");
//           createResidualStrain("c01/05", "c15");
//           createResidualStrain("c01/06", "c16");
//           createResidualStrain("c01/07", "c17");
//           createResidualStrain("c01/08", "c18");
//           
//           createResidualStrain("c02/01", "c21");
//           createResidualStrain("c02/02", "c22");
//           createResidualStrain("c02/03", "c23");
//           createResidualStrain("c02/04", "c24");
//           createResidualStrain("c02/05", "c25");
//           createResidualStrain("c02/06", "c26");
//           createResidualStrain("c02/07", "c27");
//           createResidualStrain("c02/08", "c28");
//           
//           createResidualStrain("c03/01", "c31");
//           createResidualStrain("c03/02", "c32");
//           createResidualStrain("c03/03", "c33");
//           createResidualStrain("c03/04", "c34");
//           createResidualStrain("c03/05", "c35");
//           createResidualStrain("c03/06", "c36");
//           createResidualStrain("c03/07", "c37");
//           createResidualStrain("c03/08", "c38");
//           
           createResidualStrain("d01/01", "d11");
//           createResidualStrain("d01/02", "d12");
//           createResidualStrain("d01/03", "d13");
//           createResidualStrain("d01/04", "d14");
//           createResidualStrain("d01/05", "d15");
//           createResidualStrain("d01/06", "d16");
//           createResidualStrain("d01/07", "d17");
//           createResidualStrain("d01/08", "d18");
//           
           createResidualStrain("d02/01", "d21");
//           createResidualStrain("d02/02", "d22");
//           createResidualStrain("d02/03", "d23");
//           createResidualStrain("d02/04", "d24");
//           createResidualStrain("d02/05", "d25");
//           createResidualStrain("d02/06", "d26");
//           createResidualStrain("d02/07", "d27");
//           createResidualStrain("d02/08", "d28");
//          
//           createResidualStrain("d03/01", "d31");
//           createResidualStrain("d03/02", "d32");
//           createResidualStrain("d03/03", "d33");
//           createResidualStrain("d03/04", "d34");
//           createResidualStrain("d03/05", "d35");
//           createResidualStrain("d03/06", "d36");
//           createResidualStrain("d03/07", "d37");
//           createResidualStrain("d03/08", "d38");
//           
//           createResidualStrain("e01/01", "e11");
//           createResidualStrain("e01/02", "e12");
//           createResidualStrain("e01/03", "e13");
//           createResidualStrain("e01/04", "e14");
//           createResidualStrain("e01/05", "e15");
//           createResidualStrain("e01/06", "e16");
//           createResidualStrain("e01/07", "e17");
//           createResidualStrain("e01/08", "e18");
//           
//           createResidualStrain("e02/01", "e21");
//           createResidualStrain("e02/02", "e22");
//           createResidualStrain("e02/03", "e23");
//           createResidualStrain("e02/04", "e24");
//           createResidualStrain("e02/05", "e25");
//           createResidualStrain("e02/06", "e26");
//           createResidualStrain("e02/07", "e27");
//           createResidualStrain("e02/08", "e28");
//          
//           createResidualStrain("e03/01", "e31");
//           createResidualStrain("e03/02", "e32");
//           createResidualStrain("e03/03", "e33");
//           createResidualStrain("e03/04", "e34");
//           createResidualStrain("e03/05", "e35");
//           createResidualStrain("e03/06", "e36");
//           createResidualStrain("e03/07", "e37");
//           createResidualStrain("e03/08", "e38");
//           
//           createResidualStrain("f01/01", "f11");
//           createResidualStrain("f01/02", "f12");
//           createResidualStrain("f01/03", "f13");
//           createResidualStrain("f01/04", "f14");
//           createResidualStrain("f01/05", "f15");
//           createResidualStrain("f01/06", "f16");
//           createResidualStrain("f01/07", "f17");
//           createResidualStrain("f01/08", "f18");
//           
//           createResidualStrain("f02/01", "f21");
//           createResidualStrain("f02/02", "f22");
//           createResidualStrain("f02/03", "f23");
//           createResidualStrain("f02/04", "f24");
//           createResidualStrain("f02/05", "f25");
//           createResidualStrain("f02/06", "f26");
//           createResidualStrain("f02/07", "f27");
//           createResidualStrain("f02/08", "f28");
//           
//           createResidualStrain("f03/01", "f31");
//           createResidualStrain("f03/02", "f32");
//           createResidualStrain("f03/03", "f33");
//           createResidualStrain("f03/04", "f34");
//           createResidualStrain("f03/05", "f35");
//           createResidualStrain("f03/06", "f36");
//           createResidualStrain("f03/07", "f37");
//           createResidualStrain("f03/08", "f38");
//           
//           createResidualStrain("g01/01", "g11");
//           createResidualStrain("g01/02", "g12");
//           createResidualStrain("g01/03", "g13");
//           createResidualStrain("g01/04", "g14");
//           createResidualStrain("g01/05", "g15");
//           createResidualStrain("g01/06", "g16");
//           createResidualStrain("g01/07", "g17");
//           createResidualStrain("g01/08", "g18");
//           
//           createResidualStrain("g02/01", "g21");
//           createResidualStrain("g02/02", "g22");
//           createResidualStrain("g02/03", "g23");
//           createResidualStrain("g02/04", "g24");
//           createResidualStrain("g02/05", "g25");
//           createResidualStrain("g02/06", "g26");
//           createResidualStrain("g02/07", "g27");
//           createResidualStrain("g02/08", "g28");
//           
//           createResidualStrain("g03/01", "g31");
//           createResidualStrain("g03/02", "g32");
//           createResidualStrain("g03/03", "g33");
//           createResidualStrain("g03/04", "g34");
//           createResidualStrain("g03/05", "g35");
//           createResidualStrain("g03/06", "g36");
//           createResidualStrain("g03/07", "g37");
//           createResidualStrain("g03/08", "g38");
//           
//           createResidualStrain("h01/01", "h11");
//           createResidualStrain("h01/02", "h12");
//           createResidualStrain("h01/03", "h13");
//           createResidualStrain("h01/04", "h14");
//           createResidualStrain("h01/05", "h15");
//           createResidualStrain("h01/06", "h16");
//           createResidualStrain("h01/07", "h17");
//           createResidualStrain("h01/08", "h18");
//           
//           createResidualStrain("h02/01", "h21");
//           createResidualStrain("h02/02", "h22");
//           createResidualStrain("h02/03", "h23");
//           createResidualStrain("h02/04", "h24");
//           createResidualStrain("h02/05", "h25");
//           createResidualStrain("h02/06", "h26");
//           createResidualStrain("h02/07", "h27");
//           createResidualStrain("h02/08", "h28");
//           
//           createResidualStrain("h03/01", "h31");
//           createResidualStrain("h03/02", "h32");
//           createResidualStrain("h03/03", "h33");
//           createResidualStrain("h03/04", "h34");
//           createResidualStrain("h03/05", "h35");
//           createResidualStrain("h03/06", "h36");
//           createResidualStrain("h03/07", "h37");
//           createResidualStrain("h03/08", "h38");
//          
//           createResidualStrain("i01/01", "i11");
//           createResidualStrain("i01/02", "i12");
//           createResidualStrain("i01/03", "i13");
//           createResidualStrain("i01/04", "i14");
//           createResidualStrain("i01/05", "i15");
//           createResidualStrain("i01/06", "i16");
//           createResidualStrain("i01/07", "i17");
//           createResidualStrain("i01/08", "i18");
//          
//           createResidualStrain("i02/01", "i21");
//           createResidualStrain("i02/02", "i22");
//           createResidualStrain("i02/03", "i23");
//           createResidualStrain("i02/04", "i24");
//           createResidualStrain("i02/05", "i25");
//           createResidualStrain("i02/06", "i26");
//           createResidualStrain("i02/07", "i27");
//           createResidualStrain("i02/08", "i28");
//          
//           createResidualStrain("i03/01", "i31");
//           createResidualStrain("i03/02", "i32");
//           createResidualStrain("i03/03", "i33");
//           createResidualStrain("i03/04", "i34");
//           createResidualStrain("i03/05", "i35");
//           createResidualStrain("i03/06", "i36");
//           createResidualStrain("i03/07", "i37");
//           createResidualStrain("i03/08", "i38");
//           
//           createResidualStrain("j01/01", "j11");
//           createResidualStrain("j01/02", "j12");
//           createResidualStrain("j01/03", "j13");
//           createResidualStrain("j01/04", "j14");
//           createResidualStrain("j01/05", "j15");
//           createResidualStrain("j01/06", "j16");
//           createResidualStrain("j01/07", "j17");
//           createResidualStrain("j01/08", "j18");
//           
//           createResidualStrain("j02/01", "j21");
//           createResidualStrain("j02/02", "j22");
//           createResidualStrain("j02/03", "j23");
//           createResidualStrain("j02/04", "j24");
//           createResidualStrain("j02/05", "j25");
//           createResidualStrain("j02/06", "j26");
//           createResidualStrain("j02/07", "j27");
//           createResidualStrain("j02/08", "j28");
//           
//           createResidualStrain("j03/01", "j31");
//           createResidualStrain("j03/02", "j32");
//           createResidualStrain("j03/03", "j33");
//           createResidualStrain("j03/04", "j34");
//           createResidualStrain("j03/05", "j35");
//           createResidualStrain("j03/06", "j36");
//           createResidualStrain("j03/07", "j37");
//           createResidualStrain("j03/08", "j38");

           

    }

    public static void createResidualStrain(String table, String tableName) throws IOException, SQLException {

        try {
            String dburl = "jdbc:h2:tcp://localhost/C:\\Users\\75496\\Documents\\E-Defense\\\\test/res22ed06test";
            String sql = "SELECT \"TotalTime[s]\", \"Strain[με]\" FROM \"T220TimeHistoryStrain\".\"" + table + "\"";

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
            DateAxis xaxis = new DateAxis("T[ms]");
            NumberAxis yaxis = new NumberAxis("Strain[ε]");
            // グラフを書く人(rendererを定義する。これはプロットと線を書くrenderer。（ここを変えれば棒グラフとかも可能。）
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

            // 上記の4つを使ってグラフの中身を作る。
            XYPlot plot = new XYPlot(dataset, xaxis, yaxis, renderer);

            // グラフの外見を作る。
            JFreeChart chart = new JFreeChart(plot);
//            // デフォルトだと周りがグレーになっちゃうのでそれを白に
//            chart.setBackgroundPaint(Color.WHITE);

                        // Customize the chart
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);
           
            NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont((int) 12f));
            domainAxis.setVerticalTickLabels(true);
            plot.setOutlinePaint(Color.BLACK);
            plot.setOutlineStroke(new BasicStroke(2f)); // frame around the plot
            plot.setAxisOffset(RectangleInsets.ZERO_INSETS); // remove space between frame and axis.

            // グラフを表示する。（これは伊山が作ったライブラリを使っている。デフォルトの方法はちょっと面倒なので。）
            //  JunChartUtil.show(chart);
            
            // Output the graph to SVG file
            Path svgfile = Path.of("C:\\Users\\75496\\Documents\\E-Defense\\residualstraintimehistory\\residualstraintimehistory_" + tableName + ".svg");
            try {
                JunChartUtil.svg(svgfile, 900, 300, chart);
            } catch (IOException ex) {
                Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
            }

            // やらなくても大丈夫だけど、やっといた方がいい。データベースを閉じる。
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(A001GraphTimeHistory111.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}



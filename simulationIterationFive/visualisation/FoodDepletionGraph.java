package visualisation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Color;

public class FoodDepletionGraph {
    private JFreeChart chart;
    private XYSeriesCollection dataset;
    private JPanel chartPanel;
    private Map<Integer, XYSeries> foodSeriesMap; 
    private XYLineAndShapeRenderer renderer;

    public FoodDepletionGraph() {
        dataset = new XYSeriesCollection();
        chart = ChartFactory.createXYLineChart(
                "Food Source Depletion Over Time",
                "Time (Seconds)",
                "Food Remaining",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        foodSeriesMap = new HashMap<>();
        renderer = new XYLineAndShapeRenderer();
        chart.getXYPlot().setRenderer(renderer);
        
        chartPanel = new ChartPanel(chart);
    }

    public void initializeFoodSources(int foodSourceCount, List<Integer> initialQuantities, List<Color> foodColors) {
        dataset.removeAllSeries();
        foodSeriesMap.clear();
        timeElapsed = 0;

        for (int i = 0; i < foodSourceCount; i++) {
            XYSeries series = new XYSeries("FS" + (i + 1));
            series.add(0, initialQuantities.get(i));
            foodSeriesMap.put(i, series);
            dataset.addSeries(series);

            renderer.setSeriesPaint(i, foodColors.get(i));

            renderer.setSeriesShapesVisible(i, false); 
            renderer.setSeriesLinesVisible(i, true); 
        }
    }


    public void updateGraph(List<Integer> currentQuantities) {
        timeElapsed++;

        for (int i = 0; i < currentQuantities.size(); i++) {
            XYSeries series = foodSeriesMap.get(i);
            if (series != null) {
                series.add(timeElapsed, currentQuantities.get(i));
            }
        }
    }

    public void resetGraph() {
        for (XYSeries series : foodSeriesMap.values()) {
            series.clear();
            series.add(0, 50); 
        }
        timeElapsed = 0;
    }

    public JPanel getChartPanel() {
        return chartPanel;
    }
}

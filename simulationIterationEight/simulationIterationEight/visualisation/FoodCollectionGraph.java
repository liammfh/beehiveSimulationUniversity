package visualisation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FoodCollectionGraph {
    private XYSeries foodSeries;
    private XYSeriesCollection dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private List<Integer> timeSteps;
    private List<Integer> foodCollected;
    private int lastFoodCount = 0;

    public FoodCollectionGraph() {
        foodSeries = new XYSeries("Food Collected");
        dataset = new XYSeriesCollection(foodSeries);
        chart = ChartFactory.createXYLineChart(
                "Bee Food Collection Over Time",
                "Time (Seconds)",
                "Food Collected",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chartPanel = new ChartPanel(chart);
        timeSteps = new ArrayList<>();
        foodCollected = new ArrayList<>();

        // Ensure graph starts at (0,0)
        timeSteps.add(0);
        foodCollected.add(0);
        foodSeries.add(0, 0);
    }

    public void updateGraph(int currentTime, int currentFoodCount) {
        int foodDepositedThisSecond = currentFoodCount - lastFoodCount;
        lastFoodCount = currentFoodCount;

        timeSteps.add(currentTime);
        foodCollected.add(foodDepositedThisSecond);

        foodSeries.add(currentTime, foodDepositedThisSecond);
        chartPanel.repaint();
    }

    public void resetGraph() {
        foodSeries.clear();
        timeSteps.clear();
        foodCollected.clear();
        lastFoodCount = 0;

        foodSeries.add(0, 0);
        chartPanel.repaint();
    }

    public JPanel getChartPanel() {
        return chartPanel;
    }
}

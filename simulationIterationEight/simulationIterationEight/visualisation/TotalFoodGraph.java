package visualisation;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class TotalFoodGraph {
    private XYSeriesCollection dataset;
    private XYSeries totalFoodSeries;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public TotalFoodGraph() {
        dataset = new XYSeriesCollection();
        totalFoodSeries = new XYSeries("Total Food");
        dataset.addSeries(totalFoodSeries);
        
        totalFoodSeries.add(0, 0);
        
        chart = ChartFactory.createXYLineChart(
            "Total Food Deposited Over Time",
            "Time (Seconds)",
            "Total Food Deposited",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        chartPanel = new ChartPanel(chart);
    }

    /**
     * Updates the graph by adding the latest cumulative food deposit value.
     * Ensures that when no food is collected, the graph holds the last value
     * to create a step-like pattern rather than a straight line.
     */
    public void updateGraph(int timeElapsed, int totalFood) {
        // Prevents duplicate time entries
        if (totalFoodSeries.getItemCount() > 0) {
            int lastIndex = totalFoodSeries.getItemCount() - 1;
            Number lastTime = totalFoodSeries.getX(lastIndex);
            Number lastFoodAmount = totalFoodSeries.getY(lastIndex);

            if (lastTime.intValue() == timeElapsed) {
                return; // Avoid duplicate time entries
            }

            // Ensures step-like pattern (hold previous value if no new food is collected)
            if (lastFoodAmount.intValue() == totalFood) {
                totalFoodSeries.add(timeElapsed, lastFoodAmount);
            }
        }

        // Add new value at this time step
        totalFoodSeries.add(timeElapsed, totalFood);
    }

    /**
     * Resets the graph to its initial empty state.
     */
    public void resetGraph() {
        totalFoodSeries.clear();  
        totalFoodSeries.add(0, 0);  // 🎯 **Ensure reset starts at (0,0)**
    }

    /**
     * Returns the chart panel for integration into the GUI.
     */
    public JPanel getChartPanel() {
        return chartPanel;
    }
}

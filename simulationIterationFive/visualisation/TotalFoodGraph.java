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


    public void updateGraph(int timeElapsed, int totalFood) {
        if (totalFoodSeries.getItemCount() > 0) {
            int lastIndex = totalFoodSeries.getItemCount() - 1;
            Number lastTime = totalFoodSeries.getX(lastIndex);
            Number lastFoodAmount = totalFoodSeries.getY(lastIndex);

            if (lastTime.intValue() == timeElapsed) {
                if (lastTime.intValue() == 0 && lastFoodAmount.intValue() == 0 && totalFood > 0) {
                    totalFoodSeries.updateByIndex(lastIndex, totalFood); 
                }
                return;
            }
            if (lastFoodAmount.intValue() == totalFood) {
                totalFoodSeries.add(timeElapsed, lastFoodAmount);
                return;
            }
        }

        totalFoodSeries.add(timeElapsed, totalFood);
    }


    public void resetGraph() {
        totalFoodSeries.clear();  
        totalFoodSeries.add(0, 0); 
    }


    public JPanel getChartPanel() {
        return chartPanel;
    }
}

package gui;

import simulation.SimulationPanel;
import simulation.Hive;
import visualisation.FoodCollectionGraph;
import visualisation.TotalFoodGraph;
import visualisation.FoodDepletionGraph;

import javax.swing.*;
import java.awt.*;

public class BeeSimulationGUI {
    private SimulationPanel simulationPanel;
    private Timer simulationTimer;
    private boolean isSimulationRunning = false;
    private boolean isPaused = false;
    private int timeElapsed = 0;
    private Hive hive;
    private FoodCollectionGraph foodGraph;
    private JFrame graphFrame;
    private JSlider beeSlider;
    private JSlider foodSlider;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private TotalFoodGraph totalFoodGraph = new TotalFoodGraph();
    private FoodDepletionGraph foodDepletionGraph;
    

    public BeeSimulationGUI() {
        JFrame frame = new JFrame("Bee Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);
        frame.setLayout(new BorderLayout());
        foodGraph = new FoodCollectionGraph();
        totalFoodGraph = new TotalFoodGraph();
        // **Initialize Hive**
        foodDepletionGraph = new FoodDepletionGraph();
        hive = new Hive(475, 250, 100, foodDepletionGraph);
        totalFoodGraph = new TotalFoodGraph();
        hive.setGraphs(totalFoodGraph);

        // **Simulation Panel**
        simulationPanel = new SimulationPanel(hive);
        frame.add(simulationPanel, BorderLayout.CENTER);

        // **Control Panel**
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setPreferredSize(new Dimension(800, 100));

        JLabel beeLabel = new JLabel("Number of Bees:");
        beeSlider = new JSlider(0, 100, 0);
        beeSlider.setMajorTickSpacing(20);
        beeSlider.setPaintTicks(true);
        beeSlider.setPaintLabels(true);

        JLabel foodLabel = new JLabel("Number of Food Sources:");
        foodSlider = new JSlider(0, 10, 0);
        foodSlider.setMajorTickSpacing(2);
        foodSlider.setPaintTicks(true);
        foodSlider.setPaintLabels(true);

        startButton = new JButton("Start Simulation");
        pauseButton = new JButton("Pause Simulation");
        resetButton = new JButton("Reset Simulation");

        controlPanel.add(beeLabel);
        controlPanel.add(beeSlider);
        controlPanel.add(foodLabel);
        controlPanel.add(foodSlider);
        controlPanel.add(startButton);
        controlPanel.add(pauseButton);
        controlPanel.add(resetButton);

        frame.add(controlPanel, BorderLayout.SOUTH);

        // **Button Listeners**
        startButton.addActionListener(e -> startSimulation());
        pauseButton.addActionListener(e -> simulationPanel.pauseSimulation());
        resetButton.addActionListener(e -> resetSimulation());

        frame.setVisible(true);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu visualisationMenu = new JMenu("Visualisations");
        JMenuItem openGraphs = new JMenuItem("Open Graphs");

        openGraphs.addActionListener(e -> openGraphWindow());
        visualisationMenu.add(openGraphs);
        menuBar.add(visualisationMenu);
        frame.setJMenuBar(menuBar);
    }

    private void startSimulation() {
        if (isSimulationRunning) return;

        int beeCount = beeSlider.getValue();
        int foodCount = foodSlider.getValue();

        if (beeCount == 0 || foodCount == 0) {
            JOptionPane.showMessageDialog(null,
                "Simulation cannot start without at least 1 bee and 1 food source.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to start with these parameters?",
            "Start Simulation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.NO_OPTION) {
            return;
        }
        
        simulationTimer = new Timer(1000, e -> {
            timeElapsed++;
            hive.incrementTime();
            foodGraph.updateGraph(timeElapsed, hive.getStoredFood());
            hive.updateFoodGraph(simulationPanel.getFoodSources());
        });
        simulationTimer.start();
        
        // **Initialize Simulation**
        simulationPanel.setPopulation(beeCount);
        simulationPanel.setFoodSources(foodCount);
        simulationPanel.startSimulation();
        hive.resetTime();

        beeSlider.setEnabled(false);
        foodSlider.setEnabled(false);

        isSimulationRunning = true;
        isPaused = false;
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private void resetSimulation() {
        if (simulationTimer != null) {
            simulationTimer.stop();
        }

        isSimulationRunning = false;
        isPaused = false;
        timeElapsed = 0;

        // 🎯 Reset Hive & Food Count
        hive = new Hive(475, 250, 100, foodDepletionGraph);
        foodDepletionGraph.resetGraph();
        hive.setGraphs(totalFoodGraph); // Ensure the graph is linked again
        hive.resetTime();
        
        // 🎯 Explicitly Reset Stored Food Count
        hive.resetStoredFood();  // <<< ADD THIS LINE TO RESET FOOD TO 0

        // 🎯 Reset Graphs
        totalFoodGraph.resetGraph();
        
        // 🎯 Reset Simulation Panel
        simulationPanel.resetSimulation();

        // 🎯 Reset UI Controls
        beeSlider.setEnabled(true);
        foodSlider.setEnabled(true);
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        resetButton.setEnabled(false);
    }
    
    private void openGraphWindow() {
        if (graphFrame == null || !graphFrame.isVisible()) {
            graphFrame = new JFrame("Food Collection Graphs");
            graphFrame.setSize(650, 900);
            graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            graphFrame.setLayout(new GridLayout(2, 1));

            graphFrame.add(totalFoodGraph.getChartPanel());
            graphFrame.add(foodDepletionGraph.getChartPanel());

            graphFrame.setVisible(true);
        } else {
            graphFrame.toFront();
        }
    }

    public static void main(String[] args) {
        new BeeSimulationGUI();
    }
}

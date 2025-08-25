package simulation;

import javax.swing.*;
import visualisation.FoodDepletionGraph;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SimulationPanel extends JPanel {
    private List<Agent> agents;
    private List<FoodSource> foodSources;
    private Timer timer;
    private Hive hive;
    private boolean isRunning = false;

    public SimulationPanel(Hive hive) {
        this.hive = hive;
        setBackground(Color.GREEN);
        setPreferredSize(new Dimension(1000, 500));

        agents = new ArrayList<>();
        foodSources = new ArrayList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        hive.draw(g);
        for (Agent agent : agents) agent.draw(g);
        for (FoodSource food : foodSources) food.draw(g);
    }

    public void startSimulation() {
        if (timer == null || !timer.isRunning()) {
            isRunning = true;
            timer = new Timer(16, e -> {
                for (Agent agent : agents) {
                    agent.move(getWidth(), getHeight(), agents, foodSources);
                }
                repaint();
            });
            timer.start();
        }
    }

    public void pauseSimulation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

    }	
    
    public void resumeSimulation() {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }
  
    public void setHive(Hive hive) {
        this.hive = hive;
    }
    
    public void resetSimulation() {
        if (timer != null) {
            timer.stop();
        }
        isRunning = false;
        agents.clear();
        foodSources.clear();
        repaint();
    }

    public void setPopulation(int population) {
        if (isRunning) return;

        int currentPopulation = agents.size();
        if (population > currentPopulation) {
            for (int i = 0; i < (population - currentPopulation); i++) {
                boolean isResting = (i % 10 == 0);
                agents.add(new Agent(hive, isResting));
            }
        } else if (population < currentPopulation) {
            for (int i = 0; i < (currentPopulation - population); i++) {
                agents.remove(agents.size() - 1);
            }
        }
        repaint();
    }

    public void setFoodSources(int count) {
        Random random = new Random();
        foodSources.clear();
        List<Integer> initialQuantities = new ArrayList<>();
        List<Color> foodColors = new ArrayList<>();

        Color[] FOOD_COLORS = {
            Color.BLUE, Color.RED, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.YELLOW, Color.PINK, Color.GRAY,
            Color.LIGHT_GRAY, Color.BLACK
        };

        while (foodSources.size() < count) {
            int x = random.nextInt(getWidth() - 20) + 10;
            int y = random.nextInt(getHeight() - 20) + 10;

            int hiveDistance = (int) Math.sqrt(Math.pow(x - hive.getX(), 2) + Math.pow(y - hive.getY(), 2));
            if (hiveDistance < hive.getRadius() + 50) continue;

            boolean tooClose = false;
            for (FoodSource food : foodSources) {
                int foodDistance = (int) Math.sqrt(Math.pow(x - food.getX(), 2) + Math.pow(y - food.getY(), 2));
                if (foodDistance < 30) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                int foodID = foodSources.size();
                Color assignedColor = FOOD_COLORS[foodID % FOOD_COLORS.length];
                FoodSource food = new FoodSource(x, y, 10, 50, "FS" + (foodID + 1), assignedColor);
                foodSources.add(food);
                initialQuantities.add(food.getQuantity());
                foodColors.add(assignedColor);
            }
        }

        hive.getFoodDepletionGraph().initializeFoodSources(foodSources.size(), initialQuantities, foodColors);

        repaint();
    }
    
    public List<FoodSource> getFoodSources() {
        return foodSources;
    }

    public Hive getHive() {
        return hive;
    }
}

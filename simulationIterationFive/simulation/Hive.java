package simulation;

import visualisation.FoodCollectionGraph;
import visualisation.FoodDepletionGraph;
import visualisation.TotalFoodGraph;
import java.awt.*;
import java.util.List;

public class Hive {
    private int x, y, radius;
    private int storedFood = 0;
    private int timeElapsed = 0;
    private FoodCollectionGraph foodGraph;
    private TotalFoodGraph totalFoodGraph;
    private FoodDepletionGraph foodDepletionGraph;


    public Hive(int x, int y, int radius, FoodDepletionGraph foodDepletionGraph) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.foodDepletionGraph = foodDepletionGraph;
    }

    public void setGraphs(TotalFoodGraph totalFoodGraph) {
        this.totalFoodGraph = totalFoodGraph;
    }

    public void depositFood(int amount) {
        storedFood += amount;
        incrementTime();

        if (totalFoodGraph != null) {
        	totalFoodGraph.updateGraph(timeElapsed, storedFood);
        }
    }
    
    public FoodDepletionGraph getFoodDepletionGraph() {
        return foodDepletionGraph;
    }
    
    public void updateFoodGraph(List<FoodSource> foodSources) {
        List<Integer> currentQuantities = foodSources.stream()
                .map(FoodSource::getQuantity)
                .toList(); 
        foodDepletionGraph.updateGraph(currentQuantities);
    }
   

    public void incrementTime() {
        timeElapsed++;
        
    }

    public void resetTime() {
        timeElapsed = 0;
    }
    
    public void resetFood() {
        storedFood = 0;
    }
    
    public int getStoredFood() {
        return storedFood;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String foodText = "Food: " + storedFood;
        int textWidth = g.getFontMetrics().stringWidth(foodText);
        g.drawString(foodText, x - textWidth / 2, y - radius - 10);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }
}

package simulation;

import java.awt.*;

public class FoodSource {
    private int x, y;
    private int radius;
    private int quantity;
    private String label; // FS1, FS2, etc.
    private Color color;  // Matching color for graph

    public FoodSource(int x, int y, int radius, int initialQuantity, String label, Color color) { 
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.quantity = initialQuantity;
        this.label = label;
        this.color = color;
    }
   

    public int takeFood(int amount) {
        int taken = Math.min(amount, quantity);
        quantity -= taken;
        return taken;
    }

    public boolean isDepleted() {
        return quantity <= 0;
    }
    
    public int getQuantity() {
        return quantity;  // Ensure this matches the actual variable storing food amount
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }

    public void draw(Graphics g) {
    	g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        
        g.setFont(new Font("Arial", Font.BOLD, 12));
        
        if (color.equals(Color.BLACK) || color.equals(Color.BLUE)) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }

        // 📍 Position text at the center
        String foodText = String.valueOf(quantity);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(foodText);
        int textHeight = metrics.getHeight();
        
        g.drawString(foodText, x - textWidth / 2, y + textHeight / 4);

        g.setColor(Color.WHITE);
        g.drawString(label, x - 10, y - 10); //
    }
}

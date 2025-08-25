package simulation;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Agent {
    private int x, y;
    private double dx, dy;
    private static final int SIZE = 15;
    private static final double SPEED = 3.0;
    private static final int COLLECTION_TIME = 60;
    private static final int DEPOSIT_TIME = 60;
    private static final int RESTING_TIME = 100;
    private static final int SEARCH_TIMEOUT = 500;
    private static final double REJUVENATION_CHANCE = 0.3;

    private int collectionTimer = 0;
    private int depositTimer = 0;
    private int restingTimer = RESTING_TIME;
    private int searchTimer = SEARCH_TIMEOUT;
    private int carriedFood = 0;
    private double approachAngle = 0; // 🔹 Stores angle on arrival

    private Hive hive;
    private FoodSource rememberedFoodSource = null;
    private Random random = new Random();

    public enum State {
        RESTING, SEARCHING, COLLECTING, RETURNING, DEPOSITING
    }

    private State state;

    public Agent(Hive hive, boolean isResting) {
        this.hive = hive;
        int radius = random.nextInt(hive.getRadius());
        double angle = random.nextDouble() * 2 * Math.PI;

        this.x = hive.getX() + (int) (radius * Math.cos(angle));
        this.y = hive.getY() + (int) (radius * Math.sin(angle));

        double directionAngle = random.nextDouble() * 2 * Math.PI;
        this.dx = SPEED * Math.cos(directionAngle);
        this.dy = SPEED * Math.sin(directionAngle);

        this.state = isResting ? State.RESTING : State.SEARCHING;
    }

    public void move(int panelWidth, int panelHeight, List<Agent> agents, List<FoodSource> foodSources) {
        if (state == State.RESTING) {
            restingTimer--;
            if (restingTimer <= 0 && rememberedFoodSource != null) {
                state = State.SEARCHING;
            }
            return;
        }

        if (state == State.COLLECTING) {
            collectionTimer--;
            if (collectionTimer <= 0) {
                state = State.RETURNING;
            }
            return;
        }

        if (state == State.RETURNING) {
            moveTowardHive();
            if (isCollidingWithHive()) {
                state = State.DEPOSITING;
                depositTimer = DEPOSIT_TIME;
                approachAngle = Math.atan2(dy, dx); // 🔹 Store approach angle
            }
            return;
        }

        if (state == State.DEPOSITING) {
            depositTimer--;
            if (depositTimer <= 0) {
                depositFood();
                decideNextState();
            }
            return;
        }

        avoidCollision(agents);

        x += dx;
        y += dy;
        searchTimer--;

        for (FoodSource food : foodSources) {
            if (isCollidingWith(food) && state == State.SEARCHING) {
                startCollecting(food);
                return;
            }
        }

        if (searchTimer <= 0) {
            forceReturnToHive();
            return;
        }

        checkWallCollision(panelWidth, panelHeight);
        normalizeVelocity();
    }

    private void moveTowardHive() {
        int hiveX = hive.getX();
        int hiveY = hive.getY();

        double angle = Math.atan2(hiveY - y, hiveX - x);
        dx = SPEED * Math.cos(angle);
        dy = SPEED * Math.sin(angle);

        x += dx;
        y += dy;
    }

    private void startCollecting(FoodSource food) {
        if (!food.isDepleted()) {
            state = State.COLLECTING;
            collectionTimer = COLLECTION_TIME;
            carriedFood = Math.min(food.takeFood(10), 10);
            rememberedFoodSource = food;
        }
    }

    private boolean isCollidingWithHive() {
        int distance = (int) Math.sqrt(Math.pow(x - hive.getX(), 2) + Math.pow(y - hive.getY(), 2));
        return distance < hive.getRadius();
    }

    private void depositFood() {
        if (carriedFood > 0) {
            hive.depositFood(carriedFood);
            carriedFood = 0;
        }
    }

    private void decideNextState() {
        if (rememberedFoodSource != null) {
            // **🐝 Ensure angle is properly reversed for accurate return**
            double exitAngle = approachAngle + Math.PI; // Flip direction
            dx = SPEED * Math.cos(exitAngle);
            dy = SPEED * Math.sin(exitAngle);

            // **🐝 Push slightly out of the hive WITHOUT affecting direction**
            x = hive.getX() + (int) ((hive.getRadius() + 2) * Math.cos(exitAngle));
            y = hive.getY() + (int) ((hive.getRadius() + 2) * Math.sin(exitAngle));

            // **🐝 Set state back to SEARCHING to continue the journey**
            state = State.SEARCHING;
            searchTimer = SEARCH_TIMEOUT;
        } else {
            if (random.nextDouble() < REJUVENATION_CHANCE) {
                state = State.RESTING;
                restingTimer = RESTING_TIME;
            } else {
                state = State.SEARCHING;
                searchTimer = SEARCH_TIMEOUT;
            }
        }
    }


    private void forceReturnToHive() {
        state = State.RETURNING;
    }

    private boolean isCollidingWith(FoodSource food) {
        int distance = (int) Math.sqrt(Math.pow(x - food.getX(), 2) + Math.pow(y - food.getY(), 2));
        return distance < SIZE / 2 + food.getRadius();
    }

    private void avoidCollision(List<Agent> agents) {
        double repulseX = 0;
        double repulseY = 0;

        for (Agent other : agents) {
            if (this == other || other.state == State.RESTING) continue;

            double distance = Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
            if (distance < 30) {
                double dx = this.x - other.x;
                double dy = this.y - other.y;
                double magnitude = Math.sqrt(dx * dx + dy * dy);
                if (magnitude != 0) {
                    repulseX += (dx / magnitude) * (30 - distance);
                    repulseY += (dy / magnitude) * (30 - distance);
                }
            }
        }

        this.dx += repulseX * 0.1;
        this.dy += repulseY * 0.1;
        normalizeVelocity();
    }

    private void checkWallCollision(int panelWidth, int panelHeight) {
        if (x <= 0 || x >= panelWidth - SIZE) dx = -dx;
        if (y <= 0 || y >= panelHeight - SIZE) dy = -dy;
    }

    private void normalizeVelocity() {
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        if (magnitude != 0) {
            dx = (dx / magnitude) * SPEED;
            dy = (dy / magnitude) * SPEED;
        }
    }

    public void draw(Graphics g) {
        int radius = SIZE / 2;
        if (state == State.RESTING) {
            g.setColor(Color.BLACK);
            g.fillArc(x - radius, y - radius, SIZE, SIZE, 90, 180);
            g.setColor(Color.YELLOW);
            g.fillArc(x - radius, y - radius, SIZE, SIZE, 270, 180);
        } else {
            g.setColor(Color.YELLOW);
            g.fillArc(x - radius, y - radius, SIZE, SIZE, 90, 180);
            g.setColor(Color.BLACK);
            g.fillArc(x - radius, y - radius, SIZE, SIZE, 270, 180);
        }
    }
}

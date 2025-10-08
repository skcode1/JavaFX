import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PhysicsSimulation extends Application {

    @Override
    public void start(Stage primaryStage) {
        Stage menuStage = new Stage();
        menuStage.setTitle("Newton's Laws of Motion");

        Button law1Btn = new Button("1st Law: Inertia");
        Button law2Btn = new Button("2nd Law: F=ma");
        Button law3Btn = new Button("3rd Law: Action-Reaction with Force");

        VBox menuBox = new VBox(20, law1Btn, law2Btn, law3Btn);
        menuBox.setPadding(new Insets(20));
        Scene menuScene = new Scene(menuBox, 350, 200);
        menuStage.setScene(menuScene);
        menuStage.show();

        law1Btn.setOnAction(e -> new FirstLawWindow().start(new Stage()));
        law2Btn.setOnAction(e -> new SecondLawWindow().start(new Stage()));
        law3Btn.setOnAction(e -> new ThirdLawWindow().start(new Stage()));
    }

    // ------------------ First Law Window ------------------
    class FirstLawWindow extends Application {
        double ballX = 100, ballY = 200, v = 2;
        boolean running = false;

        @Override
        public void start(Stage stage) {
            stage.setTitle("1st Law: Inertia");
            Canvas canvas = new Canvas(600, 400);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            Button startBtn = new Button("Start/Resume");
            Button stopBtn = new Button("Stop Object");
            Button resetBtn = new Button("Reset");
            HBox controls = new HBox(10, startBtn, stopBtn, resetBtn);
            controls.setPadding(new Insets(10));

            VBox root = new VBox(10, canvas, controls);
            stage.setScene(new Scene(root, 600, 450));
            stage.show();

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (running) {
                        ballX += v;
                        if (ballX > 580 || ballX < 20) v = -v;
                    }
                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillRect(0, 0, 600, 400);
                    gc.setFill(Color.BLUE);
                    gc.fillOval(ballX - 20, ballY - 20, 40, 40);
                }
            };
            timer.start();

            startBtn.setOnAction(e -> running = true);
            stopBtn.setOnAction(e -> running = false);
            resetBtn.setOnAction(e -> { ballX = 100; v = 2; running = false; });
        }
    }

    // ------------------ Second Law Window ------------------
    class SecondLawWindow extends Application {
        double ballX = 100, ballY = 200, v = 0, a = 0, force = 0, mass = 2;
        boolean running = false;
        TextField massField, forceField;

        @Override
        public void start(Stage stage) {
            stage.setTitle("2nd Law: F = ma");
            Canvas canvas = new Canvas(600, 400);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            massField = new TextField("2");
            forceField = new TextField("0");
            VBox massBox = createLabeledField("Mass (kg)", massField);
            VBox forceBox = createLabeledField("Force (N)", forceField);

            Button startBtn = new Button("Start");
            Button resetBtn = new Button("Reset");
            HBox controls = new HBox(10, massBox, forceBox, startBtn, resetBtn);
            controls.setPadding(new Insets(10));

            VBox root = new VBox(10, canvas, controls);
            stage.setScene(new Scene(root, 600, 450));
            stage.show();

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (running) {
                        try { mass = Double.parseDouble(massField.getText()); } catch(Exception e){ mass=2; }
                        try { force = Double.parseDouble(forceField.getText()); } catch(Exception e){ force=0; }
                        a = force / mass;
                        v += a * 0.05;
                        ballX += v;
                        if (ballX > 580) { ballX = 580; v = 0; }
                        if (ballX < 20) { ballX = 20; v = 0; }
                    }
                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillRect(0,0,600,400);
                    gc.setFill(Color.RED);
                    gc.fillOval(ballX-20, ballY-20, 40,40);
                    gc.setFill(Color.BLACK);
                    gc.fillText("Mass: " + mass + " kg", 10, 20);
                    gc.fillText("Force: " + force + " N", 10, 40);
                    gc.fillText("Acceleration: " + String.format("%.2f", a) + " m/s²", 10, 60);
                    gc.fillText("Velocity: " + String.format("%.2f", v) + " m/s", 10, 80);
                }
            };
            timer.start();

            startBtn.setOnAction(e -> running = true);
            resetBtn.setOnAction(e -> { ballX = 100; v = 0; a = 0; force = 0; running = false; });
        }
    }

    // ------------------ Third Law Window ------------------
    class ThirdLawWindow extends Application {
        double ballX = 100, ballY = 200, v = 0;
        boolean running = false;
        TextField forceField;
        double timeElapsed = 0;
        double appliedForce = 20;

        @Override
        public void start(Stage stage) {
            stage.setTitle("3rd Law: Action-Reaction with Force");
            Canvas canvas = new Canvas(600, 400);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            forceField = new TextField("20");
            VBox forceBox = createLabeledField("Applied Force (N)", forceField);

            Button startBtn = new Button("Start");
            Button resetBtn = new Button("Reset");
            HBox controls = new HBox(10, forceBox, startBtn, resetBtn);
            controls.setPadding(new Insets(10));

            VBox root = new VBox(10, canvas, controls);
            stage.setScene(new Scene(root, 600, 450));
            stage.show();

            AnimationTimer timer = new AnimationTimer() {
                private long lastTime = 0;

                @Override
                public void handle(long now) {
                    if (lastTime == 0) lastTime = now;
                    double deltaTime = (now - lastTime) / 1_000_000_000.0; // seconds
                    lastTime = now;

                    if (running) {
                        try { appliedForce = Double.parseDouble(forceField.getText()); } catch(Exception e){ appliedForce = 20; }

                        // Initial push proportional to applied force
                        if (v == 0 && timeElapsed == 0) v = appliedForce * 0.05;

                        ballX += v;

                        // Perfect action-reaction: bounce with same magnitude
                        if (ballX + 20 >= 580) { v = -Math.abs(v); ballX = 580-20; }
                        if (ballX - 20 <= 20) { v = Math.abs(v); ballX = 20+20; }

                        // Count time while moving
                        if (Math.abs(v) > 0.01) timeElapsed += deltaTime;
                    }

                    gc.setFill(Color.LIGHTGRAY);
                    gc.fillRect(0,0,600,400);
                    gc.setFill(Color.GREEN);
                    gc.fillOval(ballX-20, ballY-20, 40,40);
                    gc.setFill(Color.BLACK);
                    gc.fillText("Applied Force: " + String.format("%.2f", appliedForce) + " N", 10, 20);
                    gc.fillText("Velocity: " + String.format("%.2f", v) + " m/s", 10, 40);
                    gc.fillText("Time moving: " + String.format("%.2f", timeElapsed / 60.0) + " min", 10, 60);
                }
            };
            timer.start();

            startBtn.setOnAction(e -> { v = 0; timeElapsed = 0; running = true; });
            resetBtn.setOnAction(e -> { ballX = 100; v = 0; appliedForce = 20; timeElapsed = 0; running = false; });
        }
    }

    private VBox createLabeledField(String labelText, TextField field) {
        Label label = new Label(labelText);
        VBox box = new VBox(5, label, field);
        box.setPadding(new Insets(5));
        field.setPrefWidth(60);
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

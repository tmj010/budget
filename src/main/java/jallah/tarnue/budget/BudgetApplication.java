package jallah.tarnue.budget;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BudgetApplication extends Application {
    private static final String MAIN_FXML = "/fxml/main.fxml";
    private static final String TITLE = "Budget";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;

    private ConfigurableApplicationContext springContext;
    private Parent root;

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(BudgetApplication.class);
        SpringFXMLLoader fxmlLoader = springContext.getBean(SpringFXMLLoader.class);
        root = fxmlLoader.load(MAIN_FXML);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(TITLE);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.stop();
    }
}

package jallah.tarnue.budget;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SpringFXMLLoader {
    private ApplicationContext context;

    @Autowired
    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Load a node that is managed by spring
     *
     * @param fxmlPath fxml path
     * @return Node managed by spring
     * @throws IOException fxml path is not valid
     */
    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean); // Set spring as the controller factor
        loader.setLocation(getClass().getResource(fxmlPath));
        return loader.load();
    }
}

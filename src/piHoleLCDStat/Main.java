package piHoleLCDStat;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage)  {
        dash d = new dash();
        Scene s = new Scene(d);
        stage.setScene(s);
        stage.show();
    }
}

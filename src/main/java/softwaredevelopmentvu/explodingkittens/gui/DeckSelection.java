package softwaredevelopmentvu.explodingkittens.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class DeckSelection extends Application {

    @Override
    public void start(Stage stage) {
        ObservableList<String> items = FXCollections.observableArrayList(
                "one","two","three","four","five","six","seven");
        ListView<String> list = new ListView<>(items);
        ListView<String> selected = new ListView<>();
        HBox root = new HBox(list, selected);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.getSelectionModel().selectedItemProperty().addListener((obs,ov,nv)->{
            selected.setItems(list.getSelectionModel().getSelectedItems());
        });
    }

    public static void main(String[] args) {launch(args);}

}

package softwaredesign.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;

public class ScrollButton {
    private Shape leftButton;
    private Label rotationText;
    private ArrayList<String> allTexts = new ArrayList<>();

    public void navigate(Shape button){
        Sounds.playClick();
        if(button.equals(leftButton)) Collections.rotate(allTexts, 1);
        else Collections.rotate(allTexts, -1);;
        rotationText.setText(allTexts.get(0));
        rotationText.setAlignment(Pos.CENTER);
    }

    public void initButtons(Shape left, Shape right, Label rotateText){
        this.leftButton = left;
        this.rotationText = rotateText;
    }

    public void resetAndAddText(String... texts){
        allTexts = new ArrayList<>();
        addText(texts);
    }

    public void addText(String... texts){
        for(String text : texts) allTexts.add(text);
        rotationText.setText(allTexts.get(0));
        rotationText.setAlignment(Pos.CENTER);
    }

    public void remove(String removal){ allTexts.remove(removal); }
}

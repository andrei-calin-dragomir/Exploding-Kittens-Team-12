package softwaredesign.gui;

import javafx.scene.control.Control;
import org.javatuples.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomAlert {
    Control[] alerts;

    public void activate(Control alertToActivate){
        for(Control alert : alerts){
            if(alert == alertToActivate) alert.setVisible(true);
            else alert.setVisible(false);
        }
    }

    public void setAlerts(Control... alertList){
        this.alerts = alertList;
        for(Control alert : alertList) alert.setVisible(false);
    }
}

package frutty;

import java.awt.event.*;
import javax.swing.*;

public final class ScrollListener implements AdjustmentListener {
    private final JScrollBar scrollBar;
    private boolean adjustScrollBar = true;

    private int previousValue = -1;
    private int previousMaximum = -1;

    public ScrollListener(JScrollBar scrollBar){
        this.scrollBar = scrollBar;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent event){
        var listModel = scrollBar.getModel();
        var value = listModel.getValue();
        var extent = listModel.getExtent();
        var maximum = listModel.getMaximum();
        
        if (previousValue != value && previousMaximum == maximum){
            adjustScrollBar = value + extent >= maximum;
        }

        if (adjustScrollBar){
            value = maximum - extent;
            scrollBar.setValue(value);
        }

        previousValue = value;
        previousMaximum = maximum;
    }
}
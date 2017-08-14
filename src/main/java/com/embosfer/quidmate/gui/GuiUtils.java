package com.embosfer.quidmate.gui;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Created by embosfer on 13/08/2017.
 */
public class GuiUtils {

    private GuiUtils() {
    }

    public static Border quidMateBorder() {
        return new Border(new BorderStroke(
                Color.WHITE,
                BorderStrokeStyle.NONE,
                CornerRadii.EMPTY,
                new BorderWidths(5)));
    }
}

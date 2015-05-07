package com.playonlinux.ui.impl.javafx.setupwindow;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import com.playonlinux.utils.messages.CancelerSynchroneousMessage;

import java.util.List;


public class StepRepresentationMenu extends StepRepresentationMessage {
    List<String> menuItems;
    ListView listViewWidget;

    public StepRepresentationMenu(SetupWindowJavaFXImplementation parent, CancelerSynchroneousMessage messageWaitingForResponse, String textToShow,
                                  List<String> menuItems) {
        super(parent, messageWaitingForResponse, textToShow);

        this.menuItems = menuItems;
    }

    @Override
    protected void drawStepContent() {
        super.drawStepContent();
        listViewWidget = new ListView();

        listViewWidget.setItems(FXCollections.observableArrayList(menuItems));
        listViewWidget.setLayoutX(10);
        listViewWidget.setLayoutY(40);
        listViewWidget.setPrefSize(500, 240);

        this.addToContentPanel(listViewWidget);
    }

    @Override
    protected void setStepEvents() {
        this.setNextButtonAction(event ->
            ((CancelerSynchroneousMessage) this.getMessageAwaitingForResponse()).
                    setResponse(listViewWidget.getFocusModel().getFocusedItem())
        );
    }

}
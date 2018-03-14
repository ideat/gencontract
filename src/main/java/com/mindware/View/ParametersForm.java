package com.mindware.View;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class ParametersForm extends CustomComponent implements View {
    public ParametersForm(){
        Label label = new Label("Parametros !!");
        setCompositionRoot(label);
    }
}

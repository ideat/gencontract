package com.mindware.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class GenerateContractsForm extends CustomComponent implements View {
    public GenerateContractsForm(){
        Label label = new Label("Generar contratos!!");
        setCompositionRoot(label);
    }
}

package com.mindware.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class VariablesContractsForm extends CustomComponent implements View {
    public VariablesContractsForm(){
        Label label = new Label("Variables de contratos!!");
        setCompositionRoot(label);
    }
}

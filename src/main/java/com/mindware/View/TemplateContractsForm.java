package com.mindware.View;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class TemplateContractsForm extends CustomComponent implements View {
    public TemplateContractsForm(){
        Label label = new Label("Plantilla de contratos!!");
        setCompositionRoot(label);
    }
}

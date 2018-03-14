package com.mindware.View;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class ListContractForm extends CustomComponent implements View {
    public ListContractForm(){
        Label label = new Label("Lista de contratos!!");
        setCompositionRoot(label);
    }
}

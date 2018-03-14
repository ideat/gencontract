package com.mindware.View;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class RegisterNewDataForm extends CustomComponent implements View{
    public RegisterNewDataForm(){
        Label label = new Label("Registrar nuevos datos!!");
        setCompositionRoot(label);
    }
}

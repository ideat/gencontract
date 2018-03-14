package com.mindware.View;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.navigator.View;
import com.vaadin.ui.Label;

public class UserForm extends CustomComponent implements View{
    public UserForm(){
        Label label = new Label("Usuario");
        setCompositionRoot(label);
    }

}

package com.mindware.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class RolForm extends CustomComponent implements View{
    public RolForm(){
        Label label = new Label("Rol de usuarios!!");
        setCompositionRoot(label);
    }
}

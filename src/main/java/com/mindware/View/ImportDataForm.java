package com.mindware.View;

import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class ImportDataForm extends CustomComponent implements View {
    public ImportDataForm(){
        Label label = new Label("Importar datos!!");
        setCompositionRoot(label);
    }
}

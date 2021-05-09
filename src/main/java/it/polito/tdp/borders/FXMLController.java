package it.polito.tdp.borders;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.borders.model.Country;
import it.polito.tdp.borders.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField txtAnno;

    @FXML
    private ComboBox<Country> cmbStati;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCalcolaConfini(ActionEvent event) {
    	cmbStati.getItems().clear();
    	txtResult.clear();
    	String sAnno = txtAnno.getText();
    	int anno;
    	boolean ok = false;
    	try {
    		anno = Integer.parseInt(sAnno);
    		if(anno >= 1816 && anno <= 2016)
    			ok = true;
    	}
    	catch(NumberFormatException nfe) {
    		txtResult.setText("Inserire un numero");
    		return;
    	}
    	if(ok) {
    		model.createGraph(anno);
    		txtResult.appendText("Trovate " + model.getCountries().size() + " nazioni\n\n");
    		txtResult.appendText(model.getGradoVertici());
    		txtResult.appendText("\nNumero di componenti connesse: " + model.getNumberOfConnectedComponents());
    		this.cmbStati.getItems().addAll(model.getCountries());
    	}
    	else {
    		txtResult.setText("Inserire un anno compreso tra 1816 e 2016");
    		return;
    	}
    }

    @FXML
    void doStatiRaggiungibili(ActionEvent event) {
    	txtResult.clear();
    	Country c = cmbStati.getValue();
    	txtResult.setText("Stati raggiungibili a partire da " + c.toString() + ": \n\n");
    	for(Country cc : model.getRaggiungibili(c)) {
    		if(!c.equals(cc))
    			txtResult.appendText(cc.toString() + "\n");
    	}
    }

    @FXML
    void initialize() {
        assert txtAnno != null : "fx:id=\"txtAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbStati != null : "fx:id=\"cmbStati\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    }
}

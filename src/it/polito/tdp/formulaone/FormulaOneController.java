package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.exception.Formula1Exception;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {

	Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ComboBox<Season> boxAnno;

	@FXML
	private TextField textInputK;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCreaGrafo(ActionEvent event) {
		Season s = this.boxAnno.getValue();

		try {
			Driver d = this.model.retrieveBestDriver(s.getYear().getValue());
			this.txtResult.setText(String.format("Il pilota migliore dell'anno %d è %s %s", s.getYear().getValue(),
					d.getForename(), d.getSurname()));

		} catch (Formula1Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.txtResult.setText("Errore nella determinazione del pilota migliore dell'anno " + s);
		}

	}

	@FXML
	void doTrovaDreamTeam(ActionEvent event) {
		String dimensioneToString = this.textInputK.getText();
		System.out.println("<doTrovaDreamTeam> dimensione: " + dimensioneToString);

		if (dimensioneToString == null || "".equals(dimensioneToString.trim())) {
			this.txtResult.setText("Inserire un valore nel campo dimensione K.");
			return;
		}

		try {
			int dimensione = Integer.parseInt(dimensioneToString);
			List<Driver> dreamTeam = model.retrieveDreamTeam(dimensione);
			this.txtResult.setText(String.format("Dream Team: %s %s e %s %s.", dreamTeam.get(0).getForename(),
					dreamTeam.get(0).getSurname(), dreamTeam.get(1).getForename(), dreamTeam.get(1).getSurname()));

		} catch (NumberFormatException nfe) {
			this.txtResult.setText("Inserire una stringa numerica per il campo dimensione K.");
		}

	}

	@FXML
	void initialize() {
		assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
		assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
		try {
			this.boxAnno.getItems().addAll(model.getAllSeasons());
		} catch (Formula1Exception e) {
			this.txtResult.setText("<Errore nel recupero della lista delle stagioni>");
		}
	}
}

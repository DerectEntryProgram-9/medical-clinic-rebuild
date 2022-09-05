package lk.ijse.dep9.clinic.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import lk.ijse.dep9.clinic.security.SecurityContextHolder;

public class AdminDashboardFormController {

    public JFXButton btnProfileManagement;
    public JFXButton btnViewRecords;
    public JFXButton btnSettings;

    public void initialize() {
        System.out.println(SecurityContextHolder.getPrinciple());
    }

    public void btnProfileManagementOnAction(ActionEvent actionEvent) {
    }

    public void btnViewRecordsOnAction(ActionEvent actionEvent) {
    }

    public void btnSettingsOnAction(ActionEvent actionEvent) {
    }
}

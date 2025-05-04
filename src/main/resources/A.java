<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.layout.AnchorPane?>
<?import javafx.scene.layout.VBox?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.RowConstraints?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.control.*?>
<?import javafx.geometry.Insets?>

<AnchorPane xmlns:fx="http://javafx.com/fxml/1"
            xmlns="http://javafx.com/javafx/23.0.1"
            fx:controller="tn.esprit.controllers.AjouterAppointment"
            prefWidth="600.0" prefHeight="400.0" stylesheets="@style.css">
    <children>
        <VBox alignment="CENTER" spacing="20" AnchorPane.topAnchor="20" AnchorPane.bottomAnchor="20" AnchorPane.leftAnchor="20" AnchorPane.rightAnchor="20">
            <!-- Header -->
            <Label text="LegalLink Appointment" styleClass="header-label"/>

            <!-- Form Fields Grid -->
            <GridPane hgap="10" vgap="10">
                <columnConstraints>
                    <ColumnConstraints percentWidth="35"/>
                    <ColumnConstraints percentWidth="65"/>
                </columnConstraints>
                <rowConstraints>
                    <RowConstraints minHeight="30"/>
                    <RowConstraints minHeight="30"/>
                    <RowConstraints minHeight="30"/>
                    <RowConstraints minHeight="60"/>
                    <RowConstraints minHeight="30"/>
                    <RowConstraints minHeight="30"/>
                </rowConstraints>
                
                <!-- Date Field -->
                <Label text="Date:" GridPane.rowIndex="0" GridPane.columnIndex="0" />
                <DatePicker fx:id="datePicker" GridPane.rowIndex="0" GridPane.columnIndex="1" />

                <!-- Time Field -->
                <Label text="Time:" GridPane.rowIndex="1" GridPane.columnIndex="0" />
                <TextField fx:id="timeField" promptText="HH:mm" GridPane.rowIndex="1" GridPane.columnIndex="1" />

                <!-- Status Field -->
                <Label text="Status:" GridPane.rowIndex="2" GridPane.columnIndex="0" />
                <ComboBox fx:id="statusCombo" promptText="Select status" GridPane.rowIndex="2" GridPane.columnIndex="1" />

                <!-- Description Field -->
                <Label text="Description:" GridPane.rowIndex="3" GridPane.columnIndex="0" />
                <TextArea fx:id="descriptionArea" promptText="Enter description" prefHeight="60" GridPane.rowIndex="3" GridPane.columnIndex="1" />

                <!-- Client Field -->
                <Label text="Client:" GridPane.rowIndex="4" GridPane.columnIndex="0" />
                <ComboBox fx:id="clientCombo" promptText="Client ID" GridPane.rowIndex="4" GridPane.columnIndex="1" />

                <!-- Lawyer Field -->
                <Label text="Lawyer:" GridPane.rowIndex="5" GridPane.columnIndex="0" />
                <ComboBox fx:id="lawyerCombo" promptText="Lawyer ID" GridPane.rowIndex="5" GridPane.columnIndex="1" />
            </GridPane>

            <!-- Action Buttons -->
            <HBox spacing="20" alignment="CENTER">
                <Button text="Display" onAction="#display" styleClass="action-button" />
                <Button text="Save Appointment" onAction="#saveButton" styleClass="action-button" />
            </HBox>
        </VBox>
    </children>
</AnchorPane>

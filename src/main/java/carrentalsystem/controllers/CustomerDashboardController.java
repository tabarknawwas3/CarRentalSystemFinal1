package carrentalsystem.controllers;

import carrentalsystem.dao.CarDAO;
import carrentalsystem.dao.DrivingLicenseDAO;
import carrentalsystem.dao.InvoiceDAO;
import carrentalsystem.dao.RentalContractDAO;
import carrentalsystem.dao.ReservationDAO;
import carrentalsystem.dao.ReservationExtraDAO;
import carrentalsystem.dao.ReviewDAO;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import carrentalsystem.models.Car;
import carrentalsystem.models.Customer;
import carrentalsystem.models.DrivingLicense;
import carrentalsystem.models.Extra;
import carrentalsystem.models.Invoice;
import carrentalsystem.models.RentalContract;
import carrentalsystem.models.Reservation;
import carrentalsystem.models.Review;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import carrentalsystem.dao.CustomerDAO;

public class CustomerDashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label lblSectionTitle;
    @FXML private Label lblSectionContent;
    @FXML private VBox contentArea;
    @FXML private StackPane heroPane;
    @FXML private ImageView heroImage;
    @FXML private Button btnProfile;
    @FXML private Button btnAvailableCars;
    @FXML private Button btnMakeReservation;
    @FXML private Button btnMyLicense;
    @FXML private Button btnMyReservations;
    @FXML private Button btnMyContracts;
    @FXML private Button btnMyInvoices;
    @FXML private Button btnReviews;

    private Customer currentCustomer;
    private DrivingLicense currentLicense;
    private Car selectedCar;

    private final DrivingLicenseDAO drivingLicenseDAO = new DrivingLicenseDAO();
    private final CarDAO carDAO = new CarDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final ReservationExtraDAO reservationExtraDAO = new ReservationExtraDAO();
    private final RentalContractDAO rentalContractDAO = new RentalContractDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    private TextField txtLicenseNumber;
    private TextField txtCountryOfIssue;
    private TextField txtProfileFirstName;
private TextField txtProfileLastName;
private TextField txtProfilePhone;
private TextField txtProfileEmail;
private TextField txtProfileAddress;
    private DatePicker dpIssueDate;
    private DatePicker dpExpiryDate;

    private DatePicker dpReservationStartDate;
    private DatePicker dpReservationEndDate;

    private RentalContract selectedReviewContract;
    private ComboBox<Integer> cmbReviewRating;
    private TextArea txtReviewComment;

    private final Map<Extra, CheckBox> extraCheckBoxes = new HashMap<>();
    private final Map<Extra, Spinner<Integer>> extraQuantitySpinners = new HashMap<>();

    public void initData(Customer customer) {
        this.currentCustomer = customer;

        if (customer != null) {
            lblWelcome.setText("Welcome, " + customer.getFullName());
        }
    }

    public void setCustomer(Customer customer) {
        initData(customer);
    }

    @FXML
private void handleProfile() {
    setActiveButton(btnProfile);
    clearContentArea();

    lblSectionTitle.setText("My Profile");

    if (currentCustomer == null) {
        lblSectionContent.setText("No customer data is loaded.");
        return;
    }

    lblSectionContent.setText(getCustomerSummary());
}

    @FXML
    private void handleAvailableCars() {
        setActiveButton(btnAvailableCars);
        clearContentArea();

        lblSectionTitle.setText("Available Cars");
        lblSectionContent.setText("Browse available cars and choose the best option for your trip.");

        List<Car> cars = carDAO.getAllCars();

        if (cars == null || cars.isEmpty()) {
            lblSectionContent.setText("No cars are available right now.");
            return;
        }

        FlowPane carsPane = new FlowPane();
        carsPane.setHgap(18);
        carsPane.setVgap(18);
        carsPane.setStyle("-fx-padding: 10 0 0 0;");

        for (Car car : cars) {
            carsPane.getChildren().add(createCarCard(car));
        }

        ScrollPane scrollPane = new ScrollPane(carsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle(
                "-fx-background: #0d1224;"
                + "-fx-background-color: #0d1224;"
                + "-fx-border-color: transparent;"
        );

        contentArea.getChildren().add(scrollPane);
    }

    @FXML
    private void handleMakeReservation() {
        setActiveButton(btnMakeReservation);
        clearContentArea();

        lblSectionTitle.setText("Make Reservation");
        lblSectionContent.setText(
                "Please choose a car from Available Cars first, then click Reserve Now."
        );
    }

    private void openReservationForm(Car car) {
        setActiveButton(btnMakeReservation);
        clearContentArea();

        selectedCar = car;

        lblSectionTitle.setText("Make Reservation");
        lblSectionContent.setText(
                "Selected Car: " + safe(car.getBrand()) + " " + safe(car.getModel())
                + "\nDaily Price: $" + formatMoney(car.getDailyPrice())
                + "\nBranch ID: " + car.getBranchId()
                + "\nCategory ID: " + car.getCategoryId()
        );

        Label formTitle = new Label("Reservation Details");
        formTitle.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        Button btnBack = new Button("Back to Cars");
        btnBack.setStyle(normalButtonStyle());
        btnBack.setPrefWidth(140);
        btnBack.setOnAction(e -> handleAvailableCars());

        Button btnSubmit = new Button("Submit Reservation");
        btnSubmit.setStyle(primaryButtonStyle());
        btnSubmit.setPrefWidth(180);
        btnSubmit.setOnAction(e -> submitReservation());

        HBox topButtons = new HBox(12, btnBack, btnSubmit);
        topButtons.setStyle("-fx-alignment: center-right;");

        BorderPane formHeader = new BorderPane();
        formHeader.setLeft(formTitle);
        formHeader.setRight(topButtons);
        formHeader.setMaxWidth(Double.MAX_VALUE);

        dpReservationStartDate = new DatePicker();
        dpReservationStartDate.setPromptText("Start date");
        dpReservationStartDate.setMaxWidth(Double.MAX_VALUE);
        dpReservationStartDate.setStyle(datePickerStyle());

        dpReservationEndDate = new DatePicker();
        dpReservationEndDate.setPromptText("End date");
        dpReservationEndDate.setMaxWidth(Double.MAX_VALUE);
        dpReservationEndDate.setStyle(datePickerStyle());

        VBox startBox = createFieldBox("Start Date *", dpReservationStartDate);
        VBox endBox = createFieldBox("End Date *", dpReservationEndDate);

        VBox extrasBox = buildExtrasSelectionBox();

        VBox form = new VBox(12);
        form.getChildren().addAll(
                formHeader,
                startBox,
                endBox,
                extrasBox
        );

        form.setPadding(new Insets(16));
        form.setMaxWidth(Double.MAX_VALUE);

        form.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1;"
        );

        contentArea.getChildren().add(form);
    }

    private void submitReservation() {
    if (currentCustomer == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "No customer is logged in.");
        return;
    }

    if (selectedCar == null) {
        showAlert(Alert.AlertType.WARNING, "No Car Selected",
                "Please select a car before making a reservation.");
        return;
    }

    if (dpReservationStartDate == null || dpReservationEndDate == null) {
        showAlert(Alert.AlertType.ERROR, "Error",
                "Reservation date fields are not loaded correctly.");
        return;
    }

    LocalDate startDate = dpReservationStartDate.getValue();
    LocalDate endDate = dpReservationEndDate.getValue();

    if (startDate == null || endDate == null) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                "Please select both start date and end date.");
        return;
    }

    if (startDate.isBefore(LocalDate.now())) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                "Start date cannot be before today.");
        return;
    }

    if (!endDate.isAfter(startDate)) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                "End date must be after start date.");
        return;
    }

    DrivingLicense license = drivingLicenseDAO.getLicenseByCustomerId(currentCustomer.getCustomerId());

    if (license == null) {
        showAlert(Alert.AlertType.WARNING, "Driving License Required",
                "You must add your driving license before making a reservation.");
        handleMyLicense();
        return;
    }

    if (license.getExpiryDate() != null && license.getExpiryDate().isBefore(LocalDate.now())) {
        showAlert(Alert.AlertType.WARNING, "Expired License",
                "Your driving license is expired. Please update it before making a reservation.");
        handleMyLicense();
        return;
    }

    Reservation reservation = new Reservation();

    reservation.setStartDate(startDate);
    reservation.setEndDate(endDate);
    reservation.setReservationStatus("Pending");

    reservation.setCustomerId(currentCustomer.getCustomerId());

    // السيارة اللي اختارها الزبون
    reservation.setCarId(selectedCar.getCarId());

    reservation.setCategoryId(selectedCar.getCategoryId());
    reservation.setBranchId(selectedCar.getBranchId());

    int reservationId = reservationDAO.addReservationAndReturnId(reservation);

    if (reservationId <= 0) {
        showAlert(Alert.AlertType.ERROR, "Reservation Failed",
                "Could not create the reservation. Please try again.");
        return;
    }

    Map<Extra, Integer> selectedExtras = getSelectedExtras();

    if (selectedExtras != null && !selectedExtras.isEmpty()) {
        boolean extrasSaved = reservationExtraDAO.addReservationExtras(reservationId, selectedExtras);

        if (!extrasSaved) {
            showAlert(Alert.AlertType.WARNING, "Reservation Created",
                    "Reservation was created, but some extras could not be saved.");
            handleMyReservations();
            return;
        }
    }

    showAlert(Alert.AlertType.INFORMATION, "Success",
            "Reservation created successfully. Your reservation is now pending admin confirmation.");

    selectedCar = null;
    handleMyReservations();
}

    @FXML
    private void handleMyLicense() {
        setActiveButton(btnMyLicense);

        if (currentCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No customer is currently logged in.");
            return;
        }

        currentLicense = drivingLicenseDAO.getLicenseByCustomerId(currentCustomer.getCustomerId());

        clearContentArea();
        lblSectionTitle.setText("My Driving License");

        if (currentLicense == null) {
            lblSectionContent.setText(
                    "No driving license was found for your account.\n"
                    + "Please add your license information before making a reservation."
            );

            buildLicenseForm(null);
        } else {
            lblSectionContent.setText(buildLicenseDetails(currentLicense));
            buildLicenseForm(currentLicense);
        }
    }

    @FXML
    private void handleMyReservations() {
        setActiveButton(btnMyReservations);
        clearContentArea();

        lblSectionTitle.setText("My Reservations");

        if (currentCustomer == null) {
            lblSectionContent.setText("No customer is currently logged in.");
            return;
        }

        List<Reservation> reservations =
                reservationDAO.getReservationsByCustomerId(currentCustomer.getCustomerId());

        if (reservations == null || reservations.isEmpty()) {
            lblSectionContent.setText("You do not have any reservations yet.");
            return;
        }

        lblSectionContent.setText("Here are your reservation requests and statuses.");

        FlowPane reservationsPane = new FlowPane();
        reservationsPane.setHgap(18);
        reservationsPane.setVgap(18);
        reservationsPane.setStyle("-fx-padding: 10 0 20 0;");

        for (Reservation reservation : reservations) {
            reservationsPane.getChildren().add(createReservationCard(reservation));
        }

        ScrollPane reservationScrollPane = new ScrollPane(reservationsPane);
        reservationScrollPane.setFitToWidth(true);
        reservationScrollPane.setPrefHeight(430);
        reservationScrollPane.setMinHeight(430);
        reservationScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        reservationScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        reservationScrollPane.setStyle(
                "-fx-background: #0d1224;"
                + "-fx-background-color: #0d1224;"
                + "-fx-border-color: transparent;"
        );

        contentArea.getChildren().add(reservationScrollPane);
    }

    private VBox createReservationCard(Reservation reservation) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setMinHeight(260);

        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 16;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 16;"
        );

        Label title = new Label("Reservation #" + reservation.getReservationId());
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        Label status = new Label(safe(reservation.getReservationStatus()));
        status.setStyle(getReservationStatusStyle(reservation.getReservationStatus()));

        Label details = new Label(
                "Reservation Date: " + safeDate(reservation.getReservationDate())
                + "\nStart Date: " + safeDate(reservation.getStartDate())
                + "\nEnd Date: " + safeDate(reservation.getEndDate())
                + "\nCategory ID: " + reservation.getCategoryId()
                + "\nBranch ID: " + reservation.getBranchId()
        );

        details.setWrapText(true);
        details.setStyle(
                "-fx-text-fill: #ccd6f6;"
                + "-fx-font-size: 12px;"
                + "-fx-line-spacing: 4;"
        );

        card.getChildren().addAll(title, status, details);

        if ("Pending".equalsIgnoreCase(reservation.getReservationStatus())) {
            Button btnCancel = new Button("Cancel Reservation");
            btnCancel.setMaxWidth(Double.MAX_VALUE);
            btnCancel.setStyle(
                    "-fx-background-color: #3a1616;"
                    + "-fx-text-fill: #ffb4b4;"
                    + "-fx-font-weight: bold;"
                    + "-fx-background-radius: 10;"
                    + "-fx-padding: 10 18;"
                    + "-fx-cursor: hand;"
            );

            btnCancel.setOnAction(e -> cancelReservation(reservation));
            card.getChildren().add(btnCancel);
        }

        return card;
    }

    @FXML
    private void handleMyContracts() {
        setActiveButton(btnMyContracts);
        clearContentArea();

        lblSectionTitle.setText("My Contracts");

        if (currentCustomer == null) {
            lblSectionContent.setText("No customer is currently logged in.");
            return;
        }

        List<RentalContract> contracts =
                rentalContractDAO.getContractsByCustomerId(currentCustomer.getCustomerId());

        if (contracts == null || contracts.isEmpty()) {
            lblSectionContent.setText("You do not have any rental contracts yet.");
            return;
        }

        lblSectionContent.setText("View and track your rental contracts.");

        HBox sectionHeader = new HBox(12);
        sectionHeader.setStyle("-fx-alignment: center-left; -fx-padding: 4 0 10 0;");

        Label icon = new Label("📄");
        icon.setStyle(
                "-fx-background-color: #1b2350;"
                + "-fx-text-fill: #93c5fd;"
                + "-fx-font-size: 18px;"
                + "-fx-background-radius: 18;"
                + "-fx-padding: 8 10;"
        );

        VBox titleBox = new VBox(3);

        Label title = new Label("My Contracts");
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 22px;"
                + "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("View and manage your rental contracts.");
        subtitle.setStyle(
                "-fx-text-fill: #8892b0;"
                + "-fx-font-size: 13px;"
        );

        titleBox.getChildren().addAll(title, subtitle);
        sectionHeader.getChildren().addAll(icon, titleBox);

        FlowPane contractsPane = new FlowPane();
        contractsPane.setHgap(18);
        contractsPane.setVgap(18);
        contractsPane.setMaxWidth(Double.MAX_VALUE);
        contractsPane.setStyle("-fx-padding: 8 0 20 0;");

        for (RentalContract contract : contracts) {
            contractsPane.getChildren().add(createContractCard(contract));
        }

        VBox wrapper = new VBox(12);
        wrapper.setMaxWidth(Double.MAX_VALUE);
        wrapper.getChildren().addAll(sectionHeader, contractsPane);

        ScrollPane contractScrollPane = new ScrollPane(wrapper);
        contractScrollPane.setFitToWidth(true);
        contractScrollPane.setPrefHeight(430);
        contractScrollPane.setMinHeight(430);
        contractScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contractScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        contractScrollPane.setStyle(
                "-fx-background: #0d1224;"
                + "-fx-background-color: #0d1224;"
                + "-fx-border-color: transparent;"
        );

        contentArea.getChildren().add(contractScrollPane);
    }

    @FXML
    private void handleMyInvoices() {
        setActiveButton(btnMyInvoices);
        clearContentArea();

        lblSectionTitle.setText("My Invoices");

        if (currentCustomer == null) {
            lblSectionContent.setText("No customer is currently logged in.");
            return;
        }

        List<Invoice> invoices =
                invoiceDAO.getInvoicesByCustomerId(currentCustomer.getCustomerId());

        if (invoices == null || invoices.isEmpty()) {
            lblSectionContent.setText("You do not have any invoices yet.");
            return;
        }

        lblSectionContent.setText("Here are your invoices and payment statuses.");

        FlowPane invoicesPane = new FlowPane();
        invoicesPane.setHgap(18);
        invoicesPane.setVgap(18);
        invoicesPane.setStyle("-fx-padding: 10 0 20 0;");

        for (Invoice invoice : invoices) {
            invoicesPane.getChildren().add(createInvoiceCard(invoice));
        }

        ScrollPane invoiceScrollPane = new ScrollPane(invoicesPane);
        invoiceScrollPane.setFitToWidth(true);
        invoiceScrollPane.setPrefHeight(430);
        invoiceScrollPane.setMinHeight(430);
        invoiceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        invoiceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        invoiceScrollPane.setStyle(
                "-fx-background: #0d1224;"
                + "-fx-background-color: #0d1224;"
                + "-fx-border-color: transparent;"
        );

        contentArea.getChildren().add(invoiceScrollPane);
    }

    @FXML
    private void handleReviews() {
        setActiveButton(btnReviews);
        clearContentArea();

        lblSectionTitle.setText("Reviews");

        if (currentCustomer == null) {
            lblSectionContent.setText("No customer is currently logged in.");
            return;
        }

        List<RentalContract> contracts =
                rentalContractDAO.getContractsByCustomerId(currentCustomer.getCustomerId());

        List<Review> reviews =
                reviewDAO.getReviewsByCustomerId(currentCustomer.getCustomerId());

        FlowPane reviewsPane = new FlowPane();
        reviewsPane.setHgap(18);
        reviewsPane.setVgap(18);
        reviewsPane.setStyle("-fx-padding: 10 0 20 0;");

        boolean hasCompletedContract = false;

        if (contracts != null) {
            for (RentalContract contract : contracts) {
                if ("Completed".equalsIgnoreCase(contract.getContractStatus())) {
                    hasCompletedContract = true;
                    reviewsPane.getChildren().add(createCompletedContractReviewCard(contract));
                }
            }
        }

        if (reviews != null && !reviews.isEmpty()) {
            for (Review review : reviews) {
                reviewsPane.getChildren().add(createReviewCard(review));
            }
        }

        if (!hasCompletedContract && (reviews == null || reviews.isEmpty())) {
            lblSectionContent.setText(
                    "You can add a review after you have a completed rental contract."
            );
            return;
        }

        lblSectionContent.setText(
                "Add a review for a completed contract, or view your previous reviews."
        );

        ScrollPane reviewScrollPane = new ScrollPane(reviewsPane);
        reviewScrollPane.setFitToWidth(true);
        reviewScrollPane.setPrefHeight(430);
        reviewScrollPane.setMinHeight(430);
        reviewScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        reviewScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        reviewScrollPane.setStyle(
                "-fx-background: #0d1224;"
                + "-fx-background-color: #0d1224;"
                + "-fx-border-color: transparent;"
        );

        contentArea.getChildren().add(reviewScrollPane);
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/carrentalsystem/Login.fxml")
            );

            Stage stage = (Stage) lblWelcome.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setTitle("DriveEase — Car Rental System");
            ScreenUtil.makeFullScreen(stage, scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not return to login screen.");
        }
    }

    private VBox createCarCard(Car car) {
        VBox card = new VBox(10);
        card.setPrefWidth(260);
        card.setMinHeight(400);
        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 16;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 14;"
        );

        ImageView carImage = new ImageView(loadCarImage(car));
        carImage.setFitWidth(232);
        carImage.setFitHeight(135);
        carImage.setPreserveRatio(false);
        carImage.setSmooth(true);

        Label title = new Label(safe(car.getBrand()) + " " + safe(car.getModel()));
        title.setWrapText(true);
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        Label price = new Label("$" + formatMoney(car.getDailyPrice()) + " / day");
        price.setStyle(
                "-fx-text-fill: #63e6be;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
        );

        Label details = new Label(
                "Plate: " + safe(car.getPlateNumber())
                + "\nColor: " + safe(car.getColor())
                + "\nYear: " + car.getManufactureYear()
                + "\nFuel: " + safe(car.getFuelType())
                + "\nTransmission: " + safe(car.getTransmissionType())
                + "\nMileage: " + car.getMileage()
        );
        details.setWrapText(true);
        details.setStyle(
                "-fx-text-fill: #ccd6f6;"
                + "-fx-font-size: 12px;"
                + "-fx-line-spacing: 3;"
        );

        Button btnReserve = new Button("Reserve Now");
        btnReserve.setMaxWidth(Double.MAX_VALUE);
        btnReserve.setStyle(
                "-fx-background-color: #3d5af1;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 10 18;"
                + "-fx-cursor: hand;"
        );

        btnReserve.setOnAction(e -> openReservationForm(car));

        card.getChildren().addAll(carImage, title, price, details, btnReserve);
        return card;
    }

    private Image loadCarImage(Car car) {
        String imagePath = getCarImagePath(car);

        try {
            return new Image(getClass().getResource(imagePath).toExternalForm());
        } catch (Exception e) {
            return new Image(
                    getClass().getResource("/carrentalsystem/images/cars/default-car.png").toExternalForm()
            );
        }
    }

    private String getCarImagePath(Car car) {
        String brand = safe(car.getBrand()).toLowerCase();
        String model = safe(car.getModel()).toLowerCase();

        if (brand.equals("toyota") && model.equals("yaris")) {
            return "/carrentalsystem/images/cars/toyota-yaris.png";
        }

        if (brand.equals("hyundai") && model.equals("elantra")) {
            return "/carrentalsystem/images/cars/hyundai-elantra.png";
        }

        if (brand.equals("kia") && model.equals("sportage")) {
            return "/carrentalsystem/images/cars/kia-sportage.png";
        }

        if (brand.equals("bmw") && model.equals("5 series")) {
            return "/carrentalsystem/images/cars/bmw-5series.png";
        }

        return "/carrentalsystem/images/cars/default-car.png";
    }

    private void buildLicenseForm(DrivingLicense license) {
        Label formTitle = new Label(license == null ? "Add Driving License" : "Update Driving License");
        formTitle.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        txtLicenseNumber = new TextField();
        txtLicenseNumber.setPromptText("License Number");
        txtLicenseNumber.setStyle(inputStyle());

        txtCountryOfIssue = new TextField();
        txtCountryOfIssue.setPromptText("Country of Issue");
        txtCountryOfIssue.setStyle(inputStyle());

        dpIssueDate = new DatePicker();
        dpIssueDate.setPromptText("Issue Date");
        dpIssueDate.setMaxWidth(Double.MAX_VALUE);
        dpIssueDate.setStyle(datePickerStyle());

        dpExpiryDate = new DatePicker();
        dpExpiryDate.setPromptText("Expiry Date");
        dpExpiryDate.setMaxWidth(Double.MAX_VALUE);
        dpExpiryDate.setStyle(datePickerStyle());

        if (license != null) {
            txtLicenseNumber.setText(emptyIfNull(license.getLicenseNumber()));
            txtCountryOfIssue.setText(emptyIfNull(license.getCountryOfIssue()));
            dpIssueDate.setValue(license.getIssueDate());
            dpExpiryDate.setValue(license.getExpiryDate());
        }

        VBox box1 = createFieldBox("License Number *", txtLicenseNumber);
        VBox box2 = createFieldBox("Country of Issue *", txtCountryOfIssue);
        VBox box3 = createFieldBox("Issue Date *", dpIssueDate);
        VBox box4 = createFieldBox("Expiry Date *", dpExpiryDate);

        HBox row1 = new HBox(16, box1, box2);
        HBox row2 = new HBox(16, box3, box4);

        HBox.setHgrow(box1, Priority.ALWAYS);
        HBox.setHgrow(box2, Priority.ALWAYS);
        HBox.setHgrow(box3, Priority.ALWAYS);
        HBox.setHgrow(box4, Priority.ALWAYS);

        Button btnSave = new Button(license == null ? "💾 Add License" : "💾 Update License");
        btnSave.setStyle(primaryButtonStyle());
        btnSave.setOnAction(e -> saveLicense());

        HBox buttons = new HBox(12);
        buttons.setStyle("-fx-alignment: center-right;");
        buttons.getChildren().add(btnSave);

        VBox form = new VBox(14, formTitle, row1, row2, buttons);
        form.setPadding(new Insets(18));
        form.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1;"
        );

        contentArea.getChildren().add(form);
    }

    private void saveLicense() {
        if (currentCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No customer is currently logged in.");
            return;
        }

        String licenseNumber = txtLicenseNumber.getText().trim();
        String country = txtCountryOfIssue.getText().trim();
        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate expiryDate = dpExpiryDate.getValue();

        if (licenseNumber.isEmpty() || country.isEmpty() || issueDate == null || expiryDate == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill all required license fields.");
            return;
        }

        if (expiryDate.isBefore(issueDate) || expiryDate.isEqual(issueDate)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Expiry date must be after issue date.");
            return;
        }

        DrivingLicense license = currentLicense == null ? new DrivingLicense() : currentLicense;

        license.setLicenseNumber(licenseNumber);
        license.setCountryOfIssue(country);
        license.setIssueDate(issueDate);
        license.setExpiryDate(expiryDate);
        license.setCustomerId(currentCustomer.getCustomerId());

        boolean success;

        if (currentLicense == null) {
            success = drivingLicenseDAO.addLicense(license);
        } else {
            success = drivingLicenseDAO.updateLicense(license);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Driving license saved successfully.");
            handleMyLicense();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save driving license. Make sure the license number is unique.");
        }
    }

    private String buildLicenseDetails(DrivingLicense license) {
        String validity;

        if (license.getExpiryDate() == null) {
            validity = "Unknown";
        } else if (license.getExpiryDate().isBefore(LocalDate.now())) {
            validity = "Expired";
        } else {
            validity = "Valid";
        }

        return "License Number: " + safe(license.getLicenseNumber())
                + "\nIssue Date: " + safeDate(license.getIssueDate())
                + "\nExpiry Date: " + safeDate(license.getExpiryDate())
                + "\nCountry of Issue: " + safe(license.getCountryOfIssue())
                + "\nValidity: " + validity;
    }

    private VBox createFieldBox(String labelText, Region field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #ccd6f6; -fx-font-size: 12px;");

        VBox box = new VBox(6, label, field);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private void clearContentArea() {
        if (contentArea != null && contentArea.getChildren().size() > 2) {
            contentArea.getChildren().remove(2, contentArea.getChildren().size());
        }
    }

    private String getCustomerSummary() {
        if (currentCustomer == null) {
            return "No customer data is loaded.";
        }

        return "Name: " + currentCustomer.getFullName()
                + "\nUsername: " + safe(currentCustomer.getUsername())
                + "\nPhone: " + safe(currentCustomer.getPhoneNumber())
                + "\nEmail: " + safe(currentCustomer.getEmail())
                + "\nAddress: " + safe(currentCustomer.getAddress());
    }

    private void setActiveButton(Button selectedButton) {
        resetActionButtons();

        if (selectedButton != null) {
            selectedButton.setStyle(activeButtonStyle());
        }
    }

    private void resetActionButtons() {
        Button[] buttons = {
                btnProfile,
                btnAvailableCars,
                btnMakeReservation,
                btnMyLicense,
                btnMyReservations,
                btnMyContracts,
                btnMyInvoices,
                btnReviews
        };

        for (Button button : buttons) {
            if (button != null) {
                button.setStyle(normalButtonStyle());
            }
        }
    }

    private String normalButtonStyle() {
        return "-fx-background-color: #1b2350;"
                + "-fx-text-fill: #bfdbfe;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 12;"
                + "-fx-cursor: hand;";
    }

    private String activeButtonStyle() {
        return "-fx-background-color: #3d5af1;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 12;"
                + "-fx-cursor: hand;";
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private String safeDate(LocalDate value) {
        return value == null ? "-" : value.toString();
    }

    private String inputStyle() {
        return "-fx-background-color: #060818;"
                + "-fx-text-fill: #e6f1ff;"
                + "-fx-prompt-text-fill: #46506f;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 10 14;";
    }

    private String datePickerStyle() {
        return "-fx-background-color: #060818;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #3d5af1;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 10 24;"
                + "-fx-cursor: hand;";
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cancelReservation(Reservation reservation) {
        if (reservation == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No reservation selected.");
            return;
        }

        if (!"Pending".equalsIgnoreCase(reservation.getReservationStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel",
                    "Only pending reservations can be cancelled.");
            return;
        }

        reservation.setReservationStatus("Cancelled");

        boolean success = reservationDAO.updateReservation(reservation);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Reservation cancelled successfully.");
            handleMyReservations();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not cancel reservation.");
        }
    }

    private VBox createContractCard(RentalContract contract) {
    VBox card = new VBox(12);
    card.setPrefWidth(300);
    card.setMaxWidth(300);
    card.setMinWidth(300);

    card.setPadding(new Insets(18));
    card.setStyle(
            "-fx-background-color: #090e20;"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: #1f2a55;"
            + "-fx-border-radius: 18;"
            + "-fx-border-width: 1;"
    );

    HBox topRow = new HBox(10);
    topRow.setAlignment(Pos.CENTER_LEFT);

    Label title = new Label("Contract #" + contract.getContractId());
    title.setStyle(
            "-fx-text-fill: white;"
            + "-fx-font-size: 18px;"
            + "-fx-font-weight: bold;"
    );

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label statusBadge = new Label(contract.getContractStatus());
    statusBadge.setStyle(getContractStatusStyle(contract.getContractStatus()));

    topRow.getChildren().addAll(title, spacer, statusBadge);

    Label carInfo = new Label(
            contract.getCarInfo() == null || contract.getCarInfo().isBlank()
                    ? "Car: Not specified"
                    : "Car: " + contract.getCarInfo()
    );
    carInfo.setWrapText(true);
    carInfo.setStyle(
            "-fx-text-fill: #93c5fd;"
            + "-fx-font-size: 13px;"
            + "-fx-font-weight: bold;"
    );

    VBox infoBox = new VBox(8);
    infoBox.getChildren().addAll(
            createContractInfoRow("Start Date", String.valueOf(contract.getStartDate())),
            createContractInfoRow("Expected Return", String.valueOf(contract.getExpectedReturnDate())),
            createContractInfoRow("Actual Return",
                    contract.getActualReturnDate() == null
                            ? "Not returned yet"
                            : String.valueOf(contract.getActualReturnDate())),
            createContractInfoRow("Mileage Pickup",
                    contract.getMileageAtPickup() <= 0
                            ? "Not recorded"
                            : String.valueOf(contract.getMileageAtPickup())),
            createContractInfoRow("Mileage Return",
                    contract.getMileageAtReturn() <= 0
                            ? "Not recorded"
                            : String.valueOf(contract.getMileageAtReturn())),
            createContractInfoRow("Reservation ID",
                    contract.getReservationId() <= 0
                            ? "No reservation"
                            : String.valueOf(contract.getReservationId()))
    );

    Separator separator = new Separator();
    separator.setStyle("-fx-background-color: #1f2a55;");

    Label selectedExtrasTitle = new Label("Selected Extras");
    selectedExtrasTitle.setStyle(
            "-fx-text-fill: white;"
            + "-fx-font-size: 14px;"
            + "-fx-font-weight: bold;"
    );

    String selectedExtrasText = "No extras selected.";

    if (contract.getReservationId() > 0) {
        selectedExtrasText = reservationExtraDAO.getExtrasTextByReservationId(contract.getReservationId());
    }

    Label selectedExtrasLabel = new Label(selectedExtrasText);
    selectedExtrasLabel.setWrapText(true);
    selectedExtrasLabel.setStyle(
            "-fx-text-fill: #cbd5e1;"
            + "-fx-font-size: 12px;"
            + "-fx-line-spacing: 3;"
    );

    card.getChildren().addAll(
            topRow,
            carInfo,
            infoBox,
            separator,
            selectedExtrasTitle,
            selectedExtrasLabel
    );

    return card;
}

    private HBox createContractInfoRow(String iconText, String labelText, String valueText) {
        Label icon = new Label(iconText);
        icon.setStyle(
                "-fx-text-fill: #93c5fd;"
                + "-fx-font-size: 14px;"
        );

        Label label = new Label(labelText);
        label.setStyle(
                "-fx-text-fill: #8892b0;"
                + "-fx-font-size: 12px;"
        );

        Label value = new Label(valueText);
        value.setWrapText(true);
        value.setMaxWidth(185);
        value.setStyle(
                "-fx-text-fill: #e6f1ff;"
                + "-fx-font-size: 13px;"
                + "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(8);
        row.setStyle(
                "-fx-alignment: center-left;"
                + "-fx-padding: 2 0 6 0;"
                + "-fx-border-color: transparent transparent #111936 transparent;"
                + "-fx-border-width: 0 0 1 0;"
        );

        row.getChildren().addAll(icon, label, spacer, value);
        return row;
    }

    private String getContractStatusStyle(String status) {
        String base =
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 12;"
                + "-fx-padding: 6 13;";

        if ("Active".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(99, 230, 190, 0.16);"
                    + "-fx-border-color: rgba(99, 230, 190, 0.35);"
                    + "-fx-border-radius: 12;"
                    + "-fx-text-fill: #63e6be;";
        }

        if ("Completed".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(96, 165, 250, 0.16);"
                    + "-fx-border-color: rgba(96, 165, 250, 0.35);"
                    + "-fx-border-radius: 12;"
                    + "-fx-text-fill: #60a5fa;";
        }

        if ("Cancelled".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(255, 107, 107, 0.16);"
                    + "-fx-border-color: rgba(255, 107, 107, 0.35);"
                    + "-fx-border-radius: 12;"
                    + "-fx-text-fill: #ff6b6b;";
        }

        return base
                + "-fx-background-color: rgba(253, 216, 53, 0.16);"
                + "-fx-border-color: rgba(253, 216, 53, 0.35);"
                + "-fx-border-radius: 12;"
                + "-fx-text-fill: #fdd835;";
    }

    private String getReservationStatusStyle(String status) {
        String base =
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 5 12;";

        if ("Pending".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(253, 216, 53, 0.18);"
                    + "-fx-text-fill: #fdd835;";
        }

        if ("Confirmed".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(99, 230, 190, 0.18);"
                    + "-fx-text-fill: #63e6be;";
        }

        if ("Cancelled".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(255, 107, 107, 0.18);"
                    + "-fx-text-fill: #ff6b6b;";
        }

        if ("Completed".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(147, 197, 253, 0.18);"
                    + "-fx-text-fill: #93c5fd;";
        }

        return base
                + "-fx-background-color: rgba(147, 197, 253, 0.18);"
                + "-fx-text-fill: #93c5fd;";
    }

    private VBox createInvoiceCard(Invoice invoice) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setMinHeight(330);

        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 16;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 16;"
        );

        Label title = new Label("Invoice #" + invoice.getInvoiceId());
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        Label status = new Label(safe(invoice.getInvoiceStatus()));
        status.setStyle(getInvoiceStatusStyle(invoice.getInvoiceStatus()));

        Label total = new Label("$" + formatMoney(invoice.getTotalAmount()));
        total.setStyle(
                "-fx-text-fill: #63e6be;"
                + "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
        );

        Label details = new Label(
                "Issue Date: " + safeDate(invoice.getIssueDate())
                + "\nRental Cost: $" + formatMoney(invoice.getRentalCost())
                + "\nExtra Charges: $" + formatMoney(invoice.getExtraCharges())
                + "\nLate Fees: $" + formatMoney(invoice.getLateFees())
                + "\nDiscount: $" + formatMoney(invoice.getDiscount())
                + "\nTax: $" + formatMoney(invoice.getTax())
                + "\nContract ID: " + invoice.getContractId()
        );

        details.setWrapText(true);
        details.setStyle(
                "-fx-text-fill: #ccd6f6;"
                + "-fx-font-size: 12px;"
                + "-fx-line-spacing: 4;"
        );

        card.getChildren().addAll(title, status, total, details);
        return card;
    }

    private String getInvoiceStatusStyle(String status) {
        String base =
                "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 10;"
                + "-fx-padding: 5 12;";

        if ("Paid".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(99, 230, 190, 0.18);"
                    + "-fx-text-fill: #63e6be;";
        }

        if ("Unpaid".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(253, 216, 53, 0.18);"
                    + "-fx-text-fill: #fdd835;";
        }

        if ("Overdue".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(255, 107, 107, 0.18);"
                    + "-fx-text-fill: #ff6b6b;";
        }

        if ("Cancelled".equalsIgnoreCase(status)) {
            return base
                    + "-fx-background-color: rgba(255, 107, 107, 0.18);"
                    + "-fx-text-fill: #ff6b6b;";
        }

        return base
                + "-fx-background-color: rgba(147, 197, 253, 0.18);"
                + "-fx-text-fill: #93c5fd;";
    }

    private String formatMoney(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private VBox createCompletedContractReviewCard(RentalContract contract) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setMinHeight(230);

        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 16;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 16;"
        );

        Label title = new Label("Completed Contract #" + contract.getContractId());
        title.setWrapText(true);
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        Label details = new Label(
                "Car: " + safe(contract.getCarInfo())
                + "\nStart Date: " + safeDate(contract.getStartDate())
                + "\nActual Return: " + safeDate(contract.getActualReturnDate())
                + "\nStatus: " + safe(contract.getContractStatus())
        );
        details.setWrapText(true);
        details.setStyle(
                "-fx-text-fill: #ccd6f6;"
                + "-fx-font-size: 12px;"
                + "-fx-line-spacing: 4;"
        );

        boolean alreadyReviewed =
                reviewDAO.hasReviewForContract(currentCustomer.getCustomerId(), contract.getContractId());

        Button btnReview = new Button(alreadyReviewed ? "Review Already Added" : "Write Review");
        btnReview.setMaxWidth(Double.MAX_VALUE);

        if (alreadyReviewed) {
            btnReview.setDisable(true);
            btnReview.setStyle(
                    "-fx-background-color: #1b2350;"
                    + "-fx-text-fill: #8892b0;"
                    + "-fx-font-weight: bold;"
                    + "-fx-background-radius: 10;"
                    + "-fx-padding: 10 18;"
            );
        } else {
            btnReview.setStyle(primaryButtonStyle());
            btnReview.setOnAction(e -> buildReviewForm(contract));
        }

        card.getChildren().addAll(title, details, btnReview);
        return card;
    }

    private VBox createReviewCard(Review review) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setMinHeight(220);

        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 16;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 16;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 16;"
        );

        Label title = new Label("Review #" + review.getReviewId());
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        Label rating = new Label(getStars(review.getRating()) + "  (" + review.getRating() + "/5)");
        rating.setStyle(
                "-fx-text-fill: #fdd835;"
                + "-fx-font-size: 15px;"
                + "-fx-font-weight: bold;"
        );

        Label details = new Label(
                "Review Date: " + safeDate(review.getReviewDate())
                + "\nContract ID: " + review.getContractId()
                + "\nComment: " + safe(review.getComment())
        );
        details.setWrapText(true);
        details.setStyle(
                "-fx-text-fill: #ccd6f6;"
                + "-fx-font-size: 12px;"
                + "-fx-line-spacing: 4;"
        );

        card.getChildren().addAll(title, rating, details);
        return card;
    }

    private void buildReviewForm(RentalContract contract) {
        clearContentArea();

        selectedReviewContract = contract;

        lblSectionTitle.setText("Add Review");
        lblSectionContent.setText(
                "Contract #" + contract.getContractId()
                + "\nCar: " + safe(contract.getCarInfo())
                + "\n\nPlease rate your rental experience."
        );

        Label formTitle = new Label("Review Details");
        formTitle.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        cmbReviewRating = new ComboBox<>();
        cmbReviewRating.getItems().addAll(1, 2, 3, 4, 5);
        cmbReviewRating.setPromptText("Select rating");
        cmbReviewRating.setMaxWidth(Double.MAX_VALUE);
        cmbReviewRating.setStyle(
                "-fx-background-color: #060818;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
                + "-fx-padding: 4 8;"
        );

        txtReviewComment = new TextArea();
        txtReviewComment.setPromptText("Write your comment here...");
        txtReviewComment.setPrefRowCount(4);
        txtReviewComment.setWrapText(true);
        txtReviewComment.setStyle(
                "-fx-control-inner-background: #060818;"
                + "-fx-background-color: #060818;"
                + "-fx-text-fill: #e6f1ff;"
                + "-fx-prompt-text-fill: #46506f;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
        );

        VBox ratingBox = createFieldBox("Rating *", cmbReviewRating);
        VBox commentBox = createFieldBox("Comment *", txtReviewComment);

        Button btnSubmit = new Button("Submit Review");
        btnSubmit.setStyle(primaryButtonStyle());
        btnSubmit.setOnAction(e -> submitReview());

        Button btnBack = new Button("Back");
        btnBack.setStyle(normalButtonStyle());
        btnBack.setOnAction(e -> handleReviews());

        HBox buttons = new HBox(12, btnBack, btnSubmit);
        buttons.setStyle("-fx-alignment: center-right;");

        VBox form = new VBox(14, formTitle, ratingBox, commentBox, buttons);
        form.setPadding(new Insets(18));
        form.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 14;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 14;"
                + "-fx-border-width: 1;"
        );

        contentArea.getChildren().add(form);
    }

    private void submitReview() {
        if (currentCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No customer is currently logged in.");
            return;
        }

        if (selectedReviewContract == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "No contract selected.");
            return;
        }

        Integer rating = cmbReviewRating.getValue();
        String comment = txtReviewComment.getText().trim();

        if (rating == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a rating.");
            return;
        }

        if (comment.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please write a comment.");
            return;
        }

        if (reviewDAO.hasReviewForContract(currentCustomer.getCustomerId(), selectedReviewContract.getContractId())) {
            showAlert(Alert.AlertType.WARNING, "Duplicate Review",
                    "You already added a review for this contract.");
            handleReviews();
            return;
        }

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setReviewDate(LocalDate.now());
        review.setCustomerId(currentCustomer.getCustomerId());
        review.setContractId(selectedReviewContract.getContractId());

        boolean success = reviewDAO.addReview(review);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Review submitted successfully.");
            handleReviews();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not submit review.");
        }
    }

    private String getStars(int rating) {
        StringBuilder stars = new StringBuilder();

        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }

        return stars.toString();
    }

    private VBox buildExtrasSelectionBox() {
        extraCheckBoxes.clear();
        extraQuantitySpinners.clear();

        Label title = new Label("Optional Extras");
        title.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
        );

        Label hint = new Label("Choose any extra services you want to add to your reservation.");
        hint.setWrapText(true);
        hint.setStyle(
                "-fx-text-fill: #8892b0;"
                + "-fx-font-size: 12px;"
        );

        FlowPane extrasCardsPane = new FlowPane();
        extrasCardsPane.setHgap(14);
        extrasCardsPane.setVgap(14);
        extrasCardsPane.setMaxWidth(Double.MAX_VALUE);
        extrasCardsPane.setStyle("-fx-padding: 4 0 0 0;");

        List<Extra> extras = reservationExtraDAO.getAvailableExtras();

        if (extras == null || extras.isEmpty()) {
            Label empty = new Label("No extras are currently available.");
            empty.setStyle("-fx-text-fill: #8892b0; -fx-font-size: 13px;");
            extrasCardsPane.getChildren().add(empty);
        } else {
            for (Extra extra : extras) {
                extrasCardsPane.getChildren().add(createExtraRow(extra));
            }
        }

        VBox box = new VBox(10);
        box.getChildren().addAll(title, hint, extrasCardsPane);
        box.setMaxWidth(Double.MAX_VALUE);

        box.setStyle(
                "-fx-background-color: #060818;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 12;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 14;"
        );

        return box;
    }

    private VBox createExtraRow(Extra extra) {
        CheckBox checkBox = new CheckBox();
        checkBox.setStyle(
                "-fx-text-fill: #e6f1ff;"
                + "-fx-font-size: 13px;"
        );

        Label name = new Label(safe(extra.getExtraName()));
        name.setWrapText(true);
        name.setStyle(
                "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
        );

        Label price = new Label("$" + formatMoney(extra.getPricePerDay()) + " per day");
        price.setStyle(
                "-fx-text-fill: #63e6be;"
                + "-fx-font-size: 12px;"
                + "-fx-font-weight: bold;"
        );

        Label description = new Label(safe(extra.getDescription()));
        description.setWrapText(true);
        description.setMaxHeight(34);
        description.setStyle(
                "-fx-text-fill: #8892b0;"
                + "-fx-font-size: 11px;"
        );

        Spinner<Integer> quantitySpinner = new Spinner<>();
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        quantitySpinner.setEditable(false);
        quantitySpinner.setPrefWidth(75);
        quantitySpinner.setDisable(true);

        quantitySpinner.setStyle(
                "-fx-background-color: #060818;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 8;"
                + "-fx-background-radius: 8;"
        );

        checkBox.selectedProperty().addListener((obs, oldValue, newValue) -> {
            quantitySpinner.setDisable(!newValue);
        });

        extraCheckBoxes.put(extra, checkBox);
        extraQuantitySpinners.put(extra, quantitySpinner);

        HBox top = new HBox(8, checkBox, name);
        top.setStyle("-fx-alignment: center-left;");

        HBox bottom = new HBox(8, price, quantitySpinner);
        bottom.setStyle("-fx-alignment: center-left;");

        VBox card = new VBox(8);
        card.getChildren().addAll(top, description, bottom);

        card.setPrefWidth(260);
        card.setMinHeight(135);

        card.setStyle(
                "-fx-background-color: #090e20;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #1a2040;"
                + "-fx-border-radius: 12;"
                + "-fx-border-width: 1;"
                + "-fx-padding: 12;"
        );

        return card;
    }

    private Map<Extra, Integer> getSelectedExtras() {
        Map<Extra, Integer> selectedExtras = new HashMap<>();

        for (Map.Entry<Extra, CheckBox> entry : extraCheckBoxes.entrySet()) {
            Extra extra = entry.getKey();
            CheckBox checkBox = entry.getValue();

            if (checkBox.isSelected()) {
                Spinner<Integer> spinner = extraQuantitySpinners.get(extra);

                int quantity = 1;
                if (spinner != null && spinner.getValue() != null) {
                    quantity = spinner.getValue();
                }

                selectedExtras.put(extra, quantity);
            }
        }

        return selectedExtras;
    }
    @FXML
private void handleEditProfile() {
    setActiveButton(btnProfile);
    clearContentArea();

    lblSectionTitle.setText("Edit Profile");
    lblSectionContent.setText("Update your personal information.");

    if (currentCustomer == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "No customer data is loaded.");
        return;
    }

    Label formTitle = new Label("Profile Information");
    formTitle.setStyle(
            "-fx-text-fill: white;"
            + "-fx-font-size: 18px;"
            + "-fx-font-weight: bold;"
    );

    txtProfileFirstName = new TextField();
    txtProfileFirstName.setPromptText("First Name");
    txtProfileFirstName.setText(emptyIfNull(currentCustomer.getFirstName()));
    txtProfileFirstName.setStyle(inputStyle());
    txtProfileFirstName.setMaxWidth(Double.MAX_VALUE);

    txtProfileLastName = new TextField();
    txtProfileLastName.setPromptText("Last Name");
    txtProfileLastName.setText(emptyIfNull(currentCustomer.getLastName()));
    txtProfileLastName.setStyle(inputStyle());
    txtProfileLastName.setMaxWidth(Double.MAX_VALUE);

    txtProfilePhone = new TextField();
    txtProfilePhone.setPromptText("Phone Number");
    txtProfilePhone.setText(emptyIfNull(currentCustomer.getPhoneNumber()));
    txtProfilePhone.setStyle(inputStyle());
    txtProfilePhone.setMaxWidth(Double.MAX_VALUE);

    txtProfileEmail = new TextField();
    txtProfileEmail.setPromptText("Email");
    txtProfileEmail.setText(emptyIfNull(currentCustomer.getEmail()));
    txtProfileEmail.setStyle(inputStyle());
    txtProfileEmail.setMaxWidth(Double.MAX_VALUE);

    txtProfileAddress = new TextField();
    txtProfileAddress.setPromptText("Address");
    txtProfileAddress.setText(emptyIfNull(currentCustomer.getAddress()));
    txtProfileAddress.setStyle(inputStyle());
    txtProfileAddress.setMaxWidth(Double.MAX_VALUE);

    VBox firstNameBox = createFieldBox("First Name *", txtProfileFirstName);
    VBox lastNameBox = createFieldBox("Last Name *", txtProfileLastName);
    VBox phoneBox = createFieldBox("Phone Number", txtProfilePhone);
    VBox emailBox = createFieldBox("Email", txtProfileEmail);
    VBox addressBox = createFieldBox("Address", txtProfileAddress);

    firstNameBox.setMaxWidth(Double.MAX_VALUE);
    lastNameBox.setMaxWidth(Double.MAX_VALUE);
    phoneBox.setMaxWidth(Double.MAX_VALUE);
    emailBox.setMaxWidth(Double.MAX_VALUE);
    addressBox.setMaxWidth(Double.MAX_VALUE);

    Button btnBack = new Button("Back");
    btnBack.setStyle(normalButtonStyle());
    btnBack.setOnAction(e -> handleProfile());

    Button btnSave = new Button("Save Changes");
    btnSave.setStyle(primaryButtonStyle());
    btnSave.setOnAction(e -> saveProfileChanges());

    HBox buttons = new HBox(12, btnBack, btnSave);
    buttons.setStyle("-fx-alignment: center-right;");

    VBox form = new VBox(14);
    form.getChildren().addAll(
            formTitle,
            firstNameBox,
            lastNameBox,
            phoneBox,
            emailBox,
            addressBox,
            buttons
    );

    form.setPadding(new Insets(18));
    form.setMaxWidth(Double.MAX_VALUE);
    VBox.setVgrow(form, Priority.NEVER);

    form.setStyle(
            "-fx-background-color: #090e20;"
            + "-fx-background-radius: 14;"
            + "-fx-border-color: #1a2040;"
            + "-fx-border-radius: 14;"
            + "-fx-border-width: 1;"
    );
    form.setPrefWidth(900);
    form.setMaxWidth(900);
    contentArea.getChildren().add(form);
}

private void saveProfileChanges() {
    if (currentCustomer == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "No customer data is loaded.");
        return;
    }

    String firstName = txtProfileFirstName.getText().trim();
    String lastName = txtProfileLastName.getText().trim();
    String phone = txtProfilePhone.getText().trim();
    String email = txtProfileEmail.getText().trim();
    String address = txtProfileAddress.getText().trim();

    if (firstName.isEmpty() || lastName.isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                "First name and last name are required.");
        return;
    }

    if (!email.isEmpty() && !email.contains("@")) {
        showAlert(Alert.AlertType.WARNING, "Validation Error",
                "Please enter a valid email address.");
        return;
    }

    currentCustomer.setFirstName(firstName);
    currentCustomer.setLastName(lastName);
    currentCustomer.setPhoneNumber(phone);
    currentCustomer.setEmail(email);
    currentCustomer.setAddress(address);

    boolean success = customerDAO.updateCustomerProfile(currentCustomer);

    if (success) {
        lblWelcome.setText("Welcome, " + currentCustomer.getFullName());

        showAlert(Alert.AlertType.INFORMATION, "Success",
                "Profile updated successfully.");

        handleProfile();
    } else {
        showAlert(Alert.AlertType.ERROR, "Error",
                "Could not update profile. Make sure the email is not used by another customer.");
    }
}
private HBox createContractInfoRow(String label, String value) {
    HBox row = new HBox(8);
    row.setAlignment(Pos.CENTER_LEFT);

    Label labelNode = new Label(label + ":");
    labelNode.setMinWidth(115);
    labelNode.setStyle(
            "-fx-text-fill: #94a3b8;"
            + "-fx-font-size: 12px;"
            + "-fx-font-weight: bold;"
    );

    Label valueNode = new Label(value);
    valueNode.setWrapText(true);
    valueNode.setStyle(
            "-fx-text-fill: #e5e7eb;"
            + "-fx-font-size: 12px;"
    );

    row.getChildren().addAll(labelNode, valueNode);
    return row;
}

}
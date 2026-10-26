package com.example.baggagewise;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    // Declare input fields
    private EditText editWeight, editRate;
    private RadioGroup radioGroupClass;

    // Declare output fields
    private TextView textExcessWeight;
    private TextView textExcessCharge;
    private TextView textTotalPayable;

    // Declare buttons
    private Button btnCalculate, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Setup Application Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BaggageWise");
            getSupportActionBar().setSubtitle("Excess Baggage Fee Calculator");
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Connect Java variables to XML inputs
        editWeight = findViewById(R.id.editWeight);
        editRate = findViewById(R.id.editRate);
        radioGroupClass = findViewById(R.id.radioGroupClass);

        // Connect output TextViews
        textExcessWeight = findViewById(R.id.textExcessWeight);
        textExcessCharge = findViewById(R.id.textExcessCharge);
        textTotalPayable = findViewById(R.id.textTotalPayable);

        // Connect buttons
        btnCalculate = findViewById(R.id.btnCalculate);
        btnReset = findViewById(R.id.btnReset);

        // Calculate button function
        btnCalculate.setOnClickListener(v -> calculateFee());

        // Reset button function
        btnReset.setOnClickListener(v -> resetFields());

        // Open About Page
        Button btnAbout = findViewById(R.id.btnAbout);

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AboutActivity.class
            );

            startActivity(intent);
        });
    }

    // Create Toolbar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    // Handle Toolbar Actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_share) {

            // Temporary link until GitHub repository is created
            String appUrl = "https://github.com/arman2303/BaggageWise";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("text/plain");

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out BaggageWise - Excess Baggage Fee Calculator!\n"
                            + appUrl
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share BaggageWise"
                    )
            );

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Calculate excess baggage fee
    private void calculateFee() {

        String weightInput = editWeight.getText().toString().trim();
        String rateInput = editRate.getText().toString().trim();

        // Validate baggage weight
        if (weightInput.isEmpty()) {
            editWeight.setError("Please enter baggage weight");
            editWeight.requestFocus();
            return;
        }

        // Validate baggage rate
        if (rateInput.isEmpty()) {
            editRate.setError("Please enter baggage rate");
            editRate.requestFocus();
            return;
        }

        double weight;
        double rate;

        try {
            weight = Double.parseDouble(weightInput);
            rate = Double.parseDouble(rateInput);
        } catch (NumberFormatException e) {
            editWeight.setError("Please enter valid numbers");
            editRate.setError("Please enter valid numbers");
            return;
        }

        // Validate negative and invalid values
        if (!Double.isFinite(weight) || weight < 0) {
            editWeight.setError("Enter a valid non-negative weight");
            editWeight.requestFocus();
            return;
        }

        if (!Double.isFinite(rate) || rate < 0) {
            editRate.setError("Enter a valid non-negative rate");
            editRate.requestFocus();
            return;
        }

        // Determine baggage allowance
        double allowance;

        if (radioGroupClass.getCheckedRadioButtonId()
                == R.id.radioBusiness) {

            allowance = 40.0;

        } else {

            allowance = 20.0;
        }

        // Calculate excess baggage weight
        double excessWeight = Math.max(0, weight - allowance);

        // Calculate excess baggage charge
        double excessCharge = excessWeight * rate;

        // Calculate 8% service tax
        double serviceTax = excessCharge * 0.08;

        // Calculate total amount payable
        double totalPayable = excessCharge + serviceTax;

        if (!Double.isFinite(totalPayable)) {
            editRate.setError("Value is too large");
            return;
        }

        // Display calculation results
        textExcessWeight.setText(
                String.format(Locale.US, "%.2f kg", excessWeight)
        );

        textExcessCharge.setText(
                String.format(Locale.US, "RM %.2f", excessCharge)
        );

        textTotalPayable.setText(
                String.format(Locale.US, "RM %.2f", totalPayable)
        );
    }

    // Reset all input and output fields
    private void resetFields() {

        editWeight.setText("");
        editRate.setText("");

        editWeight.setError(null);
        editRate.setError(null);

        radioGroupClass.check(R.id.radioEconomy);

        textExcessWeight.setText("0.00 kg");
        textExcessCharge.setText("RM 0.00");
        textTotalPayable.setText("RM 0.00");

        editWeight.clearFocus();
        editRate.clearFocus();
    }
}

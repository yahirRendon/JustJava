
package com.example.android.justjava;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.justjava.R;

import java.text.NumberFormat;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {
    int quantity = 2; //Track quantity
    boolean americano = false; //Determine type of coffee
    boolean latte = false; //Determine type of coffee
    boolean mocha = false; //Determine type of coffee
    Button ameriButton; //Americano button
    Button latteButton; //Latte button
    Button mochaButton; //Mocha button
    ColorStateList btnHighlight; //Adjust question 4 True/False Button
    ColorStateList btnPrimaryBackground; //Adjust question 4 True/False Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Hide Keyboard unless EditText is active
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.coffee_table_cropped);


        ameriButton = (Button) findViewById(R.id.americano_button_view);
        latteButton = (Button) findViewById(R.id.latte_button_view);
        mochaButton = (Button) findViewById(R.id.mocha_button_view);
        btnHighlight = AppCompatResources.getColorStateList(getApplicationContext(), R.color.colorAccent);
        btnPrimaryBackground = AppCompatResources.getColorStateList(getApplicationContext(), R.color.colorPrimary);
    }

    /**
     * This method is called when the increment button is clicked.
     */
    public void increment(View view) {
        quantity = quantity + 1;
        if(quantity > 100) {
            quantity = 100;
            //Show an error message as a toast
            Toast.makeText(this, R.string.over_100_coffees,Toast.LENGTH_SHORT).show();
            //Exit this method as there is nothing left to do
            //return;
        }
        displayQuantity(quantity);
    }

    /**
     * This method is called when the decrement button is clicked.
     */
    public void decrement(View view) {
        quantity = quantity - 1;
        if(quantity < 1) {
            quantity = 1;
            //Show an error message as a toast
            Toast.makeText(this, R.string.under_1_coffee,Toast.LENGTH_SHORT).show();
            //Exit this method as there is nothing left to do
            //return;
        }
        displayQuantity(quantity);
    }

    /**
     * This method is called when the americano button is clicked.
     */
    public void americano(View view) {
        americano = true;
        latte = false;
        mocha = false;
        ViewCompat.setBackgroundTintList(ameriButton, btnHighlight);
        ViewCompat.setBackgroundTintList(latteButton, btnPrimaryBackground);
        ViewCompat.setBackgroundTintList(mochaButton, btnPrimaryBackground);

    }

    /**
     * This method is called when the latte button is clicked.
     */
    public void latte(View view) {
        americano = false;
        latte = true;
        mocha = false;
        ViewCompat.setBackgroundTintList(ameriButton, btnPrimaryBackground);
        ViewCompat.setBackgroundTintList(latteButton, btnHighlight);
        ViewCompat.setBackgroundTintList(mochaButton, btnPrimaryBackground);
    }

    /**
     * This method is called when the mocha button is clicked.
     */
    public void mocha(View view) {
        americano = false;
        latte = false;
        mocha = true;
        ViewCompat.setBackgroundTintList(ameriButton, btnPrimaryBackground);
        ViewCompat.setBackgroundTintList(latteButton, btnPrimaryBackground);
        ViewCompat.setBackgroundTintList(mochaButton, btnHighlight);
    }


    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        //Find the user's name
        EditText nameField = (EditText) findViewById(R.id.name_field);
        String name = nameField.getText().toString();

        //If name field is empty prompt toast to enter name
        //If type is not selected prompt toast  to select type
        if (name.matches("")) {
            Toast.makeText(this, R.string.no_name, Toast.LENGTH_SHORT).show();
        } else if(!americano && !latte && !mocha)  {
            Toast.makeText(this, R.string.no_type, Toast.LENGTH_SHORT).show();
        } else {
            //Figure out if the user wants whippped cream topping
            CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
            boolean hasWhippedCream = whippedCreamCheckBox.isChecked();
            //Figure out if the user wants chocolate topping
            CheckBox chocolateCheckBox = (CheckBox) findViewById(R.id.chocolate_checkbox);
            boolean hasChocolate = chocolateCheckBox.isChecked();

            //Prepare order summary
            int price = calculatePrice(hasWhippedCream, hasChocolate);
            String priceMessage = createOrderSummary(name, price, hasWhippedCream, hasChocolate);

            //Send order summary to email app
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject) + name);
            intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    /**
     * Calculates the price of the order.
     *
     * @param addWhippedCream determines if the user has selected whipped cream
     * @param addChocolate determines if the user has selected chocolate
     * @return total price
     */
    private int calculatePrice(boolean addWhippedCream, boolean addChocolate) {
        int basePrice;

        //Price for 1 cup of coffee
        if(americano) {
            basePrice = 3;
        } else if(latte) {
            basePrice = 6;
        } else if( mocha) {
            basePrice = 7;
        } else {
            basePrice = 0;
        }

        //Add 1 for whipped cream
        if(addWhippedCream) {
            basePrice = basePrice + 1;
        }
        //Add 2 for chocolate
        if(addChocolate) {
            basePrice = basePrice + 2;
        }
        //Calculate the total price for the order
        return basePrice * quantity;
    }

    /**
     * Create summary of order.
     *
     *
     * @param name on the order
     * @param price of the order
     * @param addWhippedCream determines if the user has selected whipped cream
     * @param addChocolate determines if the user has selected chocolate
     * @return text summary
     */
    private String createOrderSummary(String name, int price, boolean addWhippedCream, boolean addChocolate) {
        String priceMessage = getString(R.string.order_summary_name, name);
        if (americano) {
            priceMessage += "\n" + getString(R.string.americano_selected);
        } else if (latte) {
            priceMessage += "\n" + getString(R.string.latte_selected);
        } else if (mocha) {
            priceMessage += "\n" + getString(R.string.mocha_selected);
        }
        priceMessage += "\n" + getString(R.string.add_whipped_cream) + addWhippedCream;
        priceMessage += "\n" + getString(R.string.add_chocolate) + addChocolate;
        priceMessage += "\n" + getString(R.string.order_quantity) + quantity;
        priceMessage += "\n" + getString(R.string.order_total) + price;
        NumberFormat.getCurrencyInstance().format(price);
        priceMessage += "\n" + getString(R.string.thank_you);
        return priceMessage;
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int value) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + value);
    }

}





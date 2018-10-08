package com.example.tomab.calculator_fall2018;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView calculatorDisplay;

    private TextView debugAccum;
    private TextView debugOpcode;

    private static final String KEY_DISPLAY = "display";
    private static final String KEY_ACCUM = "accum";
    private static final String KEY_OPCODE = "opcode";
    private static final String KEY_RESTARTDATAENTRY = "restartDataEntry";
    private static final String KEY_DECIMALPOINTSEEN = "decimalPointSeen";

    private double accum;
    private String pendingOpcode;
    private boolean restartDataEntry;
    private boolean decimalPointSeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        String strDisplay = "0";
        if( savedInstanceState != null ) {
            strDisplay = savedInstanceState.getString(KEY_DISPLAY, "0");
            accum = savedInstanceState.getDouble(KEY_ACCUM, 0.0);
            pendingOpcode = savedInstanceState.getString(KEY_OPCODE, "");
            restartDataEntry = savedInstanceState.getBoolean(KEY_RESTARTDATAENTRY, true);
            decimalPointSeen = savedInstanceState.getBoolean(KEY_DECIMALPOINTSEEN, false);

        } else {
            clearCalculator();
        }

        calculatorDisplay = (TextView) findViewById( R.id.calcDisplay );
        calculatorDisplay.setText(strDisplay);

        debugAccum = (TextView) findViewById( R.id.debugAccum);
        debugAccum.setText("accum=" + Double.toString(accum));

        debugOpcode = (TextView) findViewById( R.id.debugOpcode);
        debugOpcode.setText("opcode=" + pendingOpcode);
    }

    // The activity begins to stop, the system calls onSaveInstanceState. Save state information
    // with a collection of key-value pairs that together define the state of the activity. The
    // key-value pairs are put into a "Bundle". The Bundle will be passed into onCreate when
    // the activity gets restored. Take a look at the documentation for "onRestoreinstanceState".
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.d(TAG, "onSaveInstanceState");

        String strDisplay = calculatorDisplay.getText().toString();
        savedInstanceState.putString(KEY_DISPLAY, strDisplay);
        savedInstanceState.putDouble(KEY_ACCUM, accum);
        savedInstanceState.putString(KEY_OPCODE, pendingOpcode);
        savedInstanceState.putBoolean(KEY_RESTARTDATAENTRY, restartDataEntry);
        savedInstanceState.putBoolean(KEY_DECIMALPOINTSEEN, decimalPointSeen);
    }

    // handle a click on a '+', '-', '*', or a '/'
    public void onOpcodeClick(View v) {
        String strOpcode = v.getTag().toString();
        Log.d(TAG, strOpcode);

        // Apply pendingOpcode to the accumulator
        // and the value in the display, storing
        // the result in strDisplay
        String strDisplay = applyOpcode(calculatorDisplay.getText().toString());
        clearCalculator();

        // update pendingOpcode with the opcode just clicked
        pendingOpcode = strOpcode;

        // convert the string result in strDisplay to
        // double and store in the accumulator
        accum = Double.parseDouble(strDisplay);

        // set the calculator text to the result
        // stored in strDisplay
        calculatorDisplay.setText(strDisplay);

        // update the debug fields
        debugAccum.setText("accum=" + strDisplay);
        debugOpcode.setText("opcode=" + strOpcode);
    }

    // handle a click on one of the digit buttons
    public void onNumericClick(View v) {
        String strDigit = v.getTag().toString();
        Log.d(TAG, strDigit);

        String strDisplay = calculatorDisplay.getText().toString();

        if (restartDataEntry == true) {
            restartDataEntry = false;
            strDisplay = strDigit;
        } else {
            strDisplay += strDigit;
        }

        // set the calculator text to the result
        // stored in strDisplay
        calculatorDisplay.setText(strDisplay);

        // update the debug fields
        debugAccum.setText("accum=" + Double.toString(accum));
        debugOpcode.setText("opcode=" + pendingOpcode);
    }

    // handle a click on the CLR button
    public void onClearClick(View v) {
        Log.d(TAG, "CLR");

        // clear the state variables
        clearCalculator();

        // set the calculator text to the result
        // stored in strDisplay
        calculatorDisplay.setText("0");

        // update the debug fields
        debugAccum.setText("accum=" + Double.toString(accum));
        debugOpcode.setText("opcode=" + pendingOpcode);
    }

    // handle a decimal point button click
    public void onDecimalPointClick(View v) {
        Log.d(TAG, ".");
        String strDisplay = calculatorDisplay.getText().toString();

        if (decimalPointSeen == false) {
            // the decimal point has not been seen yet
            if (restartDataEntry == true) {
                restartDataEntry = false;
                strDisplay = "0.";
            } else {
                strDisplay += ".";
            }
            decimalPointSeen = true;
        }

        // set the calculator text to the result
        // stored in strDisplay
        calculatorDisplay.setText(strDisplay);

        // update the debug fields
        debugAccum.setText("accum=" + Double.toString(accum));
        debugOpcode.setText("opcode=" + pendingOpcode);
    }

    public void onDelClick(View v) {
        Log.d(TAG, ".");
        String strDisplay = calculatorDisplay.getText().toString();

        // don't delete zero display
        if ((strDisplay == "0") || (strDisplay == "0.0")) {
            return;
        }

        // if the character being deleted is '.', allow for
        // another decimal point to be entered later
        if (strDisplay.charAt(strDisplay.length() - 1) == '.') {
            decimalPointSeen = false;
        }

        // delete the right most character in the display
        strDisplay = strDisplay.substring(0, strDisplay.length() - 1);

        // if you just deleted the last character in
        // the display, put 0 in the display
        if (strDisplay.length() == 0) {
            restartDataEntry = true;
            strDisplay = "0";
        }
        calculatorDisplay.setText(strDisplay);
    }

    // handle an equals button click
    public void onEqualClick(View v) {
        Log.d(TAG, "=");
        String strDisplay = applyOpcode(calculatorDisplay.getText().toString());

        clearCalculator();

        // set the calculator text to the result
        // stored in strDisplay
        calculatorDisplay.setText(strDisplay);

        // update the debug fields
        debugAccum.setText("accum=" + Double.toString(accum));
        debugOpcode.setText("opcode=" + pendingOpcode);
    }

    public void onClearEntryClick(View v) {
        Log.d(TAG, "ce");
        calculatorDisplay.setText("0");
        restartDataEntry = true;
        decimalPointSeen = false;
    }

    public void onChangeSignClick(View v) {
        String strDisplay = calculatorDisplay.getText().toString();
        if (Double.parseDouble(strDisplay) == 0.0) {
            return;
        }

        if (strDisplay.charAt(0) == '-') {
            strDisplay = strDisplay.substring(1);
        } else {
            strDisplay = "-" + strDisplay;
        }
        calculatorDisplay.setText(strDisplay);
    }

    // clear the state variables
    private void clearCalculator() {
        accum = 0.0;
        pendingOpcode = "";
        restartDataEntry = true;
        decimalPointSeen = false;
    }

    // apply the (pending) opcode to the value in the accumulator
    // and the value in the display
    private String applyOpcode(String strOperand) {
        double dblOperand = Double.parseDouble(strOperand);
        if (pendingOpcode != "") {
            char opc = pendingOpcode.charAt(0);
            switch (opc) {
                case '+':
                    dblOperand = accum + dblOperand;
                    break;
                case '-':
                    dblOperand = accum - dblOperand;
                    break;
                case '*':
                    dblOperand = accum * dblOperand;
                    break;
                case '/':
                    dblOperand = accum / dblOperand;
                    break;
            }
        }
        return Double.toString(dblOperand);
    }
}

package com.example.mathflashcards

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout

class CustomNumberKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var listener: OnKeyboardClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_keyboard, this, true)

        // Find views
        val keyboardLayout = findViewById<GridLayout>(R.id.keyboardLayout)

        // Set click listeners for number buttons
        val numberButtonIds = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )
        for (buttonId in numberButtonIds) {
            val button = findViewById<Button>(buttonId)
            button.setOnClickListener { onNumberButtonClick(button.text.toString()) }
        }

        // Set click listener for backspace button
        val backspaceButton = findViewById<Button>(R.id.buttonBackspace)
        backspaceButton.setOnClickListener { onBackspaceButtonClick() }

        // Set click listener for clear button
        val clearButton = findViewById<Button>(R.id.buttonClear)
        clearButton.setOnClickListener { onClearButtonClick() }
    }

    fun onBackspaceButtonClick() {
        // Handle button click event
        // For example, append the clicked number to a TextView
        //textView.append(number)
    }
    fun onClearButtonClick() {
        // Handle button click event
        // For example, append the clicked number to a TextView
       // textView.append(number)
    }

    fun onNumberButtonClick(number: String) {
        // Handle button click event
        // For example, append the clicked number to a TextView
        //textView.append(number)
    }


    interface OnKeyboardClickListener {
        fun onKeyClick(key: String)
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}

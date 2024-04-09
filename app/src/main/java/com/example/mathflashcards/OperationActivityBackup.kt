package com.example.mathflashcards

import android.annotation.SuppressLint
import android.graphics.Color
import android.media.SoundPool
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.random.Random

class OperationActivityBackup : AppCompatActivity() {

    // Views
    private lateinit var relativeLayBackground: RelativeLayout
    private lateinit var operator1TextView: TextView
    private lateinit var operator2TextView: TextView
    private lateinit var operandTextView: TextView
    private lateinit var answerToggleBtn: Button
    private lateinit var resultEditText: EditText
    private lateinit var operation: String
    private var correctAnswer: Int = 0


    private var textToSpeech: TextToSpeech? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get operation type from intent
        operation = intent.getStringExtra("operation").toString()

        // Set the theme based on the operation
        setTheme(operation)

        // Set the layout for the activity
        setContentView(R.layout.activity_operation)

        // Initialize views
        initializeViews()

        // Initialize Text-to-Speech
        // Initialize Text-to-Speech
        textToSpeech = TextToSpeech(this) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.setLanguage(Locale.US)
            }
        }

        // Set background color based on operation
        //setBackgroundColor(operation)




        // Get initial flashcard and set listeners
        setupFlashCard()
    }

    // Initialize views
    private fun initializeViews() {
        relativeLayBackground = findViewById(R.id.relative_background)
        operator1TextView = findViewById(R.id.text_view_operator1)
        operator2TextView = findViewById(R.id.text_view_operator2)
        operandTextView = findViewById(R.id.text_view_operand)
        resultEditText = findViewById(R.id.edit_text_answer)
        answerToggleBtn = findViewById(R.id.btn_check)
    }

    // Set theme based on the operation
    @SuppressLint("ResourceAsColor")
    private fun setTheme(operand: String) {
        when (operand) {
            "+" -> getTheme().applyStyle(R.style.additionTheme, true)
            "-" -> getTheme().applyStyle(R.style.subtractionTheme, true)
            "x" -> getTheme().applyStyle(R.style.multiplicationTheme, true)
            "÷" -> getTheme().applyStyle(R.style.divisionTheme, true)
        }
    }

    // Set background color based on the operation
    @SuppressLint("ResourceAsColor")
    private fun setBackgroundColor(operand: String) {
        val color = when (operand) {
            "+" -> "#FF8A65" // light green
            "-" -> "#9575CD" // light red
            "x" -> "#F06292" // light purple
            "÷" -> "#77a472" // light gray
            else -> "#FFFFFF" // default color (white)
        }
        when (operand) {
            "+" -> answerToggleBtn.setBackgroundColor(R.color.addition)
            "-" -> answerToggleBtn.setBackgroundColor(R.color.subtraction)
            "x" -> answerToggleBtn.setBackgroundColor(R.color.multiplication)
            "÷" -> answerToggleBtn.setBackgroundColor(R.color.division)
        }
        relativeLayBackground.setBackgroundColor(Color.parseColor(color))
    }

    // Generate a random flashcard and set listeners
    private fun setupFlashCard() {
        correctAnswer = getFlashCard()

        // Generate random wrong answers
        val wrongAnswer1 = generateWrongAnswer(correctAnswer)
        val wrongAnswer2 = generateWrongAnswer(correctAnswer)

        // Shuffle the answers
        val answers = mutableListOf(correctAnswer, wrongAnswer1, wrongAnswer2).shuffled()

        // Set answers to buttons
        findViewById<Button>(R.id.btn_option1).text = answers[0].toString()
        findViewById<Button>(R.id.btn_option2).text = answers[1].toString()
        findViewById<Button>(R.id.btn_option3).text = answers[2].toString()

        // Set click listeners for buttons
        findViewById<Button>(R.id.btn_option1).setOnClickListener { onButtonClick(answers[0]) }
        findViewById<Button>(R.id.btn_option2).setOnClickListener { onButtonClick(answers[1]) }
        findViewById<Button>(R.id.btn_option3).setOnClickListener { onButtonClick(answers[2]) }


        // Show or hide answer based on button click
        answerToggleBtn.setOnClickListener {
            if (resultEditText.text == null || resultEditText.text.toString().isEmpty()) {
                resultEditText.setText(correctAnswer.toString())
                answerToggleBtn.setText(R.string.hide_answer)
            } else {
                resultEditText.text = null
                answerToggleBtn.setText(R.string.show_answer)
            }
        }

        // Generate a new flashcard
        findViewById<ImageButton>(R.id.btn_next).setOnClickListener {
            correctAnswer = getFlashCard()
            resultEditText.text = null
        }

        // Initialize SoundPool
        val soundPool = SoundPool.Builder().build()
        //val correctSoundId = soundPool.load(applicationContext, R.raw.ding, 1)
        //val wrongSoundId = soundPool.load(applicationContext, R.raw.buzz, 1)

        // Validate user's input
        resultEditText.setOnClickListener {
            val userInput = resultEditText.text.toString().trim().toIntOrNull()

            if (userInput == null) {
                // Handle invalid input
            } else {
                if(userInput == correctAnswer) {

                    // Show correct animation
                    val correctAnimation =
                        AnimationUtils.loadAnimation(this, R.anim.shake_animation_correct)
                    resultEditText.startAnimation(correctAnimation)

                    // Pronounce "correct"
                    //speak("Correct")
                    // Play correct sound effect
                    //soundPool.play(correctSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                    // Bring the text in the EditText to the front
                    // Add a green circle view on top of the EditText

                } else {
                    // Pronounce "correct"
                   // speak("Oops! try Again")

                    // Play wrong sound effect
                    //soundPool.play(wrongSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                    // Apply shake animation for wrong answer
                    resultEditText.startAnimation(
                        AnimationUtils.loadAnimation(
                            applicationContext,
                            R.anim.shake_animation
                        )
                    )
                }
            }
        }
    }

    private fun speak(text: String) {
        textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
    }
    // Generate a random flashcard
    private fun getFlashCard(): Int {
        // Generate random operators
        var operator1 = getRandomOperator()
        var operator2 = getRandomOperator()

        // Check operators are valid before using and get updated values
        val (newOperator1, newOperator2) = checkOperators(operator1, operator2, operation)
        operator1 = newOperator1
        operator2 = newOperator2

        // Set random operators to TextViews
        operator1TextView.text = operator1.toString()
        operator2TextView.text = operator2.toString()
        operandTextView.text = operation
        resultEditText.text = null
        resultEditText.background = null

        // calculate result based on generated values
        return calculateResult(operator1, operator2, operation)
    }

    // check operators are valid before using
    private fun checkOperators(operator1: Int, operator2: Int, operand: String): Pair<Int, Int>  {
        var newOperator1 = operator1
        var newOperator2 = operator2
        when (operand) {
            "-" -> {
                if (operator2 > operator1) {
                    // Swap the values of operator1 and operator2
                    newOperator1 = operator2
                    newOperator2 = operator1
                }
            }
            "÷" -> {
                if (operator2 != 0 && operator1 % operator2 == 0) {
                    // Division is valid, no need to update operators
                } else {
                    // Division is not valid, update operators until a valid pair is found
                    var isValidPair = false
                    while (!isValidPair) {
                        newOperator1 = getRandomOperator()
                        newOperator2 = getRandomOperator()
                        if (newOperator2 != 0 && newOperator1 % newOperator2 == 0) {
                            isValidPair = true
                        }
                    }
                }
            }
        }

        // Return the updated operator values
        return Pair(newOperator1, newOperator2)
    }

    // Get a random operator
    private fun getRandomOperator(): Int {
        return Random.nextInt(1, 15)
    }

    // Calculate the result based on the operator
    private fun calculateResult(operator1: Int, operator2: Int, operand: String): Int {
        return when (operand) {
            "+" -> {
                operator1 + operator2
            }
            "-" -> {
                operator1 - operator2
            }
            "x" -> {
                operator1 * operator2
            }
            "÷" -> {
                operator1 / operator2
            }
            else -> {
                throw IllegalArgumentException("Invalid operator: $operand")
            }
        }
    }

    // Handle home button click
    fun onHomeClicked(view: View) {
        finish() // Close the activity
    }

    private fun generateWrongAnswer(correctAnswer: Int): Int {
        // Generate a wrong answer by adding a random number to the correct answer
        // You can customize this logic based on your requirements
        val offset = Random.nextInt(1, 10) // Generate a random offset
        return correctAnswer + offset
    }
    private fun onButtonClick(answer: Int) {
        if (answer == correctAnswer) {
            // Handle correct answer
        } else {
            // Handle wrong answer
        }
    }

}
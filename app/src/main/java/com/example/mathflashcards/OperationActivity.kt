package com.example.mathflashcards

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.VectorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.random.Random


class OperationActivity : AppCompatActivity() {

    // Views
    private lateinit var relativeLayBackground: RelativeLayout
    private lateinit var operator1TextView: TextView
    private lateinit var operator2TextView: TextView
    private lateinit var operandTextView: TextView
    private lateinit var  tvAnswer: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvCardCount: TextView
    private lateinit var operation: String
    private lateinit var  optionsLayout: LinearLayout
    private lateinit var  option1: Button
    private lateinit var  option2: Button
    private var correctAnswer: Int = 0
    private var cardCount: Int = 0
    private var correctAnswerCount: Int = 0
    private var wrongAnswerCount: Int = 0
    private var flashCardTimer: CountDownTimer? = null

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

        // Get initial flashcard and set listeners
        getFlashCard()

        startFlashCardTimer()
    }

    // Initialize views
    private fun initializeViews() {
        relativeLayBackground = findViewById(R.id.relative_background)
        operator1TextView = findViewById(R.id.text_view_operator1)
        operator2TextView = findViewById(R.id.text_view_operator2)
        operandTextView = findViewById(R.id.text_view_operand)
        optionsLayout = findViewById(R.id.ll_options)
        option1 = findViewById(R.id.btn_option1)
        option2 = findViewById(R.id.btn_option2)
        tvAnswer = findViewById(R.id.tv_correct_answer)
        tvTimer = findViewById(R.id.tv_timer)
        tvCardCount = findViewById(R.id.tv_card_count)

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

    /*// Generate a random flashcard and set listeners
    private fun setupFlashCard() {
        // Generate a flashcard
        getFlashCard()

    }*/

    @SuppressLint("ResourceAsColor")
    private fun generateOptions() {
        // Generate random wrong answers
        val wrongAnswer1 = generateWrongAnswer(correctAnswer)
        val wrongAnswer2 = generateWrongAnswer(correctAnswer)

        // Shuffle the answers
        val answers = mutableListOf(correctAnswer, wrongAnswer1).shuffled()

        //
        val circleDrawable = ContextCompat.getDrawable(this, R.drawable.circle) as? VectorDrawable
        val color = ContextCompat.getColor(this, R.color.opaque_white)
        circleDrawable?.setTint(color)
        option1.background = circleDrawable
        option2.background = circleDrawable

        // Set answers to buttons
        option1.text = answers[0].toString()
        option2.text = answers[1].toString()
        //findViewById<Button>(R.id.btn_option3).text = answers[2].toString()

        // Set click listeners for buttons
        option1.setOnClickListener {
            val isCorrect = answers[0] == correctAnswer
            onButtonClick(it as Button, isCorrect)
        }

        option2.setOnClickListener {
            val isCorrect = answers[1] == correctAnswer
            onButtonClick(it as Button, isCorrect)
        }
    }

    // Generate a random flashcard
    @SuppressLint("SetTextI18n")
    private fun getFlashCard() {
        if (cardCount < 10) {
            cardCount++
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
            tvCardCount.text = "$cardCount/10"
            optionsLayout.visibility = View.VISIBLE
            tvAnswer.visibility = View.GONE

            // calculate result based on generated values
            correctAnswer = calculateResult(operator1, operator2, operation)

            //Generate options based on correct answer
            generateOptions()
        } else {

            // Cancel the timer
            flashCardTimer?.cancel()

            // Show stats dialog
            showStatsDialog()
        }
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
        onBackPressed()
    }

    private fun generateWrongAnswer(correctAnswer: Int): Int {
        // Generate a wrong answer by adding a random number to the correct answer
        // You can customize this logic based on your requirements
        val offset = Random.nextInt(1, 10) // Generate a random offset
        return correctAnswer + offset
    }

    private fun onButtonClick(clickedButton: Button, isCorrect: Boolean) {
        if (isCorrect) {

            correctAnswerCount++
            // Hide the LinearLayout containing the buttons
            // optionsLayout.animate().rotationYBy(180f).setDuration(500).start()
            optionsLayout.visibility = View.GONE
            // Show the TextView with the correct answer
            tvAnswer.text = clickedButton.text
            tvAnswer.visibility = View.VISIBLE

            // Create an AnimatorSet to combine animations
            val animatorSet = AnimatorSet()

            // Rotation animation for the TextView
            val rotation = ObjectAnimator.ofFloat(tvAnswer, View.ROTATION, 0f, 360f)
            rotation.duration = 1000

            // Fading animation for the TextView
            val fadeIn = ObjectAnimator.ofFloat(tvAnswer, View.ALPHA, 0f, 1f)
            fadeIn.duration = 1000

            // Add animations to the AnimatorSet
            animatorSet.playTogether(rotation, fadeIn)

            // Start the combined animations
            animatorSet.start()

            val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.correct)
            mp.start()
            //tvAnswer.animate().alpha(1f).setDuration(500).start()

            Handler(Looper.getMainLooper()).postDelayed({
                // Code to be executed after the delay
                getFlashCard()
            }, 1000) // 2000 milliseconds = 1 second

        } else {// Handle wrong answer
            if (tvAnswer.visibility == View.GONE) {
                ++wrongAnswerCount
                --correctAnswerCount
            }
            setButtonBackgroundTint(clickedButton, false)
        }
    }

    private fun setButtonBackgroundTint(button: Button, isCorrect: Boolean) {
        val circleDrawable = ContextCompat.getDrawable(this, R.drawable.circle) as? VectorDrawable

        if(!isCorrect) {
            // Initialize SoundPool
            val mp: MediaPlayer = MediaPlayer.create(applicationContext, R.raw.wrong)
            mp.start()

            circleDrawable?.setTint(ContextCompat.getColor(this, R.color.tomato))
            // Apply the modified drawable to the button background
            button.background = circleDrawable

            // Apply shake animation for wrong answer
            button.startAnimation(
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.shake_animation
                )
            )
        }
    }

    private fun startFlashCardTimer() {
        flashCardTimer = object : CountDownTimer(60000, 1000) { // Timer duration: 60 seconds
            override fun onTick(millisUntilFinished: Long) {
                // Calculate minutes and seconds
                val minutes = millisUntilFinished / (1000 * 60)
                val seconds = (millisUntilFinished % (1000 * 60)) / 1000

                // Update timer text with remaining time in mm:ss format
                tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // Handle timer finish
                // In this example, we'll simply stop the timer and finish the activity
                flashCardTimer?.cancel()
                // Show stats dialog
                showStatsDialog()
            }
        }.start()
    }

    private fun showStatsDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.stats_dialog_layout, null)

        // Configure the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set text and image views
        val statsTextView = dialogView.findViewById<TextView>(R.id.statsTextView)
        statsTextView.text = "Correct Answers: $correctAnswerCount\nWrong Answers: $wrongAnswerCount\nTime Left: ${tvTimer.text}"


        // Set OK button click listener
        val okButton = dialogView.findViewById<Button>(R.id.okButton)
        okButton.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
            // Optionally, you can finish the activity here
            finish()
        }

        // Show the dialog
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the timer to prevent memory leaks
        flashCardTimer?.cancel()
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                finish() // Exit the app
            }
            .setNegativeButton("No", null)
            .show()
/*
    // Get the positive and negative buttons
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        // Get text color from the current theme
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true)
        val textColor = typedValue.data

        // Set the text color of the buttons
        positiveButton.setTextColor(textColor)
        negativeButton.setTextColor(textColor)*/
    }
}
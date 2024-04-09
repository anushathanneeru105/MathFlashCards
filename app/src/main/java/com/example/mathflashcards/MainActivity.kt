package com.example.mathflashcards

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import kotlinx.coroutines.newSingleThreadContext
import kotlin.experimental.ExperimentalTypeInference

class MainActivity : AppCompatActivity() {
    private lateinit var relativeLayout : RelativeLayout;
    @OptIn(ExperimentalTypeInference::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        relativeLayout = findViewById(R.id.relative_layout_main)

    }

    override fun onResume() {
        super.onResume()

        val color = getDynamicColor()
        relativeLayout.setBackgroundColor(color)
        // Change the status bar color
        // window.statusBarColor = getColor(color)

        }

    private fun getDynamicColor(): Int {
        val colors = arrayOf("#4DB6AC")
        val randomIndex = colors.indices.random()
        return Color.parseColor(colors[randomIndex])
    }


    fun onAddClicked(view: View?) {
        startOperationActivity("+")
    }

    fun onSubClicked(view: View?) {
        startOperationActivity("-")
    }

    fun onMulClicked(view: View?) {
        startOperationActivity("x")
    }

    fun onDivClicked(view: View?) {
        startOperationActivity("÷")
    }

    private fun startOperationActivity(operation: String) {
        val intent = Intent(this, OperationActivity::class.java)
        intent.putExtra("operation", operation)
        startActivity(intent)
    }
}
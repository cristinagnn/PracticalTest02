package ro.pub.cs.systems.eim.practicaltest02v8

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PracticalTest02v8MainActivity : AppCompatActivity() {

    private lateinit var editTextCurrency: EditText
    private lateinit var buttonGetRate: Button
    private lateinit var textViewRate: TextView
    private lateinit var buttonGoToCalculator: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v8_main)

        // Asocieri cu elementele din layout
        editTextCurrency = findViewById(R.id.editTextCurrency)
        buttonGetRate = findViewById(R.id.buttonGetRate)
        textViewRate = findViewById(R.id.textViewRate)
        buttonGoToCalculator = findViewById(R.id.buttonGoToCalculator)

        // Exemplu de click pe butonul de obținere a cursului
        buttonGetRate.setOnClickListener {
            val currency = editTextCurrency.text.toString().uppercase()
        }
    }
}

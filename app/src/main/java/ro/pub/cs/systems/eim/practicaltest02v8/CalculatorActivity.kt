package ro.pub.cs.systems.eim.practicaltest02v8

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class CalculatorActivity : AppCompatActivity() {

    private lateinit var editTextT1: EditText
    private lateinit var editTextT2: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // Legăm elementele din layout
        editTextT1 = findViewById(R.id.editTextT1)
        editTextT2 = findViewById(R.id.editTextT2)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewResult = findViewById(R.id.textViewResult)

        // Listener pe buton
        buttonCalculate.setOnClickListener {
            val t1 = editTextT1.text.toString()
            val t2 = editTextT2.text.toString()

            if (t1.isNotEmpty() && t2.isNotEmpty()) {
                // Ajustează IP-ul la adresa unde rulează serverul tău Python
                val url = "http://10.41.166.232:8080/?operation=plus&t1=$t1&t2=$t2"
                // Pornește request-ul asincron
                FetchResultTask().execute(url)
            } else {
                textViewResult.text = "Please enter both operands."
            }
        }
    }

    inner class FetchResultTask : AsyncTask<String, Void, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            val urlString = params[0]
            return try {
                Log.d("HTTP", "Connecting to: $urlString")
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("HTTP", "Response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val result = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("HTTP", "Response: $result")
                    connection.disconnect()
                    result
                } else {
                    Log.e("HTTP", "Server returned: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("HTTP", "Error during HTTP request", e)
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                textViewResult.text = result
            } else {
                textViewResult.text = "Error fetching result."
            }
        }
    }
}

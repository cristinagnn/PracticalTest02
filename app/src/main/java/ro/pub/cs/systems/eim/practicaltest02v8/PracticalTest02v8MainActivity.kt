package ro.pub.cs.systems.eim.practicaltest02v8

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PracticalTest02v8MainActivity : AppCompatActivity() {

    private lateinit var editTextCurrency: EditText
    private lateinit var buttonGetRate: Button
    private lateinit var textViewRate: TextView

    private var cache = mutableMapOf<String, Pair<Long, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v8_main)

        // Asocieri cu elementele din layout
        editTextCurrency = findViewById(R.id.editTextCurrency)
        buttonGetRate = findViewById(R.id.buttonGetRate)
        textViewRate = findViewById(R.id.textViewRate)

        // Exemplu de click pe butonul de obținere a cursului
        buttonGetRate.setOnClickListener {
            val currency = editTextCurrency.text.toString().uppercase()
            if (currency == "USD" || currency == "EUR") {
                getRate(currency)
            } else {
                textViewRate.text = "Invalid currency. Use USD or EUR."
            }
        }
    }

    private fun getRate(currency: String) {
        val currentTime = System.currentTimeMillis()
        val cachedData = cache[currency]

        // Verificare cache (1 minut = 60000 ms)
        if (cachedData != null && currentTime - cachedData.first < 60000) {
            Log.d("CACHE", "Using cached data for $currency")
            textViewRate.text = cachedData.second
            return
        }

        // Dacă cache-ul nu este valid, facem cererea către server
        FetchRateTask(currency).execute()
    }

    inner class FetchRateTask(private val currency: String) : AsyncTask<Void, Void, String?>() {
        override fun doInBackground(vararg params: Void?): String? {
            val urlString = "https://api.coindesk.com/v1/bpi/currentprice/$currency.json"
            return try {
                Log.d("DEBUG", "Connecting to: $urlString")
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                Log.d("DEBUG", "Response code: $responseCode")
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val result = inputStream.bufferedReader().use { it.readText() }
                    Log.d("DEBUG", "Response: $result")
                    connection.disconnect()
                    result
                } else {
                    Log.e("ERROR", "Server returned HTTP $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error during HTTP request", e)
                null
            }
        }

        override fun onPostExecute(result: String?) {
            if (result != null) {
                try {
                    Log.d("DEBUG", "Parsing JSON: $result")
                    val jsonObject = JSONObject(result)
                    val rate = jsonObject.getJSONObject("bpi").getJSONObject(currency).getString("rate")

                    // Stocare în cache
                    cache[currency] = System.currentTimeMillis() to rate

                    // Actualizare UI
                    textViewRate.text = "1 BTC = $rate $currency"
                } catch (e: Exception) {
                    Log.e("ERROR", "Error parsing JSON", e)
                    textViewRate.text = "Error parsing data."
                }
            } else {
                textViewRate.text = "Error fetching data."
            }
        }
    }
}
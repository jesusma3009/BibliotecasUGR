package dev.jmarin.bibliotecasugr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.selects.*
import it.skrape.selects.html5.div
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rvData)
        val updateDataButton = findViewById<Button>(R.id.btnUpdateData)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                updateRecyclerView(getData())
            }
            catch (e : Exception){
                val internetText = findViewById<TextView>(R.id.tvInternet)
                CoroutineScope(Dispatchers.Main).launch{
                    internetText.visibility = View.VISIBLE
                }
                Log.e("DataException", e.toString())
            }
            CoroutineScope(Dispatchers.Main).launch {
                progressBar.visibility = View.INVISIBLE
            }
        }
        updateDataButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onBackPressed() {
        finishAffinity()
    }
    private fun updateRecyclerView(data : List<LibraryData>){
        CoroutineScope (Dispatchers.Main).launch {
            recyclerView.layoutManager = LinearLayoutManager(applicationContext)
            recyclerView.adapter = LibraryRecyclerViewAdapter(data)
        }
    }
    private suspend fun getData() : List<LibraryData> {
        val data = mutableListOf<LibraryData>()
        var nameLibrary = mutableListOf<String>()
        var seatsOccupied = mutableListOf<String>()
        var freePlaces = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            skrape(HttpFetcher) {

                request {
                    url = "https://jmarin.dev/url/c4zj"
                    //userAgent = "Bibliotecas UGR Unofficial App"
                    method = Method.GET
                    followRedirects = true

                }
                response {
                    htmlDocument {
                        relaxed = true
                        div{
                            withId = "espacios_ocupacion"
                            findAll{
                                div {
                                    withClass = "col-6"
                                    findAll {
                                        nameLibrary = eachText.toMutableList()
                                    }
                                }
                                div {
                                    withClass = "text-danger"
                                    findAll {
                                        seatsOccupied = eachText.toMutableList()
                                    }
                                }
                                div {
                                    withClass = "text-success"
                                    findAll {
                                        freePlaces = eachText.toMutableList()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (i in 0 until nameLibrary.size) {
                data.add(
                    LibraryData(
                        nameLibrary[i],
                        seatsOccupied[i],
                        freePlaces[i]
                    )
                )
            }
        }
        return data.toList()
    }

}
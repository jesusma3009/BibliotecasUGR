package dev.jmarin.bibliotecasugr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.skrape.core.htmlDocument
import it.skrape.fetcher.*
import it.skrape.selects.*
import it.skrape.selects.html5.td
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rvData)
        val updateDataButton = findViewById<Button>(R.id.btnUpdateData)
        CoroutineScope(Dispatchers.IO).launch {
            try{
                updateRecyclerView(getData())
            }
            catch (e : Exception){
                val internetText = findViewById<TextView>(R.id.tvInternet)
                internetText.visibility = View.VISIBLE
                //Log.e("ErrorConnect", e.message.toString())
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
        var data = mutableListOf<LibraryData>()
        var nameLibrary = mutableListOf<String>()
        var seatsOccupied = mutableListOf<String>()
        var freePlaces = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            skrape(HttpFetcher) {

                request {
                    url =
                        "https://oficinavirtual.ugr.es/TUI/consultaAulasEstudio/ocupacion.jsp"
                    userAgent = "Bibliotecas UGR Unofficial App"
                    method = Method.GET

                }
                response {
                    htmlDocument {
                        relaxed = true
                        td {
                            withClass = "ubicacion"
                            findAll {
                                nameLibrary = eachText.toMutableList()
                            }
                        }
                        td {
                            withId = "texto-rojo"
                            findAll {
                                seatsOccupied = eachText.toMutableList()
                            }
                        }
                        td {
                            withId = "texto-verde"
                            findAll {
                                freePlaces = eachText.toMutableList()
                            }
                        }
                    }
                }
            }
            for (i in 0 until nameLibrary.size) {
                data.add(
                    LibraryData(
                        nameLibrary[i].toString(),
                        seatsOccupied[i].toString(),
                        freePlaces[i].toString()
                    )
                )
            }
            if (data.size > 1){
                data.removeLast()
            }
        }
        return data.toList()
    }
}
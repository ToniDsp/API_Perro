package com.example.doglist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doglist.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnQueryTextListener {

    private lateinit var binding:ActivityMainBinding
    private lateinit var adapter: DogAdapter
    private val dogImages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svDogs.setOnQueryTextListener(this)
        initRecyclerView()
    }
    private fun initRecyclerView(){
        adapter = DogAdapter(dogImages)
        binding.rvDogs.layoutManager = LinearLayoutManager(this)
        binding.rvDogs.adapter = adapter

    }
    private fun getRetrofit():Retrofit{
        //Siempre tiene que acabar con una barra
        val url_perro: String = "https://dog.ceo/api/breed/"
        return Retrofit.Builder()
            .baseUrl(url_perro)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun searchByName (query:String){
        //Creamos co-rutina para lo que se haga ahora, se haga en un hilo secundario
        CoroutineScope(Dispatchers.IO).launch {
        val call = getRetrofit().create(APIService::class.java).getDogsByBreeds("$query/images")
            //El body es donde esta la respuesta
        val puppies = call.body()
            runOnUiThread{
                if(call.isSuccessful){
                    //El listado de imagenes es un list de String pero puede ser una lista vacia, pero no nula.
                val images:List<String> = puppies?.images ?: emptyList()
                    dogImages.clear()
                    dogImages.addAll(images)
                    adapter.notifyDataSetChanged()
                }
                else{
                    showError()
                }
            }


        }
    }
    private fun showError(){
        Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()){
            searchByName(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        //Creado automaticamente
        return true
    }
}
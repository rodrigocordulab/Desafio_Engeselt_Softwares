@file:Suppress("Annotator")

package com.example.engeselt_desafio.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.engeselt_desafio.R
import com.example.engeselt_desafio.db.DataBase
import com.example.engeselt_desafio.data.model.FormLocal
import kotlinx.android.synthetic.main.activity_collect.*



private const val REQUEST_CODE = 42
class CollectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)

        //Pega a intent de outra activity
        val it = intent

        //Recuperei a string da outra activity
        val latitude = it.getStringExtra("latitude")
        val longitude = it.getStringExtra("longitude")

        latitudePoint.setText(latitude)
        longitudePoint.setText(longitude)

        //Botão para abrir a câmera para foto, se realizado com sucesso
        //retorna a foto para a tela, caso de problema retorna um Toast
        buttonPhotoPoint.setOnClickListener{
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent,
                    REQUEST_CODE
                )
            }else{
                Toast.makeText(this, "Problema ao abrir câmera!",Toast.LENGTH_SHORT).show()
            }
        }

        //Botão para salvar dados do local(Nome, Latitude, Longitude)
        val context = this
        buttonSalvar.setOnClickListener {
            if(namePoint.text.toString().isNotEmpty() && latitudePoint.text.toString()
                    .isNotEmpty() && longitudePoint.text.toString().isNotEmpty()
            ){
                var localPoint = FormLocal(namePoint.text.toString(), latitudePoint.text.toString().toDouble(),longitudePoint.text.toString().toDouble())
                var db = DataBase(context)
                db.insertData(localPoint)
            }else{
                Toast.makeText(context, "Por favor preencha os dados", Toast.LENGTH_SHORT)
            }

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    //Atividade resultante da câmera, onde após tirar a foto com sucesso
    //retorna os dados e mostra na tela no ID imagePoint
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val takenImage = data?.extras?.get("data") as Bitmap
            imagePoint.setImageBitmap(takenImage)
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}


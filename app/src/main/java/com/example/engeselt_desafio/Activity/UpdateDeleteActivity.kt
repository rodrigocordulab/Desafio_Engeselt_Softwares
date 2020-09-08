package com.example.engeselt_desafio.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.engeselt_desafio.R
import com.example.engeselt_desafio.db.DataBase
import com.example.engeselt_desafio.data.model.FormLocalUpdateDelete
import kotlinx.android.synthetic.main.activity_update_delete.*

class UpdateDeleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete)

        //Pega a intent de outra activity
        val it = intent

        //Recuperei a string da outra activity
        val id = it.getStringExtra("id")
        val namePoint = it.getStringExtra("namePoint")
        val latitude = it.getDoubleExtra("latitude",0.00)
        val longitude = it.getDoubleExtra("longitude",0.00)

        //Acrescenta a tela para mostrar o ID do marker
        textViewIDValor.append(id)

        //Mostra os dados atual no banco para realizar a edição
        namePointEdit.setText(namePoint)
        latitudePointEdit.setText(latitude.toString())
        longitudePointEdit.setText(longitude.toString())

        //Botão de Editar, onde vai realizar a edição puxando os dados
        //e atraves do ID vai alterar no banco para após retorna para tela
        //do mapa
        val context = this
        buttonEditar.setOnClickListener {
            if(namePointEdit.text.toString().isNotEmpty() && latitudePointEdit.text.toString()
                    .isNotEmpty() && longitudePointEdit.text.toString().isNotEmpty()
            ){
                var localPoint = FormLocalUpdateDelete(textViewIDValor.text.toString(),namePointEdit.text.toString(), latitudePointEdit.text.toString().toDouble(),longitudePointEdit.text.toString().toDouble())
                var db = DataBase(context)
                db.editData(localPoint)

                Toast.makeText(context, "Local editado com sucesso!", Toast.LENGTH_SHORT)
            }else{
                Toast.makeText(context, "Por favor preencha os dados", Toast.LENGTH_SHORT)
            }

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //Botão de Delete, onde vai deletar no banco atraves do ID para
        // após retorna para tela do mapa
        buttonDelete.setOnClickListener {
                var db = DataBase(context)
                val delete = db.deleteID(textViewIDValor.text.toString())

            if(delete.toString() == "true"){
                Toast.makeText(context, "Deletado com sucesso!", Toast.LENGTH_SHORT)
            }else{
                Toast.makeText(context, "Ocorreu um erro ao deletar!", Toast.LENGTH_SHORT)
            }

            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)


        }
    }
}
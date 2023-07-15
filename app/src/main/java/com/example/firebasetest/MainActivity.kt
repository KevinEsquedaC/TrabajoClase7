package com.example.firebasetest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.sql.DriverManager.println

class MainActivity : AppCompatActivity() {
    // Implementar FirebaseAuth
    private lateinit var auth: FirebaseAuth
    // Implementar FirebaseDatabase
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        //getSeasons()
        setSeason("8")
        //loginAnonymous()
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null){
            if (currentUser.email != ""){
                Toast.makeText(this, "Sesión ya iniciada\nCorreo: "+currentUser.email, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Bienvenido anónimo", Toast.LENGTH_LONG).show()
            }
        }else{
            login("kaesquedac@gmail.com", "Saludos123")
        }
    }

    fun login(email : String, password : String){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){
                task ->
            if (task.isSuccessful){
                val user = auth.currentUser
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Error en el inicio de sesión: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun loginAnonymous(){
        auth.signInAnonymously().addOnCompleteListener{task ->
            if (task.isSuccessful){
                // Inicio se sesión anónimo exitoso
                val user = auth.currentUser
                // Acción con el usuario autenticado
                Toast.makeText(this, "Inicio de sesión anónimo exitoso",Toast.LENGTH_LONG).show()
            }else{
                // Error en el inicio de sesión anónimo
                Toast.makeText(this, "Error en el inicio de sesión anónimo: ${task.exception?.message}",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getSeasons(){
        val reference = database.getReference("seasons")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (seasonSnapshot in dataSnapshot.children){
                    val season = seasonSnapshot.getValue(Season::class.java)
                    val message = "{ name : ${season?.name} , description : ${season?.description}, status : ${season?.status}"
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError : DatabaseError){
                println("Error al leer los datos ${databaseError.message}")
            }
        })
    }

    fun setSeason(seasonID : String){
        val reference = database.getReference("seasons")
        val season = Season("Sesión prueba2", "Descripción prueba", false)

        reference.child(seasonID).setValue(season).addOnCompleteListener {
            Toast.makeText(this@MainActivity, "Se actualizo la base de datos", Toast.LENGTH_LONG).show()
        }
    }
}
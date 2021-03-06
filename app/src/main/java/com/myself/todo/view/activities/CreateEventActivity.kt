package com.myself.todo.view.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.myself.todo.R
import com.myself.todo.Utils.Utilities
import com.myself.todo.Utils.Utilities.Companion.fadeIn
import com.myself.todo.databinding.ActivityCreateEventBinding
import com.myself.todo.model.EventsDB
import com.myself.todo.model.beans.Events
import com.myself.todo.model.beans.Tarefas
import de.mateware.snacky.Snacky
import kotlinx.android.synthetic.main.activity_create_event.*
import java.text.SimpleDateFormat
import java.util.*


class CreateEventActivity : AppCompatActivity(),TextView.OnEditorActionListener {
    private val myevent = Events()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actbind: ActivityCreateEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_event)
        saveEvent.setOnClickListener {
            updatebackground()
            salvar()
        }
        eventName.setOnFocusChangeListener { v, hasFocus ->
            updatebackground()
        }
        tasksEditText.setOnFocusChangeListener { v, hasFocus ->
            updatebackground()
        }
        tasksEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addChipToGroup(v.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        background.setImageDrawable(getDrawable(currentback))
        setContentView(actbind.root)
    }


    private fun addChipToGroup(nome: String) {
        val tarefa = Tarefas(nome, Utilities.actualday(), false)

    }



    private fun signIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val providers = listOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build())
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setLogo(R.mipmap.ic_launcher)
                    .setAvailableProviders(providers)
                    .setTheme(R.style.AppTheme)
                    .build(), Utilities.RC_SIGN_IN)
        }
    }

    private fun actualday(): String {
        val datenow = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy")
        val dia = df.format(datenow)
        println(dia)
        return dia
    }
    private fun salvar(){
        myevent.evento = eventName.text.toString()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            if (myevent.evento.isNullOrBlank()) {
                Snacky.builder().setActivity(this).error().setText("Você precisa dar um nome a esse evento.").show()
                eventName.requestFocus()
                return
            }
            EventsDB(this).inserir(myevent)
        }else{
            Snacky.builder().setActivity(this).error().setText("Faça login para adicionar eventos.").setAction("OK") {
                signIn()
            }.show()
        }
    }

    private var currentback = Utilities.randombackground()

    private fun updatebackground() {
        currentback = Utilities.randombackground()
        background.setImageDrawable(getDrawable(currentback))
        fadeIn(background).subscribe()
    }


    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        salvar()
        return false
    }
}

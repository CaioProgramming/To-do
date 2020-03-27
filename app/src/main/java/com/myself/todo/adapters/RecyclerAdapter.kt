package com.myself.todo.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.myself.todo.Beans.Events
import com.myself.todo.R
import com.myself.todo.databinding.CardlayoutBinding
import com.myself.todo.model.EventsDB
import com.myself.todo.view.activities.CreateEventActivity

class RecyclerAdapter(val activity: Activity, var eventList: ArrayList<Events>) : RecyclerView.Adapter<RecyclerAdapter.EventsViewHolder>() {
    init {
        eventList.add(Events.createEvent())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {

        val cardlayoutBinding: CardlayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.cardlayout,parent,false)
        return EventsViewHolder(cardlayoutBinding)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = eventList[position]
        holder.cardlayoutBinding.mainlayout.visibility = if (event.isAcreateEvent()){
            GONE
        }else{
            VISIBLE
        }
        holder.cardlayoutBinding.addnewevent.visibility = if (event.isAcreateEvent()){
            GONE
        }else{
            VISIBLE
        }
        if (event.isAcreateEvent()) {
            holder.cardlayoutBinding.titulo.text = event.evento
            holder.cardlayoutBinding.eventcard.setOnLongClickListener {
                AlertDialog.Builder(activity)
                        .setTitle("Tem certeza")
                        .setMessage("Deseja remover essa atividade?")
                        .setPositiveButton("Remover", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                EventsDB(activity).remover(event.id!!)
                            }
                        })
                        .create()
                        .show()
                return@setOnLongClickListener false
            }
                holder.cardlayoutBinding.tasks.adapter = RecyclerTarefasAdapter(activity,event)
            if (event.favorite){
                holder.cardlayoutBinding.eventcard.setCardBackgroundColor(activity.resources.getColor(R.color.red_500))
            }

        }else{
            holder.cardlayoutBinding.eventcard.setOnClickListener {
                addnewevent()
            }
        }

    }


    private fun addnewevent(){
        val i = Intent(activity,CreateEventActivity::class.java)
        activity.startActivity(i)
    }



    override fun getItemCount(): Int {
        return if (eventList.size == 0) {
            1
        } else {
            eventList.size
        }
    }

    class EventsViewHolder(val cardlayoutBinding: CardlayoutBinding) : RecyclerView.ViewHolder(cardlayoutBinding.root)




}
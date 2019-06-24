package com.example.stendhal_1

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.stendhal_1.datamodel.database
import kotlinx.android.synthetic.main.fragment_periodi.*


class Periodo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_periodi, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Imposto il layout manager a lineare per avere scrolling in una direzione
        list_periodi.layoutManager = LinearLayoutManager(activity) //list_periodi in fragment_periodi

        // Associo l'adapter alla RecyclerView
        list_periodi.adapter = PeriodiAdapter(database.getElencoPeriodi(), requireContext())
    }

}

package com.example.stendhal_1

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.riga_periodo.view.*


class rigaperiodoviewholder(view: View) : RecyclerView.ViewHolder(view) {
    val tvNome = view.textNome
    val tvAnno = view.textAnno
}

/* La recycler view è una lista di oggetti viewholder -  qui li andiamo a definire
* Tali view holder object sono gestiti da un adapter*/
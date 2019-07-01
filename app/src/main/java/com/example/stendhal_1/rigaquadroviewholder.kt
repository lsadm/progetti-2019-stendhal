package com.example.stendhal_1

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.riga_quadro.view.*

class rigaquadroviewholder(view: View) : RecyclerView.ViewHolder(view) {
    val tvNome = view.textNome
    val tvAnno = view.textAnno
    val tvAutore = view.textAutore
}
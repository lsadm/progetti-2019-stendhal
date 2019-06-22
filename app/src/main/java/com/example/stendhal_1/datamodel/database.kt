package com.example.stendhal_1.datamodel


//tentativo si simulazione della lista di periodi nel fragment periodi
object database {
    //lista
    private var periodi = ArrayList<Periodo>()



    //inizializzazione
    init    {
        periodi.add(Periodo("Rinascimento","1500"))
        periodi.add(Periodo("Barocco","1600"))
    }



}
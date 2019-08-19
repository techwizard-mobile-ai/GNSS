package com.lysaan.malik.vsptracker.classes

import com.lysaan.malik.vsptracker.Helper
import java.io.Serializable

class ELoad : Serializable {

    private lateinit var helper: Helper
    var loadMachine: String = ""
    var time: String = ""

    constructor() {
    }

    constructor(loadMachine: String , time: String) {
        this.loadMachine = loadMachine
        this.time = time
    }

    override fun toString(): String {
        return "ELoad(loadMachine='$loadMachine', time=$time)"
    }


}

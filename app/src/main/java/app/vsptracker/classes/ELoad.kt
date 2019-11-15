package app.vsptracker.classes

import java.io.Serializable

class ELoad : Serializable {

    private var loadMachine: String = ""
    var time: String = ""

    constructor() {}

    constructor(loadMachine: String, time: String) {
        this.loadMachine = loadMachine
        this.time = time
    }

    override fun toString(): String {
        return "ELoad(loadMachine='$loadMachine', time=$time)"
    }


}

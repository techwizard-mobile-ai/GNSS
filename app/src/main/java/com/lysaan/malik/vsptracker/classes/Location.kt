package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class Location : Serializable {
    var id: Int = 0
    var name: String = ""

    constructor() {}

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    override fun toString(): String {
        return "Location(id=$id, name=$name)"
    }
}

package com.lysaan.malik.vsptracker.classes

import java.io.Serializable

class Material : Serializable {
    var id: Int = 0
    var name: String = ""

    constructor() {}

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    override fun toString(): String {
        return "Material(id=$id, name=$name)"
    }
}

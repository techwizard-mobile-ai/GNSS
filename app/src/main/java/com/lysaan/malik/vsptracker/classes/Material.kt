package com.lysaan.malik.vsptracker.classes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Material : Serializable {

    @SerializedName ("id")
    var id: Int = 0

    @SerializedName("org_id")
    var orgID:Int = 0


    @SerializedName ("type")
    var type: Int = 0


    var isDeleted:Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("number")
    var number: String = ""

    constructor() {}

    constructor(id: Int, name: String) {
        this.id = id
        this.name = name
    }

    override fun toString(): String {
        return "Material(id=$id, orgID=$orgID, type=$type, isDeleted=$isDeleted, name='$name', number='$number')"
    }


}

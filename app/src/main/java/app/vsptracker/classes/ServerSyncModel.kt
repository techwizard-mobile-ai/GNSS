package app.vsptracker.classes

import java.io.Serializable

class ServerSyncModel (var name:String, var total:Int, var synced:Int, var remaining:Int): Serializable {

override fun toString(): String {
        return "ServerSyncModel(" +
                "name='$name', " +
                "total=$total, " +
                "synced=$synced, " +
                "remaining=$remaining" +
                ")"
    }


}
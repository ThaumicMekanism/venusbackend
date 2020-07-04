package venusbackend.utils

data class SocketMessage(var v: String, var cmd: String, var data: String="") {
    companion object {
        fun parse(data: MutableMap<String, String>): SocketMessage {
            return SocketMessage(
                v = data["v"] as String,
                cmd = data["cmd"] as String,
                data = data["data"] as String
            )
        }
    }

    fun mutableMap(): MutableMap<String, String> {
        return mutableMapOf<String, String>(Pair("v", v), Pair("cmd", cmd), Pair("data", data))
    }
}
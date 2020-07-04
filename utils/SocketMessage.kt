package venusbackend.utils

data class SocketMessage(var v: String, var cmd: String, var data: Any = "") {
    companion object {
        fun parse(data: Map<String, Any>): SocketMessage {
            return SocketMessage(
                v = data["v"] as String,
                cmd = data["cmd"] as String,
                data = data["data"] ?: ""
            )
        }
    }

    fun mutableMap(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>(Pair("v", v), Pair("cmd", cmd), Pair("data", data))
    }
}
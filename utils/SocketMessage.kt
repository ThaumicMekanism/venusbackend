package venusbackend.utils

data class SocketMessage(var v: String, var cmd: String, var data: Any = "", var id: Int = 0) {
    companion object {
        fun parse(dat: Map<String, Any>): SocketMessage {
            return SocketMessage(
                v = dat["v"] as String,
                cmd = dat["cmd"] as String,
                data = dat["data"] ?: "",
                id = dat["id"] as Int
            )
        }
    }
}
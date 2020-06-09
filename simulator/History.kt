package venusbackend.simulator

/* TODO: add a way to have a limit (maybe a deque?) */
class History (var limit: Int = 10000) {
    private val diffs = ArrayList<List<Diff>>()

    fun add(pre: List<Diff>) {
        if (this.limit > 0 && (this.diffs.size > this.limit)){
            while (this.diffs.size > this.limit) {
                diffs.removeAt(0);
            }
        }
        diffs.add(pre.toList())
    }
    fun pop() = diffs.removeAt(diffs.size - 1)
    fun isEmpty() = diffs.isEmpty()
}
package venusbackend.riscv

object Registers {
    val zero = 0
    val ra = 1
    val sp = 2
    val gp = 3
    val tp = 4
    val t0 = 5
    val t1 = 6
    val t2 = 7
    val s0 = 8
    val fp = 8
    val s1 = 9
    val a0 = 10
    val a1 = 11
    val a2 = 12
    val a3 = 13
    val a4 = 14
    val a5 = 15
    val a6 = 16
    val a7 = 17
    val s2 = 18
    val s3 = 19
    val s4 = 20
    val s5 = 21
    val s6 = 22
    val s7 = 23
    val s8 = 24
    val s9 = 25
    val s10 = 26
    val s11 = 27
    val t3 = 28
    val t4 = 29
    val t5 = 30
    val t6 = 31

    val ft0 = 0
    val ft1 = 1
    val ft2 = 2
    val ft3 = 3
    val ft4 = 4
    val ft5 = 5
    val ft6 = 6
    val ft7 = 7
    val fs0 = 8
    val fs1 = 9
    val fa0 = 10
    val fa1 = 11
    val fa2 = 12
    val fa3 = 13
    val fa4 = 14
    val fa5 = 15
    val fa6 = 16
    val fa7 = 17
    val fs2 = 18
    val fs3 = 19
    val fs4 = 20
    val fs5 = 21
    val fs6 = 22
    val fs7 = 23
    val fs8 = 24
    val fs9 = 25
    val fs10 = 26
    val fs11 = 27
    val ft8 = 28
    val ft9 = 29
    val ft10 = 30
    val ft11 = 31
}

fun getRegNameFromIndex(regid: Int, integer: Boolean = true): String {
    if (integer) {
        return when (regid) {
            0 -> "zero"
            1 -> "ra"
            2 -> "sp"
            3 -> "gp"
            4 -> "tp"
            5 -> "t0"
            6 -> "t1"
            7 -> "t2"
            8 -> "s0"
            9 -> "s1"
            10 -> "a0"
            11 -> "a1"
            12 -> "a2"
            13 -> "a3"
            14 -> "a4"
            15 -> "a5"
            16 -> "a6"
            17 -> "a7"
            18 -> "s2"
            19 -> "s3"
            20 -> "s4"
            21 -> "s5"
            22 -> "s6"
            23 -> "s7"
            24 -> "s8"
            25 -> "s9"
            26 -> "s10"
            27 -> "s11"
            28 -> "t3"
            29 -> "t4"
            30 -> "t5"
            31 -> "t6"

            else -> "UNKNOWN REGISTER $regid!"
        }
    }
    return "Not supported"
}
package venusbackend.riscv.insts.floating.double.r

import venusbackend.riscv.insts.dsl.floating.FclassRTypeInstruction

/*TODO fix so this is better.*/
val fclassd = FclassRTypeInstruction(
        name = "fclass.d",
        opcode = 0b1010011,
        funct3 = 0b001,
        funct7 = 0b1110000,
        eval32 = { a, b ->
            var bits = 0b0
            if (a.getCurrentDouble() == Double.NEGATIVE_INFINITY) bits = bits or 0b0000000001
            if (a.getCurrentDouble() < 0 && a.getCurrentDouble() >= (-1.17549435e-38).toFloat()) bits = bits or 0b0000000010
            if (a.getCurrentDouble() < 0 && a.getCurrentDouble() < (-1.17549435e-38).toFloat()) bits = bits or 0b0000000100
            if (a.getCurrentDouble().toRawBits() == 0L) bits = bits or 0b0000001000
            if (a.getCurrentDouble().toRawBits() == 0x800000000000000L) bits = bits or 0b0000010000
            if (a.getCurrentDouble() > 0 && a.getCurrentDouble() >= (1.17549435e-38).toFloat()) bits = bits or 0b0000100000
            if (a.getCurrentDouble() > 0 && a.getCurrentDouble() < (1.17549435e-38).toFloat()) bits = bits or 0b0001000000
            if (a.getCurrentDouble() == Double.POSITIVE_INFINITY) bits = bits or 0b0010000000
            if (Double.NaN.equals(a.getCurrentDouble())) bits = bits or 0b0100000000
            if (Double.NaN.equals(a.getCurrentDouble())) bits = bits or 0b1000000000
            bits
        }
)
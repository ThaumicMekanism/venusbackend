// package venusbackend
//
// import venus.vfs.VirtualFileSystem
// import venusbackend.assembler.Assembler
// import kotlin.test.Test
// import kotlin.test.assertEquals
// import venusbackend.linker.Linker
// import venusbackend.linker.ProgramAndLibraries
// import venusbackend.simulator.Simulator
// import venusbackend.simulator.SimulatorState32
//
// @Test
// fun test(){
//    val (prog, _) = Assembler.assemble(
//            """
//                    li s0 0xF0EFF0EF
//                    li s1 0x10101010
//                    vaddh x1 s0 s1
// """
//    )
//    val PandL = ProgramAndLibraries(listOf(prog), VirtualFileSystem("dummy"))
//    val linked = Linker.link(PandL)
//    val sim = Simulator(linked, state = SimulatorState32())
//    sim.run()
//    assertEquals(0x00FF00FF, sim.getReg(1).toInt())
// }
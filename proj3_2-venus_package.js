'use strict';
/**
 * Author: Stephan Kaminsky <skaminsky115@berkeley>
 * Date: 6/17/2019 9:31PM
 * Title: CS61C Summer 2019 Project3-2 Additional Instructions
 * Description: Adds the additional instructions vaddh to Venus.
 */
var venuspackage = {
  id: "CS61C SU19 proj3-2 Additional Instructions",
  requires: undefined,
  load: function(setting) {
      window.cs61c_sp19_proj3_2_vaddh = window.eval_in_venus_env(`
          function vaddh$lambda(mcode, sim) {
            var rs1 = numberToInt(sim.getReg_za3lpa$(mcode.get_cv51c2$(InstructionField$RS1_getInstance())));
            var rs2 = numberToInt(sim.getReg_za3lpa$(mcode.get_cv51c2$(InstructionField$RS2_getInstance())));
            var upper = toShort(rs1 >> 16) + toShort(rs2 >> 16);
            var lower = toShort(toShort(rs1) + toShort(rs2));
            var rd = lower << 16 | upper;
            sim.setReg_135bro$(mcode.get_cv51c2$(InstructionField$RD_getInstance()), rd);
            sim.incrementPC_3p81yu$(mcode.length);
            return Unit;
          }
          var vaddh;
          vaddh = new Instruction('vaddh', new RTypeFormat(51, 0, 15), RTypeParser_getInstance(), NoImplementation_getInstance(), new RawImplementation(vaddh$lambda), NoImplementation_getInstance(), NoImplementation_getInstance(), RTypeDisassembler_getInstance());
        `);
      codeMirror_riscv_styles[this.id] = [regexFromWords(["vaddh", "i"]), "builtin"];
      try {
        codeMirror.performLint();
      } catch(e) {}
  },
  unload: function(setting) {
      eval_in_venus_env("Instruction.Companion.allInstructions_0.remove_11rb$(window.cs61c_su19_proj3_2_vaddh)");
      delete codeMirror_riscv_styles[this.id];
      try {
        codeMirror.performLint();
      } catch(e) {}
  }
};
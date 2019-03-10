'use strict';
/**
 * Author: Stephan Kaminsky <skaminsky115@berkeley>
 * Date: 3/10/2019 12:41AM
 * Title: CS61C Spring 2019 Project3-2 Additional Instructions
 * Description: Adds the additional instructions lwc, push, and pop to Venus.
 */
var venuspackage = {
  id: "CS61C SP19 proj3-2 Additional Instructions",
  requires: undefined,
  load: function(setting) {
      window.cs61c_sp19_proj3_2_lwc = window.eval_in_venus_env(`
        function lwc$lambda(prog, mcode, args, dbg) {
          checkArgsLength_0(args.size, 4);
          mcode.set_d3qz0$(InstructionField$RD_getInstance(), regNameToNumber(args.get_za3lpa$(0)));
          mcode.set_d3qz0$(InstructionField$FUNCT7_getInstance(), getImmediate(args.get_za3lpa$(1), -64, 63));
          mcode.set_d3qz0$(InstructionField$RS1_getInstance(), regNameToNumber(args.get_za3lpa$(2)));
          mcode.set_d3qz0$(InstructionField$RS2_getInstance(), regNameToNumber(args.get_za3lpa$(3)));
          return Unit;
        }
        function lwc$lambda_0(mcode, sim) {
          var rd = mcode.get_cv51c2$(InstructionField$RD_getInstance());
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var rs2 = mcode.get_cv51c2$(InstructionField$RS2_getInstance());
          var funct7 = mcode.get_cv51c2$(InstructionField$FUNCT7_getInstance());
          var vrs1 = numberToInt(sim.getReg_za3lpa$(rs1));
          var vrs2 = numberToInt(sim.getReg_za3lpa$(rs2));
          if (vrs2 !== 0) {
            var addr = funct7 + vrs1 | 0;
            var m = sim.loadWordwCache_3p81yu$(addr);
            sim.setReg_135bro$(rd, m);
          }
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function lwc$lambda_1(mcode, sim) {
          var rd = mcode.get_cv51c2$(InstructionField$RD_getInstance());
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var rs2 = mcode.get_cv51c2$(InstructionField$RS2_getInstance());
          var funct7 = mcode.get_cv51c2$(InstructionField$FUNCT7_getInstance());
          var vrs1 = numberToLong(sim.getReg_za3lpa$(rs1));
          var vrs2 = numberToLong(sim.getReg_za3lpa$(rs2));
          if (!equals(vrs2, L0)) {
            var addr = Kotlin.Long.fromInt(funct7).add(vrs1);
            var m = sim.loadWordwCache_3p81yu$(addr);
            sim.setReg_135bro$(rd, Kotlin.Long.fromInt(m));
          }
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function lwc$lambda_2(mcode, sim) {
          var rd = mcode.get_cv51c2$(InstructionField$RD_getInstance());
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var rs2 = mcode.get_cv51c2$(InstructionField$RS2_getInstance());
          var funct7 = toQuadWord_2(mcode.get_cv51c2$(InstructionField$FUNCT7_getInstance()));
          var vrs1 = toQuadWord(sim.getReg_za3lpa$(rs1));
          var vrs2 = toQuadWord(sim.getReg_za3lpa$(rs2));
          if (!equals(vrs2, toQuadWord_2(0))) {
            var addr = funct7.plus_k7dsfo$(vrs1);
            var m = sim.loadWordwCache_3p81yu$(addr);
            sim.setReg_135bro$(rd, toQuadWord_2(m));
          }
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function lwc$lambda_3(mcode) {
          var rd = mcode.get_cv51c2$(InstructionField$RD_getInstance());
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var rs2 = mcode.get_cv51c2$(InstructionField$RS2_getInstance());
          var funct7 = mcode.get_cv51c2$(InstructionField$FUNCT7_getInstance());
          return 'lwc x' + rd + ' ' + funct7 + '(x' + rs1 + ') x' + rs2;
        }
        var lwc;
        lwc = new Instruction('lwc', new OpcodeFunct3Format(59, 2), new RawParser(lwc$lambda), NoImplementation_getInstance(), new RawImplementation(lwc$lambda_0), new RawImplementation(lwc$lambda_1), new RawImplementation(lwc$lambda_2), new RawDisassembler(lwc$lambda_3));
      `);
      window.cs61c_sp19_proj3_2_push = window.eval_in_venus_env(`
        function push$lambda(prog, mcode, args, dbg) {
          checkArgsLength_0(args.size, 1);
          mcode.set_d3qz0$(InstructionField$RS1_getInstance(), regNameToNumber(args.get_za3lpa$(0)));
          return Unit;
        }
        function push$lambda_0(mcode, sim) {
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var vrs1 = numberToInt(sim.getReg_za3lpa$(rs1));
          var vsp = numberToInt(sim.getReg_za3lpa$(Registers_getInstance().sp));
          vsp = vsp - 4 | 0;
          sim.setReg_135bro$(Registers_getInstance().sp, vsp);
          sim.storeWordwCache_z8e4lc$(vsp, vrs1);
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function push$lambda_1(mcode, sim) {
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var vrs1 = numberToLong(sim.getReg_za3lpa$(rs1));
          var vsp = numberToLong(sim.getReg_za3lpa$(Registers_getInstance().sp));
          vsp = vsp.subtract(Kotlin.Long.fromInt(4));
          sim.setReg_135bro$(Registers_getInstance().sp, vsp);
          sim.storeWordwCache_z8e4lc$(vsp, vrs1);
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function push$lambda_2(mcode, sim) {
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          var vrs1 = toQuadWord(sim.getReg_za3lpa$(rs1));
          var vsp = toQuadWord(sim.getReg_za3lpa$(Registers_getInstance().sp));
          vsp = vsp.minus_za3lpa$(4);
          sim.setReg_135bro$(Registers_getInstance().sp, vsp);
          sim.storeWordwCache_z8e4lc$(vsp, vrs1);
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function push$lambda_3(mcode) {
          var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
          return 'push x' + rs1;
        }
        var push;
        push = new Instruction('push', new OpcodeFunct3Format(59, 3), new RawParser(push$lambda), NoImplementation_getInstance(), new RawImplementation(push$lambda_0), new RawImplementation(push$lambda_1), new RawImplementation(push$lambda_2), new RawDisassembler(push$lambda_3));
      `);
      window.cs61c_sp19_proj3_2_pop = window.eval_in_venus_env(`
        function pop$lambda(prog, mcode, args, dbg) {
          checkArgsLength_0(args.size, 0);
          return Unit;
        }
        function pop$lambda_0(mcode, sim) {
          var sp = numberToInt(sim.getReg_za3lpa$(Registers_getInstance().sp));
          sim.setReg_135bro$(Registers_getInstance().sp, sp + 4 | 0);
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function pop$lambda_1(mcode, sim) {
          var sp = numberToLong(sim.getReg_za3lpa$(Registers_getInstance().sp));
          sim.setReg_135bro$(Registers_getInstance().sp, sp.add(Kotlin.Long.fromInt(8)));
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function pop$lambda_2(mcode, sim) {
          var sp = toQuadWord(sim.getReg_za3lpa$(Registers_getInstance().sp));
          sim.setReg_135bro$(Registers_getInstance().sp, sp.plus_za3lpa$(16));
          sim.incrementPC_3p81yu$(mcode.length);
          return Unit;
        }
        function pop$lambda_3(mcode) {
          return 'pop';
        }
        var pop;
        pop = new Instruction('pop', new OpcodeFunct3Format(59, 4), new RawParser(pop$lambda), NoImplementation_getInstance(), new RawImplementation(pop$lambda_0), new RawImplementation(pop$lambda_1), new RawImplementation(pop$lambda_2), new RawDisassembler(pop$lambda_3));
      `);
      codeMirror.performLint();
  },
  unload: function(setting) {
      eval_in_venus_env("Instruction.Companion.allInstructions_0.remove_11rb$(window.cs61c_sp19_proj3_2_lwc)");
      eval_in_venus_env("Instruction.Companion.allInstructions_0.remove_11rb$(window.cs61c_sp19_proj3_2_push)");
      eval_in_venus_env("Instruction.Companion.allInstructions_0.remove_11rb$(window.cs61c_sp19_proj3_2_pop)");
      codeMirror.performLint();
  }
};
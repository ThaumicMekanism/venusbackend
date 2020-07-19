'use strict';
/**
 * Author: Stephan Kaminsky <skaminsky115@berkeley>
 * Date: 7/18/2019 6:01PM
 * Title: CS61C Summer 2020 Project3-2 Additional Instructions
 * Description: Adds the additional instructions swlt to Venus.
 */
var venuspackage = {
    id: "CS61C SU20 proj3-2 Additional Instructions",
    requires: undefined,
    load: function(setting) {
        window.cs61c_su20_proj3_2_swlt = window.eval_in_venus_env(`
            function swlt$lambda(prog, mcode, args, dbg) {
            checkArgsLength_0(args.size, 3, dbg);
            var rs1 = args.get_za3lpa$(2);
            if (startsWith(rs1, '(') && endsWith(rs1, ')')) {
              var $receiver = rs1;
              var endIndex = rs1.length - 1 | 0;
              rs1 = $receiver.substring(1, endIndex);
            }var imm = prog.getImmediate_druo6x$(args.get_za3lpa$(1), -2048, 2047, dbg);
            mcode.set_d3qz0$(InstructionField$RS1_getInstance(), regNameToNumber(rs1, void 0, dbg));
            mcode.set_d3qz0$(InstructionField$RS2_getInstance(), regNameToNumber(args.get_za3lpa$(0), void 0, dbg));
            mcode.set_d3qz0$(InstructionField$IMM_4_0_getInstance(), imm);
            mcode.set_d3qz0$(InstructionField$IMM_11_5_getInstance(), imm >> 5);
            return Unit;
            }
            function swlt$lambda_0(mcode, sim) {
            var rs1 = mcode.get_cv51c2$(InstructionField$RS1_getInstance());
            var rs2 = mcode.get_cv51c2$(InstructionField$RS2_getInstance());
            var imm = constructStoreImmediate(mcode);
            var vrs1 = numberToInt(sim.getReg_za3lpa$(rs1));
            var vrs2 = numberToInt(sim.getReg_za3lpa$(rs2));
            if (vrs2 < imm) {
              var address = vrs1;
              var value = vrs2;
              sim.storeWordwCache_z8e4lc$(address, value);
            }sim.incrementPC_3p81yu$(mcode.length);
            return Unit;
            }
            var swlt;
            swlt = new Instruction('swlt', new STypeFormat(35, 7), new RawParser(swlt$lambda), NoImplementation_getInstance(), new RawImplementation(swlt$lambda_0), NoImplementation_getInstance(), NoImplementation_getInstance(), STypeDisassembler_getInstance());
            
            var PseudoDispatcher$swlt_instance;
              function PseudoDispatcher$swlt_getInstance() {
                PseudoDispatcher_initFields();
                return PseudoDispatcher$swlt_instance;
            }
        `);
        codeMirror_riscv_styles[this.id] = [regexFromWords(["swlt", "i"]), "builtin"];
        try {
            codeMirror.performLint();
        } catch(e) {}
    },
    unload: function(setting) {
        eval_in_venus_env("Instruction.Companion.allInstructions_0.remove_11rb$(window.cs61c_su20_proj3_2_swlt)");
        delete codeMirror_riscv_styles[this.id];
        try {
            codeMirror.performLint();
        } catch(e) {}
    }
};
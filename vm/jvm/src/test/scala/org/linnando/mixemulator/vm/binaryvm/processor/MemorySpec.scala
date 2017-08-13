package org.linnando.mixemulator.vm.binaryvm.processor

import org.linnando.mixemulator.vm.binary
import org.linnando.mixemulator.vm.exceptions.OverflowException
import org.specs2.mutable.Specification

class MemorySpec extends Specification {
  import binary._
  private val initialState = binary.initialState
  private val state = initialState.copy(
    registers = initialState.registers
      .updatedA(MixWord(0x061c8240))  // + 6 7 8 9 0
      .updatedX(MixWord(0x461c8240))  // - 6 7 8 9 0
      .updatedI(1, MixIndex(0x0240))  // + 9 0
      .updatedI(2, MixIndex(0x1240))  // - 9 0
      .updatedI(3, MixIndex(0x0240))  // + 9 0
      .updatedI(4, MixIndex(0x0240))  // + 9 0
      .updatedI(5, MixIndex(0x0240))  // + 9 0
      .updatedI(6, MixIndex(0x0240))  // + 9 0
      .updatedJ(MixIndex(0x0bb8)),
    memory = initialState.memory
      .updated(MixIndex(2000), MixWord(0x41403144)) // - 1 16 3 5 4
      .updated(MixIndex(2001), MixWord(0x41083105)) // - 1 2 3 4 5
  )

  "binary memory module" should {
    "load register A from memory" in {
      // A = 2000, I = 0, F = 0:5, C = 8 LDA
      execute(state, MixWord(0x1f400148)).registers.getA must be equalTo MixWord(0x41403144)
      // A = 2000, I = 0, F = 1:5, C = 8 LDA
      execute(state, MixWord(0x1f400348)).registers.getA must be equalTo MixWord(0x01403144)
      // A = 2000, I = 0, F = 3:5, C = 8 LDA
      execute(state, MixWord(0x1f400748)).registers.getA must be equalTo MixWord(0x00003144)
      // A = 2000, I = 0, F = 0:3, C = 8 LDA
      execute(state, MixWord(0x1f4000c8)).registers.getA must be equalTo MixWord(0x40001403)
      // A = 2000, I = 0, F = 4:4, C = 8 LDA
      execute(state, MixWord(0x1f400908)).registers.getA must be equalTo MixWord(0x00000005)
      // A = 2000, I = 0, F = 0:0, C = 8 LDA
      execute(state, MixWord(0x1f400008)).registers.getA must be equalTo MixWord(0x40000000)
      // A = 2000, I = 0, F = 1:1, C = 8 LDA
      execute(state, MixWord(0x1f400248)).registers.getA must be equalTo MixWord(0x00000001)
    }

    "load register I1 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 9 LD1
      execute(state, MixWord(0x1f400089)).registers.getI(1) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 9 LD1
      execute(state, MixWord(0x1f400909)).registers.getI(1) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 9 LD1
      execute(state, MixWord(0x1f400009)).registers.getI(1) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 9 LD1
      execute(state, MixWord(0x1f400249)).registers.getI(1) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 9 LD1
      execute(state, MixWord(0x1f400149)) must throwAn[OverflowException]
    }

    "load register I2 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 10 LD2
      execute(state, MixWord(0x1f40008a)).registers.getI(2) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 10 LD2
      execute(state, MixWord(0x1f40090a)).registers.getI(2) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 10 LD2
      execute(state, MixWord(0x1f40000a)).registers.getI(2) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 10 LD2
      execute(state, MixWord(0x1f40024a)).registers.getI(2) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 10 LD2
      execute(state, MixWord(0x1f40014a)) must throwAn[OverflowException]
    }

    "load register I3 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 11 LD3
      execute(state, MixWord(0x1f40008b)).registers.getI(3) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 11 LD3
      execute(state, MixWord(0x1f40090b)).registers.getI(3) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 11 LD3
      execute(state, MixWord(0x1f40000b)).registers.getI(3) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 11 LD3
      execute(state, MixWord(0x1f40024b)).registers.getI(3) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 11 LD3
      execute(state, MixWord(0x1f40014b)) must throwAn[OverflowException]
    }

    "load register I4 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 12 LD4
      execute(state, MixWord(0x1f40008c)).registers.getI(4) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 12 LD4
      execute(state, MixWord(0x1f40090c)).registers.getI(4) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 12 LD4
      execute(state, MixWord(0x1f40000c)).registers.getI(4) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 12 LD4
      execute(state, MixWord(0x1f40024c)).registers.getI(4) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 12 LD4
      execute(state, MixWord(0x1f40014c)) must throwAn[OverflowException]
    }

    "load register I5 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 13 LD5
      execute(state, MixWord(0x1f40008d)).registers.getI(5) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 13 LD5
      execute(state, MixWord(0x1f40090d)).registers.getI(5) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 13 LD5
      execute(state, MixWord(0x1f40000d)).registers.getI(5) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 13 LD5
      execute(state, MixWord(0x1f40024d)).registers.getI(5) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 13 LD5
      execute(state, MixWord(0x1f40014d)) must throwAn[OverflowException]
    }

    "load register I6 from memory" in {
      // A = 2000, I = 0, F = 0:2, C = 14 LD6
      execute(state, MixWord(0x1f40008e)).registers.getI(6) must be equalTo MixIndex(0x1050)
      // A = 2000, I = 0, F = 4:4, C = 14 LD6
      execute(state, MixWord(0x1f40090e)).registers.getI(6) must be equalTo MixIndex(0x0005)
      // A = 2000, I = 0, F = 0:0, C = 14 LD6
      execute(state, MixWord(0x1f40000e)).registers.getI(6) must be equalTo MixIndex(0x1000)
      // A = 2000, I = 0, F = 1:1, C = 14 LD6
      execute(state, MixWord(0x1f40024e)).registers.getI(6) must be equalTo MixIndex(0x0001)
      // A = 2000, I = 0, F = 0:5, C = 14 LD6
      execute(state, MixWord(0x1f40014e)) must throwAn[OverflowException]
    }

    "load register X from memory" in {
      // A = 2000, I = 0, F = 0:5, C = 15 LDX
      execute(state, MixWord(0x1f40014f)).registers.getX must be equalTo MixWord(0x41403144)
      // A = 2000, I = 0, F = 1:5, C = 15 LDX
      execute(state, MixWord(0x1f40034f)).registers.getX must be equalTo MixWord(0x01403144)
      // A = 2000, I = 0, F = 3:5, C = 15 LDX
      execute(state, MixWord(0x1f40074f)).registers.getX must be equalTo MixWord(0x00003144)
      // A = 2000, I = 0, F = 0:3, C = 15 LDX
      execute(state, MixWord(0x1f4000cf)).registers.getX must be equalTo MixWord(0x40001403)
      // A = 2000, I = 0, F = 4:4, C = 15 LDX
      execute(state, MixWord(0x1f40090f)).registers.getX must be equalTo MixWord(0x00000005)
      // A = 2000, I = 0, F = 0:0, C = 15 LDX
      execute(state, MixWord(0x1f40000f)).registers.getX must be equalTo MixWord(0x40000000)
      // A = 2000, I = 0, F = 1:1, C = 15 LDX
      execute(state, MixWord(0x1f40024f)).registers.getX must be equalTo MixWord(0x00000001)
    }

    "load register A from memory with negation" in {
      // A = 2000, I = 0, F = 0:5, C = 16 LDAN
      execute(state, MixWord(0x1f400150)).registers.getA must be equalTo MixWord(0x01403144)
      // A = 2000, I = 0, F = 1:5, C = 16 LDAN
      execute(state, MixWord(0x1f400350)).registers.getA must be equalTo MixWord(0x41403144)
      // A = 2000, I = 0, F = 3:5, C = 16 LDAN
      execute(state, MixWord(0x1f400750)).registers.getA must be equalTo MixWord(0x40003144)
      // A = 2000, I = 0, F = 0:3, C = 16 LDAN
      execute(state, MixWord(0x1f4000d0)).registers.getA must be equalTo MixWord(0x00001403)
      // A = 2000, I = 0, F = 4:4, C = 16 LDAN
      execute(state, MixWord(0x1f400910)).registers.getA must be equalTo MixWord(0x40000005)
      // A = 2000, I = 0, F = 0:0, C = 16 LDAN
      execute(state, MixWord(0x1f400010)).registers.getA must be equalTo MixWord(0x00000000)
      // A = 2000, I = 0, F = 1:1, C = 16 LDAN
      execute(state, MixWord(0x1f400250)).registers.getA must be equalTo MixWord(0x40000001)
    }

    "load register I1 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 17 LD1N
      execute(state, MixWord(0x1f400091)).registers.getI(1) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 17 LD1N
      execute(state, MixWord(0x1f400911)).registers.getI(1) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 17 LD1N
      execute(state, MixWord(0x1f400011)).registers.getI(1) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 17 LD1N
      execute(state, MixWord(0x1f400251)).registers.getI(1) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 17 LD1N
      execute(state, MixWord(0x1f400151)) must throwAn[OverflowException]
    }

    "load register I2 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 18 LD2N
      execute(state, MixWord(0x1f400092)).registers.getI(2) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 18 LD2N
      execute(state, MixWord(0x1f400912)).registers.getI(2) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 18 LD2N
      execute(state, MixWord(0x1f400012)).registers.getI(2) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 18 LD2N
      execute(state, MixWord(0x1f400252)).registers.getI(2) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 18 LD2N
      execute(state, MixWord(0x1f400152)) must throwAn[OverflowException]
    }

    "load register I3 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 19 LD3N
      execute(state, MixWord(0x1f400093)).registers.getI(3) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 19 LD3N
      execute(state, MixWord(0x1f400913)).registers.getI(3) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 19 LD3N
      execute(state, MixWord(0x1f400013)).registers.getI(3) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 19 LD3N
      execute(state, MixWord(0x1f400253)).registers.getI(3) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 19 LD3N
      execute(state, MixWord(0x1f400153)) must throwAn[OverflowException]
    }

    "load register I4 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 20 LD4N
      execute(state, MixWord(0x1f400094)).registers.getI(4) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 20 LD4N
      execute(state, MixWord(0x1f400914)).registers.getI(4) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 20 LD4N
      execute(state, MixWord(0x1f400014)).registers.getI(4) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 20 LD4N
      execute(state, MixWord(0x1f400254)).registers.getI(4) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 20 LD4N
      execute(state, MixWord(0x1f400154)) must throwAn[OverflowException]
    }

    "load register I5 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 21 LD5N
      execute(state, MixWord(0x1f400095)).registers.getI(5) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 21 LD5N
      execute(state, MixWord(0x1f400915)).registers.getI(5) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 21 LD5N
      execute(state, MixWord(0x1f400015)).registers.getI(5) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 21 LD5N
      execute(state, MixWord(0x1f400255)).registers.getI(5) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 21 LD5N
      execute(state, MixWord(0x1f400155)) must throwAn[OverflowException]
    }

    "load register I6 from memory with negation" in {
      // A = 2000, I = 0, F = 0:2, C = 22 LD6N
      execute(state, MixWord(0x1f400096)).registers.getI(6) must be equalTo MixIndex(0x0050)
      // A = 2000, I = 0, F = 4:4, C = 22 LD6N
      execute(state, MixWord(0x1f400916)).registers.getI(6) must be equalTo MixIndex(0x1005)
      // A = 2000, I = 0, F = 0:0, C = 22 LD6N
      execute(state, MixWord(0x1f400016)).registers.getI(6) must be equalTo MixIndex(0x0000)
      // A = 2000, I = 0, F = 1:1, C = 22 LD6N
      execute(state, MixWord(0x1f400256)).registers.getI(6) must be equalTo MixIndex(0x1001)
      // A = 2000, I = 0, F = 0:5, C = 22 LD6N
      execute(state, MixWord(0x1f400156)) must throwAn[OverflowException]
    }

    "load register X from memory with negation" in {
      // A = 2000, I = 0, F = 0:5, C = 23 LDXN
      execute(state, MixWord(0x1f400157)).registers.getX must be equalTo MixWord(0x01403144)
      // A = 2000, I = 0, F = 1:5, C = 23 LDXN
      execute(state, MixWord(0x1f400357)).registers.getX must be equalTo MixWord(0x41403144)
      // A = 2000, I = 0, F = 3:5, C = 23 LDXN
      execute(state, MixWord(0x1f400757)).registers.getX must be equalTo MixWord(0x40003144)
      // A = 2000, I = 0, F = 0:3, C = 23 LDXN
      execute(state, MixWord(0x1f4000d7)).registers.getX must be equalTo MixWord(0x00001403)
      // A = 2000, I = 0, F = 4:4, C = 23 LDXN
      execute(state, MixWord(0x1f400917)).registers.getX must be equalTo MixWord(0x40000005)
      // A = 2000, I = 0, F = 0:0, C = 23 LDXN
      execute(state, MixWord(0x1f400017)).registers.getX must be equalTo MixWord(0x00000000)
      // A = 2000, I = 0, F = 1:1, C = 23 LDXN
      execute(state, MixWord(0x1f400257)).registers.getX must be equalTo MixWord(0x40000001)
    }

    "store register A to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 24 STA
      execute(state, MixWord(0x1f440158)).memory.get(2001.toShort) must be equalTo MixWord(0x061c8240)
      // A = 2001, I = 0, F = 1:5, C = 24 STA
      execute(state, MixWord(0x1f440358)).memory.get(2001.toShort) must be equalTo MixWord(0x461c8240)
      // A = 2001, I = 0, F = 5:5, C = 24 STA
      execute(state, MixWord(0x1f440b58)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 24 STA
      execute(state, MixWord(0x1f440498)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 24 STA
      execute(state, MixWord(0x1f4404d8)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 24 STA
      execute(state, MixWord(0x1f440058)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register I1 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 25 ST1
      execute(state, MixWord(0x1f440159)).memory.get(2001.toShort) must be equalTo MixWord(0x00000240)
      // A = 2001, I = 0, F = 1:5, C = 25 ST1
      execute(state, MixWord(0x1f440359)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 25 ST1
      execute(state, MixWord(0x1f440b59)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 25 ST1
      execute(state, MixWord(0x1f440499)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 25 ST1
      execute(state, MixWord(0x1f4404d9)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 25 ST1
      execute(state, MixWord(0x1f440059)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register I2 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 26 ST2
      execute(state, MixWord(0x1f44015a)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 1:5, C = 26 ST2
      execute(state, MixWord(0x1f44035a)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 26 ST2
      execute(state, MixWord(0x1f440b5a)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 26 ST2
      execute(state, MixWord(0x1f44049a)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 26 ST2
      execute(state, MixWord(0x1f4404da)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 26 ST2
      execute(state, MixWord(0x1f44005a)).memory.get(2001.toShort) must be equalTo MixWord(0x40083105)
    }

    "store register I3 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 27 ST3
      execute(state, MixWord(0x1f44015b)).memory.get(2001.toShort) must be equalTo MixWord(0x00000240)
      // A = 2001, I = 0, F = 1:5, C = 27 ST3
      execute(state, MixWord(0x1f44035b)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 27 ST3
      execute(state, MixWord(0x1f440b5b)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 27 ST3
      execute(state, MixWord(0x1f44049b)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 27 ST3
      execute(state, MixWord(0x1f4404db)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 27 ST3
      execute(state, MixWord(0x1f44005b)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register I4 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 28 ST4
      execute(state, MixWord(0x1f44015c)).memory.get(2001.toShort) must be equalTo MixWord(0x00000240)
      // A = 2001, I = 0, F = 1:5, C = 28 ST4
      execute(state, MixWord(0x1f44035c)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 28 ST4
      execute(state, MixWord(0x1f440b5c)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 28 ST4
      execute(state, MixWord(0x1f44049c)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 28 ST4
      execute(state, MixWord(0x1f4404dc)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 28 ST4
      execute(state, MixWord(0x1f44005c)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register I5 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 29 ST5
      execute(state, MixWord(0x1f44015d)).memory.get(2001.toShort) must be equalTo MixWord(0x00000240)
      // A = 2001, I = 0, F = 1:5, C = 29 ST5
      execute(state, MixWord(0x1f44035d)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 29 ST5
      execute(state, MixWord(0x1f440b5d)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 29 ST5
      execute(state, MixWord(0x1f44049d)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 29 ST5
      execute(state, MixWord(0x1f4404dd)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 29 ST5
      execute(state, MixWord(0x1f44005d)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register I6 to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 30 ST6
      execute(state, MixWord(0x1f44015e)).memory.get(2001.toShort) must be equalTo MixWord(0x00000240)
      // A = 2001, I = 0, F = 1:5, C = 30 ST6
      execute(state, MixWord(0x1f44035e)).memory.get(2001.toShort) must be equalTo MixWord(0x40000240)
      // A = 2001, I = 0, F = 5:5, C = 30 ST6
      execute(state, MixWord(0x1f440b5e)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 30 ST6
      execute(state, MixWord(0x1f44049e)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 30 ST6
      execute(state, MixWord(0x1f4404de)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 30 ST6
      execute(state, MixWord(0x1f44005e)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "store register X to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 31 STX
      execute(state, MixWord(0x1f44015f)).memory.get(2001.toShort) must be equalTo MixWord(0x461c8240)
      // A = 2001, I = 0, F = 1:5, C = 31 STX
      execute(state, MixWord(0x1f44035f)).memory.get(2001.toShort) must be equalTo MixWord(0x461c8240)
      // A = 2001, I = 0, F = 5:5, C = 31 STX
      execute(state, MixWord(0x1f440b5f)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 31 STX
      execute(state, MixWord(0x1f44049f)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 31 STX
      execute(state, MixWord(0x1f4404df)).memory.get(2001.toShort) must be equalTo MixWord(0x41240105)
      // A = 2001, I = 0, F = 0:1, C = 31 STX
      execute(state, MixWord(0x1f44005f)).memory.get(2001.toShort) must be equalTo MixWord(0x40083105)
    }

    "store register J to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 32 STJ
      execute(state, MixWord(0x1f440160)).memory.get(2001.toShort) must be equalTo MixWord(0x00000bb8)
      // A = 2001, I = 0, F = 1:5, C = 32 STJ
      execute(state, MixWord(0x1f440360)).memory.get(2001.toShort) must be equalTo MixWord(0x40000bb8)
      // A = 2001, I = 0, F = 5:5, C = 32 STJ
      execute(state, MixWord(0x1f440b60)).memory.get(2001.toShort) must be equalTo MixWord(0x41083138)
      // A = 2001, I = 0, F = 2:2, C = 32 STJ
      execute(state, MixWord(0x1f4404a0)).memory.get(2001.toShort) must be equalTo MixWord(0x41e03105)
      // A = 2001, I = 0, F = 2:3, C = 32 STJ
      execute(state, MixWord(0x1f4404e0)).memory.get(2001.toShort) must be equalTo MixWord(0x41bb8105)
      // A = 2001, I = 0, F = 0:1, C = 32 STJ
      execute(state, MixWord(0x1f440060)).memory.get(2001.toShort) must be equalTo MixWord(0x38083105)
    }

    "store zero to memory" in {
      // A = 2001, I = 0, F = 0:5, C = 33 STZ
      execute(state, MixWord(0x1f440161)).memory.get(2001.toShort) must be equalTo MixWord(0x00000000)
      // A = 2001, I = 0, F = 1:5, C = 33 STZ
      execute(state, MixWord(0x1f440361)).memory.get(2001.toShort) must be equalTo MixWord(0x40000000)
      // A = 2001, I = 0, F = 5:5, C = 33 STZ
      execute(state, MixWord(0x1f440b61)).memory.get(2001.toShort) must be equalTo MixWord(0x41083100)
      // A = 2001, I = 0, F = 2:2, C = 33 STZ
      execute(state, MixWord(0x1f4404a1)).memory.get(2001.toShort) must be equalTo MixWord(0x41003105)
      // A = 2001, I = 0, F = 2:3, C = 33 STZ
      execute(state, MixWord(0x1f4404e1)).memory.get(2001.toShort) must be equalTo MixWord(0x41000105)
      // A = 2001, I = 0, F = 0:1, C = 33 STZ
      execute(state, MixWord(0x1f440061)).memory.get(2001.toShort) must be equalTo MixWord(0x00083105)
    }

    "move memory words" in {
      val prevState = initialState.copy(
        registers = initialState.registers.updatedI(1, MixIndex(3000)),
        memory = initialState.memory
          .updated(MixIndex(2000), MixWord(0x00000001))
          .updated(MixIndex(2001), MixWord(0x00000002))
          .updated(MixIndex(2002), MixWord(0x00000003))
      )
      // A = 2000, I = 0, F = 3, C = 7 MOVE
      val nextState = execute(prevState, MixWord(0x1f4000c7))
      nextState.memory.get(3000.toShort) must be equalTo MixWord(0x00000001)
      nextState.memory.get(3001.toShort) must be equalTo MixWord(0x00000002)
      nextState.memory.get(3002.toShort) must be equalTo MixWord(0x00000003)
    }

    "do nothing when the number of words to move is zero" in {
      val prevState = initialState.copy(
        registers = initialState.registers.updatedI(1, MixIndex(3000)),
        memory = initialState.memory
          .updated(MixIndex(2000), MixWord(0x00000001))
          .updated(MixIndex(2001), MixWord(0x00000002))
          .updated(MixIndex(2002), MixWord(0x00000003))
      )
      // A = 2000, I = 0, F = 0, C = 7 MOVE
      val nextState = execute(prevState, MixWord(0x1f400007))
      nextState.memory.get(3000.toShort) must be equalTo MixWord(0x00000000)
      nextState.memory.get(3001.toShort) must be equalTo MixWord(0x00000000)
      nextState.memory.get(3002.toShort) must be equalTo MixWord(0x00000000)
    }

    "move overlapping ranges in the downward direction" in {
      val prevState = initialState.copy(
        registers = initialState.registers.updatedI(1, MixIndex(1999)),
        memory = initialState.memory
          .updated(MixIndex(2000), MixWord(0x00000001))
          .updated(MixIndex(2001), MixWord(0x00000002))
          .updated(MixIndex(2002), MixWord(0x00000003))
      )
      // A = 2000, I = 0, F = 3, C = 7 MOVE
      val nextState = execute(prevState, MixWord(0x1f4000c7))
      nextState.memory.get(1999.toShort) must be equalTo MixWord(0x00000001)
      nextState.memory.get(2000.toShort) must be equalTo MixWord(0x00000002)
      nextState.memory.get(2001.toShort) must be equalTo MixWord(0x00000003)
      nextState.memory.get(2002.toShort) must be equalTo MixWord(0x00000003)
    }

    "move overlapping ranges in the upward direction" in {
      val prevState = initialState.copy(
        registers = initialState.registers.updatedI(1, MixIndex(2001)),
        memory = initialState.memory
          .updated(MixIndex(2000), MixWord(0x00000001))
          .updated(MixIndex(2001), MixWord(0x00000002))
          .updated(MixIndex(2002), MixWord(0x00000003))
      )
      // A = 2000, I = 0, F = 3, C = 7 MOVE
      val nextState = execute(prevState, MixWord(0x1f4000c7))
      nextState.memory.get(2000.toShort) must be equalTo MixWord(0x00000001)
      nextState.memory.get(2001.toShort) must be equalTo MixWord(0x00000001)
      nextState.memory.get(2002.toShort) must be equalTo MixWord(0x00000001)
      nextState.memory.get(2003.toShort) must be equalTo MixWord(0x00000001)
    }
  }
}
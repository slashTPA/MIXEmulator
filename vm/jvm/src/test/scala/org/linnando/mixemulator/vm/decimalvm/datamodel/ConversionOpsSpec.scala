package org.linnando.mixemulator.vm.decimalvm.datamodel

import org.linnando.mixemulator.vm.decimal._
import org.linnando.mixemulator.vm.exceptions.OverflowException
import org.linnando.mixemulator.vm.io.data.IOWord
import org.specs2.mutable.Specification

class ConversionOpsSpec extends Specification {
  "decimal integer types conversion" should {
    "convert a Byte to a MIX byte" in {
      getByte(1) must be equalTo MixByte(1)
      getByte(99) must be equalTo MixByte(99)
    }

    "convert a Short to a MIX index" in {
      getIndex(1) must be equalTo MixIndex(1)
      getIndex(9999) must be equalTo MixIndex(9999)
      getIndex(-1) must be equalTo MixIndex(0x4001)
      getIndex(-9999) must be equalTo MixIndex((0x4000 | 9999).toShort)
    }

    "convert an Long to a MIX word" in {
      getWord(1L) must be equalTo MixWord(1L)
      getWord(9999999999L) must be equalTo MixWord(9999999999L)
      getWord(-1L) must be equalTo MixWord(0x400000001L)
      getWord(-9999999999L) must be equalTo MixWord(0x400000000L | 9999999999L)
    }

    "convert a zero to a positive zero" in {
      getByte(0) must be equalTo MixByte(0)
      getIndex(0) must be equalTo MixIndex(0)
      getWord(0L) must be equalTo MixWord(0L)
    }

    "throw an exception if a negative value is converted to a MIX byte" in {
      getByte(-1) must throwAn[OverflowException]
    }

    "throw an exception if the value is too big" in {
      getByte(100) must throwAn[OverflowException]
      getIndex(10000) must throwAn[OverflowException]
      getIndex(-10000) must throwAn[OverflowException]
      getWord(10000000000L) must throwAn[OverflowException]
      getWord(-10000000000L) must throwAn[OverflowException]
    }
  }

  "decimal byte conversion" should {
    "convert a MIX byte to a Byte" in {
      MixByte(1).toByte must be equalTo 1
    }

    "convert a MIX byte to an Int" in {
      MixByte(1).toInt must be equalTo 1
    }

    "define zero as zero" in {
      MixByte(0).isZero must beTrue
    }

    "define one as non-zero" in {
      MixByte(1).isZero must beFalse
    }
  }

  "decimal index conversion" should {
    "convert a positive index to a word" in {
      MixIndex(1).toWord must be equalTo MixWord(1)
    }

    "convert a negative index to a word" in {
      MixIndex(0x4001).toWord must be equalTo MixWord(0x400000001L)
    }

    "convert a positive index to a Short" in {
      MixIndex(1).toShort must be equalTo 1
    }

    "convert a negative index to a Short" in {
      MixIndex(0x4001).toShort must be equalTo -1
    }
  }

  "decimal word conversion" should {
    "convert a positive word to a byte" in {
      MixWord(1L).toByte must be equalTo MixByte(1)
    }

    "throw an exception if a negative value is converted to a byte" in {
      MixWord(0x400000001L).toByte must throwAn[OverflowException]
    }

    "throw an exception if the value is too big for a byte" in {
      MixWord(100L).toByte must throwAn[OverflowException]
    }

    "convert a positive word to an index" in {
      MixWord(1L).toIndex must be equalTo MixIndex(1)
    }

    "convert a negative word to an index" in {
      MixWord(0x400000001L).toIndex must be equalTo MixIndex(0x4001)
    }

    "throw an exception if the value is too big for an index" in {
      MixWord(10000L).toIndex must throwAn[OverflowException]
    }

    "convert a positive word to a Long" in {
      MixWord(1).toLong must be equalTo 1L
    }

    "convert a negative word to a Long" in {
      MixWord(0x400000001L).toLong must be equalTo -1L
    }
  }

  "decimal double word conversion" should {
    "convert a positive double word to a word" in {
      MixDWord(0L, 1L).toWord must be equalTo MixWord(1)
    }

    "convert a negative double word to a word" in {
      MixDWord(0x400000000L, 1L).toWord must be equalTo MixWord(0x400000001L)
    }

    "throw an exception if the value is too big for a word" in {
      MixDWord(1L, 0L).toWord must throwAn[OverflowException]
    }
  }

  "decimal i/o word conversion" should {
    "convert a positive index to an i/o word" in {
      MixIndex(102).toIOWord must be equalTo IOWord(negative = false, Vector(0, 0, 0, 1, 2))
    }

    "convert a negative index to an i/o word" in {
      MixIndex((0x4000 | 102).toShort).toIOWord must be equalTo IOWord(negative = true, Vector(0, 0, 0, 1, 2))
    }

    "convert a positive word to an i/o word" in {
      MixWord(102030405L).toIOWord must be equalTo IOWord(negative = false, Vector(1, 2, 3, 4, 5))
    }

    "convert a negative word to an i/o word" in {
      MixWord(0x400000000L + 102030405L).toIOWord must be equalTo IOWord(negative = true, Vector(1, 2, 3, 4, 5))
    }

    "convert a positive i/o word to a word" in {
      getWord(IOWord(negative = false, Seq(1, 2, 3, 4, 5))) must be equalTo MixWord(102030405L)
    }

    "convert a negative i/o word to a word" in {
      getWord(IOWord(negative = true, Seq(1, 2, 3, 4, 5))) must be equalTo MixWord(0x400000000L + 102030405L)
    }
  }

  "decimal character code conversion" should {
    "convert alphanumeric characters to a number" in {
      getWord("A1\u03949@") must be equalTo MixWord(131103952L)
    }

    "convert a character code to a positive number" in {
      // + 0 0 31 32 39 37 57 47 30 30 -> 12977700
      MixDWord(313239L, 3757473030L).charToNumber must be equalTo MixWord(12977700L)
    }

    "convert a character code to a negative number" in {
      // - 0 0 31 32 39 37 57 47 30 30 -> -12977700
      MixDWord(0x400000000L | 313239L, 3757473030L).charToNumber must be equalTo MixWord(0x400000000L | 12977700L)
    }

    "convert a positive number to character code" in {
      // 12977699 -> + 30 30 31 32 39 37 37 36 39 39
      MixWord(12977699).toCharCode must be equalTo MixDWord(3030313239L, 3737363939L)
    }

    "convert a negative number to character code" in {
      // -12977699 -> - 30 30 31 32 39 37 37 36 39 39
      MixWord(0x400000000L | 12977699L).toCharCode must be equalTo MixDWord(0x400000000L | 3030313239L, 3737363939L)
    }
  }
}

package org.linnando.mixemulator.vm.binaryvm.datamodel

import org.linnando.mixemulator.vm.binary._
import org.linnando.mixemulator.vm.exceptions.OverflowException
import org.linnando.mixemulator.vm.io.data.IOWord
import org.specs2.mutable.Specification

class ConversionOpsSpec extends Specification {
  "binary integer types conversion" should {
    "convert a Byte to a MIX byte" in {
      getByte(0x1) must be equalTo MixByte(0x01)
      getByte(0x3f) must be equalTo MixByte(0x3f)
    }

    "convert a Short to a MIX index" in {
      getIndex(0x1) must be equalTo MixIndex(0x0001)
      getIndex(0xfff) must be equalTo MixIndex(0x0fff)
      getIndex(-0x1) must be equalTo MixIndex(0x1001)
      getIndex(-0xfff) must be equalTo MixIndex(0x1fff)
    }

    "convert an Long to a MIX word" in {
      getWord(0x1L) must be equalTo MixWord(0x00000001)
      getWord(0x3fffffffL) must be equalTo MixWord(0x3fffffff)
      getWord(-0x1L) must be equalTo MixWord(0x40000001)
      getWord(-0x3fffffffL) must be equalTo MixWord(0x7fffffff)
    }

    "convert a zero to a positive zero" in {
      getByte(0) must be equalTo MixByte(0x00)
      getIndex(0) must be equalTo MixIndex(0x0000)
      getWord(0L) must be equalTo MixWord(0x00000000)
    }

    "throw an exception if a negative value is converted to a MIX byte" in {
      getByte(-1) must throwAn[OverflowException]
    }

    "throw an exception if the value is too big" in {
      getByte(0x40) must throwAn[OverflowException]
      getIndex(0x1000) must throwAn[OverflowException]
      getIndex(-0x1000) must throwAn[OverflowException]
      getWord(0x40000000L) must throwAn[OverflowException]
      getWord(-0x40000000L) must throwAn[OverflowException]
    }
  }

  "binary byte conversion" should {
    "convert a MIX byte to a Byte" in {
      MixByte(0x1).toByte must be equalTo 1
    }

    "convert a MIX byte to an Int" in {
      MixByte(0x1).toInt must be equalTo 1
    }

    "define zero as zero" in {
      MixByte(0x0).isZero must beTrue
    }

    "define one as non-zero" in {
      MixByte(0x1).isZero must beFalse
    }
  }

  "binary index conversion" should {
    "convert a positive index to a word" in {
      MixIndex(0x1.toShort).toWord must be equalTo MixWord(0x1)
    }

    "convert a negative index to a word" in {
      MixIndex(0x1001.toShort).toWord must be equalTo MixWord(0x40000001)
    }

    "convert a positive index to a Short" in {
      MixIndex(0x1.toShort).toShort must be equalTo 1
    }

    "convert a negative index to a Short" in {
      MixIndex(0x1001.toShort).toShort must be equalTo -1
    }
  }

  "binary word conversion" should {
    "convert a positive word to a byte" in {
      MixWord(0x1).toByte must be equalTo MixByte(0x1)
    }

    "throw an exception if a negative value is converted to a byte" in {
      MixWord(0x40000001).toByte must throwAn[OverflowException]
    }

    "throw an exception if the value is too big for a byte" in {
      MixWord(0x40).toByte must throwAn[OverflowException]
    }

    "convert a positive word to an index" in {
      MixWord(0x1).toIndex must be equalTo MixIndex(0x1.toShort)
    }

    "convert a negative word to an index" in {
      MixWord(0x40000001).toIndex must be equalTo MixIndex(0x1001.toShort)
    }

    "throw an exception if the value is too big for an index" in {
      MixWord(0x1000).toIndex must throwAn[OverflowException]
    }

    "convert a positive word to a Long" in {
      MixWord(0x1).toLong must be equalTo 1
    }

    "convert a negative word to a Long" in {
      MixWord(0x40000001).toLong must be equalTo -1
    }
  }

  "binary double word conversion" should {
    "convert a positive double word to a word" in {
      MixDWord(0x1L).toWord must be equalTo MixWord(0x1)
    }

    "convert a negative double word to a word" in {
      MixDWord(0x1000000000000001L).toWord must be equalTo MixWord(0x40000001)
    }

    "throw an exception if the value is too big for a word" in {
      MixDWord(0x40000000).toWord must throwAn[OverflowException]
    }
  }

  "binary i/o word conversion" should {
    "convert a positive index to an i/o word" in {
      MixIndex(0x0042).toIOWord must be equalTo IOWord(negative = false, Vector(0, 0, 0, 1, 2))
    }

    "convert a negative index to an i/o word" in {
      MixIndex(0x1042).toIOWord must be equalTo IOWord(negative = true, Vector(0, 0, 0, 1, 2))
    }

    "convert a positive word to an i/o word" in {
      MixWord(0x01083105).toIOWord must be equalTo IOWord(negative = false, Vector(1, 2, 3, 4, 5))
    }

    "convert a negative word to an i/o word" in {
      MixWord(0x41083105).toIOWord must be equalTo IOWord(negative = true, Vector(1, 2, 3, 4, 5))
    }

    "convert a positive i/o word to a word" in {
      getWord(IOWord(negative = false, Seq(1, 2, 3, 4, 5))) must be equalTo MixWord(0x01083105)
    }

    "convert a negative i/o word to a word" in {
      getWord(IOWord(negative = true, Seq(1, 2, 3, 4, 5))) must be equalTo MixWord(0x41083105)
    }
  }

  "binary character code conversion" should {
    "convert alphanumeric characters to a number" in {
      getWord("A1\u03949@") must be equalTo MixWord(0x017ca9f4)
    }

    "convert a character code to a positive number" in {
      // + 0 0 31 32 39 37 57 47 30 30 -> 12977700
      MixDWord(0x00007e09e5e6f79eL).charToNumber must be equalTo MixWord(12977700)
    }

    "convert a character code to a negative number" in {
      // - 0 0 31 32 39 37 57 47 30 30 -> -12977700
      MixDWord(0x10007e09e5e6f79eL).charToNumber must be equalTo MixWord(0x40000000 | 12977700)
    }

    "convert a character code to a number with overflow" in {
      // + 1 2 3 4 5 6 7 8 9 10 -> 1234567890 % 64^5
      MixDWord(0x00420c41461c824aL).charToNumber must be equalTo MixWord(160826066)
    }

    "convert a positive number to character code" in {
      // 12977699 -> + 30 30 31 32 39 37 37 36 39 39
      MixWord(12977699).toCharCode must be equalTo MixDWord(0x079e7e09e59649e7L)
    }

    "convert a negative number to character code" in {
      // -12977699 -> - 30 30 31 32 39 37 37 36 39 39
      MixWord(0x40000000 | 12977699).toCharCode must be equalTo MixDWord(0x179e7e09e59649e7L)
    }
  }
}

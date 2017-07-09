package org.linnando.mixemulator.vm.datamodel

import org.linnando.mixemulator.vm.{BinaryProcessingModel, UniWord}
import org.linnando.mixemulator.vm.BinaryProcessingModel.{BinaryMixByte, BinaryMixDWord, BinaryMixIndex, BinaryMixWord}
import org.linnando.mixemulator.vm.exceptions.{OverflowException, WrongFieldSpecException, WrongFieldValueException}
import org.linnando.mixemulator.vm.io.data.IOWord
import org.specs2.mutable.Specification

class BinaryConversionOpsSpec extends Specification {
  "binary zero" should {
    "return zero word" in {
      BinaryProcessingModel.getZero must be equalTo BinaryMixWord(0x0)
    }
  }

  "binary byte conversion" should {
    "convert a MIX byte to a Byte" in {
      BinaryMixByte(0x1).toByte must be equalTo 1
    }

    "convert a MIX byte to an Int" in {
      BinaryMixByte(0x1).toInt must be equalTo 1
    }

    "define zero as zero" in {
      BinaryMixByte(0x0).isZero must beTrue
    }

    "define one as non-zero" in {
      BinaryMixByte(0x1).isZero must beFalse
    }
  }

  "binary index conversion" should {
    "convert a positive index to a word" in {
      BinaryMixIndex(0x1).toWord must be equalTo BinaryMixWord(0x1)
    }

    "convert a negative index to a word" in {
      BinaryMixIndex(0x1001).toWord must be equalTo BinaryMixWord(0x40000001)
    }

    "convert a positive index to a Short" in {
      BinaryMixIndex(0x1).toShort must be equalTo 1
    }

    "convert a negative index to a Short" in {
      BinaryMixIndex(0x1001).toShort must be equalTo -1
    }
  }

  "binary word conversion" should {
    "convert a positive word to an index" in {
      BinaryMixWord(0x1).toIndex must be equalTo BinaryMixIndex(0x1)
    }

    "convert a negative word to an index" in {
      BinaryMixWord(0x40000001).toIndex must be equalTo BinaryMixIndex(0x1001)
    }

    "throw an exception if the value is too big for an index" in {
      BinaryMixWord(0x1000).toIndex must throwAn[OverflowException]
    }

    "convert a positive word to a Long" in {
      BinaryMixWord(0x1).toLong must be equalTo 1
    }

    "convert a negative word to a Long" in {
      BinaryMixWord(0x40000001).toLong must be equalTo -1
    }
  }

  "binary i/o word conversion" should {
    "convert a positive i/o word to a word" in {
      BinaryProcessingModel.getWord(IOWord(negative = false, Seq(1, 2, 3, 4, 5))) must be equalTo BinaryMixWord(0x01083105)
    }

    "convert a negative i/o word to a word" in {
      BinaryProcessingModel.getWord(IOWord(negative = true, Seq(1, 2, 3, 4, 5))) must be equalTo BinaryMixWord(0x41083105)
    }

    "convert a positive word to an i/o word" in {
      BinaryMixWord(0x01083105).toIOWord must be equalTo IOWord(negative = false, Vector(1, 2, 3, 4, 5))
    }

    "convert a negative word to an i/o word" in {
      BinaryMixWord(0x41083105).toIOWord must be equalTo IOWord(negative = true, Vector(1, 2, 3, 4, 5))
    }
  }

  "binary universal word conversion" should {
    "convert a positive universal word to a word" in {
      BinaryMixWord(UniWord(negative = false, Map(
        9.toByte -> 1,
        18.toByte -> 2,
        27.toByte -> 3,
        36.toByte -> 4,
        45.toByte -> 5
      ))) must be equalTo BinaryMixWord(0x01083105)
    }

    "convert a negative universal word to a word" in {
      BinaryMixWord(UniWord(negative = true, Map(
        9.toByte -> 1,
        18.toByte -> 2,
        27.toByte -> 3,
        36.toByte -> 4,
        45.toByte -> 5
      ))) must be equalTo BinaryMixWord(0x41083105)
    }

    "fill missing fields with zeros" in {
      BinaryMixWord(UniWord(negative = true, Map(
        9.toByte -> 1,
        27.toByte -> 2,
        36.toByte -> 3,
        45.toByte -> 4
      ))) must be equalTo BinaryMixWord(0x410020c4)
    }

    "process multi-byte field specifications" in {
      BinaryMixWord(UniWord(negative = true, Map(
        11.toByte -> 123456L,
        37.toByte -> 3210L
      ))) must be equalTo BinaryMixWord(0x5e240c8a)
    }

    "reject incorrect field specifications" in {
      BinaryMixWord(UniWord(negative = false, Map(8.toByte -> 0L))) must throwA[WrongFieldSpecException]
      BinaryMixWord(UniWord(negative = false, Map(6.toByte -> 0L))) must throwA[WrongFieldSpecException]
      BinaryMixWord(UniWord(negative = false, Map(5.toByte -> 0L))) must throwA[WrongFieldSpecException]
    }

    "reject incorrect field values" in {
      BinaryMixWord(UniWord(negative = false, Map(9.toByte -> 64L))) must throwA[WrongFieldValueException]
      BinaryMixWord(UniWord(negative = false, Map(13.toByte -> 0x40000000L))) must throwA[WrongFieldValueException]
    }
  }

  "binary character code conversion" should {
    "convert a character code to a positive number" in {
      // + 0 0 31 32 39 37 57 47 30 30 -> 12977700
      BinaryMixDWord(0x00007e09e5e6f79eL).charToNumber must be equalTo BinaryMixWord(12977700)
    }

    "convert a character code to a negative number" in {
      // - 0 0 31 32 39 37 57 47 30 30 -> -12977700
      BinaryMixDWord(0x10007e09e5e6f79eL).charToNumber must be equalTo BinaryMixWord(0x40000000 | 12977700)
    }

    "convert a character code to a number with overflow" in {
      // + 1 2 3 4 5 6 7 8 9 10 -> 1234567890 % 64^5
      BinaryMixDWord(0x00420c41461c824aL).charToNumber must be equalTo BinaryMixWord(160826066)
    }

    "convert a positive number to character code" in {
      // 12977699 -> + 0 0 31 32 39 37 37 36 39 39
      BinaryMixWord(12977699).toCharCode must be equalTo BinaryMixDWord(0x079e7e09e59649e7L)
    }

    "convert a negative number to character code" in {
      // -12977699 -> - 30 30 31 32 39 37 37 36 39 39
      BinaryMixWord(0x40000000 | 12977699).toCharCode must be equalTo BinaryMixDWord(0x179e7e09e59649e7L)
    }
  }
}

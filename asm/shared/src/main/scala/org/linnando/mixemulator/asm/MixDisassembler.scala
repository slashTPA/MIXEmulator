package org.linnando.mixemulator.asm

import org.linnando.mixemulator.vm.ProcessingModel
import org.linnando.mixemulator.vm.io.data.IOWord

import scala.util.Try

class MixDisassembler(model: ProcessingModel) {
  private val BYTE_SIZE = model.BYTE_SIZE

  def disassembleLine(ioWord: IOWord): String = {
    val Seq(byte0, byte1, byte2, byte3, byte4) = ioWord.bytes
    val sign = if (ioWord.negative) "-" else ""
    Try({
      val (operator, fieldSpec) = MixDisassembler.commands(byte4)(byte3)
      val indexPart = if (byte2 > 0) s",$byte2" else ""
      val addressPart = (byte0.toInt * BYTE_SIZE + byte1).toString
      s"           $operator $sign$addressPart$indexPart$fieldSpec"
    }).getOrElse({
      val value = ((((byte0.toInt * BYTE_SIZE + byte1) * BYTE_SIZE + byte2) * BYTE_SIZE + byte3) * BYTE_SIZE + byte4).toString
      s"           CON  $sign$value"
    })
  }
}

object MixDisassembler {
  val commands: Byte => Map[Byte, (String, String)] = {
    val initialMap = Map.empty[Byte, Map[Byte, (String, String)]] withDefaultValue Map.empty[Byte, (String, String)]
    MixAssembler.commands.foldLeft(initialMap) { (map, command) =>
      val operator = f"${command._1}%-4s"
      val updatedInnerMap =
        if (command._2._3) Map(command._2._2 -> (operator, "")) withDefault { fieldSpec =>
          (operator, s"(${fieldSpec / 8}:${fieldSpec % 8})")
        }
        else map(command._2._1).updated(command._2._2, (operator, ""))
      map.updated(command._2._1, updatedInnerMap)
    }
  }
}

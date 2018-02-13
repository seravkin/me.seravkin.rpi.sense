package me.seravkin.rpi.sense.test

import cats._
import cats.data._
import cats.implicits._
import cats.syntax.all._
import me.seravkin.rpi.sense.led._
import me.seravkin.rpi.sense.led.file.io.{FileIOA, FileIOSenseFrameBuffer, Read, ReadShorts, Seek, Write, WriteShorts}
import org.scalatest._

class SenseLedSpecs extends FlatSpec with Matchers  {

  private case class FileState(seek: Int, vector: Vector[Short])

  private type TestState[A] = State[FileState, A]

  private object FileIOTestInterpreter extends (FileIOA ~> TestState) {

    override def apply[A](fa: FileIOA[A]): TestState[A] = fa match {
      case Seek(long) =>
        State.modify[FileState](_.copy(seek = (long.toInt / 2)))
      case Write(int) =>
        State.modify[FileState](arr => arr.copy(vector = arr.vector.updated(arr.seek, int)))
      case w @ WriteShorts(shorts) =>
        State.modify[FileState](arr => arr.copy(vector = shorts.toVector))
      case Read() =>
        State.inspect[FileState, Short](st => st.vector(st.seek))
      case ReadShorts(ln) =>
        State.inspect[FileState, Array[Short]](st => st.vector.toArray)
    }
  }

  private val led = SenseLed(FileIOSenseFrameBuffer)

  "SenseLed" should "seek and write LE short for single pixel" in {

    val (FileState(_, vector), _) =
      (led(2 -> 3) = Color(255,0,0))
      .foldMap(FileIOTestInterpreter)
      .run(FileState(0,Vector.fill(64)(0.toShort)))
      .value

    vector(52 / 2) should be (0x00F8.toShort)
  }

  it should "seek and read and convert to correct color for single pixel" in {

    val position = 52 / 2

    val (_, color) =
      led(2 -> 3)
        .foldMap(FileIOTestInterpreter)
        .run(FileState(0, Vector.fill(64)(0.toShort).updated(position, 0xE007.toShort)))
        .value

    color should be (Color(0,255,0))

  }

  it should "write and then read correctly" in {

    val color = Color(0, 0, 255)

    val program = for(
      _    <- led(2 -> 3) = color;
      read <- led(2 -> 3)
    ) yield read

    val (_, readColor) = program
        .foldMap(FileIOTestInterpreter)
        .run(FileState(0, Vector.fill(64)(0.toShort)))
        .value

    readColor should be (color)

  }

  it should "write correct colors to array" in {

    val color = Color(131,202,131)

    val program =
      led(Array.fill(8,8)(color))

    val (FileState(_, vector), _) = program
      .foldMap(FileIOTestInterpreter)
      .run(FileState(0, Vector.fill(64)(0.toShort)))
      .value

    vector.forall(_ == 0x5086.toShort) should be (true)

  }

  it should "read correct colors from array" in {

    val color = Color(131,202,131)

    val program = led()

    val (_, array) = program
      .foldMap(FileIOTestInterpreter)
      .run(FileState(0, Vector.fill(64)(0x5086.toShort)))
      .value

    array.flatten.forall(_ == color) should be (true)

  }

  it should "write and then read correct colors from array" in {

    val color = Color(131,202,131)

    val array = Array.fill(8,8)(color)

    val program = for(
      _      <- led(array);
      result <- led()
    ) yield result

    val (_, resultArray) = program
      .foldMap(FileIOTestInterpreter)
      .run(FileState(0, Vector.fill(64)(0.toShort)))
      .value

    resultArray.flatten.forall(_ == color) should be (true)

  }


}

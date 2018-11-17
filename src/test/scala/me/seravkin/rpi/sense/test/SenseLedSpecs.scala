package me.seravkin.rpi.sense.test

import cats._
import cats.data._
import cats.implicits._
import cats.syntax.all._
import me.seravkin.rpi.sense.led._
import me.seravkin.rpi.sense.led.file.io._
import org.scalatest._

class SenseLedSpecs extends FlatSpec with Matchers  {

  private case class FileState(seek: Int, vector: Vector[Short])

  private type TestState[A] = State[FileState, A]

  private object TestIOSenseFrameBuffer extends SenseFrameBuffer[TestState] {
    /** Seek file
      *
      * @param long length to seek
      */
    override def seek(long: Long): TestState[Unit] =
      State.modify[FileState](_.copy(seek = long.toInt / 2))

    /** Writes Short to file
      *
      * @param short Short to write
      */
    override def write(short: Short): TestState[Unit] =
      State.modify[FileState](arr => arr.copy(vector = arr.vector.updated(arr.seek, short)))

    /** Reads Short from file
      *
      * @return Read Short
      */
    override def read(): TestState[Short] =
      State.inspect[FileState, Short](st => st.vector(st.seek))

    /** Writes array to file
      *
      * @param shorts Array of shorts to write
      */
    override def write(shorts: Array[Short]): TestState[Unit] =
      State.modify[FileState](arr => arr.copy(vector = shorts.toVector))

    /** Reads array with specified byte length from file
      *
      * @param length Bytes to read
      * @return Read array
      */
    override def read(length: Int): TestState[Array[Short]] =
      State.inspect[FileState, Array[Short]](st => st.vector.toArray)
}

  private val led = SenseLed(TestIOSenseFrameBuffer)

  "SenseLed" should "seek and write LE short for single pixel" in {

    val (FileState(_, vector), _) =
      (led(2 -> 3) = Color(255,0,0))
      .run(FileState(0,Vector.fill(64)(0.toShort)))
      .value

    vector(52 / 2) should be (0x00F8.toShort)
  }

  it should "seek and read and convert to correct color for single pixel" in {

    val position = 52 / 2

    val (_, color) =
      led(2 -> 3)
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
        .run(FileState(0, Vector.fill(64)(0.toShort)))
        .value

    readColor should be (color)

  }

  it should "write correct colors to array" in {

    val color = Color(131,202,131)

    val program =
      led(Array.fill(8,8)(color))

    val (FileState(_, vector), _) = program
      .run(FileState(0, Vector.fill(64)(0.toShort)))
      .value

    vector.forall(_ == 0x5086.toShort) should be (true)

  }

  it should "read correct colors from array" in {

    val color = Color(131,202,131)

    val program = led()

    val (_, array) = program
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
      .run(FileState(0, Vector.fill(64)(0.toShort)))
      .value

    resultArray.flatten.forall(_ == color) should be (true)

  }


}

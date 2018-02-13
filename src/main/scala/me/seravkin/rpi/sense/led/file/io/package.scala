package me.seravkin.rpi.sense.led.file

import java.io.RandomAccessFile
import java.nio.ByteBuffer

import better.files.File
import cats._
import cats.effect.IO
import cats.free._

package object io {

  /** Free monad algebra for SenseFrameBuffer
    * @tparam A Return type
    */
  sealed trait FileIOA[A]

  final case class Seek(long: Long) extends FileIOA[Unit]

  final case class Write(short: Short) extends FileIOA[Unit]

  final case class Read() extends FileIOA[Short]

  final case class WriteShorts(shorts: Array[Short]) extends FileIOA[Unit]

  final case class ReadShorts(length: Int) extends FileIOA[Array[Short]]

  type FileIO[A] = Free[FileIOA, A]

  private class FileIOInterpreter(randomAccessFile: RandomAccessFile) extends (FileIOA ~> Id) {
    override def apply[A](fa: FileIOA[A]): Id[A] = fa match {
      case Seek(long) =>
        randomAccessFile.seek(long)
      case Write(int) =>
        randomAccessFile.writeShort(int)
      case Read() =>
        randomAccessFile.readShort()
      case WriteShorts(shorts) =>
        randomAccessFile.seek(0)
        val byteBuffer = ByteBuffer.allocate(shorts.length * 2)
        for (s <- shorts)
          byteBuffer.putShort(s)
        randomAccessFile.write(byteBuffer.array(), 0, shorts.length * 2)
      case ReadShorts(length) =>
        val array = Array.ofDim[Byte](length)
        randomAccessFile.read(array)
        ByteBuffer.wrap(array).asShortBuffer().array()
    }
  }

  implicit class FileIOOps[A](fileIO: FileIO[A]) {
    /** Apply file IO operations to specified file */
    def runFileIO(file: File): IO[A] = IO {
      val randomAccessFile = new RandomAccessFile(file.toJava, "rw")
      try {
        fileIO.foldMap(new FileIOInterpreter(randomAccessFile))
      } finally if (randomAccessFile != null) randomAccessFile.close()
    }
  }

}

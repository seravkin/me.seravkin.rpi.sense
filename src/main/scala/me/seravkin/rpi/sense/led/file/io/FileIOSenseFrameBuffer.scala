package me.seravkin.rpi.sense.led.file.io

import java.io.RandomAccessFile
import java.nio.ByteBuffer

import better.files.File
import cats.effect.{Bracket, Sync}

/** Free implementation of FrameBuffer Algebra
  */
final class FileIOSenseFrameBuffer[F[_] : Sync] private(randomAccessFile: RandomAccessFile) extends SenseFrameBuffer[F] {

  /** @inheritdoc*/
  override def seek(long: Long): F[Unit] = Sync[F].delay {
    randomAccessFile.seek(long)
  }

  /** @inheritdoc*/
  override def write(short: Short): F[Unit] = Sync[F].delay {
    randomAccessFile.writeShort(short)
  }

  /** @inheritdoc*/
  override def read(): F[Short] = Sync[F].delay {
    randomAccessFile.readShort()
  }

  /** @inheritdoc*/
  override def write(shorts: Array[Short]): F[Unit] = Sync[F].delay {
    randomAccessFile.seek(0)
    val byteBuffer = ByteBuffer.allocate(shorts.length * 2)
    for (s <- shorts)
      byteBuffer.putShort(s)
    randomAccessFile.write(byteBuffer.array(), 0, shorts.length * 2)
  }

  /** @inheritdoc*/
  override def read(length: Int): F[Array[Short]] = Sync[F].delay {
    val array = Array.ofDim[Byte](length)
    randomAccessFile.read(array)
    ByteBuffer.wrap(array).asShortBuffer().array()
  }

}

object FileIOSenseFrameBuffer {

  /** Correctly creates framebuffer */
  def withFile[F[_] : Bracket[?[_], Throwable] : Sync, A](file: File)(f: SenseFrameBuffer[F] => F[A]): F[A] =
    Bracket[F, Throwable].bracket(Sync[F].delay(new RandomAccessFile(file.toJava, "rw"))) { raf =>
      f(new FileIOSenseFrameBuffer[F](raf))
    } { raw =>
      Sync[F].delay(raw.close())
    }
}
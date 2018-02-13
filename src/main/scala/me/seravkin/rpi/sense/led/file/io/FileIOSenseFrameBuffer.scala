package me.seravkin.rpi.sense.led.file.io

import cats.free.Free.liftF

/** Free implementation of FrameBuffer Algebra
  */
object FileIOSenseFrameBuffer extends SenseFrameBuffer[FileIO] {

  /** @inheritdoc */
  override def seek(long: Long): FileIO[Unit] =
    liftF(Seek(long))

  /** @inheritdoc */
  override def write(short: Short): FileIO[Unit] =
    liftF(Write(short))

  /** @inheritdoc */
  override def read(): FileIO[Short] =
    liftF(Read())

  /** @inheritdoc */
  override def write(shorts: Array[Short]): FileIO[Unit] =
    liftF(WriteShorts(shorts))

  /** @inheritdoc */
  override def read(length: Int): FileIO[Array[Short]] =
    liftF(ReadShorts(length))

}

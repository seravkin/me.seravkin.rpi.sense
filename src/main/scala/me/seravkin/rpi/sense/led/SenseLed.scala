package me.seravkin.rpi.sense.led

import cats.syntax.all._
import cats._
import me.seravkin.rpi.sense.led.file.io.SenseFrameBuffer

/** Implementation of LED Algebra
  * @param frameBuffer underlying algebra for IO operations
  * @tparam F Effect of operations
  */
case class SenseLed[F[_]: Monad](frameBuffer: SenseFrameBuffer[F]) extends Led[F] {

  /**  @inheritdoc */
  override def update(point: Point, color: Color): F[Unit] = {
    val Point(x, y) = point
    val pixNum = 8 * y + x

    for(_ <- frameBuffer.seek(pixNum * 2);
        _ <- frameBuffer.write(color.toRgb565.reverseByteOrder))
      yield ()
  }

  /** @inheritdoc */
  override def apply(point: Point): F[Color] = {
    val Point(x, y) = point
    val pixNum = 8 * y + x

    for(_     <- frameBuffer.seek(pixNum * 2);
        color <- frameBuffer.read())
      yield color.reverseByteOrder.toRgb888
  }

  /** @inheritdoc */
  override def apply(colors: Array[Array[Color]]): F[Unit] = {
    frameBuffer.write(Array.tabulate[Short](64)(i => colors(i / 8)(i % 8).toRgb565.reverseByteOrder))
  }

  /** @inheritdoc */
  override def apply(): F[Array[Array[Color]]] = {
    frameBuffer.read(64).map(array => {
      Array.tabulate[Color](8, 8) { case (i, j) =>
        array(8 * j + i).reverseByteOrder.toRgb888
      }
    })
  }

}

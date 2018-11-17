package me.seravkin.rpi.sense.led.file

import better.files.Dsl._
import better.files._
import cats.effect.{IO, Sync}

/** Implementaion for finding SenseHat LED with default name
  */
final class SyncFrameBufferFinder[F[_]: Sync] extends FrameBufferFinder[F] {

  private[this] val senseName = "RPi-Sense FB"

  /** @inheritdoc */
  def get: F[Option[File]] = Sync[F].delay {
    val graphicsDir = File("/sys/class/graphics")

    if (!graphicsDir.exists || !graphicsDir.isDirectory)
      None
    else
      ls(graphicsDir)
        .filter(file => file.isDirectory && file.name.startsWith("fb"))
        .map(file => file -> file / "name")
        .filter(_._2.exists)
        .filter(_._2.isRegularFile)
        .find(_._2.contentAsString.contains(senseName))
        .map(_._1.name)
        .map("/dev" / _)
  }

}
